package edu.brown.cs.altsai.game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import states.ClientEndGame;
import states.HighScoreState;
import states.HostEndGame;
import states.MenuState;
import states.SinglePlayerEndGameState;
import states.SinglePlayerGameState;
import states.TwoPlayerClient;
import states.TwoPlayerHost;
import states.TwoPlayerStartServer;

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

    SinglePlayerGameState singlePlayer = new SinglePlayerGameState();
    TwoPlayerHost twoPlayerHost = new TwoPlayerHost();
    TwoPlayerClient twoPlayerClient = new TwoPlayerClient();


    this.addState(singlePlayer);
    this.addState(new MenuState());
    this.addState(new SinglePlayerEndGameState(singlePlayer));
    this.addState(new HighScoreState());
    this.addState(twoPlayerHost);
    this.addState(twoPlayerClient);
    this.addState(new HostEndGame(twoPlayerHost));
    this.addState(new ClientEndGame(twoPlayerClient));
    this.addState(new TwoPlayerStartServer());
  }
}