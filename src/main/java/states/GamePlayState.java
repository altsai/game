package states;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import edu.brown.cs.altsai.game.Resources;
import edu.brown.cs.altsai.game.Window;
import entities.Player;
import entities.Zombie;
import game_objects.Powerup;

/**
 * Provides a template for gameplay state objects.
 *
 * The single player and double player game play states both extend the
 * GamePlayState.
 *
 * @author bl48
 *
 */
public abstract class GamePlayState extends BasicGameState {
  // list of all entities in the game
  protected ConcurrentHashMap<String, Zombie> zombies;
  protected ConcurrentHashMap<String, Powerup> powerups;

  // players in the game
  protected ArrayList<Player> players;

  // list of constants
  protected static final int ZOMBIE_SPAWN_DELAY = 1000;
  protected static final int POWERUP_SPAWN_DELAY = 5000;
  protected static final double ZOMBIE_BASE_SPEED = 0.3 * 3;
  protected static final int MAX_DIFFICULTY_LEVEL = 8;
  protected static final double SPEED_MULTIPLIER = 0.1;

  protected Random random;
  protected int difficultyLevel;
  protected Player loser;

  // timers
  protected long lastZombieSpawnTime;
  protected long lastPowerupSpawnTime;
  protected long lastDifficultyIncreaseTime;

  // boolean to tell if game should be spawning
  protected boolean spawnOn;

  @Override
  public void init(GameContainer gc, StateBasedGame s) throws SlickException {
    this.zombies = new ConcurrentHashMap<>();
    this.powerups = new ConcurrentHashMap<>();

    this.players = new ArrayList<>();
    this.lastZombieSpawnTime = System.currentTimeMillis();
    this.lastDifficultyIncreaseTime = System.currentTimeMillis();
    this.random = new Random();
    this.difficultyLevel = 1;
    this.spawnOn = true;
    this.loser = null;
  }

  @Override
  public void render(GameContainer gc, StateBasedGame s, Graphics g)
      throws SlickException {

    g.drawImage(Resources.getImage("background"), 0, 0);

    // Draw bounding box
    g.setColor(Color.black);
    g.drawRoundRect(10, 40, Window.width - 20, Window.height - 50, 10);
    g.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));

    for (Player p : this.players) {
      p.render(gc, g);
    }
    for (String zid : zombies.keySet()) {
      zombies.get(zid).render(gc, g);
    }
    for (String pid : powerups.keySet()) {
      powerups.get(pid).render(gc, g);
    }

  }

  @Override
  public void update(GameContainer gc, StateBasedGame s, int delta)
      throws SlickException {
    spawnZombie();
    spawnPowerup();

    for (Player p : this.players) {
      p.update(gc, delta);
    }

    updateAndCheckCollisions(gc, s, delta);
    updatePowerups(gc, delta);

    // go to the home menu state when 'esc' is pressed
    if (gc.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
      s.enterState(States.MENU);
    }

  }

  @Override
  public abstract int getID();

  /**
   * Method that upates the entities and checks for collisions.
   *
   * @param gc
   *          GameContainer, window of the game
   * @param delta
   *          Int, change in time since last update
   * @throws SlickException
   */
  private void updateAndCheckCollisions(GameContainer gc, StateBasedGame s,
      int delta) throws SlickException {

    // check for player collision with every entity
    for (String zid : zombies.keySet()) {
      Zombie z = zombies.get(zid);
      z.update(gc, delta);

      // check player's lives and mark invincible as necessary
      for (Player p : this.players) {
        if (p.isCollision(z) && !p.isInvincible() && !p.isImmune()) {
          if (p.getLives() == 0) {
            this.loser = p;
            endGame(gc, s);
          } else {
            p.loseLife();
          }
        }
      }
    }

    // check for player collision with every powerup
    for (String pid : powerups.keySet()) {
      Powerup powerup = powerups.get(pid);
      for (Player p : this.players) {
        if (p.isCollision(powerup)) {
          p.collectPowerup(powerup);
        }
      }
    }
  }

  public Player getLoser() {
    return this.loser;
  }

  /**
   * Method that loops through powerups and updates them
   *
   * @param gc
   * @param delta
   */
  private void updatePowerups(GameContainer gc, int delta) {

    // DO NOT USE ENHANCED FOR LOOP HERE. IDK WHY BUT THERES A THREADING ISSUE
    // PLS DO NOT CHANGE...
    // for (int i = 0; i < this.powerups.size(); i++) {
    // this.powerups.get(i).update(gc, delta);
    // }
    for (String pid : powerups.keySet()) {
      powerups.get(pid).update(gc, delta);
    }
  }

  /**
   * Method to set whether or not the game should be spawning any zombies.
   *
   * @param flag
   */
  public void setSpawnOn(boolean flag) {
    this.spawnOn = flag;
  }

  /**
   * Method that spawns in zombies.
   *
   * Unique to each gamestate
   *
   * Zombies spawn every second, but quantity depends on difficulty level.
   */
  protected abstract void spawnZombie();

  /**
   * Method that spawns powerups.
   *
   * Unique to each gamestate
   */
  protected abstract void spawnPowerup();

  /**
   * method to end a game.
   *
   * @param s
   *          StateBasedGame s
   * @throws SlickException
   */
  protected abstract void endGame(GameContainer gc, StateBasedGame s)
      throws SlickException;

  public String generateID() {
    return UUID.randomUUID().toString();
  }

}
