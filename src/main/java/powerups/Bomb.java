package powerups;

import edu.brown.cs.altsai.game.Resources;
import entities.Entity;
import entities.Player;
import entities.Zombie;
import game_objects.Powerup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;

public class Bomb extends Powerup {

  private static final int EXPLOSION_RADIUS = 200;

  public static final int ANIMATION_WIDTH = EXPLOSION_RADIUS * 4;
  public static final int ANIMATION_HEIGHT = (int) (ANIMATION_WIDTH / 1.0315);
  public static final int ANIMATION_FRAME_TIME = 50;

  private List<Zombie> zombies;
  private List<Player> players;

  private SpriteSheet spriteSheet;
  private Animation animation;
  private float explosionX;
  private float explosionY;

  // TODO: animation field

  public Bomb(List<Powerup> p, List<Zombie> z) {
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

  public Bomb(List<Powerup> p, List<Zombie> z, List<Player> pl) {
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

      this.animation.draw(this.explosionX - (ANIMATION_WIDTH / 2),
          this.explosionY - (ANIMATION_HEIGHT / 2), ANIMATION_WIDTH,
          ANIMATION_HEIGHT);
    }

  }

  @Override
  public void update(GameContainer gc, int delta) {
    super.update(gc, delta);

    this.animation.update(delta);

    // also call deactivate for the specific powerup. Deactivate checks if
    // the effects should wear off.
  }

  @Override
  public void activate() {
    super.activate();

    // TODO: if in jail

    this.explosionX = this.affectedPlayer.getX();
    this.explosionY = this.affectedPlayer.getY();

    Iterator<Zombie> iter = this.zombies.iterator();
    while (iter.hasNext()) {
      if (withinRadius(iter.next())) {
        iter.remove();
        this.affectedPlayer.incrementScore();
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
   * Checks euclidean distance between the powerup and the entity. Then checks
   * if that distance is less than the explosionRadius.
   *
   * @param e
   *          Entity, entity that is possibly affected by the explosion.
   * @return True if entity within explosion radius.
   */
  private boolean withinRadius(Entity e) {
    return this.distTo(e) <= EXPLOSION_RADIUS;
  }

  @Override
  public void deactivate() {

    // reset all other player's speeds back after 5 seconds
    if (System.currentTimeMillis() - this.activationStartTime > 5000) {
      for (Player p : this.players) {
        p.setSpeed(p.getSpeed() / 0.9);
      }
      kill();
    }
  }

}
