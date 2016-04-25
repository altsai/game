package server;

import java.io.IOException;
import java.util.Map;

import org.newdawn.slick.state.StateBasedGame;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.minlog.Log;

import entities.Player;
import entities.Zombie;
import game_objects.Powerup;
import server.Network.ActionStart;
import server.Network.PlayerMove;
import states.GamePlayState;

/**
 * GameClient class that the client uses to communicate with server.
 *
 * @author bl48
 *
 */
public class GameClient {

  // maps of objects that the client game contains
  private Map<String, Zombie> zombies;
  private Map<String, Powerup> powerups;
  private Map<String, Player> players;

  // game state of client game
  private GamePlayState game;
  private StateBasedGame s;

  // address of the server
  private String address;

  // player ID of the client
  private String playerID;

  private Client client;

  /**
   * Constructor for a GameClient.
   *
   * @param players       Map of String to Players
   * @param zombies       Map of String to Zombies
   * @param powerups      Map of String to Powerups
   * @param playerID      String, ID of the client player
   * @param gps           GamePlayState
   * @param s             StateBasedGame
   * @param address       Address of the server
   */
  public GameClient(Map<String, Player> players
      , Map<String, Zombie> zombies
      , Map<String, Powerup> powerups
      , String playerID
      , GamePlayState gps
      , StateBasedGame s, String address) {
    this.players = players;
    this.zombies = zombies;
    this.powerups = powerups;
    this.playerID = playerID;
    this.game = gps;
    this.s = s;
    this.address = address;
  }

  /**
   * Starts the client and connects to server.
   *
   * @throws IOException on error connecting.
   */
  public void start() throws IOException {
    Log.set(Log.LEVEL_DEBUG);

    // give buffer write of 2^14, and object buffer of 2^12
    this.client = new Client(16384, 4096);
    Network.register(this.client);

    // maybe pass in the hashmaps into the listeners? and servers?
    this.client.addListener(
        new ClientListener(this.client
            , this.players
            , this.zombies
            , this.powerups
            , this.playerID
            , this.game
            , this.s
            , this));

    // catch this excpeiton when making the server in the twoplayerseverstate
    this.client.start();

    // uncomment to start LAN search
    //List<InetAddress> IPs = this.client.discoverHosts(Network.TCPPORT, 3000);

    //InetAddress address = this.client.discoverHost(Network.TCPPORT, 10000);
    //System.out.println(address);

    this.client.connect(5000, address, Network.TCPPORT, Network.UDPPORT);
  }

  /**
   * Method to check if client connected.
   * @return boolean, true if connected, else false
   */
  public boolean isConnected() {
    return this.client.isConnected();
  }

  /**
   * Sends the client player position to the Host.
   */
  public void sendClientPosition() {
    PlayerMove newMove = new PlayerMove();
    newMove.id = this.playerID;
    newMove.x = this.players.get(this.playerID).getX();
    newMove.y = this.players.get(this.playerID).getY();
    this.client.sendUDP(newMove);
  }

  /**
   * Sends a packet to Server notifying of spacebar press.
   */
  public void sendUsePowerup() {
    ActionStart packet = new ActionStart();
    packet.playerID = this.playerID;
    packet.pressedAction = true;
    this.client.sendTCP(packet);
  }

  /**
   * Closes the server.
   */
  public void close() {
    this.client.close();
  }


}
