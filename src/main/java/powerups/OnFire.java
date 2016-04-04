package powerups;

import entities.Entity;
import entities.Zombie;
import game_objects.Powerup;

import java.util.List;
import java.util.Map;

public class OnFire extends Powerup {

  private final int FIRE_TIME = 8000;
  private final int INDIV_FIRE = 2000;
  private List<Zombie> zombies;
  private Map<Entity, Integer> onFireTimes;

  /*
   * The player will not be damaged, but will kill any zombies that he runs into
   * by lighting them on fire. The on-fire zombie will stay lit for 2 seconds in
   * which time any zombie that bumps into a certain radius of the lit zombie
   * will also be lit on fire. This can cause a chain reaction that may go on
   * forever as zombies spawn, so all fires must die down after a strict limit
   * of 8 seconds and no more zombies may catch on fire even if they bump into a
   * lit zombie. All zombies killed by fire, directly or indirectly by the
   * player will be added to the score count
   */

  public OnFire(List<Powerup> p, List<Zombie> z) {
    super(p);
    // TODO Auto-generated constructor stub
    zombies = z;
  }

  @Override
  public void deactivate() {
    // TODO Auto-generated method stub

  }

}
