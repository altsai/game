package entities;

import java.util.List;
import java.util.UUID;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

import com.google.common.collect.Lists;

import edu.brown.cs.altsai.game.Resources;
import edu.brown.cs.altsai.game.Window;
import game_objects.Powerup;

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

  private final static int[] PLAYER1_CONTROLS = {Input.KEY_W, Input.KEY_S, Input.KEY_A, Input.KEY_D};
  private final static int[] PLAYER2_CONTROLS = {Input.KEY_UP, Input.KEY_DOWN, Input.KEY_LEFT, Input.KEY_RIGHT};

  // specific fields that players have
  private int lives;
  private Powerup powerup;
  private int score;
  public long invincibleTime;
  private boolean isPlayer1;
  private boolean isSingle;
  public static final double PLAYER_SPEED = 0.3;

  @Override
  /**
   * Initializes a player and set's starting attributes.
   */
  public void init(Entity other) {
    this.x = 500;
    this.y = 500;
    this.radius = 20;
    this.lives = 30;
    this.powerup = null;
    this.score = 0;
    this.image = Resources.getImage("player");
    this.speed = PLAYER_SPEED;
    this.top = 0;
    this.left = 0;
    this.bottom = Window.height;
    this.right = Window.width;
    this.isSingle = true;
    this.isPlayer1 = true;
    this.id = UUID.randomUUID().toString();
  }

  public void setPlayer1(boolean flag) {
    this.isPlayer1 = flag;
    if (flag == false) {
      this.isSingle = false;
    }
  }

  public String getName() {
    return this.name;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public void setLives(int lives) {
    this.lives = lives;
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
      this.image = Resources.getImage("invinciblePlayer");
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
    if (this.powerup != null) {
      List<String> output =  this.powerup.activate();
      this.powerup = null;
      return output;
    } else {
      return Lists.newArrayList();
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
    Input input = gc.getInput();

    if ((System.currentTimeMillis() - this.invincibleTime > 5000)) {
      this.setState(false);
      this.image = Resources.getImage("player");
    }


//    // move the player according to input and delta.
//    // both p1 and p2 have same controls since on separate windows
//    move(input, delta, PLAYER1_CONTROLS);
//
////    if (this.isPlayer1) {
////      move(input, delta, PLAYER1_CONTROLS);
////    } else {
////      move(input, delta, PLAYER2_CONTROLS);
////    }
//
//    checkActionKey(input);
  }

  /**
   * Method that moves the player in networked mode. Does not check action.
   *
   * Doesn't check action key because that should be checked in the game state.
   *
   * @param gc      Game Container.
   * @param delta   int, time since last update
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
   * @param gc      GameContainer
   * @param delta   int time since last update
   */
  public void updateAndControl(GameContainer gc, int delta) {
    update(gc, delta);
    move(gc.getInput(), delta, PLAYER1_CONTROLS);

    // have the action key be checked for in the host
    checkActionKey(gc.getInput());
  }

  /**
   * Method that checks if the user has pressed an action key
   * @param input
   */
  private void checkActionKey(Input input) {
    if (input.isKeyPressed(Input.KEY_SPACE)) {
      usePowerup();
    }

//    if (this.isSingle) {
//      if (input.isKeyPressed(Input.KEY_SPACE)) {
//        usePowerup();
//      }
//    } else {
//      if (this.isPlayer1) {
//        if (input.isKeyPressed(Input.KEY_LSHIFT)) {
//          usePowerup();
//        }
//      } else {
//        if (input.isKeyPressed(Input.KEY_RCONTROL)) {
//          usePowerup();
//        }
//      }
//    }
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

}
