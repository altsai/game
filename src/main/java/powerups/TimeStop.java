package powerups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.GameContainer;

import edu.brown.cs.altsai.game.Resources;
import entities.Entity;
import entities.Player;
import game_objects.Powerup;
import states.GamePlayState;

public class TimeStop extends Powerup {

  private final int FREEZE_TIME = 3000;
  private List<Entity> entities;
  private List<Player> players;

  private Map<Entity, Double> zombieSpeeds;
  private double playerSpeed;
  private GamePlayState game;

  public TimeStop(List<Powerup> p, List<Entity> e, GamePlayState gps) {
    // call the superconstructor to start timing
    super(p);

    // load bomb image and animation
    this.image = Resources.getImage("timestop");
    this.entities = e;
    this.game = gps;

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

    for (Entity e : entities) {
      zombieSpeeds.put(e, e.getSpeed());
      e.setSpeed(0);
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

      this.game.setSpawnOn(true);

      for (Entity e : entities) {
        if (zombieSpeeds.get(e) != null) {
          e.setSpeed(zombieSpeeds.get(e));
        }
      }

      for (Player p : players) {
        if (affectedPlayer != p) {
          p.setSpeed(playerSpeed);
        }
      }

      kill();
    }
  }
}
