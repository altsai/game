package powerups;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;

import edu.brown.cs.altsai.game.Resources;
import entities.Entity;

public class LaserShot extends Entity {

  private SpriteSheet spriteSheet;
  private Animation animation;

  private static final int ANIMATION_FRAME_TIME = 50;

  private float angle;

  public LaserShot(Entity other, float angle) {
    super(other);
    // setImage(Resources.getImage("lasershot"));
    // image.setRotation(-angle);
    setRadius(75);

    this.angle = angle;

    this.spriteSheet = Resources.getSprite("laser");
    this.animation = new Animation(this.spriteSheet, ANIMATION_FRAME_TIME);
    animation.setPingPong(true);
  }

  @Override
  public void render(GameContainer gc, Graphics g) {
    super.render(gc, g);

    animation.getCurrentFrame().setRotation(-angle);
    animation.getCurrentFrame().draw(this.getX(), this.getY(), 75, 75);
    animation.draw(-10000, -10000);
  }

  @Override
  public void init(Entity other) {
    // TODO Auto-generated method stub

  }

  @Override
  public void update(GameContainer gc, int delta) {
    // TODO Auto-generated method stub
    animation.update(delta);

  }

  @Override
  public void die() {
    // TODO Auto-generated method stub

  }

}
