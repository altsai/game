package powerups;

import edu.brown.cs.altsai.game.Resources;
import entities.Player;
import entities.Zombie;
import game_objects.Powerup;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.GameContainer;

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
  private Map<String, Player> players;

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
  public TimeStop(Map<String, Powerup> p, Map<String, Zombie> z,
      GamePlayState gps) {
    // call the superconstructor to start timing
    super(p);

    // load bomb image and animation
    this.image = Resources.getImage("timestop");
    this.zombies = z;
    this.game = gps;
    this.powerupIndex = Powerup.TIMESTOP;

    this.players = new HashMap<>();
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
  public TimeStop(Map<String, Powerup> p, Map<String, Zombie> z,
      Map<String, Player> pl, GamePlayState gps) {
    // call the superconstructor to start timing
    super(p);

    // load bomb image and animation
    this.image = Resources.getImage("timestop");
    this.zombies = z;
    this.game = gps;
    this.powerupIndex = Powerup.TIMESTOP;

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
  public List<String> activate() {
    this.isUsed = true;
    this.activationStartTime = System.currentTimeMillis();

    game.setTimeStopped(true);

    // clear the player's powerup storage after using the powerup
    this.affectedPlayer.clearPowerupStorage();

    // prevent spawning of new zombies
    this.game.setSpawnOn(false);

    for (String key : this.zombies.keySet()) {
      zombies.get(key).setSpeed(0);
    }

    for (Player p : players.values()) {
      if (!affectedPlayer.getID().equals(p.getID())) {
        p.setSpeed(0);
      }
    }

    return new LinkedList<>();
  }

  @Override
  public void deactivate() {

    if (this.isUsed
        && System.currentTimeMillis() - this.activationStartTime >= FREEZE_TIME) {
      if (affectedPlayer.getLastTimeStop() <= this.activationStartTime) {
        // tell the game to start spawning again
        this.game.setSpawnOn(true);
        game.setTimeStopped(false);

        // reset Zombie speeds
        for (String zid : zombies.keySet()) {
          Zombie z = zombies.get(zid);
          z.setSpeed(z.getInitSpeed());
        }

        // reset player speeds
        for (Player p : players.values()) {
          if (!affectedPlayer.getID().equals(p.getID())) {
            p.setSpeed(Player.PLAYER_SPEED);
          }
        }
      }

      // kill the Powerup
      kill();
    }
  }
}
