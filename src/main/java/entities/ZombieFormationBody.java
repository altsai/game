package entities;

import java.util.Map;
import java.util.UUID;

import org.newdawn.slick.GameContainer;

import edu.brown.cs.altsai.game.Resources;

public class ZombieFormationBody extends Zombie {
  private Entity leader;
  private double angle;
  private int offset;

  public ZombieFormationBody(Entity other, Map<String, Player> players,
      int off, double ang) {
    super(other, players);
    offset = off;
    angle = Math.toRadians(ang % 360);
    double leadX = leader.getX() + leader.getRadius() / 2;
    double leadY = leader.getY() + leader.getRadius() / 2;

    double multX = 1;
    double multY = 1;

    if ((ang > 90) && (ang < 270)) {
      multX = -1;
    }

    if ((ang > 180) && (ang < 360)) {
      multY = -1;
    } else if ((ang > 90) && (ang < 180)) {
      multY = -1;
    }

    x = (float) (leadX + multX * (leader.getRadius() * (offset + .5))
        * Math.cos(angle))
        - (this.getRadius() / 2);
    y = (float) (leadY + multY * (leader.getRadius() * (offset + .5))
        * Math.sin(angle))
        - (this.getRadius() / 2);
  }

  @Override
  public void init(Entity other) {
    this.leader = other;
    this.setRadius(20);
    this.radius = 20;
    this.image = Resources.getImage("zombie");
    this.speed = leader.getSpeed();

    this.state = false;
    this.id = UUID.randomUUID().toString();

    this.initial_speed = this.getSpeed();

    initFire();
  }

  @Override
  public void update(GameContainer gc, int delta) {
    followLeader(delta);

    if (this.isOnFire()) {
      fireParticles.update(delta);
    }
  }

  private void followLeader(int delta) {
    if (leader.getSpeed() == 0) {
      return;
    }

    double leaderAngle = ((ZombieFormationHead) leader).getLAngle();
    double ang = Math.toRadians((angle + leaderAngle) % 360);

    double leadX = leader.getX() + leader.getRadius() / 2;
    double leadY = leader.getY() + leader.getRadius() / 2;

    double multX = 1;
    double multY = 1;

    if ((ang > 90) && (ang < 270)) {
      multX = -1;
    }

    if ((ang > 1800) && (ang < 360)) {
      multY = -1;
    }

    float moveX = (float) (leadX + multX * (leader.getRadius() * (offset + .5))
        * Math.cos(ang))
        - (this.getRadius() / 2);
    float moveY = (float) (leadY + multY * (leader.getRadius() * (offset + .5))
        * Math.sin(ang))
        - (this.getRadius() / 2);
    moveTo(moveX, moveY);
  }
}
