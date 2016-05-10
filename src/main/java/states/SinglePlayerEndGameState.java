package states;

import java.awt.Font;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import edu.brown.cs.altsai.game.Resources;
import edu.brown.cs.altsai.game.Window;
import highscore.HighscoreSystem;

/**
 * Defines the state when the single player game has ended.
 *
 * This state is reached when the single player dies.
 *
 * @author bl48
 *
 */
public class SinglePlayerEndGameState extends BasicGameState {
  private SinglePlayerGameState spgs;
  private HighscoreSystem highscoreSystem;
  private TextField nameField;
  private boolean checkedHighscore;
  private boolean bestHighscore;
  private boolean instantiatedTextField = false;

  private TrueTypeFont headerFont;
  private TrueTypeFont textFont;
  private TrueTypeFont scoreFont;

  private static final int BUTTON_WIDTH = 180;
  private static final int BUTTON_HEIGHT = 68;

  /**
   * Constructor for a SinglePlayerEndGameState
   * @param singlePlayerGameState     single player state that just finished
   */
  public SinglePlayerEndGameState(SinglePlayerGameState singlePlayerGameState, HighscoreSystem highscoreSystem) {
    this.spgs = singlePlayerGameState;
    this.highscoreSystem = highscoreSystem;
  }

  @Override
  public void init(GameContainer gc, StateBasedGame s)
      throws SlickException {
    Font font = new Font("Arial", Font.BOLD, 50);
    headerFont = new TrueTypeFont(font, true);
    Font font2 = new Font("Arial", Font.BOLD, 20);
    textFont = new TrueTypeFont(font2, true);
    Font font3 = new Font("Arial", Font.PLAIN, 20);
    scoreFont = new TrueTypeFont(font3, true);

    if (!instantiatedTextField) {
      instantiateTextField(gc);
      instantiatedTextField = true;
    }
    nameField.setText("");
    nameField.setAcceptingInput(false);
    this.checkedHighscore = false;
    this.bestHighscore = false;
  }

  private void instantiateTextField(GameContainer gc) {
    Font font3 = new Font("Arial", Font.PLAIN, 18);
    TrueTypeFont fieldFont = new TrueTypeFont(font3, true);
    nameField = new TextField(gc, fieldFont, (Window.width - 300) / 2, 20 + headerFont.getLineHeight() + 10 + textFont.getLineHeight() + 10 + textFont.getLineHeight() + 10, 300, textFont.getLineHeight());
  }

  @Override
  public void render(GameContainer gc, StateBasedGame s, Graphics g)
      throws SlickException {
    g.drawImage(Resources.getImage("background"), 0, 0);

    // Draw header
    float currHeight = 20;
    headerFont.drawString((Window.width - headerFont.getWidth("You Died!")) / 2, currHeight, "You Died!", Color.white);
    currHeight += (headerFont.getLineHeight() + 10);
    textFont.drawString((Window.width - textFont.getWidth("Score: ")
        - scoreFont.getWidth(Integer.toString(this.spgs.getScore()))) / 2, currHeight, "Score: "
        , Color.white);
    scoreFont.drawString((Window.width - textFont.getWidth("Score: ")
        - scoreFont.getWidth(Integer.toString(this.spgs.getScore()))) / 2 + textFont.getWidth("Score: ")
        , currHeight, Integer.toString(this.spgs.getScore()), Color.red);

    // Add local score, and global score if applicable
    if (!checkedHighscore) {
      checkedHighscore = true;
      try {
        if (highscoreSystem.addLocalScore(spgs.getScore(), spgs.getElapsedTime())) {
          bestHighscore = true;
        }
      } catch (IOException | ParseException e) {
        e.printStackTrace();
      }
    }

    if (bestHighscore && highscoreSystem.isGlobal()) {
      currHeight += textFont.getLineHeight() + 10;
      String toDraw = "New best score! Enter the name you would like to be associated"
          + " with this score on the global highscores board below:";
      textFont.drawString((Window.width - textFont.getWidth(toDraw)) / 2, currHeight, toDraw, Color.white);
      nameField.setAcceptingInput(true);
      nameField.render(gc, g);
      nameField.setBackgroundColor(Color.white);
      nameField.setBorderColor(Color.black);
      nameField.setTextColor(Color.black);
    }

    Resources.getImage("statsbox").draw(0, 0, Window.width, Window.height);

    int bombKills = this.spgs.players.get(this.spgs.playerID).getBombKills();
    int blackholeKills = this.spgs.players.get(this.spgs.playerID).getBlackholeKills();
    int laserKills = this.spgs.players.get(this.spgs.playerID).getLaserKills();
    int fireKills = this.spgs.players.get(this.spgs.playerID).getFireKills();
    double distMoved = this.spgs.players.get(this.spgs.playerID).getDistTraveled();

    Resources.getDefaultFont(12).drawString(530, 380, "Zombies Vacuumed:  " + blackholeKills);
    Resources.getDefaultFont(12).drawString(530, 460, "Zombies Bombed:    " + bombKills);
    Resources.getDefaultFont(12).drawString(530, 540, "Zombies Cooked:    " + fireKills);
    Resources.getDefaultFont(12).drawString(530, 620, "Zombies Lasered:   " + laserKills);
    Resources.getDefaultFont(12).drawString(530, 700, "Distance Traveled: " + distMoved + " feet");

    Resources.getImage("buttonMainMenu").draw(20, 20, BUTTON_WIDTH, BUTTON_HEIGHT);
  }

  @Override
  public void update(GameContainer gc, StateBasedGame s, int delta)
      throws SlickException {
    // Get x and y mouse position coordinates
    int posX = gc.getInput().getMouseX();
    int posY = gc.getInput().getMouseY();

    // Back to main menu
    if (gc.getInput().isKeyPressed(Input.KEY_ESCAPE) || (gc.getInput().isMouseButtonDown(0) && posX >= 20 && posX <= 20 + BUTTON_WIDTH && posY >= 20 && posY <= 20 + BUTTON_HEIGHT)) {
      s.enterState(States.MENU);
    }

    // Add the entered name to the global highscores
    if (gc.getInput().isKeyPressed(Input.KEY_ENTER) && nameField.hasFocus() && bestHighscore) {
      try {
        if (highscoreSystem.addGlobalScore(nameField.getText(), spgs.getScore(), spgs.getElapsedTime())) {
          s.enterState(States.MENU);
        } else {
          Window.showMessage("Name Taken", "Oops! Someone in the global highscores board already has that name!");
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public int getID() {
    return States.SINGLE_PLAYER_END_GAME;
  }
}
