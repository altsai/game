package server;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.state.StateBasedGame;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

import entities.Player;
import entities.Zombie;
import game_objects.Powerup;
import server.Network.GameEnd;
import server.Network.PlayerMove;
import server.Network.PlayerUpdate;
import server.Network.PowerupNew;
import server.Network.PowerupPickup;
import server.Network.PowerupRemove;
import server.Network.ZombieDie;
import server.Network.ZombieMove;
import server.Network.ZombieNew;
import states.GamePlayState;

/**
 * GameServer class. The server that the host contains.
 *
 * @author bl48
 *
 */
public class GameServer {

  // maps of objects that the host contains
  private Map<String, Zombie> zombies;
  private Map<String, Powerup> powerups;
  private Map<String, Player> players;

  // player that the host controls
  private String player1ID;

  // gameplaysstates
  private GamePlayState game;
  private StateBasedGame s;

  private Server server;

  /**
   * Constructor for a GameServer.
   *
   * @param players         Map of String to Player
   * @param zombies         Map of String to zombies
   * @param powerups        Map of String to powerups
   * @param player1ID       PlayerID of the player that host controls
   * @param game            GamePlayState of host's game
   * @param s               StateBasedGame of host's game
   */
  public GameServer(Map<String, Player> players
      , Map<String, Zombie> zombies
      , Map<String, Powerup> powerups
      , String player1ID
      , GamePlayState game
      , StateBasedGame s) {
    this.players = players;
    this.zombies = zombies;
    this.powerups = powerups;
    this.player1ID = player1ID;
    this.game = game;
    this.s = s;
  }

  /**
   * Start method that starts the GameServer.
   *
   * @throws IOException   On error starting server
   */
  public void start() throws IOException {

    // set a debug log
    Log.set(Log.LEVEL_DEBUG);

    // server has write buffer of 2^15, and object buffer of 2^12
    this.server = new Server(32768, 4096);
    Network.register(this.server);

    // maybe pass in the hashmaps into the listeners? and servers?
    this.server.addListener(
        new ServerListener(
            this.server
            , this.players
            , this.zombies
            , this.powerups
            , this.player1ID
            , this.game
            , this.s
            , this));

    // catch this excpeiton when making the server in the twoplayerseverstate
    this.server.bind(Network.TCPPORT, Network.UDPPORT);
    this.server.start();
  }

  /**
   * Gets all the connections from the server.
   *
   * There should be only one connection because there should
   * only be one server-client pair.
   *
   * @return   Array of connections
   */
  public Connection[] getConnections() {
    return this.server.getConnections();
  }

  /**
   * Method to send a UDP packet.
   *
   * First checks to see if there is a connection to send packets
   * to, and then sends the packet.
   *
   * @param packet  Serializeable packet
   */
  private void sendUDP(Object packet) {
    if (this.getConnections().length > 0) {
      this.getConnections()[0].sendUDP(packet);
    }
  }

  /**
   * Method to send a TCP packet.
   *
   * First checks that there is a connection.
   *
   * @param packet   Serializeable packet
   */
  private void sendTCP(Object packet) {
    if (this.getConnections().length > 0) {
      this.getConnections()[0].sendTCP(packet);
    }
  }


  /**
   * Sends the host position (Player 1) to client (UDP).
   */
  public void sendHostPosition() {
    PlayerMove newMove = new PlayerMove();
    newMove.id = this.player1ID;
    newMove.x = this.players.get(this.player1ID).getX();
    newMove.y = this.players.get(this.player1ID).getY();
    sendUDP(newMove);
  }

  /**
   * Sends a packet to tell client to make a new Zombie (TCP).
   *
   * @param id          The playerID of the host.
   * @param targetID    The zombieID of the new Zombie.
   */
  public void sendNewZombie(String id, String targetID) {
    ZombieNew z = new ZombieNew();
    z.id = id;
    z.targetID = targetID;
    sendTCP(z);
  }

  /**
   * Sends a packet to move a zombie (UDP).
   *
   * @param zombie     Zombie to be moved
   */
  public void moveZombie(Zombie zombie) {
    ZombieMove z = new ZombieMove();
    z.id = zombie.getID();
    z.x = zombie.getX();
    z.y = zombie.getY();
    sendUDP(z);
  }

  /**
   * Sends an update player packet (TCP).
   *
   * @param id          PlayerID to be updated
   * @param loseLife    True if player should call loselife().
   */
  public void updatePlayer(String id, boolean loseLife) {
    Player p = this.players.get(id);
    PlayerUpdate update = new PlayerUpdate();
    update.id = id;
    update.loseLife = loseLife;
    update.score = p.getScore();
    update.speed = p.getSpeed();
    sendTCP(update);
  }

  /**
   * Send a new powerup to be spawned on client screen (TCP).
   *
   * @param p    Powerup to be spawned.
   */
  public void sendNewPowerup(Powerup p) {
    PowerupNew packet = new PowerupNew();
    packet.id = p.getID();
    packet.powerupIndex = p.getPowerupIndex();
    packet.x = p.getX();
    packet.y = p.getY();
    sendTCP(packet);
  }

  /**
   * Removes a powerup from the client's powerup map (TCP).
   *
   * @param id   ID of the powerup to be removed
   */
  public void removePowerup(String id) {
    PowerupRemove packet = new PowerupRemove();
    packet.id = id;
    sendTCP(packet);
  }

  /**
   * Removes a zombie from the client map (TCP).
   *
   * @param id    ID of the zombie to be removed.
   */
  public void removeZombie(List<String> id) {
    if (id.size() > 9) {
      // break up the list into smaller lists of 10 to send
      List<String> idGroup = new LinkedList<>();
      for (String z : id) {
        idGroup.add(z);
        if (idGroup.size() > 9) {
          ZombieDie packet = new ZombieDie();
          packet.idList = idGroup;
          sendTCP(packet);
          idGroup.clear();
        }
      }
      ZombieDie packet = new ZombieDie();
      packet.idList = idGroup;
      sendTCP(packet);

    } else {
      ZombieDie packet = new ZombieDie();
      packet.idList = id;
      sendTCP(packet);
    }
  }

  /**
   * Sends the client a packet signaling that the game ended (TCP).
   *
   * @param loserString     The string that client will print on end game page.
   */
  public void sendGameEnd(String loserString) {
    GameEnd gameEnd = new GameEnd();
    gameEnd.loserString = loserString;
    sendTCP(gameEnd);
  }

  /**
   * Sends packet notifying client that client has picked up powerup (TCP).
   *
   * @param player   Player that picked up the powerup
   * @param p        Powerup that player picked up
   */
  public void sendPowerupPickup(Player player, Powerup p) {
    PowerupPickup packet = new PowerupPickup();
    packet.id = p.getID();
    packet.playerId = player.getID();
    sendTCP(packet);
  }

  /**
   * Closes the server, shuts down connections.
   */
  public void close() {
    this.server.close();
  }
}
