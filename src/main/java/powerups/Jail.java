package powerups;

import java.util.List;
import java.util.Map;

import game_objects.Powerup;

public class Jail extends Powerup {

  public Jail(Map<String, Powerup> p) {
    super(p);
    this.powerupIndex = Powerup.JAIL;
    // TODO Auto-generated constructor stub
  }

  @Override
  public void deactivate() {
    // TODO Auto-generated method stub

  }

  @Override
  public List<String> activate() {
    // TODO Auto-generated method stub
    return null;
  }

}
