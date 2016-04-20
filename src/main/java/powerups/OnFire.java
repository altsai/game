package powerups;

import edu.brown.cs.altsai.game.Resources;
import entities.Zombie;
import game_objects.Powerup;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.newdawn.slick.GameContainer;

/**
 * OnFire Powerup that gives the player a temporary capability of lighting
 * Zombies on fire, which can then in turn light others on fire.
 *
 * @author Alison
 *
 */
public class OnFire extends Powerup {

  /**
   * The lifetime of the Powerup.
   */
  private final int FIRE_TIME = 8000;

  /**
   * The lifetime of individual fires.
   */
  private final int INDIV_FIRE = 2000;

  /**
   * Reference to the list of Zombies in the game.
   */
  private ConcurrentHashMap<String, Zombie> zombies;

  /**
   * Map of Zombies to the time they were lit on fire.
   */
  private Map<Zombie, Long> onFireTimes;

  /**
   * Constructor for OnFire.
   *
   * @param p
   *          the list of Powerups
   * @param z
   *          the list of Zombies
   */
  public OnFire(ConcurrentHashMap<String, Powerup> p,
      ConcurrentHashMap<String, Zombie> z) {
    super(p);
    // TODO set animation
    zombies = z;
    image = Resources.getImage("fire");
  }

  @Override
  public void update(GameContainer gc, int delta) {
    // call super.update() to check expiration time
    super.update(gc, delta);

    if (this.isUsed) {
      for (String zid : zombies.keySet()) {
        Zombie z = zombies.get(zid);

        if (z == null) {
          continue;
        }

        // if has been on fire for two seconds
        if (z.isOnFire()
            && ((System.currentTimeMillis() - onFireTimes.get(z)) >= INDIV_FIRE)) {
          zombies.remove(zid);
          onFireTimes.remove(z);
          affectedPlayer.incrementScore();
          continue;
        }

        // if collides with the player
        if (z.isCollision(affectedPlayer) && !z.isOnFire()) {
          // TODO replace zombie image
          z.setState(true);
          onFireTimes.put(z, System.currentTimeMillis());
        }

        // if on fire and collides with another zombie
        if (z.isOnFire()) {
          for (String ozid : zombies.keySet()) {
            Zombie other = zombies.get(ozid);
            if ((other != z) && z.isCollision(other)) {
              // TODO replace zombie image
              other.setState(true);
              onFireTimes.put(other, System.currentTimeMillis());
            }
          }
        }
      }
    }

    // check if black hole should be deactivated
    deactivate();
  }

  @Override
  public void activate() {
    super.activate();
    onFireTimes = new HashMap<>();

    // TODO reset player's image
  }

  @Override
  public void deactivate() {
    if (this.isUsed
        && System.currentTimeMillis() - this.activationStartTime >= FIRE_TIME) {
      // TODO reset player's image

      // kill all lit Zombies who have not been removed yet
      for (Zombie z : onFireTimes.keySet()) {
        zombies.remove(z);
        affectedPlayer.incrementScore();
      }

      // kill the powerup
      kill();
    }
  }

}
