package game;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import entities.Player;

/**
 * Tests Player functionality.
 *
 * @author Alison
 *
 */
public class PlayerTest {

  @Test
  public void playerInitTest() {
    Player player = new Player(null, "Player 1");
    player.setPlayer1(true);

    assertTrue(player.getX() == 500);
    assertTrue(player.getY() == 500);
    assertTrue(!player.isJailed());
    assertTrue(!player.isInvincible());
    assertTrue(!player.isImmune());
    assertTrue(player.toString().equals("Player 1"));
  }

  @Test
  public void playerImmunity() {
    Player player = new Player(null, "Player 1");
    player.setPlayer1(true);

    assertTrue(!player.isImmune());

    player.setImmune();

    assertTrue(player.isImmune());

    player.revert();

    assertTrue(!player.isImmune());
  }

}
