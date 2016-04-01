package states;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import entities.Entity;
import entities.Player;
import entities.Zombie;

/**
 * Defines the Single Player game state.
 *
 * @author bl48
 *
 */
public class SinglePlayerGameState extends BasicGameState {

  // list of all entities in the game
  private ArrayList<Entity> entities;

  private Player player1;

  @Override
  public void init(GameContainer gc, StateBasedGame s)
      throws SlickException {
    this.entities = new ArrayList<>();
    Player player1 = new Player(null);
    this.player1 = player1;
    this.entities.add(player1);
    this.entities.add(new Zombie(this.player1));
  }

  @Override
  public void render(GameContainer gc, StateBasedGame s, Graphics g)
      throws SlickException {
    for (int i = 0; i < this.entities.size(); i++) {
      this.entities.get(i).render(gc, g);
    }

  }

  @Override
  public void update(GameContainer gc, StateBasedGame s, int delta)
      throws SlickException {

    // go to the home menu state when 'esc' is pressed
    if (gc.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
      s.enterState(States.MENU);
    }
    if (gc.getInput().isKeyPressed(Input.KEY_SPACE)) {
      this.entities.add(new Zombie(this.player1));
    }

    for (int i = 0; i < this.entities.size(); i++) {
      this.entities.get(i).update(gc, delta);
    }

  }

  @Override
  public int getID() {
    return States.SINGLE_PLAYER;
  }

}
