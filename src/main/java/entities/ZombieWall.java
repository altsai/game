package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.brown.cs.altsai.game.Window;

public class ZombieWall {

  public final static int GAP_WIDTH = 3;


  public ZombieWall(Entity target, Map<String, Player> players, double speed,
      Map<String, Zombie> zombies, Map<String, List<String>> zombieFormations) {
    float x = 13.0f;
    float y = (float) ((Window.height - 50) / 2.0) + 40;
    ZombieFormationHead z = new ZombieFormationHead(target, players,
        Window.width - 40, y - 5);
    z.setSpeed(speed);
    z.setX(x);
    z.setY(y);
    zombies.put(z.getID(), z);

    boolean atCap = false;
    double yPos = y - 20;
    List<String> zfbIds = new ArrayList<>();
    int n = 1;

    Random r = new Random();
    int gap = r.nextInt(13);

    while (!atCap) {

      // if we are at the randomly generated gap, skip the next 4 updates
      if (n == gap) {
        // pick a random side to fill in (so the other side gets the gap)
        if (r.nextInt(2) == 0) {
          while (Math.abs(n - gap) <= GAP_WIDTH) {
            ZombieFormationBody b1 = new ZombieFormationBody(z, players, n, 270.0);
            zombies.put(b1.getID(), b1);
            zfbIds.add(b1.getID());
            n++;
            yPos -= 20;
            atCap = yPos < 60;
          }
        } else {
          while (Math.abs(n - gap) <= GAP_WIDTH) {
            n++;
            yPos -= 20;
            atCap = yPos < 60;
            ZombieFormationBody b2 = new ZombieFormationBody(z, players, n, 90.0);
            if (b2.getY() < Window.height - 30) {
            zombies.put(b2.getID(), b2);
            zfbIds.add(b2.getID());
            }
          }
        }
      } else {

        ZombieFormationBody b1 = new ZombieFormationBody(z, players, n, 270.0);
        zombies.put(b1.getID(), b1);
        zfbIds.add(b1.getID());

        ZombieFormationBody b2 = new ZombieFormationBody(z, players, n, 90.0);
        if (b2.getY() < Window.height - 30) {
          zombies.put(b2.getID(), b2);
          zfbIds.add(b2.getID());
        }
        n++;
        yPos -= 20;
        atCap = yPos < 60;
      }
    }

    zombieFormations.put(z.getID(), zfbIds);
  }
}
