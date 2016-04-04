package powerups;

import entities.Player;
import entities.Zombie;
import game_objects.Powerup;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;

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
  private List<Zombie> zombies;

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
  public BlackHole(List<Powerup> p, List<Zombie> z, GamePlayState gps) {
    super(p);
    // TODO set image and animation
    zombies = z;
    players = new ArrayList<>();
    players.add(affectedPlayer);
    game = gps;
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
  public BlackHole(List<Powerup> p, List<Zombie> z, List<Player> pl,
      GamePlayState gps) {
    super(p);
    // TODO set image and animation
    zombies = z;
    players = pl;
    game = gps;
  }

  @Override
  public void update(GameContainer gc, int delta) {
    // call super.update() to check expiration time
    super.update(gc, delta);

    // check for player collision with every entity
    for (Zombie z : this.zombies) {
      if (z.isCollision(this)) {
        zombies.remove(z);
        affectedPlayer.incrementScore();
      }
      z.update(gc, delta);
    }

    // check if BlackHole should be deactivated
    deactivate();
  }

  @Override
  public void activate() {
    super.activate();

    // reset location to player location
    this.setX(affectedPlayer.getX());
    this.setY(affectedPlayer.getY());

    // TODO: reset image/animation

    // turn off spawning of new Zombies
    this.game.setSpawnOn(false);

    // set target of all Zombies to the BlackHole
    for (Zombie z : zombies) {
      z.setTarget(this);
    }
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
