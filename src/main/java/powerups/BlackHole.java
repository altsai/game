package powerups;

import entities.BlackHolePlayer;
import entities.Entity;
import entities.Player;
import game_objects.Powerup;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;

public class BlackHole extends Powerup {

  private final int SUCK_TIME = 3000;
  private BlackHolePlayer blackhole;
  private List<Player> players;
  private List<Entity> entities;

  public BlackHole(List<Powerup> p, List<Entity> e) {
    super(p);
    // TODO set image and animation
    players = new ArrayList<Player>();
    entities = e;
  }

  public BlackHole(List<Powerup> p, List<Entity> e, List<Player> pl) {
    super(p);
    // TODO set image and animation
    players = pl;
    entities = e;
  }

  @Override
  public void update(GameContainer gc, int delta) {
    // call super.update() to check expiration time
    super.update(gc, delta);

    // check if black hole should be deactivated
    deactivate();
  }

  @Override
  public void activate() {
    super.activate();

    for (Entity e : entities) {

    }
  }

  @Override
  public void deactivate() {
    if (this.isUsed
        && System.currentTimeMillis() - this.activationStartTime >= SUCK_TIME) {

      players.remove(blackhole);

      // kill the powerup
      kill();
    }
  }

}
