package states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import edu.brown.cs.altsai.game.Window;

public class TwoPlayerStartServer extends BasicGameState {

  @Override
  public void init(GameContainer arg0, StateBasedGame arg1)
      throws SlickException {
    // TODO Auto-generated method stub

  }

  @Override
  public void render(GameContainer gc, StateBasedGame s, Graphics g)
      throws SlickException {
    g.drawString("Hit 1 to start a server", Window.width / 2, Window.height / 2);
    g.drawString("Hit 2 to join a server", Window.width / 2, Window.height / 2 + 20);

  }

  @Override
  public void update(GameContainer gc, StateBasedGame s, int delta)
      throws SlickException {
    // go to the singleplayer game when user presses 1
    if (gc.getInput().isKeyPressed(Input.KEY_1)) {
      // restart a new game everytime we enter a game state from menu
      s.getState(States.TWO_PLAYER_HOST).init(gc, s);
      s.enterState(States.TWO_PLAYER_HOST);
    } else if (gc.getInput().isKeyPressed(Input.KEY_2)) {
      s.getState(States.TWO_PLAYER_CLIENT).init(gc, s);
      s.enterState(States.TWO_PLAYER_CLIENT);
    }
  }

  @Override
  public int getID() {
    // TODO Auto-generated method stub
    return States.TWO_PLAYER_START_SERVER;
  }

}
