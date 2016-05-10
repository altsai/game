package entities;

import edu.brown.cs.altsai.game.Window;
import game_objects.Circle;

import java.util.Map;

import org.newdawn.slick.GameContainer;

/**
 * Zombie that determines others' orientation in a formation.
 *
 * @author Alison
 *
 */
public class ZombieFormationHead extends Zombie {
  /**
   * The angle of travel of the zombie.
   */
  protected double directionAngle;

  /**
   * Whether the zombie is entity-target-independent.
   */
  private boolean independent;

  /**
   * x coordinate of target spot.
   */
  private double xTarg;

  /**
   * y coordinate of target spot.
   */
  private double yTarg;

  /**
   * Whether the head is right-moving.
   */
  private boolean right;

  /**
   * Constructor for entity-target-dependent formation head.
   *
   * @param other
   *          the target
   * @param players
   *          the players in the game
   */
  public ZombieFormationHead(Entity other, Map<String, Player> players) {
    super(other, players);
    independent = false;
  }

  /**
   * Constructor for static point oriented head.
   *
   * @param other
   *          a given target
   * @param players
   *          the players in the game
   * @param x
   *          the x position to move to
   * @param y
   *          the y position to move to
   */
  public ZombieFormationHead(Entity other, Map<String, Player> players,
      double x, double y) {
    super(other, players);
    independent = true;
    xTarg = x;
    yTarg = y;
    right = true;
  }

  @Override
  public void update(GameContainer gc, int delta) {
    if (independent) {
      moveToPoint(delta);
      return;
    }

    Circle closest = this.player;
    if (this.player instanceof Player) {
      double dist = Double.MAX_VALUE;
      for (Player p : this.allPlayers.values()) {
        if (this.distTo(p) < dist) {
          dist = this.distTo(p);
          closest = p;
        }
      }

      this.player = closest;
    }

    double thisX = this.x - this.getRadius() / 2;
    double thisY = this.x - this.getRadius() / 2;
    double playerX = closest.getX() - closest.getRadius() / 2;
    double playerY = closest.getY() - closest.getRadius() / 2;
    directionAngle = Math.toDegrees(Math
        .atan2(playerY - thisY, playerX - thisX));

    if (directionAngle < 0) {
      directionAngle += 360;
    }

    followPlayer(delta);

    if (this.isOnFire()) {
      fireParticles.update(delta);
    }
  }

  /**
   * Moves the head left or right, depending on current movement.
   *
   * @param delta
   *          update frequency
   */
  public void moveToPoint(int delta) {
    if (right) {
      if ((this.x + this.speed) >= Window.width - 30) {
        right = false;
      }
      this.moveTo((float) (this.x + this.speed * .75), this.y);
    } else {
      if ((this.x - this.speed) <= 10) {
        right = true;
      }
      this.moveTo((float) (this.x - this.speed * .75), this.y);
    }

    if (this.isOnFire()) {
      fireParticles.update(delta);
    }
  }

  /**
   * Returns the angle the leader is traveling in.
   *
   * @return directionAngle
   */
  public double getLAngle() {
    return directionAngle;
  }

}
