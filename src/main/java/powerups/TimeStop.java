package powerups;

import edu.brown.cs.altsai.game.Resources;
import entities.Player;
import entities.Zombie;
import game_objects.Powerup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.GameContainer;

import states.GamePlayState;

public class TimeStop extends Powerup {

  private final int FREEZE_TIME = 3000;
  private List<Zombie> zombies;
  private List<Player> players;

  private Map<Zombie, Double> zombieSpeeds;
  private double playerSpeed;
  private GamePlayState game;

  public TimeStop(List<Powerup> p, List<Zombie> z, GamePlayState gps) {
    // call the superconstructor to start timing
    super(p);

    // load bomb image and animation
    this.image = Resources.getImage("timestop");
    this.zombies = z;
    this.game = gps;

    this.players = new ArrayList<>();
  }

  public TimeStop(List<Powerup> p, List<Zombie> z, List<Player> pl,
      GamePlayState gps) {
    // call the superconstructor to start timing
    super(p);

    // load bomb image and animation
    this.image = Resources.getImage("timestop");
    this.zombies = z;
    this.game = gps;

    this.players = pl;
  }

  @Override
  public void update(GameContainer gc, int delta) {
    // call super.update() to check expiration time
    super.update(gc, delta);

    // check if timestop should be deactivated
    deactivate();
  }

  @Override
  public void activate() {
    super.activate();

    zombieSpeeds = new HashMap<>();
    playerSpeed = 0;

    // prevent spawning of new zombies
    this.game.setSpawnOn(false);

    for (Zombie z : zombies) {
      zombieSpeeds.put(z, z.getSpeed());
      z.setSpeed(0);
    }

    for (Player p : players) {
      if (affectedPlayer != p) {
        playerSpeed = p.getSpeed();
        p.setSpeed(0);
      }
    }
  }

  @Override
  public void deactivate() {

    if (this.isUsed
        && System.currentTimeMillis() - this.activationStartTime >= FREEZE_TIME) {

      // tell the game to start spawning again
      this.game.setSpawnOn(true);

      for (Zombie z : zombies) {
        if (zombieSpeeds.get(z) != null) {
          z.setSpeed(zombieSpeeds.get(z));
        }
      }

      for (Player p : players) {
        if (affectedPlayer != p) {
          p.setSpeed(playerSpeed);
        }
      }

      // kill the powerup
      kill();
    }
  }
}
