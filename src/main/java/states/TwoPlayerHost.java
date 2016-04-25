package states;

import java.io.IOException;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.util.ConcurrentHashSet;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import entities.Player;
import entities.Zombie;
import game_objects.Powerup;
import powerups.Bomb;
import powerups.Speed;
import powerups.TimeStop;
import server.GameServer;
import server.Network.ZombieMove;
import server.Network.ZombieMoveList;

public class TwoPlayerHost extends GamePlayState {
  //list of all entities in the game
  private Map<String, Zombie> zombies;
  private Map<String, Powerup> powerups;
  private Set<Powerup> pickedUpPowerups;
  // players in the game
  private Map<String, Player> players;

  // list of constants
  private static final int ZOMBIE_SPAWN_DELAY = 1000;
  private static final int POWERUP_SPAWN_DELAY = 5000;
  private static final double ZOMBIE_BASE_SPEED = 0.3 * 3;
  private static final int MAX_DIFFICULTY_LEVEL = 15;
  private static final double SPEED_MULTIPLIER = 0.1;

  private Random random;
  private int difficultyLevel;
  private String loser;

  // timers
  private long lastZombieSpawnTime;
  private long lastPowerupSpawnTime;
  private long lastDifficultyIncreaseTime;

  // boolean to tell if game should be spawning
  private boolean spawnOn;

  private GameServer server;
  private boolean errorMakingServer;
  private boolean makeServer;
  private String player1ID;

  private Connection conn;
  private TwoPlayerStartServer twoPlayerStartServer;

  public TwoPlayerHost(TwoPlayerStartServer twoPlayerStartServer) {
    this.twoPlayerStartServer = twoPlayerStartServer;
  }

  @Override
  public void init(GameContainer gc, StateBasedGame s) throws SlickException {
    this.zombies = new ConcurrentHashMap<>();
    this.powerups = new ConcurrentHashMap<>();
    this.pickedUpPowerups = new ConcurrentHashSet<>();
    this.players = new ConcurrentHashMap<>();
    this.random = new Random();
    this.spawnOn = true;
    this.loser = null;
    this.difficultyLevel = 1;
    this.makeServer = false;

    Player p1 = new Player(null, "Player1");
    p1.setPlayer1(true);

    // explicitly set the id of the first player to "0"
    p1.setID("0");
    this.player1ID = p1.getID();

    this.players.put(p1.getID(), p1);
    this.lastZombieSpawnTime = System.currentTimeMillis();
    this.lastDifficultyIncreaseTime = System.currentTimeMillis();
  }

  @Override
  public void render(GameContainer gc, StateBasedGame s, Graphics g)
      throws SlickException {

    if (this.server == null || this.server.getConnections().length == 0) {
      g.drawString("Waiting for a client", 150, 150);
    }

    // check that there isn't an error making the server
    if (this.errorMakingServer) {
      g.drawString("ERROR CREATING SERVER", 0, 0);
    } else {
      g.drawString("Host", 0, 0);

      // check that a 3 second delay has completed before playing the game
      long timeSinceInit = System.currentTimeMillis() - this.initialDelayTime;
      if (timeSinceInit < (GAME_COUNTDOWN - 1000)) {
        g.drawString("Game begins in: " + ((GAME_COUNTDOWN - timeSinceInit) / 1000), 200, 200);
      } else {

        this.gameStart = true;

        for (Player p : this.players.values()) {
          p.render(gc, g);

          if (p.getID().equals(this.player1ID)) {
            g.drawString(p.getName() + " score: " + p.getScore(), 0, 50);
            g.drawString("Lives: " + p.getLives(), 0, 70);
          } else {
            g.drawString(p.getName() + " score: " + p.getScore(), 300, 50);
            g.drawString("Lives: " + p.getLives(), 300, 70);
          }
        }
        for (Zombie z : this.zombies.values()) {
          z.render(gc, g);
        }
        for (Powerup p : this.powerups.values()) {
          p.render(gc, g);
        }
      }
    }

  }

  @Override
  public void update(GameContainer gc, StateBasedGame s, int delta)
      throws SlickException {

    if (!this.makeServer) {
      try {
        this.server = new GameServer(this.players
            , this.zombies
            , this.powerups
            , this.player1ID
            , this
            , s
            , twoPlayerStartServer.getConn()
            , twoPlayerStartServer.getAddress());
        this.server.start();
        this.makeServer = true;
      } catch (IOException e) {
        System.out.println(e.getMessage());
        e.printStackTrace();
        this.errorMakingServer = true;
      }
    }


    // if the server has a connection attached, start the game
    if (this.gameStart && this.server.getConnections().length > 0) {
      spawnZombie();
      spawnPowerup();

      // update player positions, make sure to be only able to control host player
      for (Player p : this.players.values()) {
        if (p.getID().equals(this.player1ID)) {
          p.updateAndControlNetworked(gc, delta);
        } else {
          p.update(gc, delta);
        }
      }

      this.moveZombies();

      // send the host's position
      this.server.sendHostPosition();

      // send info about both updated players to the client
      for (Player p : this.players.values()) {
        this.server.updatePlayer(p.getID(), false);
      }

      // check collisions
      updateAndCheckCollisions(gc, s, delta);
      updatePowerups(gc, delta);

      // if the host presses space, use the powerup
      if (gc.getInput().isKeyPressed(Input.KEY_SPACE)) {
        List<String> removedZombies = this.players.get(this.player1ID).usePowerup();
        if (removedZombies.size() > 0) {
          this.server.removeZombie(removedZombies);
        }
      }
    }

    // go to the home menu state when 'esc' is pressed
    if (gc.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
      this.server.close();
      s.enterState(States.MENU);
    }



  }

  @Override
  public int getID() {
    return States.TWO_PLAYER_HOST;
  }

  private void moveZombies() {

    if (this.server.getConnections().length > 0) {
      if (this.zombies.size() > 9) {
        // break up the list into smaller lists of 10 to send
        List<ZombieMove> sentList = new LinkedList<>();
        for (Zombie z : this.zombies.values()) {
          ZombieMove packet = new ZombieMove();
          packet.id = z.getID();
          packet.x = z.getX();
          packet.y = z.getY();
          sentList.add(packet);
          if (sentList.size() > 9) {
            ZombieMoveList listPacket = new ZombieMoveList();
            listPacket.list = sentList;
            this.server.getConnections()[0].sendTCP(listPacket);
            sentList.clear();
          }
        }
        ZombieMoveList listPacket = new ZombieMoveList();
        listPacket.list = sentList;
        this.server.getConnections()[0].sendTCP(listPacket);

      } else {

        List<ZombieMove> sentList = new LinkedList<>();
        for (Zombie z : this.zombies.values()) {
          ZombieMove packet = new ZombieMove();
          packet.id = z.getID();
          packet.x = z.getX();
          packet.y = z.getY();
          sentList.add(packet);
        }
        ZombieMoveList listPacket = new ZombieMoveList();
        listPacket.list = sentList;
        this.server.getConnections()[0].sendTCP(listPacket);
      }
    }
  }


  /**
   * Method that upates the entities and checks for collisions.
   *
   * @param gc
   *          GameContainer, window of the game
   * @param delta
   *          Int, change in time since last update
   */
  private void updateAndCheckCollisions(GameContainer gc, StateBasedGame s,
      int delta) {


    //TODO: Combine all zombies in alist and send the list as a packet


    // check for player collision with every entity
    for (Zombie z : this.zombies.values()) {
      z.update(gc, delta);

      boolean onFire = z.isOnFire();

      // update the zombie's position and state on the client
      // might be able to optimize by having moveZombie take in a hashmap
      // process into smaller lists, and then send lists instead of individual zombi

      // check player's lives and mark invincible as necessary
      for (Player p : this.players.values()) {

        if (p.isCollision(z) && !onFire) {

          if (p.isInvincible()) {
            continue;
          }

          if (p.getLives() == 0) {
            this.loser = p.getName() + " Lost!!!";
            this.server.sendGameEnd(this.loser);

            endGame(gc, s);
          } else {
            p.loseLife();

            // update the player on losing a life
            this.server.updatePlayer(p.getID(), true);
          }
        }
      }
    }

    // check for player collision with every powerup
    for (Powerup powerup : this.powerups.values()) {
      for (Player p : this.players.values()) {

        // check that the pickedup powerup isn't in the state where it has been picked up,
        // but is still in map because it's effects haven't worn off.
        if (p.isCollision(powerup) && !this.pickedUpPowerups.contains(powerup)) {
          p.collectPowerup(powerup);
          this.pickedUpPowerups.add(powerup);
          this.server.sendPowerupPickup(p, powerup);
          //this.server.removePowerup(powerup.getID());
        }
      }
    }
  }

  @Override
  public String getLoser() {
    return this.loser;
  }

  /**
   * Method that loops through powerups and updates them
   *
   * @param gc
   * @param delta
   */
  private void updatePowerups(GameContainer gc, int delta) {

    // DO NOT USE ENHANCED FOR LOOP HERE. IDK WHY BUT THERES A THREADING ISSUE
    // PLS DO NOT CHANGE...
    for (String key : this.powerups.keySet()) {
      this.powerups.get(key).update(gc, delta);
      if (this.powerups.get(key) == null) {
        this.server.removePowerup(key);
      }
    }
  }

  /**
   * Method to set whether or not the game should be spawning any zombies.
   *
   * @param flag
   */
  @Override
  public void setSpawnOn(boolean flag) {
    this.spawnOn = flag;
  }

  @Override
  protected void spawnZombie() {
    // check if the game should be spawning zombies (time stop may have stopped
    // spawns)
    if (this.spawnOn) {

      if (System.currentTimeMillis() - this.lastZombieSpawnTime >= ZOMBIE_SPAWN_DELAY) {

        // have a random player to target
        String targetID = String.valueOf(random.nextInt(this.players.size()));
        Player target = this.players.get(targetID);

        // at any given time there is a 30% chance of multiple spawns
        if (random.nextInt(9) < 3) {
          for (int i = 0; i < this.difficultyLevel; i++) {
            // spawn targeting a random player

            Zombie newZombie = new Zombie(target);

            newZombie.setSpeed(ZOMBIE_BASE_SPEED
                + ((this.difficultyLevel - 1) * SPEED_MULTIPLIER)
                * ZOMBIE_BASE_SPEED);
            this.zombies.put(newZombie.getID(), newZombie);

            // send the spawned zombie
            this.server.sendNewZombie(newZombie.getID(), targetID);
          }
        }

        Zombie newZombie = new Zombie(target);

        newZombie.setSpeed(ZOMBIE_BASE_SPEED
            + ((this.difficultyLevel - 1) * SPEED_MULTIPLIER)
            * ZOMBIE_BASE_SPEED);
        this.zombies.put(newZombie.getID(), newZombie);

        // send the zombie
        this.server.sendNewZombie(newZombie.getID(), targetID);

        this.lastZombieSpawnTime = System.currentTimeMillis();
      }

      if (this.difficultyLevel < MAX_DIFFICULTY_LEVEL) {
        if (System.currentTimeMillis() - this.lastDifficultyIncreaseTime > 10000) {

          this.difficultyLevel++;
          this.lastDifficultyIncreaseTime = System.currentTimeMillis();
        }
      }
    }
  }


  @Override
  protected void spawnPowerup() {
    if (System.currentTimeMillis() - this.lastPowerupSpawnTime >= POWERUP_SPAWN_DELAY) {

      double randomNum = random.nextDouble();
      if (randomNum < 0.33) {
        Bomb bomb = new Bomb(powerups, zombies);
        this.powerups.put(bomb.getID(), bomb);
        this.server.sendNewPowerup(bomb);
      } else if (randomNum < 0.6 && randomNum >= 0.33) {
        Speed speed = new Speed(powerups);
        this.powerups.put(speed.getID(), speed);
        this.server.sendNewPowerup(speed);
      } else if (randomNum < 0.9 && randomNum >= 0.6) {
        TimeStop timestop = new TimeStop(powerups, zombies, players, this);
        this.powerups.put(timestop.getID(), timestop);
        this.server.sendNewPowerup(timestop);
      }

      this.lastPowerupSpawnTime = System.currentTimeMillis();
    }

  }


  @Override
  protected void endGame(GameContainer gc, StateBasedGame s) {
    s.enterState(States.HOST_END_GAME, new FadeOutTransition(),
        new FadeInTransition());
    this.server.close();
  }

}
