package entities;

import java.util.Map;

public class ZombieFormationHead extends Zombie {
  protected double directionAngle;

  public ZombieFormationHead(Entity other, Map<String, Player> players) {
    super(other, players);
    // TODO Auto-generated constructor stub
    System.out.println(this.x + ", " + this.y);
  }

}
