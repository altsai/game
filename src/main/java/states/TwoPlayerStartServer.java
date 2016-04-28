package states;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JOptionPane;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * State that represents the menu where the player decides to host or join.
 *
 */
public class TwoPlayerStartServer extends BasicGameState {

  private Connection conn;
  private List<String> serverNames;
  private List<String> serverAddresses;
  private String address;
  private TextField serverField;
  private TextField serverName;
  private boolean makeServer;
  private boolean initializedTextFields = false;

  /**
   * Instantiates a TwoPlayerStartServer state.
   * @param conn Connection to database
   */
  public TwoPlayerStartServer(Connection conn) {
    this.conn = conn;
  }

  @Override
  public void init(GameContainer gc, StateBasedGame s)
      throws SlickException {
    this.serverNames = new ArrayList<>();
    this.serverAddresses = new ArrayList<>();

    if (!initializedTextFields) {
      serverField = new TextField(gc, gc.getDefaultFont(), 500, 500, 300, 25);

      serverName = new TextField(gc, gc.getDefaultFont(), 500, 350, 300, 25);

      initializedTextFields = true;
    }

    serverField.setAcceptingInput(true);
    serverField.setFocus(true);
    serverField.setText("");

    serverName.setAcceptingInput(false);
    serverName.setText("");
    makeServer = false;

    try {
      getServers();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Fetches all servers from the database.
   * @throws SQLException on error with connection.
   */
  private void getServers() throws SQLException {
    String query = "SELECT * FROM servers";
    PreparedStatement prep = conn.prepareStatement(query);
    ResultSet rs = prep.executeQuery();

    while(rs.next()) {
      serverNames.add(rs.getString("name"));
      serverAddresses.add(rs.getString("ip"));
    }

    rs.close();
    prep.close();
  }

  public String getAddress() {
    return this.address;
  }

  @Override
  public void render(GameContainer gc, StateBasedGame s, Graphics g)
      throws SlickException {
    // Draw server names
    int y = 50;
    for (int i = 0; i < serverNames.size(); i++) {
      g.drawString((i + 1) + ". " + serverNames.get(i), 20, y);
      y+=20;
    }

    // Draw input areas
    if (makeServer) {
      g.drawString("Enter name for server here:", 500, 300);
      serverName.render(gc, g);
      serverName.setAcceptingInput(true);
    } else {
      g.drawString("Hit S to start a server", 500, 300);
    }
    g.drawString("Input server number here:", 500, 450);
    serverField.render(gc, g);
    serverField.setAcceptingInput(true);

  }

  private String getIpAddress() throws SocketException {
    String localAddr = "";
    Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
    for (; n.hasMoreElements();)
    {
      NetworkInterface e = n.nextElement();

      Enumeration<InetAddress> a = e.getInetAddresses();
      for (; a.hasMoreElements();)
      {
        InetAddress addr = a.nextElement();

        if (addr.isSiteLocalAddress()) {
          localAddr = addr.getHostAddress();
        }
      }
    }

    return localAddr;
  }

  private void addServer(String name, String localAddr) throws SQLException, SocketException {
    String add = "INSERT INTO servers VALUES (?, ?)";
    PreparedStatement prep = conn.prepareStatement(add);
    prep.setString(1, name);
    prep.setString(2, localAddr);

    prep.execute();

    prep.close();


  }

  public Connection getConn() {
    return this.conn;
  }

  @Override
  public void update(GameContainer gc, StateBasedGame s, int delta)
      throws SlickException {
    if (gc.getInput().isKeyPressed(Input.KEY_S) && !serverField.hasFocus()) {
      makeServer = true;
    }

    if (serverField.hasFocus() && gc.getInput().isKeyPressed(Input.KEY_ENTER)) {
      try {
        int serverNum = Integer.parseInt(serverField.getText());
        if (serverNum - 1 < serverAddresses.size() && serverNum > 0) {
          address = serverAddresses.get(serverNum - 1);
          s.getState(States.TWO_PLAYER_CLIENT).init(gc, s);
          s.enterState(States.TWO_PLAYER_CLIENT);
        } else {
          JOptionPane.showMessageDialog(null, "Oops! That's not a valid number!");
        }
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Oops! That's not a valid number!");
      }
    }

    if (gc.getInput().isKeyPressed(Input.KEY_ENTER) && serverName.hasFocus()) {
      try {
        address = getIpAddress();
        addServer(serverName.getText(), address);
      } catch (SocketException | SQLException e) {
        e.printStackTrace();
      }

      s.getState(States.TWO_PLAYER_HOST).init(gc, s);
      s.enterState(States.TWO_PLAYER_HOST);
    }


    // go to the home menu state when 'esc' is pressed
    if (gc.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
      s.enterState(States.MENU);
    }

  }

  @Override
  public int getID() {
    // TODO Auto-generated method stub
    return States.TWO_PLAYER_START_SERVER;
  }

}
