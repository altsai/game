package states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import powerups.Bomb;
import powerups.Speed;
import powerups.TimeStop;
import edu.brown.cs.altsai.game.Resources;
import edu.brown.cs.altsai.game.Window;
import entities.Player;
import entities.Zombie;

/**
 * Defines the two player game state.
 *
 * @author bl48
 *
 */
public class TwoPlayerGameState extends GamePlayState {
  // string for now, we'll add names to players later, so then it'll be a player
  private String winner;

  @Override
  public void init(GameContainer gc, StateBasedGame s) throws SlickException {
    super.init(gc, s);

    // instantiate two new players and add them
    Player player1 = new Player(null, "player1");
    player1.setPlayer1(true);
    Player player2 = new Player(null, "player2");
    player2.setPlayer1(false);

    player2.setImage(Resources.getImage("player2"));
    this.players.add(player1);
    this.players.add(player2);
  }

  public String getWinner() {
    return this.winner;
  }

  @Override
  public void render(GameContainer gc, StateBasedGame s, Graphics g)
      throws SlickException {

    super.render(gc, s, g);

    g.drawString("Player1 has " + this.players.get(0).getLives() + " lives",
        100, 100);
    g.drawString("Player2 has " + this.players.get(1).getLives() + " lives",
        300, 100);
    g.drawString("Player1 speed: " + this.players.get(0).getSpeed(), 100, 50);
    g.drawString("Player2 speed: " + this.players.get(1).getSpeed(), 300, 50);

    g.drawString("Hit esc to go to menu", Window.width / 2, Window.height / 2);
  }

  @Override
  public int getID() {
    return States.TWO_PLAYER;
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
      if (randomNum < 0.33) {
        Bomb bomb = new Bomb(powerups, zombies);
        this.powerups.put(generateID(), bomb);
      } else if (randomNum < 0.6 && randomNum >= 0.33) {
        Speed speed = new Speed(powerups);
        this.powerups.put(generateID(), speed);
      } else if (randomNum < 0.9 && randomNum >= 0.6) {
        TimeStop timestop = new TimeStop(powerups, zombies, players, this);
        this.powerups.put(generateID(), timestop);
      }

      this.lastPowerupSpawnTime = System.currentTimeMillis();
    }

  }

  @Override
  protected void endGame(GameContainer gc, StateBasedGame s) {
    s.enterState(States.TWO_PLAYER_END_GAME, new FadeOutTransition(),
        new FadeInTransition());
  }
}
