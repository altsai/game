package states;

import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

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
    nameField = new TextField(gc, gc.getDefaultFont(), 100, 200, 300, 25);
  }

  @Override
  public void render(GameContainer gc, StateBasedGame s, Graphics g)
      throws SlickException {
    // Add local score, and global score if applicable
    if (!checkedHighscore) {
      checkedHighscore = true;
      try {
        if (highscoreSystem.addLocalScore(spgs.getScore())) {
          bestHighscore = true;
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    if (bestHighscore) {
      g.drawString("New best score! Enter the name you would like to be associated\nwith this score on the global highscores board below:", 100, 150);
      nameField.setAcceptingInput(true);
      nameField.render(gc, g);
    }

    g.drawString("You Died, Score was: " + this.spgs.getScore(), 100, 100);
    g.drawString("Hit esc to go back to Menu", 100, 300);
  }

  @Override
  public void update(GameContainer gc, StateBasedGame s, int delta)
      throws SlickException {

    // go to the singleplayer game when user presses 1
    if (gc.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
      s.enterState(States.MENU);
    }

    // Add the entered name to the global highscores
    if (gc.getInput().isKeyPressed(Input.KEY_ENTER) && nameField.hasFocus() && bestHighscore) {
      try {
        if (highscoreSystem.addGlobalScore(nameField.getText(), spgs.getScore())) {
          s.enterState(States.MENU);
        } else {
          JOptionPane.showMessageDialog(null, "Oops! Someone in the global highscores board already has that name!");
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
