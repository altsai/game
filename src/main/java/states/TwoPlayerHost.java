package states;

import java.io.IOException;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import edu.brown.cs.altsai.game.Resources;
import edu.brown.cs.altsai.game.Window;
import entities.Entity;
import entities.Player;
import entities.Zombie;
import game_objects.Powerup;
import powerups.Bomb;
import powerups.Jail;
import powerups.LaserBeam;
import powerups.Speed;
import powerups.TimeStop;
import server.GameServer;
import server.Network.ZombieMove;
import server.Network.ZombieMoveList;

public class TwoPlayerHost extends NetworkPlay {


  private GameServer server;
  private boolean errorMakingServer;
  private boolean makeServer;
  private String player1ID;

  private static final int BUTTON_WIDTH = 180;
  private static final int BUTTON_HEIGHT = 68;

  private Connection conn;
  private TwoPlayerStartServer twoPlayerStartServer;

  public TwoPlayerHost(TwoPlayerStartServer twoPlayerStartServer) {
    this.twoPlayerStartServer = twoPlayerStartServer;
  }


  /**
   * Method that goes through all players in game and updates if needed.
   */
  private void updatePlayers() {
    for (Player p : this.players.values()) {
      if (!p.equals(this.previousPlayers.get(p.getID()))) {
        this.server.updatePlayer(p.getID(), false);
        this.previousPlayers.put(p.getID(), p);
      }
    }
  }

  @Override
  public void init(GameContainer gc, StateBasedGame s) throws SlickException {
    super.init(gc, s);

    this.makeServer = false;

    Player p1 = new Player(null, "player1");
    p1.setPlayer1(true);
    this.previousPlayers.put(p1.getID(), p1);

    // player 1 is 1/3 to right of screen, halfway vertically
    p1.setX(Window.width / 3);
    p1.setY(Window.height / 2);

    // explicitly set the id of the first player to "0"
    p1.setID("0");
    this.player1ID = p1.getID();
    this.playerID = p1.getID();

    this.players.put(p1.getID(), p1);

    monitorShutdown();
  }

  public void monitorShutdown() {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        if (server != null) {
          server.deleteServer();
        }
      }
    });
  }

  @Override
  public void render(GameContainer gc, StateBasedGame s, Graphics g)
      throws SlickException {

    g.drawImage(Resources.getImage("background"), 0, 0);

    if (this.server == null || this.server.getConnections().length == 0) {
      if (Window.width == 1390) {
        Resources.getImage("connectionPage").draw(0, 0);
      } else if (Window.width == 1132) {
        Resources.getImage("connectionPageSmall").draw(0, 0);
      }

      // Main menu button
      Resources.getImage("buttonMainMenu").draw(20, 20, BUTTON_WIDTH,
          BUTTON_HEIGHT);
    }

    // check that there isn't an error making the server
    if (this.errorMakingServer) {
      g.drawString("ERROR CREATING SERVER", 0, 0);
    } else if (server.getConnections().length > 0) {
      // g.drawString("Host", 0, 0);

      // Draw bounding box
      g.setColor(Color.gray);
      g.drawRoundRect(10, 40, Window.width - 20, Window.height - 50, 10);
      g.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));

      if (players.get("0") != null && players.get("1") != null) {
        // Draw lives
        for (int i = 0; i < this.players.get("0").getLives() + 1; i++) {
          Resources.getImage("life1").draw(15 + i * 25, 10, 20, 20);
        }
        for (int i = 0; i < this.players.get("1").getLives() + 1; i++) {
          Resources.getImage("life2").draw(Window.width - 35 - i * 25, 10, 20,
              20);
        }

        // Draw current powerups
        g.setColor(Color.gray);
        g.drawRect(Window.width / 4 - 15, 6, 30, 30);
        g.drawRect(3 * Window.width / 4 - 15, 6, 30, 30);
        g.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        Powerup currPowerup1 = this.players.get("0").getCurrPowerup();
        Powerup currPowerup2 = this.players.get("1").getCurrPowerup();
        if (currPowerup1 != null) {
          currPowerup1.getImage().draw(Window.width / 4 - 9, 11, 20, 20);
        }
        if (currPowerup2 != null) {
          currPowerup2.getImage().draw(3 * Window.width / 4 - 9, 11, 20, 20);
        }

      }

      // check that a 3 second delay has completed before playing the game
      long timeSinceInit = System.currentTimeMillis() - this.initialDelayTime;
      if (timeSinceInit < (GAME_COUNTDOWN - 1000)) {
        g.drawString("Game begins in: "
            + ((GAME_COUNTDOWN - timeSinceInit) / 1000), 200, 200);
      } else {

        this.gameStart = true;

        for (Powerup p : this.powerups.values()) {
          p.render(gc, g);
        }

        for (Powerup p : pickedUpPowerups) {
          if (p.isActivated()) {
            for (Entity e : p.getChildren()) {
              e.render(gc, g);
            }
          }
        }
        for (Player p : this.players.values()) {
          p.render(gc, g);
        }
        for (Zombie z : this.zombies.values()) {
          z.render(gc, g);
        }

        // if currently typing, draw the current message
        if (this.isTyping) {
          this.currentText.render(gc, g);
        }

        if (this.chatOn) {
          renderChat(gc, g, this.playerID);
        }

      }
    }

  }

  @Override
  protected void updateChat(GameContainer gc, StateBasedGame s, int delta) {
    super.updateChat(gc, s, delta);
    if (this.isTyping) {
      if (gc.getInput().isKeyPressed(Input.KEY_T)
          && this.currentText.getText().length() == 0) {
        this.isTyping = false;

        // if user presses enter while in current text, send text.
      } else if (this.currentText.hasFocus() && gc.getInput().isKeyPressed(Input.KEY_ENTER)) {
        String message = this.currentText.getText();
        if (message.length() > 0) {
          this.server.sendMessage(message);
          this.addMessageToQueue(message, this.playerID);
        }
        this.isTyping = false;
        this.currentText.setFocus(false);
        this.currentText.setText("");
      }
    }
  }


  @Override
  public void update(GameContainer gc, StateBasedGame s, int delta)
      throws SlickException {

    if (!this.makeServer) {
      try {
        this.server = new GameServer(this.players, this.zombies
            , this.powerups, this.previousPlayers, this.messages
            , this.player1ID, this, s, twoPlayerStartServer.getConn(),
            twoPlayerStartServer.getAddress());
        this.server.start();
        this.makeServer = true;
      } catch (IOException e) {
        if (server != null) {
          server.deleteServer();
        }
        System.out.println(e.getMessage());
        e.printStackTrace();
        this.errorMakingServer = true;
      }
    }

    // if the server has a connection attached, start the game
    if (this.gameStart && this.server.getConnections().length > 0) {
      spawnZombie();
      spawnPowerup();

      this.updateChat(gc, s, delta);


      // if not currently typing, allow movement
      if (!this.isTyping) {
        for (Player p : this.players.values()) {
          if (p.getID().equals(this.player1ID)) {
            p.updateAndControlNetworked(gc, delta);
          } else {
            p.update(gc, delta);
          }
        }
      }

      this.moveZombies();

      // send the host's position
      this.server.sendHostPosition();

      // send info about both updated players to the client
      this.updatePlayers();

      // check collisions
      updateAndCheckCollisions(gc, s, delta);
      updatePowerups(gc, delta);

      // if the host presses space, use the powerup
      if (gc.getInput().isKeyPressed(Input.KEY_SPACE)) {
        this.server.sendUsePowerup();
        List<String> removedZombies = this.players.get(this.player1ID)
            .usePowerup();
        if (removedZombies.size() > 0) {
          this.server.removeZombie(removedZombies);
        }
      }


    }

    // Get x and y mouse position coordinates
    int posX = gc.getInput().getMouseX();
    int posY = gc.getInput().getMouseY();

    // go to the home menu state when 'esc' is pressed
    if (gc.getInput().isKeyPressed(Input.KEY_ESCAPE) || ((this.server == null || this.server.getConnections().length == 0) && (gc.getInput().isMouseButtonDown(0) && posX >= 20
        && posX <= 20 + BUTTON_WIDTH && posY >= 20 && posY <= 20 + BUTTON_HEIGHT))) {
      if (this.server != null) {
        this.server.close();
      }
      s.enterState(States.MENU);
    }

  }

  @Override
  public int getID() {
    return States.TWO_PLAYER_HOST;
  }

  private void moveZombies() {
    if (this.server.getConnections().length > 0) {
      if (this.zombies.size() > 100) {
        // break up the list into smaller lists of 10 to send
        List<ZombieMove> sentList = new LinkedList<>();
        for (Zombie z : this.zombies.values()) {
          ZombieMove packet = new ZombieMove();
          packet.id = z.getID();
          packet.x = z.getX();
          packet.y = z.getY();
          sentList.add(packet);
          if (sentList.size() > 100) {
            ZombieMoveList listPacket = new ZombieMoveList();
            listPacket.list = sentList;
            this.server.getConnections()[0].sendUDP(listPacket);
            sentList.clear();
          }
        }
        ZombieMoveList listPacket = new ZombieMoveList();
        listPacket.list = sentList;
        this.server.getConnections()[0].sendUDP(listPacket);

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
        this.server.getConnections()[0].sendUDP(listPacket);
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
  @Override
  protected void updateAndCheckCollisions(GameContainer gc, StateBasedGame s,
      int delta) {

    // check for player collision with every entity
    for (Zombie z : this.zombies.values()) {
      z.update(gc, delta);

      boolean onFire = z.isOnFire();

      // check player's lives and mark invincible as necessary
      for (Player p : this.players.values()) {

        if (p.isCollision(z) && !onFire) {
          if (p.isInvincible()) {
            continue;
          }
          if (p.getLives() == 0) {
            this.loser = p.getID();
            this.server.sendGameEnd(this.loser);
            // do not call endGame here. The server handles this
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

        // check that the pickedup powerup isn't in the state where it has been
        // picked up,
        // but is still in map because it's effects haven't worn off.
        if (p.isCollision(powerup) && !this.pickedUpPowerups.contains(powerup)) {
          p.collectPowerup(powerup);
          this.pickedUpPowerups.add(powerup);
          this.server.sendPowerupPickup(p, powerup);
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
  @Override
  protected void updatePowerups(GameContainer gc, int delta) {

    // Do not use powerups.values() to iterate, we need to check
    // if the powerup is removed from the hashmap during this method.
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

            Zombie newZombie = new Zombie(target, this.players);

            newZombie.setSpeed(ZOMBIE_BASE_SPEED
                + ((this.difficultyLevel - 1) * SPEED_MULTIPLIER)
                * ZOMBIE_BASE_SPEED);
            this.zombies.put(newZombie.getID(), newZombie);

            // send the spawned zombie
            this.server.sendNewZombie(newZombie.getID(), targetID);
          }
        }

        Zombie newZombie = new Zombie(target, this.players);

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
      if (randomNum < 0.2) {
        Bomb bomb = new Bomb(powerups, zombies, players);
        this.powerups.put(bomb.getID(), bomb);
        this.server.sendNewPowerup(bomb);
      } else if (randomNum < 0.4 && randomNum >= 0.2) {
        Speed speed = new Speed(powerups);
        this.powerups.put(speed.getID(), speed);
        this.server.sendNewPowerup(speed);
      } else if (randomNum < 0.6 && randomNum >= 0.4) {
        TimeStop timestop = new TimeStop(powerups, zombies, players, this);
        this.powerups.put(timestop.getID(), timestop);
        this.server.sendNewPowerup(timestop);
      } else if (randomNum < 0.8 && randomNum >= 0.6) {
        LaserBeam lb = new LaserBeam(powerups, zombies, players, server);
        this.powerups.put(lb.getID(), lb);
        this.server.sendNewPowerup(lb);
      } else {
        Jail jail = new Jail(powerups, zombies, players);
        this.powerups.put(jail.getID(), jail);
        this.server.sendNewPowerup(jail);
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
