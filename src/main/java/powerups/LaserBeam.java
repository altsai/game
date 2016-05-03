package powerups;

import java.util.List;
import java.util.Map;

import org.newdawn.slick.GameContainer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.brown.cs.altsai.game.Resources;
import edu.brown.cs.altsai.game.Window;
import entities.Entity;
import entities.Player;
import entities.Zombie;
import game_objects.Powerup;
import server.GameServer;

public class LaserBeam extends Powerup {

  /**
   * Reference to the map of Zombies in the game.
   */
  private Map<String, Zombie> zombies;

  /**
   * Reference to Map of players in game.
   */
  private Map<String, Player> players;

  private float direction;
  private static final float LASER_SPEED = 6.0f;

  private LaserShot laser;

  private GameServer server;

  private boolean lifeLost = false;

  /**
   * Single player game constructor for laser.
   *
   * @param p
   *          Map of powerups
   * @param z
   *          Map of zombies
   */
  public LaserBeam(Map<String, Powerup> p, Map<String, Zombie> z) {
    super(p);
    this.image = Resources.getImage("laserbeam");

    zombies = z;
    this.powerupIndex = Powerup.LASER;
    this.players = Maps.newHashMap();
  }

  /**
   * Dummy constructor that only takes in powerups.
   *
   * This constructor is used by the client in the networked game because it
   * serves as the "image" of the laser beam. No processing or zombie killing is
   * done client-side.
   *
   * @param p
   *          Map of Powerups
   */
  public LaserBeam(Map<String, Powerup> p) {
    super(p);
    this.image = Resources.getImage("laserbeam");
    this.zombies = Maps.newHashMap();
    this.powerups = p;
    this.players = Maps.newHashMap();
    this.powerupIndex = Powerup.LASER;
  }

  /**
   * Two player constructor for laser (not networked).
   *
   * @param p
   *          Map of powerups
   * @param z
   *          Map of zombies
   * @param player
   *          Map of players
   */
  public LaserBeam(Map<String, Powerup> p, Map<String, Zombie> z,
      Map<String, Player> player) {
    super(p);
    this.image = Resources.getImage("laserbeam");
    zombies = z;
    this.powerupIndex = Powerup.LASER;
    players = player;
  }

  /**
   * Two Player constructor for laser (networked);
   *
   * @param p
   *          Map of powerups
   * @param z
   *          Map of zombies
   * @param player
   *          Map of players
   * @param server
   *          GameServer to send information to the client.
   */
  public LaserBeam(Map<String, Powerup> p, Map<String, Zombie> z,
      Map<String, Player> player, GameServer server) {
    super(p);
    this.image = Resources.getImage("laserbeam");
    zombies = z;
    this.powerupIndex = Powerup.LASER;
    players = player;
    this.server = server;
  }

  @Override
  public void update(GameContainer gc, int delta) {
    // call super.update() to check expiration time
    super.update(gc, delta);

    if (this.isUsed && laser != null) {

      // TODO advance along line of attack
      float x = laser.getX();
      float y = laser.getY();

      if ((direction > 0) && (direction < 180)) {
        // move up
        laser.setY(y - LASER_SPEED);
      } else if ((direction > 180) && (direction < 360)) {
        // move down
        laser.setY(y + LASER_SPEED);
      }

      if ((direction > 90) && (direction < 270)) {
        // move left
        laser.setX(x - LASER_SPEED);
      } else if ((direction > 270) && (direction != 0)) {
        // move right
        laser.setX(x + LASER_SPEED);
      } else if ((direction > 0) && (direction < 90)) {
        // move right
        laser.setX(x + LASER_SPEED);
      }

      if (direction == 0) {
        laser.setX(x + LASER_SPEED);
      } else if (direction == 90) {
        laser.setY(y - LASER_SPEED);
      } else if (direction == 180) {
        laser.setX(x - LASER_SPEED);
      } else if (direction == 270) {
        laser.setY(y + LASER_SPEED);
      }

      List<String> removedZombies = Lists.newLinkedList();

      for (String zid : zombies.keySet()) {
        Zombie z = zombies.get(zid);
        if (laser.isCollision(z)) {
          zombies.remove(zid);
          removedZombies.add(zid);

          affectedPlayer.incrementScore();
        }
      }

      // update client if this is networked version of laser
      if (server != null) {
        server.removeZombie(removedZombies);
      }

      for (Player p : players.values()) {
        if (p != affectedPlayer) {
          if (laser.isCollision(p) && !lifeLost) {
            p.loseLife();
            lifeLost = true;

            // update the client if this is networked version of laser
            if (server != null) {
              server.updatePlayer(p.getID(), true);
            }
          }
        }
      }
    }

    // check if powerup should be deactivated
    deactivate();
  }

  @Override
  public List<String> activate() {
    this.isUsed = true;
    this.activationStartTime = System.currentTimeMillis();

    // clear the player's powerup storage after using the powerup
    this.affectedPlayer.clearPowerupStorage();
    direction = affectedPlayer.getLastDir();
    // TODO reset radius to larger
    laser = new LaserShot(null, direction);
    laser.setX(affectedPlayer.getX() - laser.getRadius() / 2
        + affectedPlayer.getRadius() / 2);
    laser.setY(affectedPlayer.getY() - laser.getRadius() / 2
        + affectedPlayer.getRadius() / 2);

    // TODO return list of zombies hit by laser
    return Lists.newArrayList();
  }

  private boolean hasReachedBoundary() {
    boolean xbound = (this.x <= 0) || (this.x >= Window.width);
    boolean ybound = (this.y <= 0) || (this.y >= Window.height);
    return (xbound || ybound);
  }

  @Override
  public void deactivate() {
    if (this.isUsed && hasReachedBoundary()) { // TODO reached boundary
      // kill the powerup
      kill();
    }
  }

  @Override
  public List<Entity> getChildren() {
    if (laser != null) {
      return Lists.newArrayList(laser);
    } else {
      return Lists.newArrayList();
    }
  }

}
