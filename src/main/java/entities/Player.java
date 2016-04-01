package entities;

import edu.brown.cs.altsai.game.Resources;
import edu.brown.cs.altsai.game.Window;
import game_objects.Powerup;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

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

  public Player(Entity other) {
    super(other);
  }

  // specific fields that players have
  private int lives;
  private Powerup powerup;
  private int score;
  private long invincibleTime;
  private Color color;

  @Override
  /**
   * Initializes a player and set's starting attributes.
   */
  public void init(Entity other) {
    this.x = 500;
    this.y = 500;
    this.radius = 20;
    this.lives = 3;
    this.powerup = null;
    this.score = 0;
    this.image = Resources.getImage("player");
    this.speed = 0.3;
    this.top = 0;
    this.left = 0;
    this.bottom = Window.height;
    this.right = Window.width;
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
    p.kill();
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
  public void usePowerup() {
    this.powerup.activate();
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

    if (System.currentTimeMillis() - this.invincibleTime > 5000) {
      this.setState(false);
      this.image = Resources.getImage("player");
    }

    // move the player according to input and delta.
    move(input, delta);

    // use powerup on press of action key
    if (input.isKeyPressed(Input.KEY_SPACE)) {
      usePowerup();
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
   */
  private void move(Input input, int delta) {
    if (input.isKeyDown(Input.KEY_W)) {
      double newY = this.y - speed * delta;

      if (newY >= this.top) {
        this.y -= speed * delta;
      }
    } else if (input.isKeyDown(Input.KEY_S)) {
      double newY = this.y + speed * delta;
      if (newY <= this.bottom - this.image.getHeight()) {
        this.y += speed * delta;
      }
    }
    if (input.isKeyDown(Input.KEY_A)) {
      double newX = this.x - speed * delta;
      if (newX >= this.left) {
        this.x -= speed * delta;
      }
    } else if (input.isKeyDown(Input.KEY_D)) {
      double newX = this.x + speed * delta;
      if (newX <= this.right - this.image.getWidth()) {
        this.x += speed * delta;
      }
    }
  }

}
