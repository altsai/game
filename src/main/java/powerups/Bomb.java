package powerups;

import edu.brown.cs.altsai.game.Resources;
import edu.brown.cs.altsai.game.Window;
import entities.Entity;
import entities.Player;
import game_objects.Powerup;

import java.util.List;
import java.util.Random;

public class Bomb extends Powerup {

  private int explosionRadius;
  private List<Entity> entities;

  // animation field

  public Bomb(List<Powerup> p, List<Entity> e) {
    // call the superconstructor to start timing
    super(p);

    Random r = new Random();

    this.x = r.nextFloat() * Window.width;

    this.y = r.nextFloat() * Window.height;

    this.radius = 20;

    // load bomb image and animation
    this.image = Resources.getImage("bomb");
    this.explosionRadius = 50;
    this.entities = e;
  }

  @Override
  public void activate() {
    super.activate();
    for (int i = 0; i < entities.size(); i++) {
      if (withinRadius(entities.get(i))) {
        if (entities.get(i) instanceof Player) {
          if (!(entities.get(i) == affectedPlayer)) {
            // TODO: if player2 is in radius
            // reduce speed, break jail
          }
        } else {
          entities.remove(i);
          affectedPlayer.incrementScore();
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

    // if entity is an invincible player, player is not in radius of bomb ever.
    if (e instanceof Player) {
      Player other = (Player) e;
      if (other.isInvincible()) {
        return false;
      }
    }

    // player who activated bomb
    if (e == affectedPlayer) {
      return false;
    }

    if (Math.abs(this.x - e.getX()) > (e.getRadius() + this.explosionRadius)
        || Math.abs(this.y - e.getY()) > (e.getRadius() + this.explosionRadius)) {
      return false;
    } else {
      return distTo(e) <= this.explosionRadius;
    }
  }

  @Override
  public void deactivate() {
    // TODO Auto-generated method stub
  }

}
