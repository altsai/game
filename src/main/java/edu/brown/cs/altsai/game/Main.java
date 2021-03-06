package edu.brown.cs.altsai.game;

import highscore.HighscoreSystem;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;

import javax.swing.JOptionPane;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import states.AboutState;
import states.ClientEndGame;
import states.HighScoreState;
import states.HostEndGame;
import states.MenuState;
import states.SinglePlayerEndGameState;
import states.SinglePlayerGameState;
import states.TwoPlayerClient;
import states.TwoPlayerEndGame;
import states.TwoPlayerGameState;
import states.TwoPlayerHost;
import states.TwoPlayerStartServer;

/**
 * @author
 */
public class Main extends StateBasedGame {

  /**
   * Connection to database.
   */
  private Connection conn;

  /**
   * High score system for the game.
   */
  private HighscoreSystem highscoreSystem;

  /**
   * AppContainer.
   */
  private static AppGameContainer app;

  /**
   * Instantiates Main menu.
   *
   * @throws NumberFormatException
   *           upon number format exception
   * @throws IOException
   *           upon IO error
   * @throws ParseException
   *           upon parse error
   */
  public Main() throws NumberFormatException, IOException, ParseException {
    super("WASD To Live");

    try {
      conn = instantiateConnection();
      this.highscoreSystem = new HighscoreSystem("highscores.txt", 10, conn);
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, "Unable to connect to the internet. "
          + "The game is now running in offline mode.\nTo run in online mode, "
          + "fix your connection and restart the game.", "Connection Error",
          JOptionPane.WARNING_MESSAGE);
      this.highscoreSystem = new HighscoreSystem("highscores.txt", 10);
    }
  }

  /**
   * Instantiates a connection to the database.
   *
   * @return a connection to the database
   * @throws SQLException
   *           upon sql exception
   * @throws ClassNotFoundException
   *           upon class not found
   */
  private Connection instantiateConnection() throws SQLException,
      ClassNotFoundException {
    String url = "jdbc:mysql://cs32db.csox7dghpjsn.us-east-1.rds.amazonaws.com:3306/";
    String userName = "cs32user";
    String password = "cs32pass";
    String dbName = "cs32db";
    String driver = "com.mysql.jdbc.Driver";
    Class.forName(driver);
    return DriverManager.getConnection(url + dbName, userName, password);
  }

  public static void main(String[] arguments) {
    // System.setProperty("org.lwjgl.librarypath",
    // new File("natives").getAbsolutePath());
    try {
      app = new AppGameContainer(new Main());
      app.setDisplayMode(Window.width, Window.height, false);
      app.start();
    } catch (SlickException | IOException | NumberFormatException
        | ParseException e) {
      e.printStackTrace();
    }
  }

  /**
   * Sets parameters of window.
   *
   * @param widthNew
   *          width of window
   * @param heightNew
   *          height of window
   */
  public static void setWidthHeight(int widthNew, int heightNew) {
    Window.setWidthHeight(widthNew, heightNew);
    try {
      app.setDisplayMode(Window.width, Window.height, false);
    } catch (NumberFormatException | SlickException e) {
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
    TwoPlayerStartServer twoPlayerStartServer = new TwoPlayerStartServer(conn);
    TwoPlayerHost twoPlayerHost = new TwoPlayerHost(twoPlayerStartServer);
    TwoPlayerClient twoPlayerClient = new TwoPlayerClient(twoPlayerStartServer);

    TwoPlayerGameState twoPlayerSameScreen = new TwoPlayerGameState();

    this.addState(new MenuState(highscoreSystem));
    this.addState(singlePlayer);
    this.addState(new SinglePlayerEndGameState(singlePlayer, highscoreSystem));
    this.addState(new HighScoreState(highscoreSystem));
    if (highscoreSystem.isGlobal()) {
      this.addState(twoPlayerHost);
      this.addState(twoPlayerClient);
      this.addState(new HostEndGame(twoPlayerHost));
      this.addState(new ClientEndGame(twoPlayerClient));
      this.addState(twoPlayerStartServer);
    }
    this.addState(twoPlayerSameScreen);
    this.addState(new TwoPlayerEndGame(twoPlayerSameScreen));
    this.addState(new AboutState());
  }
}