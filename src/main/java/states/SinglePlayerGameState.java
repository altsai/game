package states;

import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import edu.brown.cs.altsai.game.Window;
import entities.Entity;
import entities.Player;
import entities.Zombie;

/**
 * Defines the Single Player game state.
 *
 * @author bl48
 *
 */
public class SinglePlayerGameState extends BasicGameState {
  //list of all entities in the game
  private ArrayList<Entity> entities;
  private Player player1;
  public static int score;

  // list of constants
  private static final int ZOMBIE_SPAWN_DELAY = 1000;
  private static final double ZOMBIE_BASE_SPEED = 0.3 * 3;


  private Random random;
  private int difficultyLevel;

  // timers
  private long lastZombieSpawnTime;
  private long lastDifficultyIncreaseTime;


  @Override
  public void init(GameContainer gc, StateBasedGame s)
      throws SlickException {
    this.entities = new ArrayList<>();
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

    g.drawString("Player has " + player1.getLives() + " lives", 0, 0);

    g.drawString("Hit esc to go to menu", Window.width / 2, Window.height / 2);
  }

  @Override
  public void update(GameContainer gc, StateBasedGame s, int delta)
      throws SlickException {

    spawnZombie();

    this.player1.update(gc, delta);

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
   * @return    int, score of the player
   */
  public int getScore() {
    return this.player1.getScore();
  }

  private void spawnZombie() {
    if (System.currentTimeMillis() - this.lastZombieSpawnTime >= ZOMBIE_SPAWN_DELAY) {

      if (random.nextInt(9) < 3) {
        for (int i = 0; i < this.difficultyLevel; i++) {
          Zombie newZombie = new Zombie(this.player1);

          newZombie.setSpeed(ZOMBIE_BASE_SPEED + ((this.difficultyLevel - 1) * 0.1) * ZOMBIE_BASE_SPEED);
          this.entities.add(new Zombie(this.player1));
        }
      }

      Zombie newZombie = new Zombie(this.player1);

      newZombie.setSpeed(ZOMBIE_BASE_SPEED + ((this.difficultyLevel - 1) * 0.1) * ZOMBIE_BASE_SPEED);
      this.entities.add(new Zombie(this.player1));

      this.lastZombieSpawnTime = System.currentTimeMillis();
    }

    if (this.difficultyLevel < 8) {
      if (System.currentTimeMillis() - this.lastDifficultyIncreaseTime > 10000) {
        this.difficultyLevel++;
        this.lastDifficultyIncreaseTime = System.currentTimeMillis();
      }
    }
  }

  private void endGame(StateBasedGame s) {
    SinglePlayerEndGameState endGame = new SinglePlayerEndGameState(this);
    s.enterState(States.SINGLE_PLAYER_END_GAME);
  }

}
