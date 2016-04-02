package powerups;

import edu.brown.cs.altsai.game.Resources;
import entities.Entity;
import entities.Player;
import game_objects.Powerup;

import java.util.ArrayList;
import java.util.List;

public class Bomb extends Powerup {

  private final int EXPLOSION_RADIUS = 200;
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

    players = pl;
  }

  @Override
  public void activate() {
    super.activate();

    // TODO: if in jail

    for (int i = 0; i < entities.size(); i++) {
      if (withinRadius(entities.get(i))) {
        entities.remove(i);
        affectedPlayer.incrementScore();
        i--;
      }
    }

    for (Player p : players) {
      if (p != affectedPlayer) {
        if (!p.isInvincible()) {
          // TODO: reduce speed of other player
        }
      }
    }
  }

  /**
   * Method to check if an entity is within the explosion radius.
   *
   * First checks if the x and y coordinates are even worth considering by
   * seeing if difference between them is less than the explosionRadius. If it
   * is possibly within range, then the euclidean distance between the powerup
   * and the entity is found and we check if that distance is less than the
   * explosionRadius.
   *
   * @param e
   *          Entity, entity that is possibly affected by the explosion.
   * @return True if entity within explosion radius.
   */
  private boolean withinRadius(Entity e) {
    double distance = Math.sqrt(Math.pow(x - e.getX(), 2)
        + Math.pow(y - e.getY(), 2));

    return distance <= EXPLOSION_RADIUS;
  }

  @Override
  public void deactivate() {
    // TODO Auto-generated method stub
  }

}
