package powerups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.GameContainer;

import edu.brown.cs.altsai.game.Resources;
import entities.Player;
import entities.Zombie;
import game_objects.Powerup;
import states.GamePlayState;

/**
 * TimeStop Powerup freezes the game for three seconds, also halting new Powerup
 * and Zombie generation.
 *
 * @author Alison
 *
 */
public class TimeStop extends Powerup {

  /**
   * TimeStop's lifetime in milliseconds.
   */
  private final int FREEZE_TIME = 3000;

  /**
   * Reference to the list of Zombies in the game.
   */
  private Map<String, Zombie> zombies;

  /**
   * Reference to the list of Players in the game.
   */
  private List<Player> players;

  /**
   * Maps frozen Zombies to their pre-frozen speeds.
   */
  private Map<Zombie, Double> zombieSpeeds;

  /**
   * The GamePlayState.
   */
  private GamePlayState game;

  /**
   * Constructor for TimeStop.
   *
   * @param p
   *          the list of Powerups in the game
   * @param z
   *          the list of Zombies in the game
   * @param gps
   *          the GamePlayState
   */
  public TimeStop(Map<String, Powerup> p, Map<String, Zombie> z, GamePlayState gps) {
    // call the superconstructor to start timing
    super(p);

    // load bomb image and animation
    this.image = Resources.getImage("timestop");
    this.zombies = z;
    this.game = gps;

    this.players = new ArrayList<>();
  }

  /**
   * Constructor for TimeStop.
   *
   * @param p
   *          the list of Powerups in the game
   * @param z
   *          the list of Zombies in the game
   * @param pl
   *          the list of Players in the game
   * @param gps
   *          the GamePlayState
   */
  public TimeStop(Map<String, Powerup> p, Map<String, Zombie> z, List<Player> pl,
      GamePlayState gps) {
    // call the superconstructor to start timing
    super(p);

    // load bomb image and animation
    this.image = Resources.getImage("timestop");
    this.zombies = z;
    this.game = gps;

    this.players = pl;
  }

  @Override
  public void update(GameContainer gc, int delta) {
    // call super.update() to check expiration time
    super.update(gc, delta);

    // check if TimeStop should be deactivated
    deactivate();
  }

  @Override
  public void activate() {
    super.activate();

    zombieSpeeds = new HashMap<>();

    // prevent spawning of new zombies
    this.game.setSpawnOn(false);

    for (String zid : zombies.keySet()) {
      Zombie z = zombies.get(zid);
      if (z.getSpeed() != 0) {
        zombieSpeeds.put(z, z.getSpeed());
        z.setSpeed(0);
      }
    }

    for (Player p : players) {
      if (affectedPlayer != p) {
        p.setCanMove(false);
      }
    }
  }

  @Override
  public void deactivate() {

    if (this.isUsed
        && System.currentTimeMillis() - this.activationStartTime >= FREEZE_TIME) {

      // tell the game to start spawning again
      this.game.setSpawnOn(true);

      // reset Zombie speeds
      for (String zid : zombies.keySet()) {
        Zombie z = zombies.get(zid);
        if (zombieSpeeds.get(z) != null) {
          z.setSpeed(zombieSpeeds.get(z));
        }
      }

      for (Player p : players) {
        if (affectedPlayer != p) {
          p.setCanMove(true);
        }
      }

      // kill the Powerup
      kill();
    }
  }
}
