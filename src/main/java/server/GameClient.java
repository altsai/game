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

public class GameClient {
  private Map<String, Zombie> zombies;
  private Map<String, Powerup> powerups;
  private Map<String, Player> players;
  private GamePlayState game;
  private StateBasedGame s;

  private String playerID;

  private Client client;

  public GameClient(Map<String, Player> players
      , Map<String, Zombie> zombies
      , Map<String, Powerup> powerups
      , String playerID
      , GamePlayState gps
      , StateBasedGame s) throws IOException {
    this.players = players;
    this.zombies = zombies;
    this.powerups = powerups;
    this.playerID = playerID;
    this.game = gps;
    this.s = s;
  }

  public void start() throws IOException {
    Log.set(Log.LEVEL_DEBUG);
    this.client = new Client();
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

    //InetAddress address = this.client.discoverHost(Network.TCPPORT, 3000);
    this.client.connect(5000, "127.0.0.1", Network.TCPPORT, Network.UDPPORT);
  }

  public boolean isConnected() {
    return this.client.isConnected();
  }

  public void sendClientPosition() {
    PlayerMove newMove = new PlayerMove();
    newMove.id = this.playerID;
    newMove.x = this.players.get(this.playerID).getX();
    newMove.y = this.players.get(this.playerID).getY();
    this.client.sendUDP(newMove);
  }

  public void sendUsePowerup() {
    ActionStart packet = new ActionStart();
    packet.playerID = this.playerID;
    packet.pressedAction = true;
    this.client.sendTCP(packet);
  }

  public void close() {
    this.client.close();
  }


}
