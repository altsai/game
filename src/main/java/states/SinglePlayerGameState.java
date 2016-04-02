package states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import edu.brown.cs.altsai.game.Window;
import entities.Player;

/**
 * Defines the Single Player game state.
 *
 * @author bl48
 *
 */
public class SinglePlayerGameState extends GamePlayState {

  @Override
  public void init(GameContainer gc, StateBasedGame s) throws SlickException {
    super.init(gc, s);

    // add players to the player list from the superclass
    Player player1 = new Player(null);
    this.players.add(player1);
  }

  @Override
  public void render(GameContainer gc, StateBasedGame s, Graphics g)
      throws SlickException {

    super.render(gc, s, g);

    g.drawString("Player has " + this.players.get(0).getLives() + " lives", 100, 100);
    g.drawString("Player speed: " + this.players.get(0).getSpeed(), 50, 50);

    g.drawString("Hit esc to go to menu", Window.width / 2, Window.height / 2);
  }


  @Override
  public int getID() {
    return States.SINGLE_PLAYER;
  }

  /**
   * Method to get the score for the player of this state.
   *
   * @return int, score of the player
   */
  public int getScore() {
    return this.players.get(0).getScore();
  }


  @Override
  /**
   * Method that ends the single player game.
   *
   * @param s
   *          StateBasedGame
   */
  protected void endGame(StateBasedGame s) {
    s.enterState(States.SINGLE_PLAYER_END_GAME, new FadeOutTransition(),
        new FadeInTransition());
  }

}
