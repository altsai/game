package edu.brown.cs.altsai.game;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import highscore.HighscoreSystem;
import states.HighScoreState;
import states.MenuState;
import states.SinglePlayerEndGameState;
import states.SinglePlayerGameState;
import states.TwoPlayerEndGame;
import states.TwoPlayerGameState;

/**
 * @author
 */
public class Main extends StateBasedGame {

  private HighscoreSystem highscoreSystem;

  public Main() throws ClassNotFoundException, SQLException, IOException {
    super("Survival game");

    Connection conn = instantiateConnection();
    this.highscoreSystem = new HighscoreSystem("highscores.txt", 10, conn);
  }

  private Connection instantiateConnection() throws SQLException, ClassNotFoundException {
    String url = "jdbc:mysql://cs32db.csox7dghpjsn.us-east-1.rds.amazonaws.com:3306/";
    String userName = "cs32user";
    String password = "cs32pass";
    String dbName = "cs32db";
    String driver = "com.mysql.jdbc.Driver";
    Class.forName(driver);
    return DriverManager.getConnection(url + dbName, userName, password);
  }

  public static void main(String[] arguments) {
    //    System.setProperty("org.lwjgl.librarypath",
    //    new File("natives").getAbsolutePath());
    try {
      AppGameContainer app = new AppGameContainer(new Main());
      app.setDisplayMode(Window.width, Window.height, false);
      app.start();
    } catch (SlickException | ClassNotFoundException | SQLException | IOException e) {
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
    TwoPlayerGameState twoPlayer = new TwoPlayerGameState();

    this.addState(singlePlayer);
    this.addState(new MenuState());
    this.addState(new SinglePlayerEndGameState(singlePlayer, highscoreSystem));
    this.addState(new HighScoreState(highscoreSystem));
    this.addState(twoPlayer);
    this.addState(new TwoPlayerEndGame(twoPlayer));
  }
}