package states;

import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import edu.brown.cs.altsai.game.Window;
import highscore.HighscoreSystem;

/**
 * Defines the high score game state.
 *
 * Contains menus for high score data.
 *
 * @author bl48
 *
 */
public class HighScoreState extends BasicGameState {

  private HighscoreSystem highscoreSystem;

  public HighScoreState (HighscoreSystem highscoreSystem) {
    this.highscoreSystem = highscoreSystem;
  }

  @Override
  public void init(GameContainer gc, StateBasedGame s)
      throws SlickException {
    // TODO Auto-generated method stub

  }

  private String getScoreString(List<String[]> list, int timeFrame) {
    StringBuilder toReturn = new StringBuilder();

    if (timeFrame == 0) {
      toReturn.append("-------Current Global High Scores------\n");
    } else if (timeFrame == 1) {
      toReturn.append("-------Current Global High Scores Of The Day------\n");
    } else if (timeFrame == 2) {
      toReturn.append("-------Current Global High Scores Of The Month------\n");
    } else if (timeFrame == 3) {
      toReturn.append("-------Current Global High Scores Of The Year------\n");
    }
    for (String[] score : list) {
      toReturn.append((score[0] + ": " + score[1] + " - " + score[2] + " AT " + score[3] + "\n"));
    }

    return toReturn.toString();
  }

  private String getLocalScoreString(List<Integer> list) {
    StringBuilder toReturn = new StringBuilder();

    toReturn.append("-------Current Local High Scores------\n");
    for (Integer score : list) {
      toReturn.append(score + "\n");
    }

    return toReturn.toString();
  }

  @Override
  public void render(GameContainer gc, StateBasedGame s, Graphics g)
      throws SlickException {
    // Get global highscores and local highscores
    List<String[]> globalHighscores = highscoreSystem.getGlobalScores();
    List<String[]> globalHighscoresOfDay = highscoreSystem.getGlobalScoresOfDay();
    List<String[]> globalHighscoresOfMonth = highscoreSystem.getGlobalScoresOfMonth();
    List<String[]> globalHighscoresOfYear = highscoreSystem.getGlobalScoresOfYear();
    List<Integer> localHighscores = highscoreSystem.getLocalScores();

    // Draw the string
    g.drawString(getLocalScoreString(localHighscores) + getScoreString(globalHighscores, 0) + "\n" + getScoreString(globalHighscoresOfDay, 1) + "\n" + getScoreString(globalHighscoresOfMonth, 2) + "\n" + getScoreString(globalHighscoresOfYear, 3), 0, 0);
    g.drawString("Hit escape to go to menu", Window.width / 2, Window.height / 2);

  }

  @Override
  public void update(GameContainer gc, StateBasedGame s, int delta)
      throws SlickException {

    // go to the singleplayer game when user presses 1
    if (gc.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
      s.enterState(States.MENU);
    }
  }

  @Override
  public int getID() {
    return States.HIGH_SCORES;
  }

}
