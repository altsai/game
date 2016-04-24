package server;

import java.util.Map;

import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import powerups.Bomb;
import powerups.Speed;
import powerups.TimeStop;
import server.Network.GameEnd;
import server.Network.PlayerMove;
import server.Network.PlayerNew;
import server.Network.PlayerUpdate;
import server.Network.PowerupNew;
import server.Network.PowerupPickup;
import server.Network.PowerupRemove;
import server.Network.UsePowerup;
import server.Network.ZombieDie;
import server.Network.ZombieFire;
import server.Network.ZombieMove;
import server.Network.ZombieMoveList;
import server.Network.ZombieNew;
import states.GamePlayState;
import states.States;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.google.common.collect.Maps;

import entities.Player;
import entities.Zombie;
import game_objects.Powerup;

public class ClientListener extends Listener {
  private Map<String, Zombie> zombies;
  private Map<String, Powerup> powerups;
  private Map<String, Player> players;
  private boolean connected;
  private boolean endGame;
  private Client client;
  private String playerID;
  private GamePlayState game;
  private StateBasedGame s;
  private GameClient gc;

  public ClientListener(Client client, Map<String, Player> players,
      Map<String, Zombie> zombies, Map<String, Powerup> powerups,
      String playerID, GamePlayState gps, StateBasedGame s, GameClient gc) {

    this.client = client;
    this.players = players;
    this.zombies = zombies;
    this.powerups = powerups;
    this.playerID = playerID;
    this.game = gps;
    this.s = s;
    this.gc = gc;
  }

  public boolean isConnected() {
    return this.connected;
  }

  public boolean isGameEnd() {
    return this.endGame;
  }

  @Override
  public void connected(Connection c) {
    this.connected = true;
    PlayerNew newPlayer = new PlayerNew();
    newPlayer.name = "Player2";
    newPlayer.isPlayer1 = false;
    newPlayer.id = this.playerID;
    this.client.sendTCP(newPlayer);
    this.game.setTimeInit(System.currentTimeMillis());
  }

  @Override
  public void disconnected(Connection c) {
    this.endGame = true;

    // if we had a previous connection and the game hasn't ended due to a death
    if (this.connected && !this.game.isGameEnd()) {
      this.connected = false;
      this.gc.close();
      this.game.setLoser("Connection lost");
      this.s.enterState(States.CLIENT_END_GAME, new FadeOutTransition(),
          new FadeInTransition());
    } else if (this.connected && this.game.isGameEnd()) {
      this.s.enterState(States.CLIENT_END_GAME, new FadeOutTransition(),
          new FadeInTransition());
    }
  }

  @Override
  public void received(Connection c, Object o) {

    // add the first player to this player list
    if (o instanceof PlayerNew) {
      PlayerNew newPlayer = (PlayerNew) o;
      if (newPlayer.isPlayer1) {
        Player p1 = new Player(null, newPlayer.name);
        p1.setPlayer1(newPlayer.isPlayer1);
        p1.setID(newPlayer.id);

        // make sure to add the host player
        this.players.put(newPlayer.id, p1);
      }
    }

    // move the host player after receiving position update
    if (o instanceof PlayerMove) {
      PlayerMove move = (PlayerMove) o;
      this.players.get(move.id).setX(move.x);
      this.players.get(move.id).setY(move.y);
    }

    if (o instanceof ZombieNew) {
      ZombieNew zombie = (ZombieNew) o;
      Zombie newZombie = new Zombie(this.players.get(zombie.targetID));
      newZombie.setID(zombie.id);
      this.zombies.put(zombie.id, newZombie);
    }

    if (o instanceof ZombieFire) {
      ZombieFire update = (ZombieFire) o;
      Zombie selected = this.zombies.get(update.id);
      if (selected != null) {
        selected.setState(update.onFire);
      }
    }

    if (o instanceof ZombieMove) {
      ZombieMove move = (ZombieMove) o;
      Zombie selected = this.zombies.get(move.id);
      if (selected != null) {
        selected.setX(move.x);
        selected.setY(move.y);
      }
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

    if (o instanceof PowerupNew) {
      PowerupNew packet = (PowerupNew) o;
      switch (packet.powerupIndex) {
      case Powerup.BOMB:
        Bomb newBomb = new Bomb(this.powerups, Maps.newHashMap(), this.players);
        newBomb.setX(packet.x);
        newBomb.setY(packet.y);
        this.powerups.put(packet.id, newBomb);
        break;
      case Powerup.SPEED:
        Speed newSpeed = new Speed(this.powerups);
        newSpeed.setX(packet.x);
        newSpeed.setY(packet.y);
        this.powerups.put(packet.id, newSpeed);
        break;
      case Powerup.TIMESTOP:
        TimeStop newTime = new TimeStop(this.powerups, Maps.newHashMap(),
            this.players, this.game);
        newTime.setX(packet.x);
        newTime.setY(packet.y);
        this.powerups.put(packet.id, newTime);
        break;
      default:
        break;
      }
    }

    if (o instanceof PowerupRemove) {
      PowerupRemove packet = (PowerupRemove) o;
      this.powerups.remove(packet.id);
    }

    if (o instanceof ZombieDie) {
      ZombieDie packet = (ZombieDie) o;
      for (String key : packet.idList) {
        this.zombies.remove(key);
      }
    }

    if (o instanceof GameEnd) {
      GameEnd packet = (GameEnd) o;
      this.game.setLoser(packet.loserString);
      this.game.setGameEnd(true);
    }

    if (o instanceof PowerupPickup) {
      PowerupPickup packet = (PowerupPickup) o;
      Player player = this.players.get(packet.playerId);
      Powerup p = this.powerups.get(packet.id);
      if (player == null || p == null) {
        return;
      } else {
        player.collectPowerup(p);
      }
    }

    if (o instanceof UsePowerup) {
      UsePowerup packet = (UsePowerup) o;
      Player p = this.players.get(packet.playerId);
      if (p != null) {
        p.usePowerup();
      }
    }

    if (o instanceof ZombieMoveList) {
      ZombieMoveList packet = (ZombieMoveList) o;
      for (ZombieMove move : packet.list) {
        Zombie target = this.zombies.get(move.id);
        target.setX(move.x);
        target.setY(move.y);
      }
    }

    // if (o instanceof AnimationPacket) {
    // AnimationPacket packet = (AnimationPacket) o;
    //
    // }

  }
}
