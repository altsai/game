package entities;

import java.util.List;

import game_objects.Powerup;

public interface PlayerAction {

  int getLives();

  void loseLife();

  void collectPowerup(Powerup p);

  int getScore();

  void incrementScore();

  List<String> usePowerup();

  void clearPowerupStorage();
}
