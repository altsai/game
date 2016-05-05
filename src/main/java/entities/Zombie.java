package entities;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.particles.ParticleSystem;

import com.google.common.collect.Maps;

import edu.brown.cs.altsai.game.Resources;
import edu.brown.cs.altsai.game.Window;
import effects.FireEmitterCustom;
import game_objects.Circle;

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

  private double initial_speed;

  // zombies keep track of a player and a specific target area of the player.
  private Circle player;

  private ParticleSystem fireParticles;
  private FireEmitterCustom emitter;

  private Map<String, Player> allPlayers;


  /**
   * Method that checks if the zombie is within a radius of the player.
   *
   * Used to protect players from zombie spawns.
   *
   * @return  boolean, true if the zombie is within radius, else false.
   */
  private boolean inPlayerRadius() {
    // check the player map for multiplayer
    for (Player p : this.allPlayers.values()) {
      if (this.distTo(p) < 100) {
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
      this.x = r.nextFloat() * Window.width;
      this.y = r.nextFloat() * Window.height;
    }
  }

  @Override
  public void init(Entity other) {

    // give an empty hashmap to begin with
    this.allPlayers = Maps.newHashMap();

    this.player = other;
    this.setRadius(20);

    Random r = new Random();

    this.x = r.nextFloat() * Window.width;
    this.y = r.nextFloat() * Window.height;

    this.radius = 20;
    this.image = Resources.getImage("zombie");
    this.speed = ((Player) player).getSpeed() * 3;

    this.state = false;
    this.id = UUID.randomUUID().toString();

    this.initial_speed = this.getSpeed();

    initFire();
  }

  private void initFire() {
    fireParticles = new ParticleSystem(Resources.getImage("particle"), 1500);
    //    File xmlFile = new File("particle/fire.xml");
    try {
      //      ConfigurableEmitter emitter = ParticleIO.loadEmitter(xmlFile);
      //      emitter.setPosition(this.radius / 2, this.radius / 2);
      //      fireParticles.addEmitter(emitter);
      emitter = new FireEmitterCustom((int) this.radius / 2, (int) this.radius / 2, 10);
      fireParticles.addEmitter(emitter);
      fireParticles.setBlendingMode(ParticleSystem.BLEND_ADDITIVE);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void setTarget(Circle other) {
    this.player = other;
  }

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

    //TODO: If we want the zombie to follow the closest player
    // instead of randomly selecting one, change here.

    /*
     * Go through the player map and find the closest one,
     * set target to be that closest one
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
    // TODO Auto-generated method stub

  }

  /**
   * Method to tell if the zombie is on fire.
   *
   * @return True if zombie on fire.
   */
  public boolean isOnFire() {
    return this.state;
  }

  public double getInitSpeed() {
    return initial_speed;
  }

  /**
   * Method that calculates the coordinates to follow when chasing player.
   *
   * @param delta
   *          Int, difference between last update time and now
   */
  private void followPlayer(int delta) {

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
