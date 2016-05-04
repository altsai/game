package entities;

import edu.brown.cs.altsai.game.Resources;
import edu.brown.cs.altsai.game.Window;
import effects.FireEmitterCustom;
import game_objects.Powerup;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.particles.ParticleSystem;

import com.google.common.collect.Lists;

/**
 * Defines the Player object.
 *
 * Player is an instance of an Entity and also has a circular hitbox. Player
 * also implements the PlayerAction interface for player specifics.
 *
 * @author bl48
 *
 */
public class Player extends Entity implements PlayerAction {
  private String name;

  public Player(Entity other, String name) {
    super(other);
    this.name = name;
  }

  private final static int[] PLAYER1_CONTROLS = { Input.KEY_W, Input.KEY_S,
      Input.KEY_A, Input.KEY_D };
  private final static int[] PLAYER2_CONTROLS = { Input.KEY_UP, Input.KEY_DOWN,
      Input.KEY_LEFT, Input.KEY_RIGHT };

  // specific fields that players have
  private int lives;
  private Powerup powerup;
  private int score;
  public long invincibleTime;
  private boolean isPlayer1;
  private long lastBombFired;
  private long lastTimeStop;
  private long lastFire;
  private boolean canMove;
  private boolean immune;
  private ParticleSystem fireParticles;
  private FireEmitterCustom emitter;
  private Map<String, Boolean> jails;

  private static final int ANIMATION_FRAME_TIME = 100;

  private SpriteSheet spriteSheet;
  private Animation animation;

  private float lastDir;
  public static final double PLAYER_SPEED = 0.3;

  public static final float EMITTER_SIZE = 30;

  @Override
  /**
   * Initializes a player and set's starting attributes.
   */
  public void init(Entity other) {
    this.x = 500;
    this.y = 500;
    this.radius = 30;
    this.lives = 10;
    this.powerup = null;
    this.score = 0;
    this.image = Resources.getImage("player");
    this.speed = PLAYER_SPEED;
    this.top = 42;
    this.left = 12;
    this.bottom = Window.height - (this.radius / 2);
    this.right = Window.width - (this.radius / 2);
    this.isPlayer1 = true;
    this.lastBombFired = 0;
    this.lastTimeStop = 0;
    this.spriteSheet = Resources.getSprite("injuredAnimation");
    this.animation = new Animation(this.spriteSheet, ANIMATION_FRAME_TIME);
    this.animation.setPingPong(true);
    this.canMove = true;
    this.immune = false;
    this.lastDir = 0;
    this.id = UUID.randomUUID().toString();
    jails = new ConcurrentHashMap<>();

    initFire();
  }

  /**
   * Initializes the fire particle emitter for player.
   */
  private void initFire() {
    fireParticles = new ParticleSystem(Resources.getImage("particle"), 1500);
    // File xmlFile = new File("particle/fire.xml");
    try {
      // ConfigurableEmitter emitter = ParticleIO.loadEmitter(xmlFile);
      // emitter.setPosition(this.radius / 2, this.radius / 2);
      // fireParticles.addEmitter(emitter);
      emitter = new FireEmitterCustom((int) this.radius / 2,
          (int) this.radius / 2, EMITTER_SIZE);
      fireParticles.addEmitter(emitter);
      fireParticles.setBlendingMode(ParticleSystem.BLEND_ADDITIVE);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Sets the current player as player1 in the game (the host).
   *
   * @param flag
   *          Boolean, true if player1, else false.
   */
  public void setPlayer1(boolean flag) {
    this.isPlayer1 = flag;
  }

  /**
   * Returns the name of the player.
   *
   * @return String, name of the player
   */
  public String getName() {
    return this.name;
  }

  /**
   * Sets the current player's score.
   *
   * @param score
   *          Integer, score of a player.
   */
  public void setScore(int score) {
    this.score = score;
  }

  /**
   * Sets the lives of a player.
   *
   * @param lives
   *          Integer, number of lives
   */
  public void setLives(int lives) {
    this.lives = lives;
  }

  /**
   * Checks if the current player is player1.
   *
   * @return Boolean, true if player1, else false.
   */
  public boolean isPlayer1() {
    return this.isPlayer1;
  }

  /**
   * Gets the left boundary.
   *
   * @return float, left boundary
   */
  public float getLeft() {
    return this.left;
  }

  /**
   * Gets the right boundary.
   *
   * @return float, right boundary
   */
  public float getRight() {
    return this.right;
  }

  /**
   * Gets the top boundary.
   *
   * @return float, top boundary
   */
  public float getTop() {
    return this.top;
  }

  /**
   * Gets the bottom boundary.
   *
   * @return float, bottom boundary
   */
  public float getBottom() {
    return this.bottom;
  }

  /**
   * Sets the last time that a bomb is fired for the player.
   *
   * @param time
   *          Long, last time that a bomb is fired.
   */
  public void setLastBombFired(long time) {
    this.lastBombFired = time;
  }

  @Override
  public int getLives() {
    return this.lives;
  }

  @Override
  public void loseLife() {
    if (this.lives == 0) {
      return;
    } else {
      this.lives--;
      clearPowerupStorage();
      this.setState(true);
      this.invincibleTime = System.currentTimeMillis();
    }
  }

  @Override
  public void collectPowerup(Powerup p) {
    this.powerup = p;
    this.powerup.setPlayer(this);
    p.pickUp();
  }

  @Override
  public void clearPowerupStorage() {
    this.powerup = null;
  }

  @Override
  public int getScore() {
    return this.score;
  }

  @Override
  public void incrementScore() {
    this.score++;
  }

  @Override
  public List<String> usePowerup() {
    if (this.powerup == null) {
      return Lists.newArrayList();
    } else {
      switch (this.powerup.getPowerupIndex()) {
      case Powerup.BOMB:
        this.lastBombFired = System.currentTimeMillis();
        break;
      case Powerup.ON_FIRE:
        emitter.setSize(EMITTER_SIZE);
        this.lastFire = System.currentTimeMillis();
        break;
      case Powerup.TIMESTOP:
        this.lastTimeStop = System.currentTimeMillis();
        break;
      }
      List<String> output = this.powerup.activate();
      this.powerup = null;
      return output;
    }
  }

  @Override
  public void render(GameContainer gc, Graphics g) {
    super.render(gc, g);

    if (this.isInvincible()) {
      this.animation.draw(this.x, this.y, 30, 30);
    }

    if (this.isImmune()) {
      fireParticles.render(this.x, this.y);
    }
  }

  @Override
  /**
   * Update method to fetch new information about the player.
   *
   * Does not allow for action key or movement.
   * Only used for checking invincibility.
   *
   * @param gc         GameContainer, window of the game
   * @param delta      Integer, amount of time since last update
   */
  public void update(GameContainer gc, int delta) {

    // Update animation
    if (this.isInvincible()) {
      this.animation.update(delta);
    }

    if (this.isImmune()) {
      fireParticles.update(delta);
      emitter.reduceSize(.063f);
    }

    if (!immune && state
        && (System.currentTimeMillis() - this.invincibleTime > 5000)) {
      this.setState(false);
      this.image = Resources.getImage("player");
    }
  }

  /**
   * Method that moves the player in networked mode. Does not check action.
   *
   * Doesn't check action key because that should be checked in the game state.
   *
   * @param gc
   *          Game Container.
   * @param delta
   *          int, time since last update
   */
  public void updateAndControlNetworked(GameContainer gc, int delta) {
    update(gc, delta);
    move(gc.getInput(), delta, PLAYER1_CONTROLS);
  }

  /**
   * Method that moves the player and checks for action key.
   *
   * Used in single player.
   *
   * @param gc
   *          GameContainer
   * @param delta
   *          int time since last update
   */
  public void updateAndControl(GameContainer gc, int delta) {
    update(gc, delta);
    move(gc.getInput(), delta, PLAYER1_CONTROLS);

    // have the action key be checked for in the host
    checkActionKey(gc.getInput());
  }

  public void updateAndControlTwoPlayerSameScreen(GameContainer gc, int delta) {
    update(gc, delta);
    if (this.isPlayer1()) {
      move(gc.getInput(), delta, PLAYER1_CONTROLS);
    } else {
      move(gc.getInput(), delta, PLAYER2_CONTROLS);
    }

    checkActionKeyTwoPlayerSameScreen(gc.getInput());
  }

  /**
   * Method that checks if the user has pressed an action key
   *
   * @param input
   */
  private void checkActionKey(Input input) {
    if (input.isKeyPressed(Input.KEY_SPACE)) {
      usePowerup();
    }
  }

  private void checkActionKeyTwoPlayerSameScreen(Input input) {
    if (this.isPlayer1) {
      if (input.isKeyPressed(Input.KEY_LSHIFT)) {
        usePowerup();
      }
    } else {
      if (input.isKeyPressed(Input.KEY_RCONTROL)) {
        usePowerup();
      }
    }
  }

  @Override
  public void die() {
  }

  /**
   * Method to determine if the player is currently invincible
   *
   * Players are invincible if they have just spawned in.
   *
   * @return Boolean, true if invincible
   */
  public boolean isInvincible() {
    return this.state;
  }

  /**
   * Method to set the player immune after being on fire.
   */
  public void setImmune() {
    immune = true;
  }

  /**
   * Method to return the current powerup in storage.
   *
   * @return Powerup, powerup used by player.
   */
  public Powerup getCurrPowerup() {
    return this.powerup;
  }

  /**
   * Method to set the player to be not immune.
   */
  public void revert() {
    immune = false;
  }

  /**
   * Method to check if player is immune (on fire).
   *
   * @return Boolean, true if immune, else false
   */
  public boolean isImmune() {
    return immune;
  }

  /**
   * Method to determine the player's new position according to WASD input.
   *
   * @param input
   *          Input, the key pressed
   * @param delta
   *          Integer, amount fo time since last update
   * @param keys
   *          Controls. Index 1 is up, 2 down, 3 left 4 right.
   */
  private void move(Input input, int delta, int[] keys) {
    if (!canMove) {
      return;
    }

    boolean upFlag = false;
    boolean downFlag = false;
    boolean leftFlag = false;
    boolean rightFlag = false;

    float xMulti = 1.0f;
    float yMulti = 1.0f;

    if (input.isKeyDown(keys[0])) {
      yMulti = -2.0f;

      double newY = this.y - speed * delta;
      upFlag = true;

      if (newY >= this.top) {
        this.y -= speed * delta;
      }
    } else if (input.isKeyDown(keys[1])) {
      yMulti = 2.0f;

      double newY = this.y + speed * delta;
      downFlag = true;

      if (newY <= this.bottom - this.image.getHeight()) {
        this.y += speed * delta;
      }
    }
    if (input.isKeyDown(keys[2])) {
      xMulti = 3f;

      double newX = this.x - speed * delta;
      leftFlag = true;

      if (newX >= this.left) {
        this.x -= speed * delta;
      }
    } else if (input.isKeyDown(keys[3])) {
      xMulti = -3f;

      double newX = this.x + speed * delta;
      rightFlag = true;

      if (newX <= this.right - this.image.getWidth()) {
        this.x += speed * delta;
      }
    }

    emitter.setMultipliers(xMulti, yMulti);

    if (upFlag && rightFlag) {
      lastDir = 45;
    } else if (upFlag && leftFlag) {
      lastDir = 135;
    } else if (upFlag) {
      lastDir = 90;
    } else if (downFlag && rightFlag) {
      lastDir = 315;
    } else if (downFlag && leftFlag) {
      lastDir = 225;
    } else if (downFlag) {
      lastDir = 270;
    } else if (rightFlag) {
      lastDir = 0;
    } else if (leftFlag) {
      lastDir = 180;
    }
  }

  /**
   * Sets the last direction that the player was facing.
   *
   * @param dir
   *          float, angle in degrees
   */
  public void setDirection(float dir) {
    this.lastDir = dir;
  }

  @Override
  public String toString() {
    return this.name;
  }

  /**
   * Gets the last time that a bomb was fired by player.
   *
   * @return Long, last time that bomb was fired.
   */
  public long getLastBombFired() {
    return lastBombFired;
  }

  /**
   * Gets the last time that a timestop was used by player.
   *
   * @return Long, last time that timestop was used.
   */
  public long getLastTimeStop() {
    return lastTimeStop;
  }

  /**
   * gets the last time that a fire powerup was used.
   *
   * @return Long, last time.
   */
  public long getLastFire() {
    return lastFire;
  }

  /**
   * Gets the last direction that the player was facing.
   *
   * @return float, direction in angle degrees.
   */
  public float getLastDir() {
    return lastDir;
  }

  public void removeJail(String id) {
    jails.remove(id);
  }

  public void addJail(String id) {
    jails.put(id, true);
  }

  public boolean isJailed() {
    for (String s : jails.keySet()) {
      if (jails.get(s)) {
        return true;
      }
    }
    return false;
  }
}
