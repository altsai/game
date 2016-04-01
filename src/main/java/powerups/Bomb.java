package powerups;

import edu.brown.cs.altsai.game.Resources;
import edu.brown.cs.altsai.game.Window;
import entities.Entity;
import entities.Player;
import game_objects.Circle;
import game_objects.Powerup;

import java.util.List;
import java.util.Random;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class Bomb extends Circle implements Powerup {
  private Image image;
  private int explosionRadius;
  private List<Powerup> powerups;
  private List<Entity> entities;

  public Bomb(Player p, List<Powerup> power, List<Entity> ent) {
    super();

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
    this.image = Resources.getImage("invinciblePlayer");
    explosionRadius = 50;
    powerups = power;
    entities = ent;
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
  public void render(GameContainer gc, Graphics g) {
    if (this.image != null) {
      image.draw(this.x, this.y, this.radius, this.radius);
    }
  }
}
