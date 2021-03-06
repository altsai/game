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
  /**
   * Name of player.
   */
  private String name;

  /**
   * Constructor for player.
   *
   * @param other
   *          for the superconstructor; typically null
   * @param name
   *          the name of the player
   */
  public Player(Entity other, String name) {
    super(other);
    this.name = name;
  }

  /**
   * Input keys for player 1.
   */
  private final static int[] PLAYER1_CONTROLS = { Input.KEY_W, Input.KEY_S,
      Input.KEY_A, Input.KEY_D };

  /**
   * Input keys for player 2.
   */
  private final static int[] PLAYER2_CONTROLS = { Input.KEY_UP, Input.KEY_DOWN,
      Input.KEY_LEFT, Input.KEY_RIGHT };

  // specific fields that players have
  /**
   * Number of lives player has.
   */
  private int lives;

  /**
   * Powerup player is holding.
   */
  private Powerup powerup;

  /**
   * Player's score.
   */
  private int score;

  /**
   * Time they are invincible.
   */
  public long invincibleTime;

  /**
   * Whether they are player 1.
   */
  private boolean isPlayer1;

  /**
   * Timestamp for last bomb set off.
   */
  private long lastBombFired;

  /**
   * Timestamp for last timestop used.
   */
  private long lastTimeStop;

  /**
   * Timestamp for last fire powerup used.
   */
  private long lastFire;

  /**
   * Whether the player can move.
   */
  private boolean canMove;

  /**
   * Whether the player is immune (on fire).
   */
  private boolean immune;

  /**
   * Particle effects for player on fire powerup.
   */
  private ParticleSystem fireParticles;
  private FireEmitterCustom emitter;

  /**
   * Map of player ids to whether they are jailed.
   */
  private Map<String, Boolean> jails;

  private static final int ANIMATION_FRAME_TIME = 150;

  /**
   * Animations for player.
   */
  private SpriteSheet spriteSheetPlayer1;
  private Animation animationPlayer1;
  private SpriteSheet spriteSheetPlayer2;
  private Animation animationPlayer2;

  /**
   * Last direction moved.
   */
  private float lastDir;

  /**
   * Player speed.
   */
  public static final double PLAYER_SPEED = 0.3;

  public static final float EMITTER_SIZE = 30;

  /**
   * Advanced statistics for end game display.
   */
  private double distanceTraveled;
  private float lastX;
  private float lastY;
  private int bombKills;
  private int blackholeKills;
  private int laserKills;
  private int fireKills;

  @Override
  /**
   * Initializes a player and set's starting attributes.
   */
  public void init(Entity other) {
    this.x = 500;
    this.y = 500;
    this.lastX = 500;
    this.lastY = 500;
    this.radius = 30;
    this.lives = 2;
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
    this.spriteSheetPlayer1 = Resources.getSprite("injuredAnimationPlayer1");
    this.animationPlayer1 = new Animation(this.spriteSheetPlayer1,
        ANIMATION_FRAME_TIME);
    this.animationPlayer1.setPingPong(true);
    this.spriteSheetPlayer2 = Resources.getSprite("injuredAnimationPlayer2");
    this.animationPlayer2 = new Animation(this.spriteSheetPlayer2,
        ANIMATION_FRAME_TIME);
    this.animationPlayer2.setPingPong(true);
    this.canMove = true;
    this.immune = false;
    this.lastDir = 0;
    this.id = UUID.randomUUID().toString();
    jails = new ConcurrentHashMap<>();

    initFire();
  }

  /**
   * Refreshes player boundaries.
   */
  public void refreshBoundaries() {
    this.bottom = Window.height - (this.radius / 2);
    this.right = Window.width - (this.radius / 2);

  }

  /**
   * Refreshes player position.
   */
  public void refreshXY() {
    this.setX(Window.width / 3);
    this.setY(Window.height / 2);
  }

  /**
   * Returns how many zombies were killed by bombs.
   *
   * @return int number of zombies killed by bombs.
   */
  public int getBombKills() {
    return bombKills;
  }

  /**
   * Returns kills by black holes.
   *
   * @return number killed by black holes
   */
  public int getBlackholeKills() {
    return blackholeKills;
  }

  /**
   * Sets kills by black holes.
   */
  public void setBlackholeKills(int blackholeKills) {
    this.blackholeKills = blackholeKills;
  }

  /**
   * Sets kills by bombs.
   */
  public void setBombKills(int bombKills) {
    this.bombKills = bombKills;
  }

  /**
   * Returns kills by laser.
   *
   * @return number killed by laser
   */
  public int getLaserKills() {
    return laserKills;
  }

  /**
   * Sets kills by black holes.
   */
  public void setLaserKills(int laserKills) {
    this.laserKills = laserKills;
  }

  /**
   * Returns kills by fire.
   *
   * @return number killed by fire
   */
  public int getFireKills() {
    return fireKills;
  }

  /**
   * Sets kills by fire.
   */
  public void setFireKills(int fireKills) {
    this.fireKills = fireKills;
  }

  /**
   * Initializes the fire particle emitter for player.
   */
  private void initFire() {
    fireParticles = new ParticleSystem(Resources.getImage("particle"), 1500);
    try {
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
      if (this.isPlayer1) {
        this.animationPlayer1.draw(this.x, this.y, 30, 30);
      } else {
        this.animationPlayer2.draw(this.x, this.y, 30, 30);
      }
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
      if (this.isPlayer1) {
        this.animationPlayer1.update(delta);
      } else {
        this.animationPlayer2.update(delta);
      }
    }

    if (this.isImmune()) {
      fireParticles.update(delta);
      emitter.reduceSize(.063f);
    }

    if (state && (System.currentTimeMillis() - this.invincibleTime > 5000)) {
      this.setState(false);
      if (this.isPlayer1) {
        this.image = Resources.getImage("player");
      } else {
        this.image = Resources.getImage("player2");
      }
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

  /**
   * Checks whether player has attempted to use powerup.
   *
   * @param input
   *          the input key
   */
  private void checkActionKeyTwoPlayerSameScreen(Input input) {
    if (this.isPlayer1) {
      if (input.isKeyPressed(Input.KEY_LSHIFT)) {
        usePowerup();
      }
    } else {
      if (input.isKeyPressed(Input.KEY_RSHIFT)) {
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

    double movedPixels = movedPixels(this.x, this.y);
    this.lastX = this.x;
    this.lastY = this.y;
    this.distanceTraveled += movedPixels * Window.feetPerPixel;
  }

  /**
   * Returns the distance traveled by the player in feet.
   *
   * Takes a pixel size and using the screen DPI and resolution, calculates the
   * feet traveled.
   *
   * @return double Feet traveled by the player.
   */
  public double getDistTraveled() {
    return Math.round(this.distanceTraveled * 100.0) / 100.0;
  }

  private double movedPixels(float x, float y) {
    double squaredDist = Math.pow(x - this.lastX, 2)
        + Math.pow(y - this.lastY, 2);
    return Math.sqrt(squaredDist);
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (lastBombFired ^ (lastBombFired >>> 32));
    result = prime * result + lives;
    return result;
  }

  @Override
  /**
   * Player equality to check if two players are equal.
   *
   * Used mainly in networking to check if a player should
   * be updated because it is lacking vital information.
   *
   * @param obj     Object
   */
  public boolean equals(Object obj) {
    if (obj instanceof Player) {
      Player other = (Player) obj;
      return (this.lastBombFired == other.lastBombFired && this.id == other.id
          && this.speed == other.speed && this.top == other.top
          && this.left == other.left && this.right == other.right && this.bottom == other.bottom);
    } else {
      return false;
    }
  }

  /**
   * Removes jail from player.
   *
   * @param id
   *          the id of player to unjail
   */
  public void removeJail(String id) {
    jails.remove(id);
  }

  /**
   * Adds jail to player.
   *
   * @param id
   *          the id of the player to jail
   */
  public void addJail(String id) {
    jails.put(id, true);
  }

  /**
   * Returns whether the player is jailed.
   * 
   * @return whether the palyer is jailed.
   */
  public boolean isJailed() {
    for (String s : jails.keySet()) {
      if (jails.get(s)) {
        return true;
      }
    }
    return false;
  }
}
