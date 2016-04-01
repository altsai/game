package game_objects;

import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.google.common.collect.Lists;

import entities.Player;

/**
 * Interface that all Powerup objects must implement.
 *
 * @author bl48
 *
 */
public abstract class Powerup extends Circle {

  protected Image image;
  protected Color color;
  protected long spawnStartTime;
  protected Player affectedPlayer;
  protected long spawnInTime;
  protected boolean isPickedUp;
  protected boolean isUsed;
  protected List<Powerup> powerups;

  /**
   * Constructor for a powerup.
   *
   * This constructor sets the spawnInTime of the powerup and also sets
   * isPickedUp to be false.
   *
   * This constructor must be called by the subclass.
   *
   */
  public Powerup(List<Powerup> p) {
    this.spawnInTime = System.currentTimeMillis();
    this.isPickedUp = false;
    this.powerups = p;
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
        && System.currentTimeMillis() - this.spawnInTime > 10000) {
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
  public void activate() {
    this.isUsed = true;
  }

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
    powerups.remove(this);
  }

}
