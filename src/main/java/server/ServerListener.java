package server;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import entities.Player;
import entities.Zombie;
import game_objects.Powerup;
import server.Network.ActionStart;
import server.Network.PlayerMove;
import server.Network.PlayerNew;
import server.Network.PlayerUpdate;
import server.Network.UsePowerup;
import server.Network.ZombieDie;
import states.GamePlayState;
import states.States;

public class ServerListener extends Listener {
  private Map<String, Zombie> zombies;
  private Map<String, Powerup> powerups;
  private Map<String, Player> players;
  private boolean connected;
  private boolean endGame;
  private Server server;
  private String player1ID;
  private GamePlayState game;
  private StateBasedGame s;
  private GameServer gs;

  public ServerListener(Server server
      , Map<String, Player> players
      , Map<String, Zombie> zombies
      , Map<String, Powerup> powerups
      , String player1ID
      , GamePlayState game
      , StateBasedGame s
      , GameServer gs) {
    this.server = server;
    this.players = players;
    this.zombies = zombies;
    this.powerups = powerups;
    this.player1ID = player1ID;
    this.game = game;
    this.s = s;
    this.gs = gs;
  }

  public boolean isConnected() {
    return this.server.getConnections().length > 0;
  }

  public boolean isGameEnd() {
    return this.endGame;
  }

  @Override
  public void connected(Connection c) {
    if (this.connected) {
      c.close();
    } else {
      PlayerNew newPlayer = new PlayerNew();
      newPlayer.id = this.player1ID;
      newPlayer.isPlayer1 = true;
      newPlayer.name = "Player1";
      c.sendTCP(newPlayer);
      this.connected = true;
      this.game.setTimeInit(System.currentTimeMillis());
    }
  }

  @Override
  public void disconnected(Connection c) {
    gs.deleteServer();

    this.endGame = true;
    if (this.connected) {
      this.connected = false;
      this.game.setLoser("Connection lost");
      this.gs.close();
      this.s.enterState(States.HOST_END_GAME, new FadeOutTransition(),
          new FadeInTransition());
    }
  }

  @Override
  public void received(Connection c, Object o) {

    // if given a new player packet, adds the new player
    if (o instanceof PlayerNew) {
      PlayerNew newPlayer = (PlayerNew) o;
      Player p = new Player(null, newPlayer.name);
      p.setID(newPlayer.id);
      p.setPlayer1(newPlayer.isPlayer1);

      if (this.players.size() == 1) {
        this.players.put(p.getID(), p);
      }
    }

    if (o instanceof PlayerMove) {
      PlayerMove move = (PlayerMove) o;
      this.players.get(move.id).setX(move.x);
      this.players.get(move.id).setY(move.y);
    }

    if (o instanceof PlayerUpdate) {
      PlayerUpdate update = (PlayerUpdate) o;
      Player p = this.players.get(update.id);
      p.setSpeed(update.speed);
      p.setScore(update.score);
      if (update.loseLife) {
        p.loseLife();
      }
    }

    // the client has pressed action key, so the host must let the client
    // character use the powerup.
    // the host then must tell client what zombies are killed
    if (o instanceof ActionStart) {
      ActionStart response = (ActionStart) o;
      Player p = this.players.get(response.playerID);


      // tell the client to use the powerup
      UsePowerup packett = new UsePowerup();
      packett.playerId = p.getID();
      this.server.getConnections()[0].sendTCP(packett);

      List<String> removed = p.usePowerup();

      if (removed.size() > 9) {
        // break up the list into smaller lists of 10 to send
        List<String> idGroup = new LinkedList<>();
        for (String z : removed) {
          idGroup.add(z);
          if (idGroup.size() > 9) {
            ZombieDie packet = new ZombieDie();
            packet.idList = idGroup;
            this.server.getConnections()[0].sendTCP(packet);
            idGroup.clear();
          }
        }
        ZombieDie packet = new ZombieDie();
        packet.idList = idGroup;
        this.server.getConnections()[0].sendTCP(packet);

      } else {
        ZombieDie packet = new ZombieDie();
        packet.idList = removed;
        this.server.getConnections()[0].sendTCP(packet);
      }

      //      // also update the client with new info on the client player
      //      // ex: The client used speed powerup, so now needs to speed up
      //      PlayerUpdate update = new PlayerUpdate();
      //      update.id = p.getID();
      //      update.loseLife = false;
      //      update.score = p.getScore();
      //      update.speed = p.getSpeed();
      //      this.server.getConnections()[0].sendTCP(update);
    }


  }
}
