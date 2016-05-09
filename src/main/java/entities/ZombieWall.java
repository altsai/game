package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.brown.cs.altsai.game.Window;

public class ZombieWall {
  public ZombieWall(Entity target, Map<String, Player> players, double speed,
      Map<String, Zombie> zombies, Map<String, List<String>> zombieFormations) {
    float x = 13.0f;
    float y = (float) ((Window.height - 50) / 2.0) + 40;
    ZombieFormationHead z = new ZombieFormationHead(target, players,
        Window.width - 40, y - 15);
    z.setSpeed(speed);
    z.setX(x);
    z.setY(y);
    zombies.put(z.getID(), z);

    boolean atCap = false;
    double yPos = y - 20;
    List<String> zfbIds = new ArrayList<>();
    int n = 1;

    while (!atCap) {
      ZombieFormationBody b1 = new ZombieFormationBody(z, players, n, 270.0);
      zombies.put(b1.getID(), b1);
      zfbIds.add(b1.getID());

      ZombieFormationBody b2 = new ZombieFormationBody(z, players, n, 90.0);
      zombies.put(b2.getID(), b2);
      zfbIds.add(b2.getID());

      yPos -= 20;
      atCap = yPos < 60;
      n += 1;
    }

    zombieFormations.put(z.getID(), zfbIds);
  }
}
