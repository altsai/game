package states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import edu.brown.cs.altsai.game.Resources;
import edu.brown.cs.altsai.game.Window;

/**
 * Class that controls the ending screen for offline multiplayer
 * @author bl48
 *
 */
public class TwoPlayerEndGame extends BasicGameState {
  //takes in the SinglePlayerGameState just played
  private GamePlayState gps;

  private static final int BUTTON_WIDTH = 180;
  private static final int BUTTON_HEIGHT = 68;

  /**
   * Constructor for a SinglePlayerEndGameState
   * @param singlePlayerGameState     single player state that just finished
   */
  public TwoPlayerEndGame(GamePlayState gps) {
    this.gps = gps;
  }

  @Override
  public void init(GameContainer gc, StateBasedGame s)
      throws SlickException {
    gc.getInput().clearControlPressedRecord();
    gc.getInput().clearKeyPressedRecord();
    gc.getInput().clearMousePressedRecord();
  }

  @Override
  public void render(GameContainer gc, StateBasedGame s, Graphics g)
      throws SlickException {
    g.drawImage(Resources.getImage("background"), 0, 0);

    if (this.gps.getLoser().equals("1")) {
      if (Window.width == 1390) {
        Resources.getImage("gameOver1").draw(0, 0);
      } else if (Window.width == 1132) {
        Resources.getImage("gameOver1Small").draw(0, 0);
      }
    } else if (this.gps.getLoser().equals("0")) {
      if (Window.width == 1390) {
        Resources.getImage("gameOver2").draw(0, 0);
      } else if (Window.width == 1132) {
        Resources.getImage("gameOver2Small").draw(0, 0);
      }
    }

    // Main menu button
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
      s.getState(States.MENU).init(gc, s);
      s.enterState(States.MENU);
    }
  }

  @Override
  public int getID() {
    return States.TWO_PLAYER_END_GAME;
  }
}
