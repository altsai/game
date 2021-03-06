package powerups;

import edu.brown.cs.altsai.game.Resources;
import entities.Player;
import entities.Zombie;
import game_objects.Powerup;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

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
   * Map of zombie formations in the game.
   */
  private Map<String, List<String>> zombieFormations;

  /**
   * Players in the game.
   */
  private Map<String, Player> players;

  /**
   * Reference to the game.
   */
  private GamePlayState game;

  /**
   * How long the black hole operates for.
   */
  private final static long EFFECT_DURATION = 3000;

  /**
   * Large version of image.
   */
  private Image imageLarge;

  /**
   * Constructor for the BlackHole.
   *
   * @param p
   *          the list of Powerups in the game
   * @param z
   *          the list of Zombies in the game
   * @param gps
   *          the GamePlayState
   * @param zf
   *          the zombie formations in the game
   * @param pl
   *          the players in the game
   */
  public BlackHole(Map<String, Powerup> p, Map<String, Zombie> z,
      GamePlayState gps, Map<String, List<String>> zf, Map<String, Player> pl) {
    super(p);
    zombies = z;
    game = gps;
    image = Resources.getImage("blackhole");
    imageLarge = Resources.getImage("blackholeLarge");
    this.powerupIndex = Powerup.BLACK_HOLE;
    zombieFormations = zf;
    players = pl;
  }

  @Override
  public void render(GameContainer gc, Graphics g) {
    super.render(gc, g);

    if (this.isUsed) {

      // trigger animation
      imageLarge.draw(this.x - 110, this.y - 110, 250, 250);
    }

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
          affectedPlayer
              .setBlackholeKills(affectedPlayer.getBlackholeKills() + 1);
        }
        z.update(gc, delta);
      }

      // rotate image
      imageLarge.rotate(-(.05f * delta));
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

    // turn off spawning of new Zombies
    this.game.setSpawnOn(false);

    for (String s : zombieFormations.keySet()) {
      if (zombies.get(s) != null) {
        replaceZombie(zombies.get(s));
      }

      for (int i = 0; i < zombieFormations.get(s).size(); i++) {
        if (zombies.get(zombieFormations.get(s).get(i)) != null) {
          replaceZombie(zombies.get(zombieFormations.get(s).get(i)));
        }
      }

      zombieFormations.remove(s);
    }

    // set target of all Zombies to the BlackHole
    for (Zombie z : this.zombies.values()) {
      z.setTarget(this);
    }

    return new LinkedList<>();
  }

  @Override
  public void deactivate() {
    // the effects only last for 3 seconds now
    if (this.isUsed
        && (System.currentTimeMillis() - this.activationStartTime > EFFECT_DURATION)) {
      for (Zombie z : this.zombies.values()) {

        // this check is necessary in case the use uses two blackholes in a row
        // we want the zombies to follow the blackhole that hasn't expired
        // instead of the player
        if (z.getTarget() == this) {
          z.setTarget(this.affectedPlayer);
        }
      }

      if (!game.isTimeStopped()) {
        this.game.setSpawnOn(true);
      }

      kill();
    }
  }

  /**
   * Replaces a zombie in a formation with a normal one.
   *
   * @param z
   *          the zombie to replace
   */
  private void replaceZombie(Zombie z) {
    Random random = new Random();
    Player target = this.players.get(String.valueOf(random.nextInt(this.players
        .size())));

    Zombie zomb = new Zombie(target, players);

    if (game.isTimeStopped()) {
      zomb.setSpeed(0);
    } else {
      zomb.setSpeed(.9);
    }

    if (game.inOnFire(z.getID())) {
      zomb.setState(true);
      zomb.setZombID(z.getID());
    }

    zomb.setX(z.getX());
    zomb.setY(z.getY());
    zombies.put(zomb.getID(), zomb);

    if (!game.inOnFire(z.getID())) {
      zombies.remove(z.getID());
    }
  }

}
