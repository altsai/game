package server;

import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

/**
 * Keeps commonalitites to both server and client.
 * @author bl48
 *
 */
public class Network {

  public final static int UDPPORT = 1234, TCPPORT = 1234;

  static public void register(EndPoint point) {
    Kryo kryo = point.getKryo();
    //TODO: Register all kryo classes here
    kryo.register(PlayerMove.class);
    kryo.register(PlayerWin.class);
    kryo.register(PlayerJail.class);
    kryo.register(PlayerUpdate.class);
    kryo.register(PlayerWin.class);
    kryo.register(PlayerJail.class);
    kryo.register(PlayerNew.class);
    kryo.register(ZombieNew.class);
    kryo.register(ZombieFire.class);
    kryo.register(ZombieMove.class);
    kryo.register(PowerupNew.class);
    kryo.register(PowerupRemove.class);
    kryo.register(ZombieDie.class);
    kryo.register(java.util.LinkedList.class);
    kryo.register(ActionStart.class);
    kryo.register(GameEnd.class);
    kryo.register(AnimationPacket.class);
    kryo.register(PowerupPickup.class);
    kryo.register(UsePowerup.class);
    kryo.register(ZombieMoveList.class);
  }

  static public class PlayerNew {
    public String id;
    public String name;
    public boolean isPlayer1;
  }

  static public class PlayerMove {
    public String id;
    public float x;
    public float y;
  }

  static public class PlayerUpdate {
    public String id;
    public double speed;
    public boolean loseLife;
    public int score;
  }

  static public class PlayerWin {
    public String id;
  }

  static public class PlayerJail {
    public String id;
    public boolean inJail;
    public float top;
    public float left;
    public float bottom;
    public float right;
  }

  static public class PickUpPowerup {
    public String playerId;
    public String id;
  }

  static public class ZombieNew {
    public String id;
    public String targetID;
  }

  static public class ZombieMove {
    public String id;
    public float x;
    public float y;
  }

  static public class ZombieMoveList {
    public List<ZombieMove> list;
  }

  static public class ZombieFire {
    public String id;
    public boolean onFire;
  }

  static public class ZombieDie {
    public List<String> idList;
  }

  static public class PowerupNew {
    public String id;
    public float x;
    public float y;
    public int powerupIndex;
  }

  static public class PowerupRemove {
    public String id;
  }

  static public class ActionStart {
    public String playerID;
    public boolean pressedAction;
  }

  static public class GameEnd {
    public String loserString;
  }

  static public class AnimationPacket {
    public String animationName;
    public int animationHeight;
    public int animationWidth;
  }

  static public class PowerupPickup {
    public String id;
    public String playerId;
  }

  static public class UsePowerup {
    public String playerId;
  }


}
