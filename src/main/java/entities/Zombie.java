package entities;

import edu.brown.cs.altsai.game.Resources;
import edu.brown.cs.altsai.game.Window;
import effects.FireEmitterCustom;
import game_objects.Circle;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.particles.ParticleSystem;

import com.google.common.collect.Maps;

/**
 * Defines the zombie object.
 *
 * @author bl48
 *
 */
public class Zombie extends Entity {

  /**
   * Constructor for a zombie object.
   *
   * @param other
   *          Entity, an entity to follow
   */
  public Zombie(Entity other, Map<String, Player> players) {
    super(other);
    this.allPlayers = players;
    // change spawn to protect player
    setSpawn();
  }

  /**
   * Whether the zombie is part of a wall.
   */
  protected boolean wallInPlace;

  /**
   * Initial speed of zombie.
   */
  protected double initial_speed;

  /**
   * The targeted player.
   */
  protected Circle player;

  /**
   * Particle system for fire effects.
   */
  protected ParticleSystem fireParticles;
  protected FireEmitterCustom emitter;

  /**
   * All players in the game.
   */
  protected Map<String, Player> allPlayers;

  /**
   * Method that checks if the zombie is within a radius of the player.
   *
   * Used to protect players from zombie spawns.
   *
   * @return boolean, true if the zombie is within radius, else false.
   */
  private boolean inPlayerRadius() {
    // check the player map for multiplayer
    for (Player p : this.allPlayers.values()) {
      if (this.distTo(p) < 200) {
        return true;
      }
    }

    return false;
  }

  /**
   * Changes the spawn location of the zombie to be far from player.
   */
  private void setSpawn() {
    Random r = new Random();
    while (inPlayerRadius()) {
      this.x = r.nextFloat() * (Window.width - 30 - (2 * 20)) + 15;
      this.y = r.nextFloat() * (Window.height - 60 - (2 * 20)) + 45;
    }
  }

  @Override
  public void init(Entity other) {

    // give an empty hashmap to begin with
    this.allPlayers = Maps.newHashMap();

    this.player = other;
    this.setRadius(20);

    Random r = new Random();

    this.x = r.nextFloat() * (Window.width - 30 - (2 * 20)) + 15;
    this.y = r.nextFloat() * (Window.height - 60 - (2 * 20)) + 45;

    this.radius = 20;
    this.image = Resources.getImage("zombie");
    this.speed = Player.PLAYER_SPEED * 3;

    this.state = false;
    this.id = UUID.randomUUID().toString();

    this.initial_speed = this.getSpeed();

    initFire();
  }

  /**
   * Initializes fire animation effects.
   */
  protected void initFire() {
    fireParticles = new ParticleSystem(Resources.getImage("particle"), 1500);
    try {
      emitter = new FireEmitterCustom((int) this.radius / 2,
          (int) this.radius / 2, 10);
      fireParticles.addEmitter(emitter);
      fireParticles.setBlendingMode(ParticleSystem.BLEND_ADDITIVE);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Sets zombie target.
   *
   * @param other
   *          the new target
   */
  public void setTarget(Circle other) {
    this.player = other;
  }

  /**
   * Returns the zombie target.
   *
   * @return the zombie target
   */
  public Circle getTarget() {
    return this.player;
  }

  @Override
  public void render(GameContainer gc, Graphics g) {
    super.render(gc, g);

    if (this.isOnFire()) {
      fireParticles.render(this.x, this.y);
    }
  }

  @Override
  public void update(GameContainer gc, int delta) {

    // TODO: If we want the zombie to follow the closest player
    // instead of randomly selecting one, change here.

    /*
     * Go through the player map and find the closest one, set target to be that
     * closest one
     */

    if (this.player instanceof Player) {
      double dist = Double.MAX_VALUE;
      Circle closest = this.player;
      for (Player p : this.allPlayers.values()) {
        if (this.distTo(p) < dist) {
          dist = this.distTo(p);
          closest = p;
        }
      }

      this.player = closest;
    }

    followPlayer(delta);

    if (this.isOnFire()) {
      fireParticles.update(delta);
    }
  }

  @Override
  public void die() {
  }

  /**
   * Method to tell if the zombie is on fire.
   *
   * @return True if zombie on fire.
   */
  public boolean isOnFire() {
    return this.state;
  }

  /**
   * Gets the initial speed of the zombie.
   * 
   * @return Double, initial speed of zombie.
   */
  public double getInitSpeed() {
    return initial_speed;
  }

  /**
   * Sets initial speed.
   *
   * @param s
   *          the initial speed to set zombie field to
   */
  public void setInitSpeed(double s) {
    initial_speed = s;
  }

  /**
   * Sets zombie id.
   *
   * @param i
   *          the id
   */
  public void setZombID(String i) {
    id = i;
  }

  /**
   * Method that calculates the coordinates to follow when chasing player.
   *
   * @param delta
   *          Int, difference between last update time and now
   */
  protected void followPlayer(int delta) {

    float playerX = this.player.getX();
    float playerY = this.player.getY();

    float xDiff = playerX - this.x;
    float yDiff = playerY - this.y;

    double h = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));

    float xMove = (float) (xDiff / h * this.speed);
    float yMove = (float) (yDiff / h * this.speed);

    this.moveTo(this.x + xMove, this.y + yMove);
  }

}
