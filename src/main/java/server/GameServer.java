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
import server.Network.AnimationPacket;
import server.Network.GameEnd;
import server.Network.PlayerMove;
import server.Network.PlayerUpdate;
import server.Network.PowerupNew;
import server.Network.PowerupPickup;
import server.Network.PowerupRemove;
import server.Network.ZombieDie;
import server.Network.ZombieFire;
import server.Network.ZombieMove;
import server.Network.ZombieNew;
import states.GamePlayState;

public class GameServer {
  private Map<String, Zombie> zombies;
  private Map<String, Powerup> powerups;
  private Map<String, Player> players;
  private String player1ID;
  private GamePlayState game;
  private StateBasedGame s;

  private Server server;


  public GameServer(Map<String, Player> players
      , Map<String, Zombie> zombies
      , Map<String, Powerup> powerups
      , String player1ID
      , GamePlayState game
      , StateBasedGame s) throws IOException {
    this.players = players;
    this.zombies = zombies;
    this.powerups = powerups;
    this.player1ID = player1ID;
    this.game = game;
    this.s = s;
  }

  public void start() throws IOException {
    Log.set(Log.LEVEL_DEBUG);
    this.server = new Server(16384, 4096);
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

  public Connection[] getConnections() {
    return this.server.getConnections();
  }

  private void sendUDP(Object packet) {
    if (this.getConnections().length > 0) {
      this.getConnections()[0].sendUDP(packet);
    }
  }

  private void sendTCP(Object packet) {
    if (this.getConnections().length > 0) {
      this.getConnections()[0].sendTCP(packet);
    }
  }



  public void sendHostPosition() {
    PlayerMove newMove = new PlayerMove();
    newMove.id = this.player1ID;
    newMove.x = this.players.get(this.player1ID).getX();
    newMove.y = this.players.get(this.player1ID).getY();
    sendUDP(newMove);
  }

  public void sendNewZombie(String id, String targetID) {
    ZombieNew z = new ZombieNew();
    z.id = id;
    z.targetID = targetID;
    sendTCP(z);
  }

  public void updateZombieFire(Zombie zombie) {
    ZombieFire z = new ZombieFire();
    z.id = zombie.getID();
    z.onFire = zombie.isOnFire();
    sendTCP(z);
  }

  public void moveZombie(Zombie zombie) {
    ZombieMove z = new ZombieMove();
    z.id = zombie.getID();
    z.x = zombie.getX();
    z.y = zombie.getY();
    sendUDP(z);
  }

  public void updatePlayer(String id, boolean loseLife) {
    Player p = this.players.get(id);
    PlayerUpdate update = new PlayerUpdate();
    update.id = id;
    update.loseLife = loseLife;
    update.score = p.getScore();
    update.speed = p.getSpeed();
    sendTCP(update);
  }

  public void sendNewPowerup(Powerup p) {
    PowerupNew packet = new PowerupNew();
    packet.id = p.getID();
    packet.powerupIndex = p.getPowerupIndex();
    packet.x = p.getX();
    packet.y = p.getY();
    sendTCP(packet);
  }

  public void removePowerup(String id) {
    PowerupRemove packet = new PowerupRemove();
    packet.id = id;
    sendTCP(packet);
  }

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

  public void sendGameEnd(String loserString) {
    GameEnd gameEnd = new GameEnd();
    gameEnd.loserString = loserString;
    sendTCP(gameEnd);
  }

  public void sendAnimation(String animationName, int height, int width) {
    if (animationName == null || animationName.length() == 0) {
      return;
    }
    AnimationPacket packet = new AnimationPacket();
    packet.animationName = animationName;
    packet.animationHeight = height;
    packet.animationWidth = width;
    sendTCP(packet);
  }

  public void sendPowerupPickup(Player player, Powerup p) {
    PowerupPickup packet = new PowerupPickup();
    packet.id = p.getID();
    packet.playerId = player.getID();
    sendTCP(packet);
  }

  public void close() {
    this.server.close();
  }
}
