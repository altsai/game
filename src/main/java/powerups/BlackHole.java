package powerups;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.GameContainer;

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
  private List<Player> players;

  /**
   * Reference to the game.
   */
  private GamePlayState game;

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
    // TODO set image and animation
    zombies = z;
    players = new ArrayList<>();
    players.add(affectedPlayer);
    game = gps;
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
  public BlackHole(Map<String, Powerup> p, Map<String, Zombie> z, List<Player> pl,
      GamePlayState gps) {
    super(p);
    // TODO set image and animation
    zombies = z;
    players = pl;
    game = gps;
    this.powerupIndex = Powerup.BLACK_HOLE;
  }

  @Override
  public void update(GameContainer gc, int delta) {
    // call super.update() to check expiration time
    super.update(gc, delta);

    // check for player collision with every entity
    for (String key : this.zombies.keySet()) {
      if (this.zombies.get(key).isCollision(this)) {
        zombies.remove(key);
        affectedPlayer.incrementScore();
      }
      this.zombies.get(key).update(gc, delta);
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
    for (String key : zombies.keySet()) {
      zombies.get(key).setTarget(this);
    }

    return new LinkedList<>();
  }

  @Override
  public void deactivate() {
    if (this.isUsed && zombies.size() == 0) {
      this.game.setSpawnOn(true);

      // kill the Powerup
      kill();
    }
  }

}
