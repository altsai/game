package powerups;

import edu.brown.cs.altsai.game.Resources;
import edu.brown.cs.altsai.game.Window;
import entities.Player;
import game_objects.Circle;
import game_objects.Powerup;

import java.util.Random;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class Bomb extends Circle implements Powerup {
  private Image image;

  public Bomb(Player p) {
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
  }

  @Override
  public void activate() {
    // TODO Auto-generated method stub

  }

  @Override
  public void deactivate() {
    // TODO Auto-generated method stub

  }

  @Override
  public void kill() {
    image = null;
  }

  @Override
  public void render(GameContainer gc, Graphics g) {
    if (this.image != null) {
      image.draw(this.x, this.y, this.radius, this.radius);
    }
  }
}
