package game_objects;

/**
 * Defines a circular hitbox.
 *
 * Used by player and zombie and some powerups as a base. Circles in the game
 * are 30 x 30 px
 *
 * @author bl48
 *
 */
public class Circle {
  // fields of the circle
  protected String id;
  protected float x;
  protected float y;
  protected float radius;

  /**
   * Empty constructor for a circle
   */
  public Circle() {
  }

  /**
   * Constructor for a specific Circle object.
   *
   * @param x
   *          float, x coordinate that circle spawns in
   * @param y
   *          float, y coordinate that circle spawns in
   * @param radius
   *          float, radius of the circle object
   */
  public Circle(float x, float y, float radius) {
    this.x = x;
    this.y = y;
    this.radius = radius;
  }

  /**
   * Gets the x coordinate of the circle object center.
   *
   * @return float, x coordinate of center
   */
  public float getX() {
    return this.x;
  }

  /**
   * Gets the y coordinate of the circle object center.
   *
   * @return float, y coordinate of center
   */
  public float getY() {
    return this.y;
  }

  public void setX(float newX) {
    x = newX;
  }

  public void setY(float newY) {
    y = newY;
  }

  /**
   * Getter for radius of circle.
   *
   * @return float, radius of circle
   */
  public float getRadius() {
    return this.radius;
  }

  public void setRadius(float r) {
    radius = r;
  }

  /**
   * Returns the distance from top left corner of game window.
   *
   * @return double, distance from top left corner of game window.
   */
  public double getDistanceFromOrigin() {
    return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
  }

  /**
   * Returns the distance to another circle object.
   *
   * Used to determine collisions with other game objects.
   *
   * @param other
   *          Circle
   * @return double, distance to the other Circle.
   */
  public double distTo(Circle other) {
    double distSquared = Math.pow(this.x - other.getX(), 2)
        + Math.pow(this.y - other.getY(), 2);
    return Math.sqrt(distSquared);
  }

  /**
   * Determines if there is a collision between this and other Circle.
   *
   * @param other
   *          Circle
   * @return True if collision occurs, else false.
   */
  public boolean isCollision(Circle other) {
    if (Math.abs(this.x - other.getX()) > (other.getRadius() + this.radius)
        || Math.abs(this.y - other.getY()) > (other.getRadius() + this.radius)) {
      return false;
    } else {
      return distTo(other) <= this.radius;
    }
  }

  /**
   * Gets the unique ID for this object
   * @return
   */
  public String getID() {
    return this.id;
  }

}
