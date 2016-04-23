package game_objects;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.google.common.collect.Lists;

import edu.brown.cs.altsai.game.Window;
import entities.Player;

/**
 * Interface that all Powerup objects must implement.
 *
 * @author bl48
 *
 */
public abstract class Powerup extends Circle {

  public static final int POWERUP_RADIUS = 20;
  public static final int EXPIRATION_DURATION = 10000;

  protected Image image;
  protected Color color;
  protected long spawnStartTime;
  protected Player affectedPlayer;
  protected long activationStartTime;
  protected boolean isPickedUp;
  protected boolean isUsed;
  protected Map<String, Powerup> powerups;
  protected int powerupIndex;

  public static final int BLACK_HOLE = 0;
  public static final int BOMB = 1;
  public static final int JAIL = 2;
  public static final int ON_FIRE = 3;
  public static final int SPEED = 4;
  public static final int TIMESTOP = 5;
  /**
   * Constructor for a powerup.
   *
   * This constructor sets the spawnInTime of the powerup and also sets
   * isPickedUp to be false.
   *
   * This constructor must be called by the subclass.
   *
   */
  public Powerup(Map<String, Powerup> p) {
    this.spawnStartTime = System.currentTimeMillis();
    this.isPickedUp = false;
    this.powerups = p;

    Random r = new Random();
    this.x = r.nextFloat() * Window.width;
    this.y = r.nextFloat() * Window.height;
    this.radius = POWERUP_RADIUS;
    this.id = UUID.randomUUID().toString();
  }

  /**
   * Method to set the player to effect after pickup.
   *
   * Used during two player mode when either player could pickup powerup. This
   * method is called in the Player.collectPowerup() method.
   *
   * @param player
   */
  public void setPlayer(Player player) {
    this.affectedPlayer = player;
  }

  /**
   * Method to render the Entity object and draw it in to the game window.
   *
   * @param gc
   *          GameContainer, window of the game.
   * @param g
   *          Graphics
   */
  public void render(GameContainer gc, Graphics g) {
    if (this.image != null && !this.isPickedUp) {
      image.draw(this.x, this.y, this.radius, this.radius, this.color);
    }
  }

  public Image getImage() {
    return this.image;
  }

  /**
   * Method to be called when the powerup is picked up.
   */
  public void pickUp() {
    this.isPickedUp = true;
  }

  /**
   * Method that loops and updates new information about powerups objects.
   *
   * This is the method that should check if the powerups has expired. Should
   * also check if the powerup's effect has worn off.
   *
   * @param gc
   *          GameContainer, window of the game
   * @param delta
   *          Integer, change in time since last update
   */
  public void update(GameContainer gc, int delta) {
    if (!this.isPickedUp
        && System.currentTimeMillis() - this.spawnStartTime > EXPIRATION_DURATION) {
      this.kill();
    }

    // also call deactivate for the specific powerup. Deactivate checks if
    // the effects should wear off.
  }

  /**
   * Returns the location (x,y) of the Entity as a list.
   *
   * @return List of floats, first float is x, second is y.
   */
  List<Float> getLocation() {
    return Lists.newArrayList(this.x, this.y);
  }

  /**
   * Method to use the powerup and start effects.
   *
   * Must be called by the subclass on override!
   */
  public abstract List<String> activate();

  /**
   * Method to stop the effects of the powerup.
   */
  public abstract void deactivate();

  /**
   * Method to remove the powerup from the map.
   *
   * Different from deactivate in that kill() can be called without using the
   * powerup.
   */
  public void kill() {
    powerups.remove(this.getID());
  }

  public int getPowerupIndex() {
    return this.powerupIndex;
  }

}
