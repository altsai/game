package powerups;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.newdawn.slick.GameContainer;

import com.google.common.collect.Lists;

import edu.brown.cs.altsai.game.Resources;
import edu.brown.cs.altsai.game.Window;
import entities.Player;
import entities.Zombie;
import game_objects.Powerup;

public class Jail extends Powerup {

  private final int JAIL_RADIUS = 150;
  private final int JAIL_LIFETIME = 5000;
  private boolean bombFired;

  public Jail(ConcurrentHashMap<String, Powerup> p,
      ConcurrentHashMap<String, Zombie> z, List<Player> pl) {
    super(p);
    bombFired = false;
    image = Resources.getImage("jail");
    // TODO set image
    this.powerupIndex = Powerup.JAIL;
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
  public List<String> activate() {
    this.isUsed = true;
    this.activationStartTime = System.currentTimeMillis();

    // clear the player's powerup storage after using the powerup
    this.affectedPlayer.clearPowerupStorage();

    float x = affectedPlayer.getX();
    float y = affectedPlayer.getY();

    affectedPlayer.setBoundary(y - JAIL_RADIUS, y + JAIL_RADIUS, x
        - JAIL_RADIUS, x + JAIL_RADIUS);

    return Lists.newArrayList();
  }

  @Override
  public void deactivate() {
    long time = System.currentTimeMillis();
    if ((isUsed && time - activationStartTime >= JAIL_LIFETIME)
        || (isUsed && bombFired)) {
      affectedPlayer.setBoundary(0, Window.height, 0, Window.width);

      // kill the powerup
      kill();
    }
  }

}
