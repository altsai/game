package powerups;

import edu.brown.cs.altsai.game.Resources;
import entities.Player;
import entities.Zombie;
import game_objects.Powerup;

import java.util.List;

import org.newdawn.slick.GameContainer;

public class Jail extends Powerup {

  private final int JAIL_RADIUS = 150;
  private final int JAIL_LIFETIME = 5000;
  private boolean bombFired;

  public Jail(List<Powerup> p, List<Zombie> z, List<Player> pl) {
    super(p);
    bombFired = false;
    image = Resources.getImage("jail");
    // TODO set image
  }

  @Override
  public void update(GameContainer gc, int delta) {
    // call super.update() to check expiration time
    super.update(gc, delta);

    long lastBomb = this.affectedPlayer.getLastBombFired();

    if (this.isUsed && (lastBomb > this.activationStartTime)) {
      bombFired = true;
    }

    // check if jail should be deactivated
    deactivate();
  }

  @Override
  public void activate() {
    super.activate();

    float x = affectedPlayer.getX();
    float y = affectedPlayer.getY();

    affectedPlayer.setBoundary(y - JAIL_RADIUS, y + JAIL_RADIUS, x
        - JAIL_RADIUS, x + JAIL_RADIUS);
  }

  @Override
  public void deactivate() {
    long time = System.currentTimeMillis();
    if ((isUsed && time - activationStartTime >= JAIL_LIFETIME)
        || (isUsed && bombFired)) {
      affectedPlayer.setBoundary(0, 800, 0, 1000);

      // kill the powerup
      kill();
    }
  }

}
