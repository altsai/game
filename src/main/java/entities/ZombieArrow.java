package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Representation of a player-oriented arrow.
 *
 * @author Alison
 *
 */
public class ZombieArrow {
  /**
   * Constructor for a player-oriented arrow.
   *
   * @param target
   *          the target to follow
   * @param players
   *          the players in the game
   * @param speed
   *          the speed to move at
   * @param zombies
   *          the map of zombies in the game
   * @param zombieFormations
   *          the map of zombie formations in the game
   */
  public ZombieArrow(Entity target, Map<String, Player> players, double speed,
      Map<String, Zombie> zombies, Map<String, List<String>> zombieFormations) {
    ZombieFormationHead z = new ZombieFormationHead(target, players);
    z.setSpeed(speed);
    z.setInitSpeed(speed);
    zombies.put(z.getID(), z);
    ZombieFormationBody b1 = new ZombieFormationBody(z, players, 1, 135.0);
    zombies.put(b1.getID(), b1);
    ZombieFormationBody b2 = new ZombieFormationBody(z, players, 2, 135.0);
    zombies.put(b2.getID(), b2);
    ZombieFormationBody b3 = new ZombieFormationBody(z, players, 1, 225.0);
    zombies.put(b3.getID(), b3);
    ZombieFormationBody b4 = new ZombieFormationBody(z, players, 2, 225.0);
    zombies.put(b4.getID(), b4);

    // add ZombieFormationBody zombies to the leader list
    List<String> zfbIds = new ArrayList<>();
    zfbIds.add(b1.getID());
    zfbIds.add(b2.getID());
    zfbIds.add(b3.getID());
    zfbIds.add(b4.getID());
    zombieFormations.put(z.getID(), zfbIds);
  }
}
