package states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import powerups.BlackHole;
import powerups.Bomb;
import powerups.Speed;
import powerups.TimeStop;
import edu.brown.cs.altsai.game.Window;
import entities.Player;
import entities.Zombie;

/**
 * Defines the Single Player game state.
 *
 * @author bl48
 *
 */
public class SinglePlayerGameState extends GamePlayState {

  @Override
  public void init(GameContainer gc, StateBasedGame s) throws SlickException {
    super.init(gc, s);

    // add players to the player list from the superclass
    Player player1 = new Player(null, "player1");
    player1.setPlayer1(true);
    this.players.add(player1);
  }

  @Override
  public void render(GameContainer gc, StateBasedGame s, Graphics g)
      throws SlickException {

    super.render(gc, s, g);

    g.drawString("Player has " + this.players.get(0).getLives() + " lives",
        100, 100);
    g.drawString("Player speed: " + this.players.get(0).getSpeed(), 50, 50);

    g.drawString("Hit esc to go to menu", Window.width / 2, Window.height / 2);
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
    return this.players.get(0).getScore();
  }

  @Override
  /**
   * Method that ends the single player game.
   *
   * @param s
   *          StateBasedGame
   */
  protected void endGame(GameContainer gc, StateBasedGame s)
      throws SlickException {
    s.getState(States.SINGLE_PLAYER_END_GAME).init(gc, s);
    s.enterState(States.SINGLE_PLAYER_END_GAME, new FadeOutTransition(),
        new FadeInTransition());
  }

  @Override
  protected void spawnZombie() {
    // check if the game should be spawning zombies (time stop may have stopped
    // spawns)
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
            this.zombies.put(generateID(), newZombie);
          }
        }

        Zombie newZombie = new Zombie(target);

        newZombie.setSpeed(ZOMBIE_BASE_SPEED
            + ((this.difficultyLevel - 1) * SPEED_MULTIPLIER)
            * ZOMBIE_BASE_SPEED);
        this.zombies.put(generateID(), newZombie);

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

  @Override
  protected void spawnPowerup() {
    if (System.currentTimeMillis() - this.lastPowerupSpawnTime >= POWERUP_SPAWN_DELAY) {

      double randomNum = random.nextDouble();
      if (randomNum < 0.2) {
        // TODO fix - functionality delay
        Bomb bomb = new Bomb(powerups, zombies);
        this.powerups.put(generateID(), bomb);
      } else if (randomNum < 0.4 && randomNum >= 0.2) {
        Speed speed = new Speed(powerups);
        this.powerups.put(generateID(), speed);
      } else if (randomNum < 0.6 && randomNum >= 0.4) {
        // TODO fix - overlapping usage
        TimeStop timestop = new TimeStop(powerups, zombies, this);
        this.powerups.put(generateID(), timestop);
      } else if (randomNum < 0.8 && randomNum >= 0.6) {
        // TODO figure out what the fuck is wrong with this
        // OnFire onfire = new OnFire(powerups, zombies);
        // this.powerups.put(generateID(), onfire);
      } else {
        BlackHole blackhole = new BlackHole(powerups, zombies, this);
        this.powerups.put(generateID(), blackhole);
      }

      this.lastPowerupSpawnTime = System.currentTimeMillis();
    }

  }

}
