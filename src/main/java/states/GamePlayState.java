package states;

import java.awt.Font;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.util.ConcurrentHashSet;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import edu.brown.cs.altsai.game.Resources;
import edu.brown.cs.altsai.game.Window;
import entities.Entity;
import entities.Player;
import entities.Zombie;
import game_objects.Powerup;
import powerups.BlackHole;

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
  protected Map<String, Zombie> zombies;
  protected Map<String, Powerup> powerups;
  protected Set<Powerup> pickedUpPowerups;
  protected Map<String, List<String>> zombieFormations;
  protected Map<String, Long> onFireTimes;

  // players in the game
  protected Map<String, Player> players;

  // list of constants
  protected static final int ZOMBIE_SPAWN_DELAY = 1000;
  protected static final int POWERUP_SPAWN_DELAY = 3000;
  protected static final double ZOMBIE_BASE_SPEED = 0.3 * 3;
  protected static final int MAX_DIFFICULTY_LEVEL = 8;
  protected static final double SPEED_MULTIPLIER = 0.1;
  protected static final long INC_DIFFICULTY_TIME = 30;

  // keep this a little higher than 3 seconds because user needs to see the 3
  protected static final long GAME_COUNTDOWN = 3800;

  protected Random random;
  protected int difficultyLevel;
  protected String loser;

  // timers
  protected long lastZombieSpawnTime;
  protected long lastPowerupSpawnTime;
  protected long lastDifficultyIncreaseTime;
  protected long initialDelayTime;
  protected boolean gameStart;

  // boolean to tell if game should be spawning
  protected boolean spawnOn;
  protected boolean timeStopped;
  protected String playerID;

  protected boolean gameEnd;

  protected int elapsedTime;

  protected boolean pauseMenu;
  protected TrueTypeFont ttf;
  private static final int BUTTON_WIDTH = 250;
  private static final int BUTTON_HEIGHT = 95;
  private static final int PAUSE_MENU_HEIGHT = 300;
  private static final int PAUSE_MENU_WIDTH = 300;

  @Override
  public void init(GameContainer gc, StateBasedGame s) throws SlickException {
    this.zombies = new ConcurrentHashMap<>();
    this.powerups = new ConcurrentHashMap<>();
    this.players = new ConcurrentHashMap<>();
    this.pickedUpPowerups = new ConcurrentHashSet<>();
    this.zombieFormations = new ConcurrentHashMap<>();
    this.onFireTimes = new ConcurrentHashMap<>();

    this.lastZombieSpawnTime = System.currentTimeMillis();
    this.lastDifficultyIncreaseTime = System.currentTimeMillis();
    this.random = new Random();
    this.difficultyLevel = 1;
    this.spawnOn = true;
    this.loser = null;
    this.initialDelayTime = System.currentTimeMillis();

    this.pauseMenu = false;
    Font font = new Font("Arial", Font.BOLD, 50);
    ttf = Resources.getDefaultFont(40);
    timeStopped = false;

    gc.getInput().clearControlPressedRecord();
    gc.getInput().clearKeyPressedRecord();
    gc.getInput().clearMousePressedRecord();
  }

  /**
   * Getter for time since the game has begun.
   * @return Int, time since game as begun, milliseconds
   */
  public int getElapsedTime() {
    return this.elapsedTime;
  }

  /**
   * Setter for setting the time since game has begun.
   * @param newTime Int, time in milliseconds
   */
  public void setElapsedTime(int newTime) {
    elapsedTime = newTime;
  }

  @Override
  public void render(GameContainer gc, StateBasedGame s, Graphics g)
      throws SlickException {

    g.drawImage(Resources.getImage("background"), 0, 0);

    // Draw bounding box
    g.setColor(Color.gray);
    g.drawRoundRect(10, 40, Window.width - 20, Window.height - 50, 10);
    g.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));

    long timeSinceInit = System.currentTimeMillis() - this.initialDelayTime;
    if (timeSinceInit < (GAME_COUNTDOWN - 1000)) {
      this.setElapsedTime(0);

      int currNum = (int) ((GAME_COUNTDOWN - timeSinceInit) / 1000);
      if (currNum == 3) {
        Resources.getImage("3").draw(
            (Window.width - Resources.getImage("3").getWidth()) / 2,
            (Window.height - Resources.getImage("3").getHeight()) / 2);
      } else if (currNum == 2) {
        Resources.getImage("2").draw(
            (Window.width - Resources.getImage("2").getWidth()) / 2,
            (Window.height - Resources.getImage("2").getHeight()) / 2);
      } else if (currNum == 1) {
        Resources.getImage("1").draw(
            (Window.width - Resources.getImage("1").getWidth()) / 2,
            (Window.height - Resources.getImage("1").getHeight()) / 2);
      }
    } else {
      if (!this.gameStart) {
        // set elapsed time here.... once game begins
        this.elapsedTime = 0;
        this.gameStart = true;
      }

      for (Powerup p : this.powerups.values()) {
        p.render(gc, g);
      }
      for (Powerup p : pickedUpPowerups) {
        if (p.isActivated()) {
          for (Entity e : p.getChildren()) {
            e.render(gc, g);
          }
        }
      }
      for (Player p : this.players.values()) {
        p.render(gc, g);
      }
      for (Zombie z : this.zombies.values()) {
        z.render(gc, g);
      }

      // Pause menu
      if (pauseMenu) {
        // Background rectangle
        g.setColor(Color.gray);
        float currY = (Window.height - PAUSE_MENU_HEIGHT) / 2;
        g.fillRoundRect((Window.width - PAUSE_MENU_WIDTH) / 2, currY,
            PAUSE_MENU_WIDTH, PAUSE_MENU_HEIGHT, 10);

        // Title
        currY += 20;
        ttf.drawString((Window.width - ttf.getWidth("PAUSED")) / 2, currY,
            "PAUSED", Color.white);

        // Buttons
        currY += (20 + ttf.getLineHeight());
        Resources.getImage("buttonResume").draw(
            (Window.width - BUTTON_WIDTH) / 2, currY, BUTTON_WIDTH,
            BUTTON_HEIGHT);
        currY += (BUTTON_HEIGHT);
        Resources.getImage("buttonMainMenuLarge").draw(
            (Window.width - BUTTON_WIDTH) / 2, currY, BUTTON_WIDTH,
            BUTTON_HEIGHT);
      }

    }

  }

  @Override
  public void update(GameContainer gc, StateBasedGame s, int delta)
      throws SlickException {
    if (this.gameStart && !this.pauseMenu) {

      elapsedTime += delta;

      spawnZombie();
      spawnPowerup();

      for (Player p : this.players.values()) {
        p.updateAndControl(gc, delta);
      }

      boolean breakFormation = false;
      for (String id : zombieFormations.keySet()) {
        if (zombies.get(id) == null) {
          breakFormation = true;
        }

        for (int i = 0; i < zombieFormations.get(id).size(); i++) {
          Zombie z = zombies.get(zombieFormations.get(id).get(i));
          if (z == null) {
            breakFormation = true;
          } else if (z.getTarget() instanceof BlackHole) {
            breakFormation = false;
            break;
          }
        }

        if ((zombies.get(id) != null)
            && (zombies.get(id).getTarget() instanceof BlackHole)) {
          breakFormation = false;
        }

        if (breakFormation) {
          breakFormation(id);
        }

        breakFormation = false;
      }

      updateAndCheckCollisions(gc, s, delta);
      updatePowerups(gc, delta);
    } else {
      updatePowerupsPaused(gc, delta);
    }

    // Get x and y mouse position coordinates
    int posX = gc.getInput().getMouseX();
    int posY = gc.getInput().getMouseY();
    boolean inX = false;

    if (pauseMenu && gc.getInput().isMouseButtonDown(0)
        && posX >= (Window.width - BUTTON_WIDTH) / 2
        && posX <= (Window.width - BUTTON_WIDTH) / 2 + BUTTON_WIDTH) {
      inX = true;
    }

    // bring up/down pause menu
    if (gc.getInput().isKeyPressed(Input.KEY_ESCAPE)
        || (inX
            && posY >= (Window.height - PAUSE_MENU_HEIGHT) / 2 + 20
            + ttf.getLineHeight() + 20 && posY <= (Window.height - PAUSE_MENU_HEIGHT)
            / 2 + 20 + ttf.getLineHeight() + 20 + BUTTON_HEIGHT)) {
      pauseMenu = !pauseMenu;
    }

    // back to main menu
    if (inX
        && posY >= (Window.height - PAUSE_MENU_HEIGHT) / 2 + 20
        + ttf.getLineHeight() + 20 + BUTTON_HEIGHT
        && posY <= (Window.height - PAUSE_MENU_HEIGHT) / 2 + 20
        + ttf.getLineHeight() + 20 + BUTTON_HEIGHT + BUTTON_HEIGHT) {
      s.getState(States.MENU).init(gc, s);
      s.enterState(States.MENU);
    }
  }

  /**
   * Method to update the times for powerups even when paused.
   *
   * Changes the times of powerups so that when you pause them, they
   * don't dissappear after 10 seconds.
   *
   * @param gc      GameContainer object
   * @param delta   Int, time since last update
   */
  protected void updatePowerupsPaused(GameContainer gc, int delta) {
    for (Powerup p : this.powerups.values()) {
      p.updatePaused(gc, delta);
    }
  }

  /**
   * Method that breaks the formations of wall/arrow zombies.
   * @param id  String, the id of the formation
   */
  private void breakFormation(String id) {
    if (timeStopped) {
      return;
    }

    if (zombies.get(id) != null) {
      replaceZombie(zombies.get(id));
    }

    for (int i = 0; i < zombieFormations.get(id).size(); i++) {
      if (zombies.get(zombieFormations.get(id).get(i)) != null) {
        replaceZombie(zombies.get(zombieFormations.get(id).get(i)));
      }
    }

    zombieFormations.remove(id);
  }

  /**
   * Method that replaces zombies.
   * @param z   Zombie object
   */
  private void replaceZombie(Zombie z) {
    Player target = this.players.get(String.valueOf(random.nextInt(this.players
        .size())));

    if (z.isOnFire()) {
      return;
    }

    Zombie zomb = new Zombie(target, players);
    zomb.setSpeed(ZOMBIE_BASE_SPEED);
    zomb.setX(z.getX());
    zomb.setY(z.getY());
    this.zombies.put(zomb.getID(), zomb);
    zombies.remove(z.getID());
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
  protected void updateAndCheckCollisions(GameContainer gc, StateBasedGame s,
      int delta) throws SlickException {

    // check for player collision with every entity
    for (Zombie z : this.zombies.values()) {
      z.update(gc, delta);

      // check player's lives and mark invincible as necessary
      for (Player p : this.players.values()) {
        if (p.isCollision(z) && !p.isInvincible() && !p.isImmune()) {
          if (p.getLives() == 0) {
            this.loser = p.getID();
            endGame(gc, s);
          } else {
            p.loseLife();
          }
        }
      }
    }

    // check for player collision with every powerup
    for (Powerup powerup : this.powerups.values()) {
      for (Player p : this.players.values()) {
        if (p.isCollision(powerup) && !this.pickedUpPowerups.contains(powerup)) {
          p.collectPowerup(powerup);
          this.pickedUpPowerups.add(powerup);
        }
      }
    }
  }

  /**
   * Gets the loser of the game played.
   * @return String, line designating the loser
   */
  public String getLoser() {
    return this.loser;
  }

  /**
   * Sets the loser of the game played.
   * @param loser  STring, line designating the loser.
   */
  public void setLoser(String loser) {
    this.loser = loser;
  }

  /**
   * Method that loops through powerups and updates them
   *
   * @param gc
   * @param delta
   */
  protected void updatePowerups(GameContainer gc, int delta) {
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
   * Sets if the game has ended.
   * @param flat, Boolean true if game ended, else false.
   */
  public void setGameEnd(boolean flat) {
    this.gameEnd = flat;
  }

  /**
   * Method to set the game play state to time stop.
   *
   * Called when a timestop powerup is used.
   * @param t  Boolean, true if stop time, else false
   */
  public void setTimeStopped(boolean t) {
    timeStopped = t;
  }

  /**
   * Method to check if time is stopped after using timestop powerup.
   * @return boolean, true if stopped.
   */
  public boolean isTimeStopped() {
    return timeStopped;
  }

  /**
   * Method to check if a zombie is on fire.
   * @param id String, id of a zombie
   * @return boolean, true if zombie is on fire, else false
   */
  public boolean inOnFire(String id) {
    return (onFireTimes.get(id) != null);
  }

  /**
   * Method to check if the game has ended.
   * @return boolean, true if game ended.
   */
  public boolean isGameEnd() {
    return this.gameEnd;
  }

  /**
   * Sets the time that the game play state was initialized.
   * @param time, long
   */
  public void setTimeInit(long time) {
    this.initialDelayTime = time;
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

}
