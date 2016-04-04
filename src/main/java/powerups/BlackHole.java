package powerups;

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
  private List<Zombie> zombies;
  private List<Player> players;
  private Random random;
  private GamePlayState game;

  public BlackHole(List<Powerup> p, List<Zombie> z, GamePlayState gps) {
    super(p);
    // TODO set image and animation
    zombies = z;
    players = new ArrayList<>();
    players.add(affectedPlayer);
    game = gps;
  }

  public BlackHole(List<Powerup> p, List<Zombie> z, List<Player> pl,
      GamePlayState gps) {
    super(p);
    // TODO set image and animation
    zombies = z;
    players = pl;
    game = gps;
  }

  @Override
  public void update(GameContainer gc, int delta) {
    // call super.update() to check expiration time
    super.update(gc, delta);

    // check for player collision with every entity
    for (Zombie z : this.zombies) {
      if (z.isCollision(this)) {
        zombies.remove(z);
        affectedPlayer.incrementScore();
      }
      z.update(gc, delta);
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

    for (Zombie z : zombies) {
      z.setTarget(this);
    }
  }

  @Override
  public void deactivate() {
    if (this.isUsed
        && System.currentTimeMillis() - this.activationStartTime >= SUCK_TIME) {
      for (Zombie z : zombies) {
        // TODO: assign based on distance rather than random?
        Player target = players.get(random.nextInt(this.players.size()));
        z.setTarget(target);
      }

      this.game.setSpawnOn(true);

      // kill the powerup
      kill();
    }
  }

}
