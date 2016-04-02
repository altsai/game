package powerups;

import edu.brown.cs.altsai.game.Resources;
import entities.Entity;
import entities.Player;
import game_objects.Powerup;

import java.util.ArrayList;
import java.util.List;

public class TimeStop extends Powerup {

  private final int FREEZE_TIME = 3000;
  private List<Entity> entities;
  private List<Player> players;

  public TimeStop(List<Powerup> p, List<Entity> e) {
    // call the superconstructor to start timing
    super(p);

    // load bomb image and animation
    this.image = Resources.getImage("timestop");
    this.entities = e;

    players = new ArrayList<>();
  }

  public TimeStop(List<Powerup> p, List<Entity> e, List<Player> pl) {
    // call the superconstructor to start timing
    super(p);

    // load bomb image and animation
    this.image = Resources.getImage("timestop");
    this.entities = e;

    players = pl;
  }

  @Override
  public void activate() {
    super.activate();
  }

  @Override
  public void deactivate() {
    // TODO Auto-generated method stub
  }
}
