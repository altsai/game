package powerups;

import java.util.List;
import java.util.Map;

import org.newdawn.slick.GameContainer;

import com.google.common.collect.Lists;

import edu.brown.cs.altsai.game.Window;
import entities.Zombie;
import game_objects.Powerup;

public class LaserBeam extends Powerup {

  /**
   * Reference to the list of Zombies in the game.
   */
  private Map<String, Zombie> zombies;

  private float direction;

  public LaserBeam(Map<String, Powerup> p,
      Map<String, Zombie> z) {
    super(p);
    zombies = z;
    this.powerupIndex = Powerup.LASER;
  }

  @Override
  public void update(GameContainer gc, int delta) {
    // call super.update() to check expiration time
    super.update(gc, delta);

    if (this.isUsed) {
      // TODO advance along line of attack
      float x = this.getX();
      float y = this.getY();

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
    this.image.setRotation(direction);


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

}
