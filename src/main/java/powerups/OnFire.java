package powerups;

import edu.brown.cs.altsai.game.Resources;
import entities.Zombie;
import game_objects.Powerup;

import java.util.LinkedList;
import java.util.List;
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

  private final int FIRE_RADIUS = 35;
  private final int FIRE_RADIUS_PLAYER = 45;

  /**
   * Reference to the list of Zombies in the game.
   */
  private Map<String, Zombie> zombies;

  /**
   * Map of Zombies to the time they were lit on fire.
   */
  private Map<String, Long> onFireTimes;

  /**
   * Constructor for OnFire.
   *
   * @param p
   *          the list of Powerups
   * @param z
   *          the list of Zombies
   */
  public OnFire(Map<String, Powerup> p, Map<String, Zombie> z) {
    super(p);
    // TODO set animation
    zombies = z;
    image = Resources.getImage("fire");
    onFireTimes = new ConcurrentHashMap<>();
    this.powerupIndex = Powerup.ON_FIRE;
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
        if (z.distTo(affectedPlayer) <= FIRE_RADIUS_PLAYER && (onFireTimes.get(zid) == null)) {
          z.setState(true);
          onFireTimes.put(zid, System.currentTimeMillis());
        }

        // if on fire and collides with another zombie
        if (onFireTimes.get(zid) != null) {
          for (String ozid : zombies.keySet()) {
            Zombie other = zombies.get(ozid);
            if ((!ozid.equals(zid)) && (z.distTo(other) <= FIRE_RADIUS)) {
              other.setState(true);
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
  public List<String> activate() {
    this.isUsed = true;
    this.activationStartTime = System.currentTimeMillis();

    // clear the player's powerup storage after using the powerup
    this.affectedPlayer.clearPowerupStorage();
    affectedPlayer.setImmune();

    onFireTimes = new ConcurrentHashMap<>();

    // TODO reset player's image

    return new LinkedList<>();
  }

  @Override
  public void deactivate() {
    if (this.isUsed
        && System.currentTimeMillis() - this.activationStartTime >= FIRE_TIME) {
      if (affectedPlayer.getLastFire() == this.activationStartTime) {
        affectedPlayer.revert();
      }

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
