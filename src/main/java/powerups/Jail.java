package powerups;

import edu.brown.cs.altsai.game.Resources;
import edu.brown.cs.altsai.game.Window;
import entities.Player;
import entities.Zombie;
import game_objects.Powerup;

import java.util.List;
import java.util.Map;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

import com.google.common.collect.Lists;

/**
 * Representation of jail to confine other player to.
 *
 * @author Alison
 *
 */
public class Jail extends Powerup {
  /**
   * Radius of jail confines.
   */
  public static final int JAIL_RADIUS = 230;

  /**
   * How long jail lasts.
   */
  private final int JAIL_LIFETIME = 5000;

  /**
   * Whether a bomb has been fired.
   */
  private boolean bombFired;

  /**
   * Whether the other player is already jailed.
   */
  private boolean jailAlready;

  /**
   * Players in the game.
   */
  private Map<String, Player> players;

  /**
   * Jail constructor.
   * 
   * @param p
   *          powerups in the game
   * @param z
   *          zombies in the game
   * @param pl
   *          players in the game
   */
  public Jail(Map<String, Powerup> p, Map<String, Zombie> z,
      Map<String, Player> pl) {
    super(p);
    bombFired = false;
    image = Resources.getImage("jail");
    players = pl;
    this.powerupIndex = Powerup.JAIL;
  }

  @Override
  public void render(GameContainer gc, Graphics g) {
    super.render(gc, g);
    float jail_diam = JAIL_RADIUS * 2;

    if ((activationStartTime != 0) && jailAlready) {
      g.draw(new Rectangle(other.getLeft(), other.getTop(), jail_diam,
          jail_diam));
    } else if (activationStartTime != 0) {
      float upperLeftX = otherx - JAIL_RADIUS;
      float upperLeftY = othery - JAIL_RADIUS;
      float lowerRightX = upperLeftX + jail_diam;
      float lowerRightY = upperLeftY + jail_diam;

      if ((upperLeftX < 10) && (upperLeftY < 40)) {
        g.draw(new Rectangle(11, 41, jail_diam, jail_diam));
      } else if ((lowerRightX >= Window.width - 10)
          && (lowerRightY >= Window.height - 10)) {
        g.draw(new Rectangle(Window.width - 11 - jail_diam, Window.height - 11
            - jail_diam, jail_diam, jail_diam));
      } else if ((lowerRightX >= Window.width - 10) && (upperLeftY < 40)) { // NEW
        g.draw(new Rectangle(Window.width - 11 - jail_diam, 41, jail_diam,
            jail_diam));
      } else if ((upperLeftX < 10) && (lowerRightY >= Window.height - 10)) { // NEW
        g.draw(new Rectangle(11, Window.height - 11 - jail_diam, jail_diam,
            jail_diam));
      } else if (upperLeftX < 10) {
        g.draw(new Rectangle(11, upperLeftY, jail_diam, jail_diam));
      } else if (upperLeftY < 40) {
        g.draw(new Rectangle(upperLeftX, 41, jail_diam, jail_diam));
      } else if (lowerRightX > Window.width - 10) {
        g.draw(new Rectangle(Window.width - 11 - jail_diam, upperLeftY,
            jail_diam, jail_diam));
      } else if (lowerRightY > Window.height - 10) {
        g.draw(new Rectangle(upperLeftX, Window.height - 11 - jail_diam,
            jail_diam, jail_diam));
      } else {
        g.draw(new Rectangle(upperLeftX, upperLeftY, jail_diam, jail_diam));
      }
    }
  }

  @Override
  public void update(GameContainer gc, int delta) {
    // call super.update() to check expiration time
    super.update(gc, delta);

    if (this.isUsed) {
      long lastBomb = this.affectedPlayer.getLastBombFired();

      if (this.isUsed && (lastBomb > this.activationStartTime)) {
        bombFired = true;
      }
    }

    // check if jail should be deactivated
    deactivate();
  }

  @Override
  public List<String> activate() {
    other = otherPlayer(affectedPlayer);
    this.isUsed = true;
    jailAlready = other.isJailed();
    this.activationStartTime = System.currentTimeMillis();
    otherx = other.getX();
    othery = other.getY();
    other.addJail(this.id);

    // clear the player's powerup storage after using the powerup
    this.affectedPlayer.clearPowerupStorage();

    // check that there is another player, set it to the boundary
    if ((other != null) && !jailAlready) {
      float jail_diam = JAIL_RADIUS * 2;
      float upperLeftX = otherx - JAIL_RADIUS;
      float upperLeftY = othery - JAIL_RADIUS;
      float lowerRightX = upperLeftX + jail_diam;
      float lowerRightY = upperLeftY + jail_diam;

      if ((upperLeftX < 10) && (upperLeftY < 40)) {
        upperLeftX = 11;
        upperLeftY = 41;
        lowerRightX = upperLeftX + jail_diam;
        lowerRightY = upperLeftY + jail_diam;
      } else if ((lowerRightX >= Window.width - 10)
          && (lowerRightY >= Window.height - 10)) {
        upperLeftX = Window.width - 11 - jail_diam;
        upperLeftY = Window.height - 11 - jail_diam;
        lowerRightX = upperLeftX + jail_diam;
        lowerRightY = upperLeftY + jail_diam;
      } else if ((lowerRightX >= Window.width - 10) && (upperLeftY < 40)) { // NEW
        upperLeftX = Window.width - 11 - jail_diam;
        upperLeftY = 41;
        lowerRightX = upperLeftX + jail_diam;
        lowerRightY = upperLeftY + jail_diam;
      } else if ((upperLeftX < 10) && (lowerRightY >= Window.height - 10)) { // NEW
        upperLeftX = 11;
        upperLeftY = Window.height - 11 - jail_diam;
        lowerRightX = upperLeftX + jail_diam;
        lowerRightY = upperLeftY + jail_diam;
      } else if (upperLeftX < 10) {
        upperLeftX = 11;
        lowerRightX = upperLeftX + jail_diam;
        lowerRightY = upperLeftY + jail_diam;
      } else if (upperLeftY < 40) {
        upperLeftY = 41;
        lowerRightX = upperLeftX + jail_diam;
        lowerRightY = upperLeftY + jail_diam;
      } else if (lowerRightX > Window.width - 10) {
        upperLeftX = Window.width - 11 - jail_diam;
        lowerRightX = upperLeftX + jail_diam;
        lowerRightY = upperLeftY + jail_diam;
      } else if (lowerRightY > Window.height - 10) {
        upperLeftY = Window.height - 11 - jail_diam;
        lowerRightX = upperLeftX + jail_diam;
        lowerRightY = upperLeftY + jail_diam;
      }

      other.setBoundary(upperLeftY, lowerRightY, upperLeftX, lowerRightX);
    }

    return Lists.newArrayList();
  }

  @Override
  public void deactivate() {
    long time = System.currentTimeMillis();
    if ((isUsed && time - activationStartTime >= JAIL_LIFETIME)
        || (isUsed && bombFired)) {

      if (other != null) {
        other.removeJail(this.id);
        if (!other.isJailed()) {
          other.setBoundary(42, Window.height - (this.radius / 2), 12,
              Window.width - (this.radius / 2));
        }
      }

      // kill the powerup
      kill();
    }
  }

  /**
   * Method that returns the other player (not affected player in the game).
   *
   * @param thisPlayer
   *          The affectedPlayer, player that picked up the jail
   * @return Player, other player to be jailed
   */
  private Player otherPlayer(Player thisPlayer) {
    for (Player p : this.players.values()) {
      if (!p.getName().equals(thisPlayer.getName())) {
        return p;
      }
    }

    return null;
  }

}
