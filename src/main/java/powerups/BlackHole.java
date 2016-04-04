package powerups;

import entities.Entity;
import entities.Player;
import entities.Zombie;
import game_objects.Powerup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.newdawn.slick.GameContainer;

public class BlackHole extends Powerup {

  private final int SUCK_TIME = 3000;
  private List<Entity> entities;
  private List<Player> players;
  private Random random;

  public BlackHole(List<Powerup> p, List<Entity> e) {
    super(p);
    // TODO set image and animation
    entities = e;
    players = new ArrayList<>();
    players.add(affectedPlayer);
  }

  public BlackHole(List<Powerup> p, List<Entity> e, List<Player> pl) {
    super(p);
    // TODO set image and animation
    entities = e;
    players = pl;
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
      ((Zombie) e).setTarget(this);
    }
  }

  @Override
  public void deactivate() {
    if (this.isUsed
        && System.currentTimeMillis() - this.activationStartTime >= SUCK_TIME) {
      for (Entity e : entities) {
        Player target = players.get(random.nextInt(this.players.size()));
        ((Zombie) e).setTarget(target);
      }

      // kill the powerup
      kill();
    }
  }

}
