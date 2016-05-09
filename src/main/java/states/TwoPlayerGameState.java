package states;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import edu.brown.cs.altsai.game.Resources;
import edu.brown.cs.altsai.game.Window;
import entities.Player;
import entities.Zombie;
import game_objects.Powerup;
import powerups.Bomb;
import powerups.Jail;
import powerups.LaserBeam;
import powerups.Speed;
import powerups.TimeStop;

/**
 * Defines the two player game state.
 *
 * @author bl48
 *
 */
public class TwoPlayerGameState extends GamePlayState {
  // string for now, we'll add names to players later, so then it'll be a player
  private String winner;

  private static final int BUTTON_WIDTH = 280;
  private static final int BUTTON_HEIGHT = 67;
  private static final int PAUSE_MENU_HEIGHT = 300;
  private static final int PAUSE_MENU_WIDTH = 300;

  @Override
  public void init(GameContainer gc, StateBasedGame s) throws SlickException {
    super.init(gc, s);

    // instantiate two new players and add them
    Player player1 = new Player(null, "player1");
    player1.setPlayer1(true);
    player1.setID("0");
    Player player2 = new Player(null, "player2");
    player2.setPlayer1(false);
    player2.setID("1");

    player2.setImage(Resources.getImage("player2"));
    this.players.put(player1.getID(), player1);
    this.players.put(player2.getID(), player2);
  }

  public String getWinner() {
    return this.winner;
  }

  @Override
  public void update(GameContainer gc, StateBasedGame s, int delta)
      throws SlickException {
    if (this.gameStart) {

      if (!this.pauseMenu) {
      elapsedTime += delta;

      spawnZombie();
      spawnPowerup();

      for (Player p : this.players.values()) {
        p.updateAndControlTwoPlayerSameScreen(gc, delta);
      }

      updateAndCheckCollisions(gc, s, delta);
      updatePowerups(gc, delta);

      } else {
        this.updatePowerupsPaused(gc, delta);
      }

      // Get x and y mouse position coordinates
      int posX = gc.getInput().getMouseX();
      int posY = gc.getInput().getMouseY();
      boolean inX = false;


      if (pauseMenu && gc.getInput().isMouseButtonDown(0) && posX >= (Window.width - BUTTON_WIDTH) / 2 && posX <= (Window.width - BUTTON_WIDTH) / 2 + BUTTON_WIDTH) {
        inX = true;
      }

      // bring up/down pause menu
      if (gc.getInput().isKeyPressed(Input.KEY_ESCAPE) || (inX && posY >= (Window.height - PAUSE_MENU_HEIGHT) / 2 + 20 + ttf.getLineHeight() + 20 && posY <= (Window.height - PAUSE_MENU_HEIGHT) / 2 + 20 + ttf.getLineHeight() + 20 + BUTTON_HEIGHT)) {
        pauseMenu = !pauseMenu;
      }

      // back to main menu
      if (inX && posY >= (Window.height - PAUSE_MENU_HEIGHT) / 2 + 20 + ttf.getLineHeight() + 20 + BUTTON_HEIGHT + 20 && posY <= (Window.height - PAUSE_MENU_HEIGHT) / 2 + 20 + ttf.getLineHeight() + 20 + BUTTON_HEIGHT + 20 + BUTTON_HEIGHT) {
        s.enterState(States.MENU);
      }
    }

  }

  @Override
  public void render(GameContainer gc, StateBasedGame s, Graphics g)
      throws SlickException {

    super.render(gc, s, g);

    // Draw lives
    for (int i = 0; i < this.players.get("0").getLives() + 1; i++) {
      Resources.getImage("life").draw(15 + i * 25, 10, 20, 20);
    }
    for (int i = 0; i < this.players.get("1").getLives() + 1; i++) {
      Resources.getImage("life").draw(Window.width - 35 - i * 25, 10, 20, 20);
    }

    // Draw current powerups
    g.setColor(Color.gray);
    g.drawRect(Window.width / 4 - 15, 6, 30, 30);
    g.drawRect(3 * Window.width / 4 - 15, 6, 30, 30);
    g.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
    Powerup currPowerup1 = this.players.get("0").getCurrPowerup();
    Powerup currPowerup2 = this.players.get("1").getCurrPowerup();
    if (currPowerup1 != null) {
      currPowerup1.getImage().draw(Window.width / 4 - 9, 11, 20, 20);
    }
    if (currPowerup2 != null) {
      currPowerup2.getImage().draw(3 * Window.width / 4 - 9, 11, 20, 20);
    }
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
        Player target = this.players.get(String.valueOf(random.nextInt(this.players.size())));

        // at any given time there is a 30% chance of multiple spawns
        if (random.nextInt(9) < 3) {
          for (int i = 0; i < this.difficultyLevel; i++) {
            // spawn targeting a random player

            Zombie newZombie = new Zombie(target, this.players);

            newZombie.setSpeed(ZOMBIE_BASE_SPEED
                + ((this.difficultyLevel - 1) * SPEED_MULTIPLIER)
                * ZOMBIE_BASE_SPEED);
            this.zombies.put(newZombie.getID(), newZombie);
          }
        }

        Zombie newZombie = new Zombie(target, this.players);

        newZombie.setSpeed(ZOMBIE_BASE_SPEED
            + ((this.difficultyLevel - 1) * SPEED_MULTIPLIER)
            * ZOMBIE_BASE_SPEED);
        this.zombies.put(newZombie.getID(), newZombie);

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
        Bomb bomb = new Bomb(powerups, zombies, players);
        this.powerups.put(bomb.getID(), bomb);
      } else if (randomNum < 0.4 && randomNum >= 0.2) {
        Speed speed = new Speed(powerups);
        this.powerups.put(speed.getID(), speed);
      } else if (randomNum < 0.6 && randomNum >= 0.4) {
        TimeStop timestop = new TimeStop(powerups, zombies, players, this);
        this.powerups.put(timestop.getID(), timestop);
      } else if (randomNum < 0.8 && randomNum >= 0.6) {
        LaserBeam lb = new LaserBeam(powerups, zombies, players);
        this.powerups.put(lb.getID(), lb);
      } else {
        Jail jail = new Jail(powerups, zombies, players);
        this.powerups.put(jail.getID(), jail);
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
