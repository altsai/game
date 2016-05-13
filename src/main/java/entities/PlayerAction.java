package entities;

import game_objects.Powerup;

import java.util.List;

/**
 * Describes player behavior.
 *
 * @author Alison
 *
 */
public interface PlayerAction {

  /**
   * Returns number of lives the player has.
   *
   * @return the number of lives left
   */
  int getLives();

  /**
   * Decrements the number of lives the player has.
   */
  void loseLife();

  /**
   * Allows the player to pick up a powerup.
   *
   * @param p
   *          the powerup to store
   */
  void collectPowerup(Powerup p);

  /**
   * Returns the player score.
   *
   * @return the player score
   */
  int getScore();

  /**
   * Increments player score.
   */
  void incrementScore();

  /**
   * Uses stored powerup.
   *
   * @return list of zombie ids of zombies killed
   */
  List<String> usePowerup();

  /**
   * Clears stored powerup.
   */
  void clearPowerupStorage();
}
