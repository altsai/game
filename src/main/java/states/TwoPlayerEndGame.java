package states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class TwoPlayerEndGame extends BasicGameState {
  //takes in the SinglePlayerGameState just played
  private TwoPlayerGameState tpgs;

  /**
   * Constructor for a SinglePlayerEndGameState
   * @param singlePlayerGameState     single player state that just finished
   */
  public TwoPlayerEndGame(TwoPlayerGameState tpgs) {
    this.tpgs = tpgs;
  }

  @Override
  public void init(GameContainer gc, StateBasedGame s)
      throws SlickException {
    // TODO Auto-generated method stub

  }

  @Override
  public void render(GameContainer gc, StateBasedGame s, Graphics g)
      throws SlickException {
    g.drawString(this.tpgs.getLoser() + " lost!", 100, 300);
    g.drawString("Hit esc to go back to Menu", 100, 200);
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
    return States.TWO_PLAYER_END_GAME;
  }
}
