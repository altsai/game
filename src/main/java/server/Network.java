package server;

import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import game_objects.PlayerMessage;

/**
 * Keeps commonalitites to both server and client.
 * @author bl48
 *
 */
public class Network {

  public final static int UDPPORT = 1234, TCPPORT = 1234;

  /**
   * Class that MUST be called on all servers and clients.
   *
   * Registers the packets sent.
   * @param point
   */
  static public void register(EndPoint point) {
    Kryo kryo = point.getKryo();
    //TODO: Register all kryo classes here
    kryo.register(PlayerMove.class);
    kryo.register(PlayerJail.class);
    kryo.register(PlayerUpdate.class);
    kryo.register(PlayerJail.class);
    kryo.register(PlayerNew.class);
    kryo.register(ZombieNew.class);
    kryo.register(ZombieMove.class);
    kryo.register(PowerupNew.class);
    kryo.register(PowerupRemove.class);
    kryo.register(ZombieDie.class);
    kryo.register(java.util.LinkedList.class);
    kryo.register(java.util.ArrayList.class);
    kryo.register(ActionStart.class);
    kryo.register(GameEnd.class);
    kryo.register(PowerupPickup.class);
    kryo.register(UsePowerup.class);
    kryo.register(ZombieMoveList.class);
    kryo.register(PlayerMessage.class);
  }

  /**
   * Packet signaling new Player creation.
   */
  static public class PlayerNew {
    public String id;
    public String name;
    public boolean isPlayer1;
  }

  /**
   * Packet signaling Player move.
   */
  static public class PlayerMove {
    public String id;
    public float x;
    public float y;
    public float lastDir;
  }

  /**
   * Packet containg updated Player info.
   *
   * Info on id, speed, loseLife, score.
   *
   */
  static public class PlayerUpdate {
    public String id;
    public double speed;
    public boolean loseLife;
    public int score;
    public float top;
    public float left;
    public float bottom;
    public float right;
    public long lastBombFired;
  }

  /**
   * Info on player boundaries.
   */
  static public class PlayerJail {
    public String id;
    public boolean inJail;
    public float top;
    public float left;
    public float bottom;
    public float right;
  }

  /**
   * Packet telling what player picked up what powerup.
   */
  static public class PickUpPowerup {
    public String playerId;
    public String id;
  }

  /**
   * Packet creates new Zombie.
   */
  static public class ZombieNew {
    public String id;
    public String targetID;
  }

  /**
   * Packet containing info on zombie move.
   */
  static public class ZombieMove {
    public String id;
    public float x;
    public float y;
  }

  /**
   * Packet containing grouped info on zombie move.
   */
  static public class ZombieMoveList {
    public List<ZombieMove> list;
  }

  /**
   * Packet that contains Zombie ids to be removed.
   */
  static public class ZombieDie {
    public List<String> idList;
  }

  /**
   * Packet that contains info on spawning new powerup.
   */
  static public class PowerupNew {
    public String id;
    public float x;
    public float y;
    public int powerupIndex;
  }

  /**
   * Packet that contains info on removing powerup.
   */
  static public class PowerupRemove {
    public String id;
  }

  /**
   * Packet that contains info on Player using powerup.
   */
  static public class ActionStart {
    public String playerID;
    public boolean pressedAction;
  }

  /**
   * Packet that contains end game information.
   */
  static public class GameEnd {
    public String loserString;
  }

  /**
   * Packet that contains info on powerup pickup.
   */
  static public class PowerupPickup {
    public String id;
    public String playerId;
  }

  /**
   * Packet that contains info on using powerup.
   */
  static public class UsePowerup {
    public String playerId;
  }


}
