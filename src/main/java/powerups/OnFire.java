package powerups;

import entities.Entity;
import game_objects.Powerup;

import java.util.List;

public class OnFire extends Powerup {

  private final int FIRE_TIME = 8000;
  private final int INDIV_FIRE = 2000;
  private List<Entity> entities;

  public OnFire(List<Powerup> p) {
    super(p);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void deactivate() {
    // TODO Auto-generated method stub

  }

}
