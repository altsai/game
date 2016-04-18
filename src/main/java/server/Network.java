package server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

/**
 * Keeps commonalitites to both server and client.
 * @author bl48
 *
 */
public class Network {

  private final static int UDPPORT = 1234, TCPPORT = 1234;

  static public void register(EndPoint point) {
    Kryo kryo = point.getKryo();
    //TODO: Register all kryo classes here
    kryo.register(PlayerMove.class);
    kryo.register(PlayerSpeed.class);
    kryo.register(PlayerLoseLife.class);
    kryo.register(PlayerScore.class);
    kryo.register(PlayerInvincible.class);
    kryo.register(PlayerWin.class);
    kryo.register(PlayerJail.class);
  }

  static public class PlayerMove {
    public float x;
    public float y;
  }

  static public class PlayerSpeed {
    public boolean speedUp;
    public double speed;
  }

  static public class PlayerLoseLife {
    public boolean loseLife;
  }

  static public class PlayerScore {
    public int score;
  }

  static public class PlayerInvincible {
    public boolean invincible;
  }

  static public class PlayerWin {
    public String name;
  }

  static public class PlayerJail {
    public boolean inJail;
    public float top;
    public float left;
    public float bottom;
    public float right;
  }

  static public class PickUpPowerup {
    public String id;
  }



}
