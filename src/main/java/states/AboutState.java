package states;

import java.awt.Font;

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
 * State that controls the page for instructions.
 */
public class AboutState extends BasicGameState {

  // width of buttons
  private static final int BUTTON_WIDTH = 180;
  private static final int BUTTON_HEIGHT = 68;

  // fonts used on the
  private TrueTypeFont headerFont;
  private TrueTypeFont textFont;

  @Override
  public void init(GameContainer gc, StateBasedGame s) throws SlickException {
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

    // Main menu button
    Resources.getImage("buttonMainMenu").draw(20, 20, BUTTON_WIDTH,
        BUTTON_HEIGHT);

    if (Window.width == 1390) {
      Resources.getImage("aboutPage").draw(0, 0);
    } else {
      Resources.getImage("aboutPageSmall").draw(0, 0);
    }
  }

  @Override
  public void update(GameContainer gc, StateBasedGame s, int delta)
      throws SlickException {
    // Get x and y mouse position coordinates
    int posX = gc.getInput().getMouseX();
    int posY = gc.getInput().getMouseY();

    // Back to main menu
    if (gc.getInput().isKeyPressed(Input.KEY_ESCAPE)
        || (gc.getInput().isMouseButtonDown(0) && posX >= 20
        && posX <= 20 + BUTTON_WIDTH && posY >= 20 && posY <= 20 + BUTTON_HEIGHT)) {
      s.getState(States.MENU).init(gc, s);
      s.enterState(States.MENU);
    }
  }

  @Override
  public int getID() {
    return States.ABOUT;
  }

}
