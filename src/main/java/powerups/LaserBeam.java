package powerups;

import edu.brown.cs.altsai.game.Resources;
import edu.brown.cs.altsai.game.Window;
import entities.Entity;
import entities.Zombie;
import game_objects.Powerup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.GameContainer;

import com.google.common.collect.Lists;

public class LaserBeam extends Powerup {

  /**
   * Reference to the list of Zombies in the game.
   */
  private Map<String, Zombie> zombies;

  private float direction;

  private LaserShot laser;

  public LaserBeam(Map<String, Powerup> p, Map<String, Zombie> z) {
    super(p);
    this.image = Resources.getImage("laserbeam");
    zombies = z;
  }

  @Override
  public void update(GameContainer gc, int delta) {
    // call super.update() to check expiration time
    super.update(gc, delta);

    if (this.isUsed) {
      // TODO advance along line of attack
      float x = laser.getX();
      float y = laser.getY();

      if ((direction > 0) && (direction < 180)) {
        // move up
        laser.setY(y - 3);
      } else if ((direction > 180) && (direction < 360)) {
        // move down
        laser.setY(y + 3);
      }

      if ((direction > 90) && (direction < 270)) {
        // move left
        laser.setX(x - 3);
      } else if ((direction > 270) && (direction != 0)) {
        // move right
        laser.setX(x + 3);
      } else if ((direction > 0) && (direction < 90)) {
        // move right
        laser.setX(x + 3);
      }

      if (direction == 0) {
        laser.setX(x + 3);
      } else if (direction == 90) {
        laser.setY(y - 3);
      } else if (direction == 180) {
        laser.setX(x - 3);
      } else if (direction == 270) {
        laser.setY(y + 3);
      }

      for (String zid : zombies.keySet()) {
        Zombie z = zombies.get(zid);
        if (this.isCollision(z)) {
          zombies.remove(zid);
          affectedPlayer.incrementScore();
        }
      }
    }

    // check if powerup should be deactivated
    deactivate();
  }

  @Override
  public List<String> activate() {
    this.isUsed = true;
    this.activationStartTime = System.currentTimeMillis();

    // clear the player's powerup storage after using the powerup
    this.affectedPlayer.clearPowerupStorage();
    direction = affectedPlayer.getLastDir();
    // TODO reset radius to larger
    laser = new LaserShot(null);
    laser.setX(affectedPlayer.getX());
    laser.setY(affectedPlayer.getY());

    // TODO return list of zombies hit by laser
    return Lists.newArrayList();
  }

  private boolean hasReachedBoundary() {
    boolean xbound = (this.x <= 0) || (this.x >= Window.width);
    boolean ybound = (this.y <= 0) || (this.y >= Window.height);
    return (xbound || ybound);
  }

  @Override
  public void deactivate() {
    if (this.isUsed && hasReachedBoundary()) { // TODO reached boundary
      // kill the powerup
      kill();
    }
  }

  @Override
  public List<Entity> getChildren() {
    List<Entity> toReturn = new ArrayList<>();
    toReturn.add(laser);
    return toReturn;
  }

}
