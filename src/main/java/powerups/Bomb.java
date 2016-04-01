package powerups;

import java.util.List;
import java.util.Random;

import edu.brown.cs.altsai.game.Resources;
import edu.brown.cs.altsai.game.Window;
import entities.Entity;
import entities.Player;
import game_objects.Powerup;

public class Bomb extends Powerup {

  private int explosionRadius;
  private List<Powerup> powerups;
  private List<Entity> entities;
  // animation field

  public Bomb(List<Powerup> power, List<Entity> ent) {
    // call the superconstructor to start timing
    super();

    Random r = new Random();

    this.x = r.nextFloat() * Window.width;

    this.y = r.nextFloat() * Window.height;

    this.radius = 20;

    // load bomb image and animation
    this.image = Resources.getImage("bomb");
    this.explosionRadius = 50;
    this.powerups = power;
    this.entities = ent;
  }

  @Override
  public void activate() {
    super.activate();
    for (int i = 0; i < entities.size(); i++) {

    }
  }

  /**
   * Method to check if an entity is within the explosion radius.
   *
   * First checks if the x and y coordinates are even worth considering
   * by seeing if difference between them is less than the explosionRadius.
   * If it is possibly within range, then the euclidean distance between
   * the powerup and the entity is found and we check if that distance is
   * less than the explosionRadius.
   *
   * @param e      Entity, entity that is possibly affected by the explosion.
   * @return       True if entity within explosion radius.
   */
  private boolean withinRadius(Entity e) {

    // if entity is an invincible player, player is not in radius of bomb ever.
    if (e instanceof Player) {
      Player other = (Player) e;
      if (other.isInvincible()) {
        return false;
      }
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

    // kill after deactivating
    if (this.isUsed) {
      // do something to deactivate after use
      // then kill
      kill();
    }
  }

  @Override
  public void kill() {
    powerups.remove(this);
  }

}
