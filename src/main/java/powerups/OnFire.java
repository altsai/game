package powerups;

import edu.brown.cs.altsai.game.Resources;
import entities.Zombie;
import game_objects.Powerup;

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

  private final int FIRE_RADIUS = 25;

  /**
   * Reference to the list of Zombies in the game.
   */
  private ConcurrentHashMap<String, Zombie> zombies;

  /**
   * Map of Zombies to the time they were lit on fire.
   */
  private ConcurrentHashMap<String, Long> onFireTimes;

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
    onFireTimes = new ConcurrentHashMap<>();
  }

  @Override
  public void update(GameContainer gc, int delta) {
    // call super.update() to check expiration time
    super.update(gc, delta);

    if (this.isUsed) {
      for (String zid : zombies.keySet()) {
        Zombie z = zombies.get(zid);

        // if has been on fire for two seconds
        if ((onFireTimes.get(zid) != null)
            && ((System.currentTimeMillis() - onFireTimes.get(zid)) >= INDIV_FIRE)) {
          zombies.remove(zid);
          onFireTimes.remove(zid);
          affectedPlayer.incrementScore();
          continue;
        }

        // if collides with the player
        if (z.isCollision(affectedPlayer) && (onFireTimes.get(zid) == null)) {
          z.setImage(Resources.getImage("firezombie"));
          onFireTimes.put(zid, System.currentTimeMillis());
        }

        // if on fire and collides with another zombie
        if (onFireTimes.get(zid) != null) {
          for (String ozid : zombies.keySet()) {
            Zombie other = zombies.get(ozid);
            if ((!ozid.equals(zid)) && (z.distTo(other) <= FIRE_RADIUS)) {
              other.setImage(Resources.getImage("firezombie"));
              onFireTimes.put(ozid, System.currentTimeMillis());
            }
          }
        }
      }
    }

    // check if powerup should be deactivated
    deactivate();
  }

  @Override
  public void activate() {
    super.activate();
    affectedPlayer.setImmune();
  }

  @Override
  public void deactivate() {
    if (this.isUsed
        && System.currentTimeMillis() - this.activationStartTime >= FIRE_TIME) {
      affectedPlayer.revert();

      // kill all lit Zombies who have not been removed yet
      for (String zid : onFireTimes.keySet()) {
        zombies.remove(zid);
        affectedPlayer.incrementScore();
      }

      // kill the powerup
      kill();
    }
  }

}
