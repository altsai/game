package edu.brown.cs.altsai.game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

/**
 * @author
 */
public class Main extends BasicGame {
  public Main() {
    super("Wizard game");
  }

  public static void main(String[] arguments) {
    // System.setProperty("org.lwjgl.librarypath",
    // new File("natives").getAbsolutePath());
    try {
      AppGameContainer app = new AppGameContainer(new Main());
      app.setDisplayMode(500, 400, false);
      app.start();
    } catch (SlickException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void init(GameContainer container) throws SlickException {
  }

  @Override
  public void update(GameContainer container, int delta) throws SlickException {
  }

  @Override
  public void render(GameContainer container, Graphics g) throws SlickException {
  }
}