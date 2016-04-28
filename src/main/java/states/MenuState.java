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
 * Defines the Menu state of the game.
 *
 * @author bl48
 *
 */
public class MenuState extends BasicGameState {

  private static final int BUTTON_WIDTH = 280;
  private static final int BUTTON_HEIGHT = 67;

  private TrueTypeFont ttf;
  private TrueTypeFont ttf2;

  @Override
  public void init(GameContainer gc, StateBasedGame s) throws SlickException {
    // TODO Auto-generated method stub
    Font font = new Font("Arial", Font.BOLD, 50);
    ttf = new TrueTypeFont(font, true);
    Font font2 = new Font("Helvetica", Font.BOLD, 20);
    ttf2 = new TrueTypeFont(font2, true);
  }

  @Override
  public void render(GameContainer gc, StateBasedGame s, Graphics g)
      throws SlickException {
    g.drawImage(Resources.getImage("background"), 0, 0);

    // Draw title
    Resources.getImage("w_key").draw(Window.width / 2 - 85 - 100, Window.height / 8, 128,
        102);
    Resources.getImage("a_key").draw(Window.width / 2 - 85 * 2 - 100,
        Window.height / 8 + 85, 128, 102);
    Resources.getImage("s_key").draw(Window.width / 2 - 85 - 100, Window.height / 8 + 85,
        128, 102);
    Resources.getImage("d_key").draw(Window.width / 2 - 100,
        Window.height / 8 + 85, 128, 102);
    ttf.drawString(Window.width / 2 + 125 - 100, Window.height / 8 + 115, "To Live",
        Color.black);

    // Draw buttons
    Resources.getImage("buttonSingle").draw(Window.width / 2 - BUTTON_WIDTH / 2 - 20,
        Window.height / 8 + 200, BUTTON_WIDTH, BUTTON_HEIGHT);

    Resources.getImage("buttonMulti").draw(Window.width / 2 - BUTTON_WIDTH / 2 - 20,
        Window.height / 8 + 300, BUTTON_WIDTH, BUTTON_HEIGHT);

    Resources.getImage("buttonRank").draw(Window.width / 2 - BUTTON_WIDTH / 2 - 20,
        Window.height / 8 + 400, BUTTON_WIDTH, BUTTON_HEIGHT);

    Resources.getImage("buttonAbout").draw(Window.width / 2 - BUTTON_WIDTH / 2 - 20,
        Window.height / 8 + 500, BUTTON_WIDTH, BUTTON_HEIGHT);

  }

  @Override
  public void update(GameContainer gc, StateBasedGame s, int delta)
      throws SlickException {

    // Get x and y mouse position coordinates
    int posX = gc.getInput().getMouseX();
    int posY = gc.getInput().getMouseY();

    // Check if they are clicking in the button area
    if (gc.getInput().isMousePressed(0) && posX >= Window.width / 2 - BUTTON_WIDTH / 2 - 20
        && posX <= Window.width / 2 - BUTTON_WIDTH / 2 - 20 + BUTTON_WIDTH) {
      if (posY >= Window.height / 8 + 200
          && posY <= Window.height / 8 + 200 + BUTTON_HEIGHT) {
        // restart a new game every time we enter a game state from menu
        s.getState(States.SINGLE_PLAYER).init(gc, s);
        s.enterState(States.SINGLE_PLAYER);
      } else if (posY >= Window.height / 8 + 300
          && posY <= Window.height / 8 + 300 + BUTTON_HEIGHT) {
        s.getState(States.TWO_PLAYER_START_SERVER).init(gc, s);
        s.enterState(States.TWO_PLAYER_START_SERVER);
      } else if (posY >= Window.height / 8 + 400
          && posY <= Window.height / 8 + 400 + BUTTON_HEIGHT) {
        s.getState(States.HIGH_SCORES).init(gc, s);
        s.enterState(States.HIGH_SCORES);
      }
    }

    // Key press detections
    if (gc.getInput().isKeyPressed(Input.KEY_1)) {
      s.getState(States.SINGLE_PLAYER).init(gc, s);
      s.enterState(States.SINGLE_PLAYER);
    } else if (gc.getInput().isKeyPressed(Input.KEY_2)) {
      s.getState(States.TWO_PLAYER_START_SERVER).init(gc, s);
      s.enterState(States.TWO_PLAYER_START_SERVER);
    } else if (gc.getInput().isKeyDown(Input.KEY_3)) {
      s.getState(States.HIGH_SCORES).init(gc, s);
      s.enterState(States.HIGH_SCORES);
    }
  }

  @Override
  public int getID() {
    return States.MENU;
  }

}