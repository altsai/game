package states;

import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import edu.brown.cs.altsai.game.Window;
import entities.Entity;
import entities.Player;
import entities.Zombie;
import game_objects.Powerup;
import powerups.Bomb;

/**
 * Defines the Single Player game state.
 *
 * @author bl48
 *
 */
public class SinglePlayerGameState extends BasicGameState {
  // list of all entities in the game
  private ArrayList<Entity> entities;
  private ArrayList<Powerup> powerups;

  // the sole player of SinglePlayerGameState
  private Player player1;

  // list of constants
  private static final int ZOMBIE_SPAWN_DELAY = 1000;
  private static final int POWERUP_SPAWN_DELAY = 5000;
  private static final double ZOMBIE_BASE_SPEED = 0.3 * 3;
  private static final int MAX_DIFFICULTY_LEVEL = 15;
  private static final double SPEED_MULTIPLIER = 0.3;

  private Random random;
  private int difficultyLevel;

  // timers
  private long lastZombieSpawnTime;
  private long lastPowerupSpawnTime;
  private long lastDifficultyIncreaseTime;

  @Override
  public void init(GameContainer gc, StateBasedGame s) throws SlickException {
    this.entities = new ArrayList<>();
    this.powerups = new ArrayList<>();

    Player player1 = new Player(null);
    this.player1 = player1;
    this.lastZombieSpawnTime = System.currentTimeMillis();
    this.lastDifficultyIncreaseTime = System.currentTimeMillis();
    this.random = new Random();
    this.difficultyLevel = 1;
  }

  @Override
  public void render(GameContainer gc, StateBasedGame s, Graphics g)
      throws SlickException {

    this.player1.render(gc, g);
    for (int i = 0; i < this.entities.size(); i++) {
      this.entities.get(i).render(gc, g);
    }
    for (int i = 0; i < this.powerups.size(); i++) {
      this.powerups.get(i).render(gc, g);
    }

    g.drawString("Player has " + player1.getLives() + " lives", 100, 100);

    g.drawString("Hit esc to go to menu", Window.width / 2, Window.height / 2);
  }

  @Override
  public void update(GameContainer gc, StateBasedGame s, int delta)
      throws SlickException {
    spawnZombie();
    spawnPowerup();
    this.player1.update(gc, delta);

    updateAndCheckCollisions(gc, s, delta);

    // go to the home menu state when 'esc' is pressed
    if (gc.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
      s.enterState(States.MENU);
    }

  }

  @Override
  public int getID() {
    return States.SINGLE_PLAYER;
  }

  /**
   * Method to get the score for the player of this state.
   *
   * @return int, score of the player
   */
  public int getScore() {
    return this.player1.getScore();
  }

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
    for (int i = 0; i < this.entities.size(); i++) {
      this.entities.get(i).update(gc, delta);

      // check player's lives and mark invincible as necessary
      if (player1.isCollision(this.entities.get(i))) {

        if (player1.isInvincible()) {
          continue;
        }

        if (player1.getLives() == 0) {
          endGame(s);
        } else {
          player1.loseLife();
        }
      }
    }

    // check for player collision with every powerup
    for (int i = 0; i < this.powerups.size(); i++) {
      if (player1.isCollision(powerups.get(i))) {
        player1.collectPowerup(powerups.get(i));
      }
    }
  }

  /**
   * Method that spawns in zombies.
   *
   * Zombies spawn every second, but quantity depends on difficulty level.
   */
  private void spawnZombie() {

    if (System.currentTimeMillis() - this.lastZombieSpawnTime >= ZOMBIE_SPAWN_DELAY) {

      // at any given time there is a 30% chance of multiple spawns
      if (random.nextInt(9) < 3) {
        for (int i = 0; i < this.difficultyLevel; i++) {
          Zombie newZombie = new Zombie(this.player1);

          newZombie.setSpeed(ZOMBIE_BASE_SPEED
              + ((this.difficultyLevel - 1) * SPEED_MULTIPLIER)
              * ZOMBIE_BASE_SPEED);
          this.entities.add(new Zombie(this.player1));
        }
      }

      Zombie newZombie = new Zombie(this.player1);

      newZombie.setSpeed(ZOMBIE_BASE_SPEED + ((this.difficultyLevel - 1) * 0.1)
          * ZOMBIE_BASE_SPEED);
      this.entities.add(new Zombie(this.player1));

      this.lastZombieSpawnTime = System.currentTimeMillis();
    }

    if (this.difficultyLevel < MAX_DIFFICULTY_LEVEL) {
      if (System.currentTimeMillis() - this.lastDifficultyIncreaseTime > 10000) {

        this.difficultyLevel++;
        this.lastDifficultyIncreaseTime = System.currentTimeMillis();
      }
    }
  }

  private void spawnPowerup() {

    if (System.currentTimeMillis() - this.lastPowerupSpawnTime >= POWERUP_SPAWN_DELAY) {

      // TODO: code to randomize Powerups

      Bomb bomb = new Bomb(this.player1, powerups, entities);
      this.powerups.add(bomb);

      this.lastPowerupSpawnTime = System.currentTimeMillis();
    }
  }

  /**
   * Method that ends the single player game.
   *
   * @param s
   *          StateBasedGame
   */
  private void endGame(StateBasedGame s) {
    s.enterState(States.SINGLE_PLAYER_END_GAME, new FadeOutTransition(),
        new FadeInTransition());
  }

}
