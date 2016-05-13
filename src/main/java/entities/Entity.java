package entities;

import game_objects.Circle;

import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.google.common.collect.Lists;

/**
 * Abstract class that defines objects in the game (Zombies, Player).
 *
 * @author bl48
 *
 */
public abstract class Entity extends Circle {

  /**
   * Speed of entity.
   */
  protected double speed;

  /**
   * Top boundary of entity.
   */
  protected float top;

  /**
   * Bottom boundary of entity.
   */
  protected float bottom;

  /**
   * Left boundary of entity.
   */
  protected float left;

  /**
   * Right boundary of entity.
   */
  protected float right;

  /**
   * State of entity.
   */
  protected boolean state;

  /**
   * Image of entity.
   */
  protected Image image;

  /**
   * Color of entity.
   */
  protected Color color;

  /**
   * Constructor for an entity calls init() method of subclass.
   *
   * Used to initialize the fields specific to a subclass.
   */
  public Entity(Entity other) {
    init(other);
  }

  /**
   * Method to initialize the Entity object with specific traits.
   * 
   * @param other
   *          Entity, another entity object to be related with
   */
  public abstract void init(Entity other);

  /**
   * Method to render the Entity object and draw it in to the game window.
   *
   * @param gc
   *          GameContainer, window of the game.
   * @param g
   *          Graphics
   */
  public void render(GameContainer gc, Graphics g) {
    if (this.image != null) {
      image.draw(this.x, this.y, this.radius, this.radius, this.color);
    }
  }

  /**
   * Method to set the image graphic of this entity.
   *
   * @param image
   *          Image object.
   */
  public void setImage(Image image) {
    this.image = image;
  }

  /**
   * Method that loops and updates new information about entity objects.
   *
   * @param gc
   *          GameContainer, window of the game
   * @param delta
   *          Integer, change in time since last update
   */
  public abstract void update(GameContainer gc, int delta);

  /**
   * Returns the location (x,y) of the Entity as a list.
   *
   * @return List of floats, first float is x, second is y.
   */
  List<Float> getLocation() {
    return Lists.newArrayList(this.x, this.y);
  }

  /**
   * Set's the speed that the entity moves at.
   *
   * @param speed
   *          float, speed to move at.
   */
  public void setSpeed(double speed) {
    this.speed = speed;
  }

  /**
   * Gets the current speed.
   * 
   * @return double, current speed
   */
  public double getSpeed() {
    return this.speed;
  }

  /**
   * Destroys the Entity object and clears any information.
   */
  public abstract void die();

  /**
   * Method that sets the new position of the Entity.
   *
   * @param x
   *          float, new x coordinate
   * @param y
   *          float, new y coordinate
   */
  public void moveTo(float x, float y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Sets a new boundary in which the entity can move in.
   *
   * @param top
   *          float, uppermost coordinate
   * @param bottom
   *          float, bottommost coordinate
   * @param left
   *          float, leftmost coordinate
   * @param right
   *          float, rightmost coordinate
   */
  public void setBoundary(float top, float bottom, float left, float right) {
    this.top = top;
    this.bottom = bottom;
    this.left = left;
    this.right = right;
  }

  /**
   * Sets any special attributes that the Entity has.
   *
   * Players can be invincible, zombies can be onFire
   *
   * @param flag
   *          True if special state is on.
   */
  public void setState(boolean flag) {
    this.state = flag;
  }

}
