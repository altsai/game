package powerups;

import entities.Zombie;
import game_objects.Powerup;

import java.util.concurrent.ConcurrentHashMap;

import org.newdawn.slick.GameContainer;

public class LaserBeam extends Powerup {

  /**
   * Reference to the list of Zombies in the game.
   */
  private ConcurrentHashMap<String, Zombie> zombies;

  private float direction;

  public LaserBeam(ConcurrentHashMap<String, Powerup> p,
      ConcurrentHashMap<String, Zombie> z) {
    super(p);
    zombies = z;
  }

  @Override
  public void update(GameContainer gc, int delta) {
    // call super.update() to check expiration time
    super.update(gc, delta);

    if (this.isUsed) {
      // TODO advance along line of attack
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
  public void activate() {
    super.activate();
    direction = affectedPlayer.getLastDir();
    // TODO set image at rotated angle
  }

  @Override
  public void deactivate() {
    if (this.isUsed) { // TODO reached boundary
      // kill the powerup
      kill();
    }
  }

}
