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
  private static final int BUTTON_HEIGHT = 140;

  private TrueTypeFont ttf;
  private TrueTypeFont ttf2;

  @Override
  public void init(GameContainer gc, StateBasedGame s) throws SlickException {
    // TODO Auto-generated method stub
    Font font = new Font("Helvetica", Font.BOLD, 50);
    ttf = new TrueTypeFont(font, true);
    Font font2 = new Font("Helvetica", Font.BOLD, 20);
    ttf2 = new TrueTypeFont(font2, true);
  }

  @Override
  public void render(GameContainer gc, StateBasedGame s, Graphics g)
      throws SlickException {
    g.drawImage(Resources.getImage("background"), 0, 0);

    // Draw title
    Resources.getImage("key").draw(Window.width / 5, Window.height / 8, 128,
        102);
    Resources.getImage("key").draw(Window.width / 5 - 85,
        Window.height / 8 + 85, 128, 102);
    Resources.getImage("key").draw(Window.width / 5, Window.height / 8 + 85,
        128, 102);
    Resources.getImage("key").draw(Window.width / 5 + 85,
        Window.height / 8 + 85, 128, 102);
    ttf.drawString(Window.width / 5 + 38, Window.height / 8 + 30, "W",
        Color.gray);
    ttf.drawString(Window.width / 5 - 40, Window.height / 8 + 115, "A",
        Color.gray);
    ttf.drawString(Window.width / 5 + 45, Window.height / 8 + 115, "S",
        Color.gray);
    ttf.drawString(Window.width / 5 + 130, Window.height / 8 + 115, "D",
        Color.gray);
    ttf.drawString(Window.width / 5 + 200, Window.height / 8 + 115, "To Live",
        Color.white);

    // Draw buttons
    Resources.getImage("playerLarge").draw(Window.width / 5 - 57,
        Window.height / 8 + 200, 70, 70);
    Resources.getImage("button").draw(Window.width / 5 + 40,
        Window.height / 8 + 167, BUTTON_WIDTH, BUTTON_HEIGHT);
    ttf2.drawString(Window.width / 5 + 70, Window.height / 8 + 230,
        "New Single Player Game");

    Resources.getImage("playerLarge").draw(Window.width / 5 - 57,
        Window.height / 8 + 300, 70, 70);
    Resources.getImage("button").draw(Window.width / 5 + 40,
        Window.height / 8 + 267, BUTTON_WIDTH, BUTTON_HEIGHT);
    ttf2.drawString(Window.width / 5 + 70, Window.height / 8 + 330,
        "Online Multiplayer Mode");

    Resources.getImage("playerLarge").draw(Window.width / 5 - 57,
        Window.height / 8 + 400, 70, 70);
    Resources.getImage("button").draw(Window.width / 5 + 40,
        Window.height / 8 + 367, BUTTON_WIDTH, BUTTON_HEIGHT);
    ttf2.drawString(Window.width / 5 + 140, Window.height / 8 + 430, "Rankings");

    Resources.getImage("playerLarge").draw(Window.width / 5 - 57,
        Window.height / 8 + 500, 70, 70);
    Resources.getImage("button").draw(Window.width / 5 + 40,
        Window.height / 8 + 467, BUTTON_WIDTH, BUTTON_HEIGHT);
    ttf2.drawString(Window.width / 5 + 157, Window.height / 8 + 530, "About");

  }

  @Override
  public void update(GameContainer gc, StateBasedGame s, int delta)
      throws SlickException {

    // Get x and y mouse position coordinates
    int posX = gc.getInput().getMouseX();
    int posY = gc.getInput().getMouseY();

    // Check if they are clicking in the button area
    if (gc.getInput().isMousePressed(0) && posX >= Window.width / 5 + 40
        && posX <= Window.width / 5 + 40 + BUTTON_WIDTH) {
      if (posY >= Window.height / 8 + 200
          && posY <= Window.height / 8 + 200 + BUTTON_HEIGHT / 2) {
        // restart a new game every time we enter a game state from menu
        s.getState(States.SINGLE_PLAYER).init(gc, s);
        s.enterState(States.SINGLE_PLAYER);
      } else if (posY >= Window.height / 8 + 300
          && posY <= Window.height / 8 + 300 + BUTTON_HEIGHT / 2) {
        s.getState(States.TWO_PLAYER_START_SERVER).init(gc, s);
        s.enterState(States.TWO_PLAYER_START_SERVER);
      } else if (posY >= Window.height / 8 + 400
          && posY <= Window.height / 8 + 400 + BUTTON_HEIGHT / 2) {
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
      s.enterState(States.HIGH_SCORES);
    }
  }

  @Override
  public int getID() {
    return States.MENU;
  }

}