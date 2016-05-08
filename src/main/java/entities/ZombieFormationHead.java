package entities;

import game_objects.Circle;

import java.util.Map;

import org.newdawn.slick.GameContainer;

public class ZombieFormationHead extends Zombie {
  protected double directionAngle;
  private boolean independent;
  private double xTarg;
  private double yTarg;

  public ZombieFormationHead(Entity other, Map<String, Player> players) {
    super(other, players);
    independent = false;
  }

  public ZombieFormationHead(Entity other, Map<String, Player> players,
      boolean i, double x, double y) {
    super(other, players);
    independent = i;
    xTarg = x;
    yTarg = y;
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

  public void moveToPoint(int delta) {
    double thisX = this.x - this.getRadius() / 2;
    double thisY = this.x - this.getRadius() / 2;
    directionAngle = Math.toDegrees(Math.atan2(yTarg - thisY, xTarg - thisX));

    if (directionAngle < 0) {
      directionAngle += 360;
    }

    if (this.isOnFire()) {
      fireParticles.update(delta);
    }
  }

  public double getLAngle() {
    return directionAngle;
  }

}
