package powerups;

import org.newdawn.slick.GameContainer;

import edu.brown.cs.altsai.game.Resources;
import entities.Entity;

public class LaserShot extends Entity {

  public LaserShot(Entity other, float angle) {
    super(other);
    setImage(Resources.getImage("lasershot"));
    image.setRotation(-angle);
    setRadius(75);
  }

  @Override
  public void init(Entity other) {
    // TODO Auto-generated method stub

  }

  @Override
  public void update(GameContainer gc, int delta) {
    // TODO Auto-generated method stub

  }

  @Override
  public void die() {
    // TODO Auto-generated method stub

  }

}
