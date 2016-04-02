package powerups;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.brown.cs.altsai.game.Resources;
import entities.Entity;
import entities.Player;
import game_objects.Powerup;

public class Bomb extends Powerup {

  private static final int EXPLOSION_RADIUS = 200;
  private List<Entity> entities;
  private List<Player> players;

  // TODO: animation field

  public Bomb(List<Powerup> p, List<Entity> e) {
    // call the superconstructor to start timing
    super(p);

    // load bomb image and animation
    this.image = Resources.getImage("bomb");
    this.entities = e;

    players = new ArrayList<>();
  }

  public Bomb(List<Powerup> p, List<Entity> e, List<Player> pl) {
    // call the superconstructor to start timing
    super(p);

    // load bomb image and animation
    this.image = Resources.getImage("bomb");
    this.entities = e;

    this.players = pl;
  }

  @Override
  public void activate() {
    super.activate();

    // TODO: if in jail

    Iterator<Entity> iter = this.entities.iterator();
    while (iter.hasNext()) {
      if (withinRadius(iter.next())) {
        iter.remove();
        this.affectedPlayer.incrementScore();
      }
    }

    // check if any other players have been hit for multiplayer
    for (Player p : players) {
      if (p != affectedPlayer) {
        // if the other player isn't invincible and is in blast radius
        if (!p.isInvincible() && withinRadius(p)) {
          // other player's speed is -10%
          p.setSpeed(p.getSpeed() * 0.9);
        }
      }
    }

    // always check for deactivation of speed decrease on each update
    deactivate();
  }

  /**
   * Method to check if an entity is within the explosion radius.
   *
   * Checks euclidean distance between the powerup
   * and the entity. Then checks if that distance is less than the
   * explosionRadius.
   *
   * @param e
   *          Entity, entity that is possibly affected by the explosion.
   * @return True if entity within explosion radius.
   */
  private boolean withinRadius(Entity e) {
    return this.distTo(e) <= EXPLOSION_RADIUS;
  }

  @Override
  public void deactivate() {

    // reset all other player's speeds back after 5 seconds
    if (System.currentTimeMillis() - this.activationStartTime > 5000) {
      for (Player p : this.players) {
        p.setSpeed(p.getSpeed() / 0.9);
      }
      kill();
    }
  }

}
