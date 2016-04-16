package powerups;

import entities.Player;
import entities.Zombie;
import game_objects.Powerup;

import java.util.List;

public class Jail extends Powerup {

  private final int JAIL_RADIUS = 150;
  private final int JAIL_LIFETIME = 5000;

  public Jail(List<Powerup> p, List<Zombie> z, List<Player> pl) {
    super(p);
    // TODO set image
  }

  @Override
  public void deactivate() {
    // TODO Auto-generated method stub

  }

}
