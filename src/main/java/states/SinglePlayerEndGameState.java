package states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class SinglePlayerEndGameState extends BasicGameState {
  SinglePlayerGameState spgs;

  public SinglePlayerEndGameState() {

  }

  public SinglePlayerEndGameState(SinglePlayerGameState singlePlayerGameState) {
    this.spgs = singlePlayerGameState;
  }

  @Override
  public void init(GameContainer gc, StateBasedGame s)
      throws SlickException {
    // TODO Auto-generated method stub

  }

  @Override
  public void render(GameContainer gc, StateBasedGame s, Graphics g)
      throws SlickException {
    g.drawString("You Died, Score was: " + this.spgs.getScore(), 100, 100);
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
    return States.SINGLE_PLAYER_END_GAME;
  }
}
