package states;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import edu.brown.cs.altsai.game.Resources;
import edu.brown.cs.altsai.game.Window;

/**
 * End game screen for when the client game ends.
 */
public class ClientEndGame extends BasicGameState {
  //takes in the SinglePlayerGameState just played
  private GamePlayState gps;

  private static final int BUTTON_WIDTH = 180;
  private static final int BUTTON_HEIGHT = 68;

  private TrueTypeFont headerFont;
  private TrueTypeFont textFont;

  /**
   * Constructor for a SinglePlayerEndGameState
   * @param singlePlayerGameState     single player state that just finished
   */
  public ClientEndGame(GamePlayState gps) {
    this.gps = gps;
  }

  @Override
  public void init(GameContainer gc, StateBasedGame s)
      throws SlickException {
    // TODO Auto-generated method stub
    Font font = new Font("Arial", Font.BOLD, 50);
    headerFont = new TrueTypeFont(font, true);
    Font font2 = new Font("Arial", Font.BOLD, 20);
    textFont = new TrueTypeFont(font2, true);

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
    } else {
      headerFont.drawString((Window.width - headerFont.getWidth("Connection Lost")) / 2, 20, "Connection Lost", Color.white);
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
    return States.CLIENT_END_GAME;
  }
}
