package powerups;

import java.util.List;
import java.util.Map;

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

  private Map<String, Player> players;

  public Jail(Map<String, Powerup> p,
      Map<String, Zombie> z, Map<String, Player> pl) {
    super(p);
    bombFired = false;
    image = Resources.getImage("jail");
    players = pl;
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

    activationx = affectedPlayer.getX();
    activationy = affectedPlayer.getY();

    // get the other player in the game
    Player other = otherPlayer(this.affectedPlayer);

    // check that there is another player, set it to the boundary
    if (other != null) {
      other.setBoundary(y - JAIL_RADIUS, y + JAIL_RADIUS, x
          - JAIL_RADIUS, x + JAIL_RADIUS);
    }

    return Lists.newArrayList();
  }

  @Override
  public void deactivate() {
    long time = System.currentTimeMillis();
    if ((isUsed && time - activationStartTime >= JAIL_LIFETIME)
        || (isUsed && bombFired)) {

      Player other = otherPlayer(this.affectedPlayer);

      if (other != null) {
        other.setBoundary(0, Window.height, 0, Window.width);
      }

      // kill the powerup
      kill();
    }
  }


  /**
   * Method that returns the other player (not affected player in the game).
   *
   * @param thisPlayer   The affectedPlayer, player that picked up the jail
   * @return             Player, other player to be jailed
   */
  private Player otherPlayer(Player thisPlayer) {
    for (Player p : this.players.values()) {
      if (p != this.affectedPlayer) {
        return p;
      }
    }

    return null;
  }

}
