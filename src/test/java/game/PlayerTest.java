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

  @Test
  public void playerScore() {
    Player player = new Player(null, "Player 1");
    player.setPlayer1(true);

    assertTrue(player.getScore() == 0);

    player.incrementScore();

    assertTrue(player.getScore() == 1);
  }

  @Test
  public void nullPowerup() {
    Player player = new Player(null, "Player 1");
    player.setPlayer1(true);

    assertTrue(player.getCurrPowerup() == null);
    assertTrue(player.usePowerup().size() == 0);
  }

  @Test
  public void playerJail() {
    Player player = new Player(null, "Player 1");
    player.setPlayer1(true);

    assertTrue(!player.isJailed());

    player.addJail(player.getID());

    assertTrue(player.isJailed());

    player.removeJail(player.getID());

    assertTrue(!player.isJailed());
  }

}
