package states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import edu.brown.cs.altsai.game.Window;

/**
 * Defines the Menu state of the game.
 *
 * @author bl48
 *
 */
public class MenuState extends BasicGameState {

  @Override
  public void init(GameContainer gc, StateBasedGame s)
      throws SlickException {
    // TODO Auto-generated method stub

  }

  @Override
  public void render(GameContainer gc, StateBasedGame s, Graphics g)
      throws SlickException {
    g.drawString("Hit 1 to go to single player", Window.width / 2, Window.height / 2);
    g.drawString("Hit 2 to go to double player", Window.width / 2, Window.height / 2 + 20);
    g.drawString("Hit 3 to go to high scores", Window.width / 2, Window.height / 2 + 40);

  }

  @Override
  public void update(GameContainer gc, StateBasedGame s, int delta)
      throws SlickException {

    // go to the singleplayer game when user presses 1
    if (gc.getInput().isKeyPressed(Input.KEY_1)) {
      // restart a new game everytime we enter a game state from menu
      s.getState(States.SINGLE_PLAYER).init(gc, s);
      s.enterState(States.SINGLE_PLAYER);
    } else if (gc.getInput().isKeyPressed(Input.KEY_2)) {
      s.getState(States.TWO_PLAYER).init(gc, s);
      s.enterState(States.TWO_PLAYER);
    } else if (gc.getInput().isKeyDown(Input.KEY_3)) {
      s.enterState(States.HIGH_SCORES);
    }
  }

  @Override
  public int getID() {
    return States.MENU;
  }

}