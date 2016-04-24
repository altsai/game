package powerups;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.newdawn.slick.GameContainer;

import edu.brown.cs.altsai.game.Resources;
import entities.Player;
import entities.Zombie;
import game_objects.Powerup;
import states.GamePlayState;

/**
 * Black Hole Powerup that sucks all Zombies currently on the board to its
 * location and then kills them.
 *
 * @author Alison
 *
 */
public class BlackHole extends Powerup {

  /**
   * Reference to the list of zombies in the game.
   */
  private Map<String, Zombie> zombies;

  /**
   * Reference to the list of players in the game.
   */
  private Map<String, Player> players;


  /**
   * Reference to the game.
   */
  private GamePlayState game;

  private final static long EFFECT_DURATION = 3000;

  /**
   * Constructor for the BlackHole.
   *
   * @param p
   *          the list of Powerups in the game
   * @param z
   *          the list of Zombies in the game
   * @param gps
   *          the GamePlayState
   */
  public BlackHole(Map<String, Powerup> p, Map<String, Zombie> z, GamePlayState gps) {
    super(p);
    // TODO animation
    zombies = z;
    players = new ConcurrentHashMap<>();
    game = gps;
    image = Resources.getImage("blackhole");
    this.powerupIndex = Powerup.BLACK_HOLE;
  }

  /**
   * Constructor for the BlackHole.
   *
   * @param p
   *          the list of Powerups in the game
   * @param z
   *          the list of Zombies in the game
   * @param pl
   *          the list of players in the game
   * @param gps
   *          the GamePlayState
   */
  public BlackHole(Map<String, Powerup> p, Map<String, Zombie> z, Map<String, Player> pl,
      GamePlayState gps) {
    super(p);
    // TODO animation
    zombies = z;
    players = pl;
    game = gps;
    image = Resources.getImage("blackhole");
    this.powerupIndex = Powerup.BLACK_HOLE;
  }

  @Override
  public void update(GameContainer gc, int delta) {
    // call super.update() to check expiration time
    super.update(gc, delta);
    if (this.isUsed) {
      // check for player collision with every entity
      for (String zid : zombies.keySet()) {
        Zombie z = zombies.get(zid);
        if (z.isCollision(this)) {
          zombies.remove(zid);
          affectedPlayer.incrementScore();
        }
        z.update(gc, delta);
      }
    }

    // check if BlackHole should be deactivated
    deactivate();
  }

  @Override
  public List<String> activate() {
    this.isUsed = true;
    this.activationStartTime = System.currentTimeMillis();

    // clear the player's powerup storage after using the powerup
    this.affectedPlayer.clearPowerupStorage();

    // reset location to player location
    this.setX(affectedPlayer.getX());
    this.setY(affectedPlayer.getY());

    // TODO: reset image/animation

    // turn off spawning of new Zombies
    this.game.setSpawnOn(false);

    // set target of all Zombies to the BlackHole
    for (Zombie z : this.zombies.values()) {
      z.setTarget(this);
    }

    return new LinkedList<>();
  }

  @Override
  public void deactivate() {
//    if (this.isUsed && zombies.size() == 0) {
//      this.game.setSpawnOn(true);
//
//      // kill the Powerup
//      kill();
//    }

    // the effects only last for 3 seconds now
    if (this.isUsed && (System.currentTimeMillis() - this.activationStartTime > EFFECT_DURATION)) {
      for (Zombie z : this.zombies.values()) {
        if (z.getTarget() == this) {
          z.setTarget(this.affectedPlayer);
        }
      }
      this.game.setSpawnOn(true);

      kill();
    }
  }

}
