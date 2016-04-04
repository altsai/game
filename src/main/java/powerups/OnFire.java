package powerups;

import entities.Zombie;
import game_objects.Powerup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.GameContainer;

public class OnFire extends Powerup {

  private final int FIRE_TIME = 8000;
  private final int INDIV_FIRE = 2000;
  private List<Zombie> zombies;
  private Map<Zombie, Long> onFireTimes;

  public OnFire(List<Powerup> p, List<Zombie> z) {
    super(p);
    // TODO set image
    zombies = z;
  }

  @Override
  public void update(GameContainer gc, int delta) {
    // call super.update() to check expiration time
    super.update(gc, delta);

    for (Zombie z : zombies) {
      // if has been on fire for two seconds
      if (z.isOnFire()
          && ((System.currentTimeMillis() - onFireTimes.get(z)) >= INDIV_FIRE)) {
        zombies.remove(z);
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
        for (Zombie other : zombies) {
          if ((other != z) && z.isCollision(other)) {
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

      for (Zombie z : onFireTimes.keySet()) {
        zombies.remove(z);
        affectedPlayer.incrementScore();
      }

      // kill the powerup
      kill();
    }
  }

}
