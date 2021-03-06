package states;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import edu.brown.cs.altsai.game.Resources;
import edu.brown.cs.altsai.game.Window;
import entities.Player;
import entities.Zombie;
import entities.ZombieArrow;
import entities.ZombieWall;
import game_objects.Powerup;
import highscore.HighscoreSystem;
import powerups.BlackHole;
import powerups.Bomb;
import powerups.LaserBeam;
import powerups.OnFire;
import powerups.Speed;
import powerups.TimeStop;

/**
 * Defines the Single Player game state.
 *
 * @author bl48
 *
 */
public class SinglePlayerGameState extends GamePlayState {

  private TrueTypeFont ttf;
  private TrueTypeFont ttf2;
  private int iter;

  @Override
  public void init(GameContainer gc, StateBasedGame s) throws SlickException {
    super.init(gc, s);

    // add players to the player list from the superclass
    Player player1 = new Player(null, "Player 1");
    player1.setPlayer1(true);

    Font font = new Font("Arial", Font.BOLD, 20);
    ttf = new TrueTypeFont(font, true);
    Font font2 = new Font("Arial", Font.PLAIN, 20);
    ttf2 = new TrueTypeFont(font2, true);

    player1.setID("0");
    this.players.put(player1.getID(), player1);
    this.playerID = player1.getID();

    this.setElapsedTime(0);

    gc.getInput().clearControlPressedRecord();
    gc.getInput().clearKeyPressedRecord();
    gc.getInput().clearMousePressedRecord();
  }

  @Override
  public void render(GameContainer gc, StateBasedGame s, Graphics g)
      throws SlickException {
    super.render(gc, s, g);

    // Draw lives
    for (int i = 0; i < this.players.get("0").getLives() + 1; i++) {
      Resources.getImage("life1").draw(Window.width - 35 - i * 25, 10, 20, 20);
    }

    // Draw score
    ttf.drawString(15, 10, "Score", Color.white);
    ttf.drawString((Window.width / 2 - ttf.getWidth(getTimeSurvived())) / 2,
        10, getTimeSurvived());
    ttf2.drawString(80, 10, Integer.toString(this.getScore()), Color.green);

    // Draw current power-up in the middle
    g.setColor(Color.gray);
    g.drawRect(Window.width / 2 - 15, 6, 30, 30);
    g.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
    Powerup currPowerup = this.players.get("0").getCurrPowerup();
    if (currPowerup != null) {
      currPowerup.getImage().draw(Window.width / 2 - 9, 11, 20, 20);
    }
  }

  @Override
  public int getID() {
    return States.SINGLE_PLAYER;
  }

  public String getTimeSurvived() {
    return HighscoreSystem.timeToString(elapsedTime);
  }

  /**
   * Method to get the score for the player of this state.
   *
   * @return int, score of the player
   */
  public int getScore() {
    return this.players.get(this.playerID).getScore();
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
        Player target = this.players.get(String.valueOf(random
            .nextInt(this.players.size())));
        int prob = random.nextInt(20);
        if (prob < 2) {
          new ZombieArrow(target, players, ZOMBIE_BASE_SPEED * 1.5, zombies,
              zombieFormations);
        } else if (prob == 2) {
          new ZombieWall(target, players, ZOMBIE_BASE_SPEED, zombies,
              zombieFormations);
        } else {
          // at any given time there is a 30% chance of multiple spawns
          if (random.nextInt(10) < 3) {
            for (int i = 0; i < this.difficultyLevel; i++) {
              // spawn targeting a random player

              Zombie newZombie = new Zombie(target, this.players);

              // newZombie.setSpeed(ZOMBIE_BASE_SPEED
              // + ((this.difficultyLevel - 1) * SPEED_MULTIPLIER)
              // * ZOMBIE_BASE_SPEED);
              newZombie.setSpeed(ZOMBIE_BASE_SPEED);
              this.zombies.put(newZombie.getID(), newZombie);
            }
          }

          Zombie newZombie = new Zombie(target, this.players);

          // newZombie.setSpeed(ZOMBIE_BASE_SPEED
          // + ((this.difficultyLevel - 1) * SPEED_MULTIPLIER)
          // * ZOMBIE_BASE_SPEED);
          newZombie.setSpeed(ZOMBIE_BASE_SPEED);
          this.zombies.put(newZombie.getID(), newZombie);
        }

        this.lastZombieSpawnTime = System.currentTimeMillis();
      }

      if (this.difficultyLevel < MAX_DIFFICULTY_LEVEL) {
        if (System.currentTimeMillis() - this.lastDifficultyIncreaseTime > INC_DIFFICULTY_TIME * 1000) {

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
      if (randomNum < .35) { // 35%
        Bomb bomb = new Bomb(powerups, zombies);
        this.powerups.put(bomb.getID(), bomb);
      } else if (randomNum < 0.50 && randomNum >= 0.35) { // 15%
        Speed speed = new Speed(powerups);
        this.powerups.put(speed.getID(), speed);
      } else if (randomNum < 0.65 && randomNum >= 0.50) { // 15%
        TimeStop timestop = new TimeStop(powerups, zombies, this);
        this.powerups.put(timestop.getID(), timestop);
      } else if (randomNum < 0.73 && randomNum >= 0.65) { // 8%
        OnFire onfire = new OnFire(powerups, zombies, onFireTimes);
        this.powerups.put(onfire.getID(), onfire);
      } else if (randomNum < .85 && randomNum >= .73) { // 12%
        BlackHole blackhole = new BlackHole(powerups, zombies, this,
            zombieFormations, players);
        this.powerups.put(blackhole.getID(), blackhole);
      } else { // 15%
        LaserBeam lb = new LaserBeam(powerups, zombies);
        this.powerups.put(lb.getID(), lb);
      }

      this.lastPowerupSpawnTime = System.currentTimeMillis();
    }

  }

}
