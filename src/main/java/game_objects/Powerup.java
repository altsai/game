package game_objects;

/**
 * Interface that all Powerup objects must implement.
 *
 * @author bl48
 *
 */
public interface Powerup {

  /**
   * Method to use the powerup and start effects.
   */
  public abstract void activate();

  /**
   * Method to stop the effects of the powerup.
   */
  public abstract void deactivate();

  /**
   * Method to remove the powerup from the map.
   *
   * Different from deactivate in that kill() can be called without using the
   * powerup.
   */
  public abstract void kill();
}
