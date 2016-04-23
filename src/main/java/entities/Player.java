package entities;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SpriteSheet;

import edu.brown.cs.altsai.game.Resources;
import edu.brown.cs.altsai.game.Window;
import game_objects.Powerup;
import powerups.Bomb;

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
  private long invincibleTime;
  private boolean isPlayer1;
  private boolean isSingle;
  private long lastBombFired;
  private boolean canMove;
  private boolean immune;

  private static final int ANIMATION_FRAME_TIME = 100;

  private SpriteSheet spriteSheet;
  private Animation animation;

  @Override
  /**
   * Initializes a player and set's starting attributes.
   */
  public void init(Entity other) {
    this.x = 500;
    this.y = 500;
    this.radius = 30;
    this.lives = 2;
    this.powerup = null;
    this.score = 0;
    this.image = Resources.getImage("player");
    this.speed = 0.3;
    this.top = 0;
    this.left = 0;
    this.bottom = Window.height;
    this.right = Window.width;
    this.isSingle = true;
    this.isPlayer1 = true;
    this.lastBombFired = 0;
    this.spriteSheet = Resources.getSprite("injuredAnimation");
    this.animation = new Animation(this.spriteSheet, ANIMATION_FRAME_TIME);
    this.animation.setPingPong(true);
    this.canMove = true;
    this.immune = false;
  }

  public void setPlayer1(boolean flag) {
    this.isPlayer1 = flag;
    if (flag == false) {
      this.isSingle = false;
    }
  }

  public boolean isPlayer1() {
    return this.isPlayer1;
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
    if (!this.isInvincible()) {
      this.powerup = p;
      this.powerup.setPlayer(this);
      p.pickUp();
    }
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

  public Powerup getCurrPowerup() {
    return this.powerup;
  }

  @Override
  public void usePowerup() {
    if (this.powerup != null) {
      this.powerup.activate();
      this.powerup = null;
      if (this.powerup instanceof Bomb) {
        this.lastBombFired = System.currentTimeMillis();
      }
    }
  }

  @Override
  public void render(GameContainer gc, Graphics g) {
    super.render(gc, g);

    if (this.isInvincible()) {
      this.animation.draw(this.x, this.y, 30, 30);
    }
  }

  @Override
  /**
   * Update method to fetch new information about the player.
   *
   * @param gc         GameContainer, window of the game
   * @param delta      Integer, amount of time since last update
   */
  public void update(GameContainer gc, int delta) {
    Input input = gc.getInput();

    // Update animation
    if (this.isInvincible()) {
      this.animation.update(delta);
    }

    if (!immune && state
        && (System.currentTimeMillis() - this.invincibleTime > 5000)) {
      this.setState(false);
      this.image = Resources.getImage("player");
    }

    // move the player according to input and delta.
    if (this.isPlayer1) {
      move(input, delta, PLAYER1_CONTROLS);
    } else {
      move(input, delta, PLAYER2_CONTROLS);
    }

    checkActionKey(input);
  }

  /**
   * Method that checks if the user has pressed an action key
   *
   * @param input
   */
  private void checkActionKey(Input input) {
    if (this.isSingle) {
      if (input.isKeyPressed(Input.KEY_SPACE)) {
        usePowerup();
      }
    } else {
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
  }

  @Override
  public void die() {
    // TODO Auto-generated method stub

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

  public void setImmune() {
    setImage(Resources.getImage("player2"));
    immune = true;
  }

  public void revert() {
    setImage(Resources.getImage("player"));
    immune = false;
  }

  public boolean isImmune() {
    return immune;
  }

  @Override
  public double getSpeed() {
    return this.speed;
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

    if (input.isKeyDown(keys[0])) {
      double newY = this.y - speed * delta;

      if (newY >= this.top) {
        this.y -= speed * delta;
      }
    } else if (input.isKeyDown(keys[1])) {
      double newY = this.y + speed * delta;
      if (newY <= this.bottom - this.image.getHeight()) {
        this.y += speed * delta;
      }
    }
    if (input.isKeyDown(keys[2])) {
      double newX = this.x - speed * delta;
      if (newX >= this.left) {
        this.x -= speed * delta;
      }
    } else if (input.isKeyDown(keys[3])) {
      double newX = this.x + speed * delta;
      if (newX <= this.right - this.image.getWidth()) {
        this.x += speed * delta;
      }
    }
  }

  @Override
  public String toString() {
    return this.name;
  }

  public long getLastBombFired() {
    return lastBombFired;
  }

  public void setCanMove(boolean b) {
    canMove = b;
  }
}
