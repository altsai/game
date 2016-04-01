package entities;

import game_objects.Powerup;

public interface PlayerAction {

  int getLives();

  void updateLives(int numLives);

  void collectPowerup(Powerup p);

  int getScore();

  void incrementScore();

  void usePowerup();
}
