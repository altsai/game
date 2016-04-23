package powerups;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.GameContainer;

import entities.Zombie;
import game_objects.Powerup;

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
  private Map<String, Zombie> zombies;

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
  public OnFire(Map<String, Powerup> p, Map<String, Zombie> z) {
    super(p);
    // TODO set image
    zombies = z;
    this.powerupIndex = Powerup.ON_FIRE;
  }

  @Override
  public void update(GameContainer gc, int delta) {
    // call super.update() to check expiration time
    super.update(gc, delta);

    for (String key : this.zombies.keySet()) {
      // if has been on fire for two seconds
      Zombie z = this.zombies.get(key);
      if (z.isOnFire()
          && ((System.currentTimeMillis() - onFireTimes.get(z)) >= INDIV_FIRE)) {
        zombies.remove(key);
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
        for (String otherKey : zombies.keySet()) {
          Zombie other = this.zombies.get(otherKey);
          if ((otherKey != key) && z.isCollision(other)) {
            // TODO replace zombie image
            other.setState(true);
            onFireTimes.put(other, System.currentTimeMillis());
          }
        }
      }
    }

    // check if black hole should be deactivated
    deactivate();
  }

  @Override
  public List<String> activate() {
    this.isUsed = true;
    this.activationStartTime = System.currentTimeMillis();

    // clear the player's powerup storage after using the powerup
    this.affectedPlayer.clearPowerupStorage();

    onFireTimes = new HashMap<>();

    // TODO reset player's image

    return new LinkedList<>();
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
