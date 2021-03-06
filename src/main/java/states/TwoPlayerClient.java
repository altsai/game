package states;

import java.io.IOException;

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
import server.GameClient;

/**
 * Client class that controls the gameplay of the client.
 * @author bl48
 *
 */
public class TwoPlayerClient extends NetworkPlay {

  // list of constants

  private GameClient client;
  private boolean errorMakingClient;
  private boolean hasClient;
  private String playerID;
  private TwoPlayerStartServer twoPlayerStartServer;

  /**
   * Construction for a two player client class.
   *
   * @param twoPlayerStartServer
   * Takes in a start server object that has ip addresses.
   */
  public TwoPlayerClient(TwoPlayerStartServer twoPlayerStartServer) {
    this.twoPlayerStartServer = twoPlayerStartServer;
  }

  @Override
  public void init(GameContainer gc, StateBasedGame s) throws SlickException {
    super.init(gc, s);

    gc.getInput().clearControlPressedRecord();
    gc.getInput().clearKeyPressedRecord();
    gc.getInput().clearMousePressedRecord();

    this.hasClient = false;
    this.gameEnd = false;

    // add the second player to the player array
    Player p2 = new Player(null, "Player 2");
    p2.setPlayer1(false);
    p2.setImage(Resources.getImage("player2"));

    // player 2 is 2/3 to the right of the screen and halfway vertically
    p2.setX(Math.round(Window.width * (0.666)));
    p2.setY(Window.height / 2);

    // explicitly set the id of player2 to "1"
    p2.setID("1");
    this.playerID = p2.getID();
    this.players.put(p2.getID(), p2);
  }

  @Override
  public void render(GameContainer gc, StateBasedGame s, Graphics g)
      throws SlickException {

    g.drawImage(Resources.getImage("background"), 0, 0);

    if (!this.hasClient) {
      g.drawString("Looking for host", 0, 0);;
    }

    if (this.errorMakingClient) {
      g.drawString("ERROR CREATING Client", 0, 0);
    } else {

      //g.drawString("Client", 0, 0);

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
          Resources.getImage("life2").draw(Window.width - 35 - i * 25, 10, 20, 20);
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
        int currNum = (int) ((GAME_COUNTDOWN - timeSinceInit) / 1000);
        if (currNum == 3) {
          Resources.getImage("3").draw(
              (Window.width - Resources.getImage("3").getWidth()) / 2,
              (Window.height - Resources.getImage("3").getHeight()) / 2);
        } else if (currNum == 2) {
          Resources.getImage("2").draw(
              (Window.width - Resources.getImage("2").getWidth()) / 2,
              (Window.height - Resources.getImage("2").getHeight()) / 2);
        } else if (currNum == 1) {
          Resources.getImage("1").draw(
              (Window.width - Resources.getImage("1").getWidth()) / 2,
              (Window.height - Resources.getImage("1").getHeight()) / 2);
        }
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
          this.client.sendMessage(message);
          this.addMessageToQueue(message, this.playerID);
        }
        this.isTyping = false;
        this.currentText.setText("");
        this.currentText.setFocus(false);
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
            , this.messages
            , this.playerID
            , this
            , s
            , twoPlayerStartServer.getAddress(),
            twoPlayerStartServer.getConn());
        this.client.start();
        this.client.deleteServer();
      } catch (IOException e) {
        System.out.println(e.getMessage());
        e.printStackTrace();
        this.errorMakingClient = true;
      }
    }

    // check that the client is connected to a host, wait until the list has 2 players
    if (this.gameStart && this.client.isConnected() && this.players.size() == 2) {
      // update the client player and send position to host

      this.updateChat(gc, s, delta);

      if (!isTyping) {
        // check the player ID, only control the one that client should be controlling
        for (Player p : this.players.values()) {
          if (p.getID().equals(this.playerID)) {
            p.updateAndControlNetworked(gc, delta);
          } else {
            p.update(gc, delta);
          }
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
      s.getState(States.MENU).init(gc, s);
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
    s.enterState(States.CLIENT_END_GAME, new FadeOutTransition(),
        new FadeInTransition());
    this.client.close();
  }

  @Override
  protected void updatePowerups(GameContainer gc, int delta) {

    // DO NOT USE ENHANCED FOR LOOP HERE. IDK WHY BUT THERES A THREADING ISSUE
    // PLS DO NOT CHANGE...
    synchronized (this.powerups) {
      for (String key : this.powerups.keySet()) {
        this.powerups.get(key).update(gc, delta);
      }
    }
  }

}
