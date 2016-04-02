package powerups;

import edu.brown.cs.altsai.game.Resources;
import game_objects.Powerup;

import java.util.List;

import org.newdawn.slick.GameContainer;

/**
 * Defines the speed powerup that boosts a Players speed by +20%.
 *
 * @author bl48
 *
 */
public class Speed extends Powerup {

  // keeps track of how long the speed increase has been
  private long speedIncreaseStartTime;

  /**
   * Constructor for a Speed Powerup.
   *
   * Takes in a list of powerups in the game (so that we can update the
   * powerup). Also calls the super constructor which will start timers
   * necessary for keeping track of expiration and usage.
   *
   * @param powerups
   *          List of powerups in the game currently
   */
  public Speed(List<Powerup> p) {
    // call the super constructor first to start timers
    super(p);

    this.image = Resources.getImage("speed");
  }

  @Override
  /**
   * Update method unique to speed.
   *
   * In this case, speed doesn't have any animations or other effects,
   * so there isn't anything else that the Speed.update() method does.
   * This is just an example for other powerups that may have unique effects.
   *
   */
  public void update(GameContainer gc, int delta) {
    // call super.update() to check expiration time
    super.update(gc, delta);

    // if other powerups have effects, they should go here...
    deactivate();
  }

  @Override
  /**
   * All subclasses of Powerup should override activate with the specific
   * powerup's effects.
   *
   * Note that super.activate() is called first.
   */
  public void activate() {
    // call super.activate() to mark the powerup as activated.
    super.activate();

    // Speed specific method to increase player speed
    this.speedIncreaseStartTime = System.currentTimeMillis();
    this.affectedPlayer.setSpeed(this.affectedPlayer.getSpeed() * 1.2);
  }

  @Override
  public void deactivate() {
    if (this.isUsed) {
      if (System.currentTimeMillis() - this.speedIncreaseStartTime > 8000) {
        this.affectedPlayer.setSpeed(this.affectedPlayer.getSpeed() / 1.2);
      }
    }
  }

}
