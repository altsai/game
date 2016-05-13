package states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import edu.brown.cs.altsai.game.Window;

/**
 * Class that controls the lobby page for picking servers to join.
 *
 */
public class TwoPlayerServerName extends BasicGameState {

  private String serverName;
  private TextField inputBox;


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
    g.drawString("Hit Enter to start a server", Window.width / 2, Window.height / 2);
  }

  @Override
  public void update(GameContainer gc, StateBasedGame s, int delta)
      throws SlickException {
    // go to the twoplayerhost game state and pass in the current name
    if (gc.getInput().isKeyPressed(Input.KEY_ENTER)) {
      // restart a new game everytime we enter a game state from menu
      s.getState(States.TWO_PLAYER_HOST).init(gc, s);
      s.enterState(States.TWO_PLAYER_HOST);
    } else if (gc.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
      s.getState(States.MENU).init(gc, s);
      s.enterState(States.MENU);
    }
  }

  @Override
  public int getID() {
    return States.TWO_PLAYER_NAME_SERVER;
  }

}
