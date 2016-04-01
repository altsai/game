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
  protected Player effectedPlayer;


  /**
   * Method to render the Entity object and draw it in to the game window.
   *
   * @param gc    GameContainer, window of the game.
   * @param g     Graphics
   */
  public void render(GameContainer gc, Graphics g) {
    if (this.image != null) {
      image.draw(this.x, this.y, this.radius, this.radius, this.color);
    }
  }

  /**
   * Method that loops and updates new information about entity objects.
   *
   * @param gc        GameContainer, window of the game
   * @param delta     Integer, change in time since last update
   */
  public abstract void update(GameContainer gc, int delta);

  /**
   * Returns the location (x,y) of the Entity as a list.
   *
   * @return   List of floats, first float is x, second is y.
   */
  List<Float> getLocation() {
    return Lists.newArrayList(this.x, this.y);
  }

  /**
   * Method to use the powerup and start effects.
   */
  public abstract void activate();

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
  public abstract void kill();

}
