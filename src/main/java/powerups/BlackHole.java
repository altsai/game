package powerups;

import entities.Entity;
import entities.Player;
import entities.Zombie;
import game_objects.Powerup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.newdawn.slick.GameContainer;

import states.GamePlayState;

public class BlackHole extends Powerup {

  private final int SUCK_TIME = 3000;
  private List<Entity> entities;
  private List<Player> players;
  private Random random;
  private GamePlayState game;

  public BlackHole(List<Powerup> p, List<Entity> e, GamePlayState gps) {
    super(p);
    // TODO set image and animation
    entities = e;
    players = new ArrayList<>();
    players.add(affectedPlayer);
    game = gps;
  }

  public BlackHole(List<Powerup> p, List<Entity> e, List<Player> pl,
      GamePlayState gps) {
    super(p);
    // TODO set image and animation
    entities = e;
    players = pl;
    game = gps;
  }

  @Override
  public void update(GameContainer gc, int delta) {
    // call super.update() to check expiration time
    super.update(gc, delta);

    // check for player collision with every entity
    for (Entity e : this.entities) {
      if (e.isCollision(this)) {
        entities.remove(e);
        affectedPlayer.incrementScore();
      }
      e.update(gc, delta);
    }

    // check if black hole should be deactivated
    deactivate();
  }

  @Override
  public void activate() {
    super.activate();

    this.setX(affectedPlayer.getX());
    this.setY(affectedPlayer.getY());

    // TODO: reset image/animation

    this.game.setSpawnOn(false);

    for (Entity e : entities) {
      ((Zombie) e).setTarget(this);
    }
  }

  @Override
  public void deactivate() {
    if (this.isUsed
        && System.currentTimeMillis() - this.activationStartTime >= SUCK_TIME) {
      for (Entity e : entities) {
        // TODO: assign based on distance rather than random?
        Player target = players.get(random.nextInt(this.players.size()));
        ((Zombie) e).setTarget(target);
      }

      this.game.setSpawnOn(true);

      // kill the powerup
      kill();
    }
  }

}
