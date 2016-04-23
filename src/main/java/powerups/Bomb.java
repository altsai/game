package powerups;

import edu.brown.cs.altsai.game.Resources;
import entities.Entity;
import entities.Player;
import entities.Zombie;
import game_objects.Powerup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;

/**
 * Bomb Powerup that destroys any Zombies within a certain radius. In two-player
 * mode, it can be used to break out of jail or reduce the other player's speed.
 *
 * @author Alison
 *
 */
public class Bomb extends Powerup {

  /**
   * The explosion radius in pixels.
   */
  private static final int EXPLOSION_RADIUS = 200;

  /**
   * The width of the animation.
   */
  public static final int ANIMATION_WIDTH = EXPLOSION_RADIUS * 4;

  /**
   * The height of the animation.
   */
  public static final int ANIMATION_HEIGHT = (int) (ANIMATION_WIDTH / 1.0315);

  /**
   * The animation frame time.
   */
  public static final int ANIMATION_FRAME_TIME = 50;

  /**
   * Reference to the list of Zombies in the game.
   */
  private ConcurrentHashMap<String, Zombie> zombies;

  /**
   * Reference to the list of players in the game.
   */
  private List<Player> players;

  /**
   * SpriteSheet for the Bomb.
   */
  private SpriteSheet spriteSheet;

  /**
   * Animation for the Bomb.
   */
  private Animation animation;

  /**
   * x-coordinate of the explosion point.
   */
  private float explosionX;

  /**
   * y-coordinate of the explosion point.
   */
  private float explosionY;

  /**
   * Constructor for the Bomb.
   *
   * @param p
   *          the list of Powerups in the game
   * @param z
   *          the list of Zombies in the game
   */
  public Bomb(ConcurrentHashMap<String, Powerup> p,
      ConcurrentHashMap<String, Zombie> z) {
    // call the superconstructor to start timing
    super(p);

    // load bomb image and animation
    this.image = Resources.getImage("bomb");
    this.spriteSheet = Resources.getSprite("bomb_explosion");
    this.animation = new Animation(this.spriteSheet, 500);
    this.animation.setLooping(false);
    this.zombies = z;

    players = new ArrayList<>();
  }

  /**
   * Constructor for the Bomb.
   *
   * @param p
   *          the list of Powerups in the game
   * @param z
   *          the list of Zombies in the game
   * @param pl
   *          the list of Players in the game
   */
  public Bomb(ConcurrentHashMap<String, Powerup> p,
      ConcurrentHashMap<String, Zombie> z, List<Player> pl) {
    // call the superconstructor to start timing
    super(p);

    // load bomb image and animation
    this.image = Resources.getImage("bomb");
    this.spriteSheet = Resources.getSprite("bomb_explosion");
    this.animation = new Animation(this.spriteSheet, ANIMATION_FRAME_TIME);
    this.animation.setLooping(false);
    this.zombies = z;

    this.players = pl;
  }

  @Override
  public void render(GameContainer gc, Graphics g) {
    super.render(gc, g);
    if (this.isUsed) {
      // trigger animation
      this.animation.draw(this.explosionX - (ANIMATION_WIDTH / 2),
          this.explosionY - (ANIMATION_HEIGHT / 2), ANIMATION_WIDTH,
          ANIMATION_HEIGHT);
    }

  }

  @Override
  public void update(GameContainer gc, int delta) {
    super.update(gc, delta);

    this.animation.update(delta);

    deactivate();
  }

  @Override
  public void activate() {
    super.activate();

    this.explosionX = this.affectedPlayer.getX();
    this.explosionY = this.affectedPlayer.getY();

    // check to see which Zombies are within the radius
    for (String zid : zombies.keySet()) {
      if (withinRadius(zombies.get(zid))) {
        zombies.remove(zid);
        affectedPlayer.incrementScore();
      }
    }

    // check if any other players have been hit for multiplayer
    for (Player p : players) {
      if (p != affectedPlayer) {
        // if the other player isn't invincible and is in blast radius
        if (!p.isInvincible() && withinRadius(p)) {
          // other player's speed is -10%
          p.setSpeed(p.getSpeed() * 0.9);
        }
      }
    }

    // always check for deactivation of speed decrease on each update
    deactivate();
  }

  /**
   * Method to check if an entity is within the explosion radius.
   *
   * Checks Euclidean distance between the Powerup and the entity. Then checks
   * if that distance is less than the explosionRadius.
   *
   * @param e
   *          Entity, entity that is possibly affected by the explosion.
   * @return True if entity within explosion radius.
   */
  private boolean withinRadius(Entity e) {
    return affectedPlayer.distTo(e) <= EXPLOSION_RADIUS;
  }

  @Override
  public void deactivate() {

    // reset all other player's speeds back after 5 seconds
    if (this.isUsed
        && (System.currentTimeMillis() - this.activationStartTime > 5000)) {
      for (Player p : this.players) {
        p.setSpeed(p.getSpeed() / 0.9);
      }
      kill();
    }
  }

}
