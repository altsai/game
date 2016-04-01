package powerups;

import java.util.List;
import java.util.Random;

import org.newdawn.slick.GameContainer;

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

  public Bomb(Player p, List<Powerup> power, List<Entity> ent) {
    Random r = new Random();

    this.x = r.nextFloat() * Window.width;

    while (Math.abs(this.x - p.getX()) <= 100) {
      this.x = r.nextFloat() * Window.width;
    }

    this.y = r.nextFloat() * Window.height;

    while (Math.abs(this.y - p.getY()) <= 100) {
      this.y = r.nextFloat() * Window.height;
    }

    this.radius = 20;

    // load bomb image and animation
    this.image = Resources.getImage("invinciblePlayer");
    this.explosionRadius = 50;
    this.powerups = power;
    this.entities = ent;
  }

  @Override
  public void activate() {
    for (int i = 0; i < entities.size(); i++) {

    }
  }

  private boolean withinRadius(Entity e) {

  }

  @Override
  public void deactivate() {
    // TODO Auto-generated method stub

  }

  @Override
  public void kill() {
    powerups.remove(this);
  }

  @Override
  public void update(GameContainer gc, int delta) {
    // TODO Auto-generated method stub

  }

}
