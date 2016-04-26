package states;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.util.ConcurrentHashSet;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import entities.Entity;
import entities.Player;
import entities.Zombie;
import game_objects.Powerup;
import server.GameClient;

public class TwoPlayerClient extends GamePlayState {
  //list of all entities in the game
  private Map<String, Zombie> zombies;
  private Map<String, Powerup> powerups;

  private Set<Powerup> pickedUpPowerups;

  // players in the game
  private Map<String, Player> players;

  // list of constants

  private GameClient client;
  private boolean errorMakingClient;
  private boolean hasClient;
  private String playerID;
  private TwoPlayerStartServer twoPlayerStartServer;

  public TwoPlayerClient(TwoPlayerStartServer twoPlayerStartServer) {
    this.twoPlayerStartServer = twoPlayerStartServer;
  }

  @Override
  public void init(GameContainer gc, StateBasedGame s) throws SlickException {
    // make sure all collections are concurrent
    this.zombies = new ConcurrentHashMap<>();
    this.powerups = new ConcurrentHashMap<>();
    this.players = new ConcurrentHashMap<>();
    this.pickedUpPowerups = new ConcurrentHashSet<>();
    this.hasClient = false;
    this.gameEnd = false;

    // add the second player to the player array
    Player p2 = new Player(null, "player2");
    p2.setPlayer1(false);

    // explicitly set the id of player2 to "1"
    p2.setID("1");
    this.playerID = p2.getID();
    this.players.put(p2.getID(), p2);
  }

  @Override
  public void render(GameContainer gc, StateBasedGame s, Graphics g)
      throws SlickException {

    if (!this.hasClient) {
      g.drawString("Looking for host", 0, 0);;
    }

    if (this.errorMakingClient) {
      g.drawString("ERROR CREATING Client", 0, 0);
    } else {

      g.drawString("Client", 0, 0);

      // check that a 3 second delay has completed before playing the game
      long timeSinceInit = System.currentTimeMillis() - this.initialDelayTime;
      if (timeSinceInit < (GAME_COUNTDOWN - 1000)) {
        g.drawString("Game begins in: " + ((GAME_COUNTDOWN - timeSinceInit) / 1000), 200, 200);
      } else {

        this.gameStart = true;

        for (Player p : this.players.values()) {
          p.render(gc, g);

          if (p.getID().equals(this.playerID)) {
            g.drawString(p.getName() + " score: " + p.getScore(), 300, 50);
            g.drawString("Lives: " + p.getLives(), 300, 70);
          } else {
            g.drawString(p.getName() + " score: " + p.getScore(), 0, 50);
            g.drawString("Lives: " + p.getLives(), 0, 70);
          }
        }
        for (Zombie z : this.zombies.values()) {
          z.render(gc, g);
        }
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
      }
    }

  }

  @Override
  public void update(GameContainer gc, StateBasedGame s, int delta)
      throws SlickException {

    if (!this.hasClient) {
      System.out.println("Making client and trying to connect");
      this.hasClient = true;
      try {
        this.client = new GameClient(this.players
            , this.zombies
            , this.powerups
            , this.pickedUpPowerups
            , this.playerID
            , this
            , s
            , twoPlayerStartServer.getAddress());
        this.client.start();
      } catch (IOException e) {
        System.out.println(e.getMessage());
        e.printStackTrace();
        this.errorMakingClient = true;
      }
    }

    // check that the client is connected to a host, wait until the list has 2 players
    if (this.gameStart && this.client.isConnected() && this.players.size() == 2) {
      // update the client player and send position to host

      // check the player ID, only control the one that client should be controlling
      for (Player p : this.players.values()) {
        if (p.getID().equals(this.playerID)) {
          p.updateAndControlNetworked(gc, delta);
        } else {
          p.update(gc, delta);
        }
      }

      this.client.sendClientPosition();

      // if the client presses space, use the powerup
      if (gc.getInput().isKeyPressed(Input.KEY_SPACE)) {
        this.client.sendUsePowerup();
      }

      if (this.gameEnd) {
        endGame(gc, s);
      }

      updatePowerups(gc, delta);
    }


    // go to the home menu state when 'esc' is pressed
    if (gc.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
      this.client.close();
      s.enterState(States.MENU);
    }

  }

  @Override
  public int getID() {
    return States.TWO_PLAYER_CLIENT;
  }

  @Override
  protected void spawnZombie() {
    return;
  }

  @Override
  protected void spawnPowerup() {
    return;
  }

  @Override
  protected void endGame(GameContainer gc, StateBasedGame s) {
    return;
  }

  private void updatePowerups(GameContainer gc, int delta) {

    // DO NOT USE ENHANCED FOR LOOP HERE. IDK WHY BUT THERES A THREADING ISSUE
    // PLS DO NOT CHANGE...
    for (String key : this.powerups.keySet()) {
      this.powerups.get(key).update(gc, delta);
    }
  }

}
