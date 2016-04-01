package edu.brown.cs.altsai.game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import states.MenuState;
import states.SinglePlayerGameState;

/**
 * @author
 */
public class Main extends StateBasedGame {

  public Main() {
    super("Survival game");
  }

  public static void main(String[] arguments) {
//    System.setProperty("org.lwjgl.librarypath",
//    new File("natives").getAbsolutePath());
    try {
      AppGameContainer app = new AppGameContainer(new Main());
      app.setDisplayMode(Window.width, Window.height, false);
      app.start();
    } catch (SlickException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void initStatesList(GameContainer gc) throws SlickException {
    gc.setTargetFrameRate(60);
    gc.setAlwaysRender(true);
    gc.setMaximumLogicUpdateInterval(60);
    gc.setVSync(true);
    gc.setShowFPS(false);

    new Resources();

    this.addState(new SinglePlayerGameState());
    this.addState(new MenuState());
  }
}