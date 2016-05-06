package states;

import java.awt.Font;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.StateBasedGame;

import entities.Player;
import game_objects.PlayerMessage;

/**
 * Abstract class that defines Network Playing attributes.
 *
 * Extends GamePlayState
 * @author bl48
 *
 */
public abstract class NetworkPlay extends GamePlayState {

  //list of constants
  protected static final int ZOMBIE_SPAWN_DELAY = 1000;
  protected static final int POWERUP_SPAWN_DELAY = 5000;
  protected static final double ZOMBIE_BASE_SPEED = 0.3 * 3;
  protected static final int MAX_DIFFICULTY_LEVEL = 15;
  protected static final double SPEED_MULTIPLIER = 0.1;
  public static final int CHAT_CACHE_LENGTH = 5;

  //map that keep track of the last player's attributes
  // used to decide whether or not an update is needed
  protected Map<String, Player> previousPlayers;

  // cache of messages to display
  protected Queue<PlayerMessage> messages;
  // textfield for user to enter a message
  protected TextField currentText;
  // flag to determine if user is in chat mode
  protected boolean isTyping;
  // flag to determine if the user wants chat to appear
  protected boolean chatOn;

  protected TrueTypeFont playerFont;
  protected TrueTypeFont otherFont;

  @Override
  public abstract int getID();

  @Override
  public void init(GameContainer gc, StateBasedGame s) throws SlickException {
    super.init(gc, s);

    Font font = new Font("Arial", Font.BOLD, 20);
    playerFont = new TrueTypeFont(font, true);

    Font other = new Font("Arial", Font.ITALIC, 20);
    otherFont = new TrueTypeFont(other, true);

    this.previousPlayers = new ConcurrentHashMap<>();
    this.messages = new ConcurrentLinkedQueue<>();
    this.currentText = new TextField(gc, playerFont, 500, 400, 400, 25);
    this.currentText.setMaxLength(120);
    this.isTyping = false;
    this.chatOn = true;

  }

  /**
   * Method to add a message from current player to the chat queue for display.
   *
   * The queue will automatically have length 5
   * @param message
   */
  protected void addMessageToQueue(String message, String playerID) {
    PlayerMessage newMessage = new PlayerMessage();
    newMessage.message = message;
    newMessage.playerID = playerID;
    if (this.messages.size() == CHAT_CACHE_LENGTH) {
      this.messages.poll();
    }
    this.messages.add(newMessage);
  }


  /**
   * Method to render the chat and print all cached messages on the screen.
   * @param gc GameContainer object
   * @param g  Graphics object to draw the strings
   */
  protected void renderChat(GameContainer gc, Graphics g, String playerID) {
    float x = 20;
    float y = 300;
    for (PlayerMessage m : this.messages) {
      if (m.playerID.equals(playerID)) {
        playerFont.drawString(x, y, "YOU:   " + m.message, new Color(0, 0, 102));
      } else {
        otherFont.drawString(x, y,  "OTHER: " + m.message, new Color(128, 0, 0));
      }
      y += 25;
    }
  }

  /**
   * Method to update the chat with user input.
   *
   * The user can type T to start typing.
   * The user can hit enter to submit the message or if the field is
   * empty, hit T again to hid the chat.
   *
   * @param gc        GameContainer object
   * @param s         StateBasedGame object
   * @param delta     Integer, time elapsed since last update
   */
  protected void updateChat(GameContainer gc, StateBasedGame s, int delta) {
    if (gc.getInput().isKeyPressed(Input.KEY_T)) {
      if (!isTyping) {
        isTyping = true;
        this.currentText.setFocus(true);
        this.currentText.setText("");
      }
    }

    if (!isTyping && !this.currentText.hasFocus() && gc.getInput().isKeyPressed(Input.KEY_C)) {
      this.chatOn = !this.chatOn;
    }
  }

  @Override
  protected abstract void spawnZombie();

  @Override
  protected abstract void spawnPowerup();

  @Override
  protected abstract void endGame(GameContainer gc, StateBasedGame s)
      throws SlickException;

}
