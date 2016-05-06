package server;

import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.google.common.collect.Maps;

import entities.Player;
import entities.Zombie;
import game_objects.PlayerMessage;
import game_objects.Powerup;
import powerups.Bomb;
import powerups.Jail;
import powerups.LaserBeam;
import powerups.Speed;
import powerups.TimeStop;
import server.Network.ActionStart;
import server.Network.GameEnd;
import server.Network.PlayerMove;
import server.Network.PlayerNew;
import server.Network.PlayerUpdate;
import server.Network.PowerupNew;
import server.Network.PowerupPickup;
import server.Network.PowerupRemove;
import server.Network.UsePowerup;
import server.Network.ZombieDie;
import server.Network.ZombieMove;
import server.Network.ZombieMoveList;
import server.Network.ZombieNew;
import states.GamePlayState;
import states.NetworkPlay;
import states.States;

/**
 * ClientListener class that receives packet from server and converts to actions.
 *
 * @author bl48
 *
 */
public class ClientListener extends Listener {

  public int zombiemove;
  public int zombiedie;
  public int playerupdate;
  public int playermove;
  public int zombieNew;
  public int PowerupNew;
  public int PowerupRemove;
  public int zombieMoveList;



  // maps of objects that the client renders
  private Map<String, Zombie> zombies;
  private Map<String, Powerup> powerups;
  private Map<String, Player> players;
  private Set<Powerup> pickedUpPowerups;
  private Queue<PlayerMessage> messages;

  // boolean to determine if client is connected
  private boolean connected;

  // boolean to determine if the game ended
  private boolean endGame;

  // client to send packets to server with
  private Client client;

  // the playerID of the player the client controls
  private String playerID;

  // games to change game state
  private GamePlayState game;
  private StateBasedGame s;
  private GameClient gc;

  /**
   * Constructor for a ClientListener.
   *
   * @param client       Client object
   * @param players      Map of Strings to Players
   * @param zombies      Map of Strings to Zombies
   * @param powerups     Map of Strings to Powerup IDs
   * @param playerID     PlayerID of the player that client controls
   * @param gps          GamePlayState
   * @param s            StateBasedGame
   * @param gc           GameClient
   */
  public ClientListener(Client client, Map<String, Player> players,
      Map<String, Zombie> zombies, Map<String, Powerup> powerups
      , Set<Powerup> pickedUpPowerups, Queue<PlayerMessage> messages
      , String playerID, GamePlayState gps, StateBasedGame s, GameClient gc) {

    this.client = client;
    this.players = players;
    this.zombies = zombies;
    this.powerups = powerups;
    this.playerID = playerID;
    this.pickedUpPowerups = pickedUpPowerups;
    this.messages = messages;
    this.game = gps;
    this.s = s;
    this.gc = gc;
  }

  /**
   * Determines if the client is connected.
   *
   * @return boolean, true if connected, otherwise false.
   */
  public boolean isConnected() {
    return this.connected;
  }

  /**
   * Determines if the game ended.
   *
   * @return boolean, true if game ended, else false.
   */
  public boolean isGameEnd() {
    return this.endGame;
  }

  @Override
  /**
   * Method to run as soon as the client finds a server to connect with.
   *
   * Here is where we would send a packet to the server and check that
   * the server is the correct server to connect with.
   */
  public void connected(Connection c) {
    this.connected = true;

    // create a new player to represent the client
    PlayerNew newPlayer = new PlayerNew();
    // give the name Player2 for now
    newPlayer.name = "Player2";
    newPlayer.isPlayer1 = false;
    newPlayer.id = this.playerID;

    // send the server this player so that the server is aware of the client
    this.client.sendTCP(newPlayer);

    // set the time that the game is initiated (used for the 3 second delay)
    this.game.setTimeInit(System.currentTimeMillis());
  }

  @Override
  /**
   * Method that runs when the client is disconnected from server.
   */
  public void disconnected(Connection c) {

    System.out.println(this.playermove + " playermove");
    System.out.println(this.playerupdate + " playerupdate");
    System.out.println(this.PowerupNew + " powerupnew");
    System.out.println(this.PowerupRemove + " powerupremove");
    System.out.println(this.zombiedie + " zombieDie");
    System.out.println(this.zombiemove + " zombieMove");
    System.out.println(this.zombieMoveList + " zombieMovelist");
    System.out.println(this.zombieNew + " zombienew");

    this.endGame = true;

    // if we had a previous connection and the game hasn't ended due to a death
    if (this.connected && !this.game.isGameEnd()) {
      this.connected = false;
      this.gc.close();
      this.game.setLoser("Connection lost");
      this.s.enterState(States.CLIENT_END_GAME, new FadeOutTransition(),
          new FadeInTransition());

      // if we had a previous connection and game ended due to loss
    } else if (this.connected && this.game.isGameEnd()) {
      this.s.enterState(States.CLIENT_END_GAME, new FadeOutTransition(),
          new FadeInTransition());
    }
  }

  @Override
  /**
   * Method that takes an incoming packet and deserializes it
   */
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
      this.playermove ++;
      PlayerMove move = (PlayerMove) o;
      this.players.get(move.id).setX(move.x);
      this.players.get(move.id).setY(move.y);
      this.players.get(move.id).setDirection(move.lastDir);
    }

    // create a new zombie to put in zombie map
    if (o instanceof ZombieNew) {
      this.zombieNew++;
      ZombieNew zombie = (ZombieNew) o;
      Zombie newZombie = new Zombie(this.players.get(zombie.targetID), this.players);
      newZombie.setID(zombie.id);
      this.zombies.put(zombie.id, newZombie);
    }

    // update the zombie's position in map
    if (o instanceof ZombieMove) {
      this.zombiemove++;
      ZombieMove move = (ZombieMove) o;
      Zombie selected = this.zombies.get(move.id);
      if (selected != null) {
        selected.setX(move.x);
        selected.setY(move.y);
      }
    }

    // update variables of a player
    if (o instanceof PlayerUpdate) {
      this.playerupdate++;
      PlayerUpdate update = (PlayerUpdate) o;
      Player p = this.players.get(update.id);
      p.setSpeed(update.speed);
      p.setScore(update.score);
      if (update.loseLife) {
        p.loseLife();
      }
      p.setBoundary(update.top, update.bottom, update.left, update.right);
      p.setLastBombFired(update.lastBombFired);
    }

    // create new powerup to put in powerup map
    if (o instanceof PowerupNew) {
      this.PowerupNew++;
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
      case Powerup.JAIL:
        Jail newJail = new Jail(this.powerups, this.zombies, this.players);
        newJail.setX(packet.x);
        newJail.setY(packet.y);
        this.powerups.put(packet.id, newJail);
        break;
      case Powerup.LASER:

        // change this once laser is finished
        LaserBeam newLaser = new LaserBeam(this.powerups);
        newLaser.setX(packet.x);
        newLaser.setY(packet.y);
        this.powerups.put(packet.id, newLaser);
        break;

      default:
        break;
      }
    }

    // removes a powerup from the map (expired)
    if (o instanceof PowerupRemove) {
      this.PowerupRemove++;
      PowerupRemove packet = (PowerupRemove) o;
      this.powerups.remove(packet.id);
    }

    // removes zombie from map (zombie killed)
    if (o instanceof ZombieDie) {
      this.zombiedie++;
      ZombieDie packet = (ZombieDie) o;
      for (String key : packet.idList) {
        this.zombies.remove(key);
      }
    }

    // ends game
    if (o instanceof GameEnd) {
      GameEnd packet = (GameEnd) o;

      // sets the current game's loser to whatever loser the packet says
      this.game.setLoser(packet.loserString);
      this.game.setGameEnd(true);
    }

    // if we get a message, add it to the queue
    if (o instanceof PlayerMessage) {
      PlayerMessage message = (PlayerMessage) o;

      synchronized (this.messages) {
        // make sure we only hold 5 messages
        if (this.messages.size() == NetworkPlay.CHAT_CACHE_LENGTH) {
          this.messages.poll();
        }
        this.messages.add(message);
      }
    }

    // picks up a powerup
    if (o instanceof PowerupPickup) {
      PowerupPickup packet = (PowerupPickup) o;
      Player player = this.players.get(packet.playerId);
      Powerup p = this.powerups.get(packet.id);
      if (player == null || p == null) {
        return;
      } else {
        player.collectPowerup(p);
        this.pickedUpPowerups.add(p);
      }
    }

    // packet that signals the spacebar press
    if (o instanceof UsePowerup) {
      UsePowerup packet = (UsePowerup) o;
      Player p = this.players.get(packet.playerId);
      if (p != null) {
        p.usePowerup();
      }
    }

    // packet that moves a list of zombies
    if (o instanceof ZombieMoveList) {
      this.zombieMoveList++;
      ZombieMoveList packet = (ZombieMoveList) o;
      if (packet.list.size() > 0) {
        for (ZombieMove move : packet.list) {
          Zombie target = this.zombies.get(move.id);
          if (target != null) {
            target.setX(move.x);
            target.setY(move.y);
          }
        }
      }
    }

    if (o instanceof ActionStart) {
      ActionStart response = (ActionStart) o;
      Player p = this.players.get(response.playerID);
      if (p != null) {
        p.usePowerup();
      }
    }

  }
}
