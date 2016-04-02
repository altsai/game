package powerups;

import edu.brown.cs.altsai.game.Resources;
import entities.Entity;
import entities.Player;
import game_objects.Powerup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.GameContainer;

public class TimeStop extends Powerup {

  private final int FREEZE_TIME = 3000;
  private List<Entity> entities;
  private List<Player> players;

  private Map<Entity, Double> zombieSpeeds;
  private double playerSpeed;

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
  public void update(GameContainer gc, int delta) {
    // call super.update() to check expiration time
    super.update(gc, delta);

    if (isUsed
        && (System.currentTimeMillis() - activationStartTime) >= FREEZE_TIME) {
      deactivate();
    }
  }

  @Override
  public void activate() {
    // TODO: set to prevent spawning of new zombie
    super.activate();

    zombieSpeeds = new HashMap<>();
    playerSpeed = 0;

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
  }
}
