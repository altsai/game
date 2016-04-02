package states;

import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import entities.Entity;
import entities.Player;
import entities.Zombie;
import game_objects.Powerup;
import powerups.Bomb;
import powerups.Speed;
import powerups.TimeStop;

/**
 * Provides a template for gameplay state objects.
 *
 * The single player and double player game play states
 * both extend the GamePlayState.
 *
 * @author bl48
 *
 */
public abstract class GamePlayState extends BasicGameState {
  //list of all entities in the game
  protected ArrayList<Entity> entities;
  protected ArrayList<Powerup> powerups;

  // players in the game
  protected ArrayList<Player> players;

  // list of constants
  protected static final int ZOMBIE_SPAWN_DELAY = 1000;
  protected static final int POWERUP_SPAWN_DELAY = 5000;
  protected static final double ZOMBIE_BASE_SPEED = 0.3 * 3;
  protected static final int MAX_DIFFICULTY_LEVEL = 15;
  protected static final double SPEED_MULTIPLIER = 0.1;

  protected Random random;
  protected int difficultyLevel;

  // timers
  protected long lastZombieSpawnTime;
  protected long lastPowerupSpawnTime;
  protected long lastDifficultyIncreaseTime;

  // boolean to tell if game should be spawning
  private boolean spawnOn;

  @Override
  public void init(GameContainer gc, StateBasedGame s)
      throws SlickException {
    this.entities = new ArrayList<>();
    this.powerups = new ArrayList<>();

    this.players = new ArrayList<>();
    this.lastZombieSpawnTime = System.currentTimeMillis();
    this.lastDifficultyIncreaseTime = System.currentTimeMillis();
    this.random = new Random();
    this.difficultyLevel = 1;
    this.spawnOn = true;
  }

  @Override
  public void render(GameContainer gc, StateBasedGame s, Graphics g)
      throws SlickException {

    for (Player p : this.players) {
      p.render(gc, g);
    }
    for (Entity e : this.entities) {
      e.render(gc, g);
    }
    for (Powerup p : this.powerups) {
      p.render(gc, g);
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
   */
  private void updateAndCheckCollisions(GameContainer gc, StateBasedGame s,
      int delta) {

    // check for player collision with every entity
    for (Entity e : this.entities) {
      e.update(gc, delta);

      // check player's lives and mark invincible as necessary
      for (Player p : this.players) {
        if (p.isCollision(e)) {

          if (p.isInvincible()) {
            continue;
          }

          if (p.getLives() == 0) {
            endGame(s);
          } else {
            p.loseLife();
          }
        }
      }
    }

    // check for player collision with every powerup
    for (Powerup powerup : this.powerups) {
      for (Player p : this.players) {
        if (p.isCollision(powerup)) {
          p.collectPowerup(powerup);
        }
      }
    }
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
    for (int i = 0; i < this.powerups.size(); i++) {
      this.powerups.get(i).update(gc, delta);
    }
  }

  /**
   * Method to set whether or not the game should be spawning any zombies.
   * @param flag
   */
  public void setSpawnOn(boolean flag) {
    this.spawnOn = flag;
  }

  /**
   * Method that spawns in zombies.
   *
   * Zombies spawn every second, but quantity depends on difficulty level.
   */
  private void spawnZombie() {

    // check if the game should be spawning zombies (time stop may have stopped spawns)
    if (this.spawnOn) {

      if (System.currentTimeMillis() - this.lastZombieSpawnTime >= ZOMBIE_SPAWN_DELAY) {

        // have a random player to target
        Player target = this.players.get(random.nextInt(this.players.size()));

        // at any given time there is a 30% chance of multiple spawns
        if (random.nextInt(9) < 3) {
          for (int i = 0; i < this.difficultyLevel; i++) {
            // spawn targeting a random player

            Zombie newZombie = new Zombie(target);

            newZombie.setSpeed(ZOMBIE_BASE_SPEED
                + ((this.difficultyLevel - 1) * SPEED_MULTIPLIER)
                * ZOMBIE_BASE_SPEED);
            this.entities.add(newZombie);
          }
        }

        Zombie newZombie = new Zombie(target);

        newZombie.setSpeed(ZOMBIE_BASE_SPEED + ((this.difficultyLevel - 1) * SPEED_MULTIPLIER)
            * ZOMBIE_BASE_SPEED);
        this.entities.add(newZombie);

        this.lastZombieSpawnTime = System.currentTimeMillis();
      }

      if (this.difficultyLevel < MAX_DIFFICULTY_LEVEL) {
        if (System.currentTimeMillis() - this.lastDifficultyIncreaseTime > 10000) {

          this.difficultyLevel++;
          this.lastDifficultyIncreaseTime = System.currentTimeMillis();
        }
      }
    }
  }

  private void spawnPowerup() {

    if (System.currentTimeMillis() - this.lastPowerupSpawnTime >= POWERUP_SPAWN_DELAY) {

      double randomNum = random.nextDouble();
      if (randomNum < 0.3) {
        Bomb bomb = new Bomb(powerups, entities);
        this.powerups.add(bomb);
      } else if (randomNum < 0.6 && randomNum >= 0.33) {
        Speed speed = new Speed(powerups);
        this.powerups.add(speed);
      } else if (randomNum < 0.9 && randomNum >= 0.6) {
        TimeStop timestop = new TimeStop(powerups, entities, this);
        this.powerups.add(timestop);
      }

      this.lastPowerupSpawnTime = System.currentTimeMillis();
    }
  }

  /**
   * method to end a game.
   *
   * @param s     StateBasedGame s
   */
  protected abstract void endGame(StateBasedGame s);

}
