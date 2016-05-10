package powerups;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;

import edu.brown.cs.altsai.game.Resources;
import entities.Entity;

/**
 * Laser shot that moves across the screen, eliminating zombies.
 *
 * @author Alison
 *
 */
public class LaserShot extends Entity {
  /**
   * The sprite sheet for the LaserShot.
   */
  private SpriteSheet spriteSheet;

  /**
   * The animation for the LaserShot.
   */
  private Animation animation;

  /**
   * How long each frame lasts.
   */
  private static final int ANIMATION_FRAME_TIME = 50;

  /**
   * Radius.
   */
  public static final float LASER_SHOT_RADIUS = 75;

  /**
   * Correcting offset.
   */
  public static final float LASER_OFFSET = 20;

  /**
   * Size of animation in pixels.
   */
  public static final float ANIMATION_SIZE = 120;

  /**
   * Angle of travel.
   */
  private float angle;

  /**
   * Constructs a LaserShot.
   * 
   * @param other
   *          to be passed to the superconstructor
   * @param angle
   *          the angle to travel at
   */
  public LaserShot(Entity other, float angle) {
    super(other);
    // setImage(Resources.getImage("lasershot"));
    // image.setRotation(-angle);
    setRadius(LASER_SHOT_RADIUS);

    this.angle = angle;

    this.spriteSheet = Resources.getSprite("lasershot");
    this.animation = new Animation(this.spriteSheet, ANIMATION_FRAME_TIME);
    animation.setPingPong(true);
  }

  @Override
  public void render(GameContainer gc, Graphics g) {
    super.render(gc, g);

    if (angle > 0 && angle < 90) {
      animation.getCurrentFrame().setRotation(angle + 90);
    } else if (angle > 90 && angle < 180) {
      animation.getCurrentFrame().setRotation(angle - 90);
    } else if (angle > 180 && angle < 270) {
      animation.getCurrentFrame().setRotation(angle + 90);
    } else if (angle > 270) {
      animation.getCurrentFrame().setRotation(angle - 90);
    } else if (angle == 90 || angle == 270) {
      animation.getCurrentFrame().setRotation(angle);
    } else if (angle == 180) {
      animation.getCurrentFrame().setRotation(0);
    } else {
      animation.getCurrentFrame().setRotation(angle + 180);
    }

    animation.getCurrentFrame().draw(this.getX(), this.getY() - LASER_OFFSET,
        ANIMATION_SIZE, ANIMATION_SIZE);

    animation.draw(-10000, -10000);
  }

  @Override
  public void init(Entity other) {
  }

  @Override
  public void update(GameContainer gc, int delta) {
    animation.update(delta);

  }

  @Override
  public void die() {

  }

}
