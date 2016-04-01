package powerups;

import edu.brown.cs.altsai.game.Resources;
import edu.brown.cs.altsai.game.Window;
import game_objects.Circle;
import game_objects.Powerup;

import java.util.Random;

import org.newdawn.slick.Image;

public class Bomb extends Circle implements Powerup {
  private Image image;

  public Bomb() {
    super();

    Random r = new Random();

    this.x = r.nextFloat() * Window.width;

    this.y = r.nextFloat() * Window.height;

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
    // TODO Auto-generated method stub

  }
}
