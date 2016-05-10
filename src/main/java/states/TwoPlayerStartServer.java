package states;

import java.awt.Font;
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

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import edu.brown.cs.altsai.game.Resources;
import edu.brown.cs.altsai.game.Window;

/**
 * State that represents the menu where the player decides to host or join.
 *
 */
public class TwoPlayerStartServer extends BasicGameState {

  private static final int BUTTON_WIDTH = 180;
  private static final int BUTTON_HEIGHT = 68;

  private Connection conn;
  private List<String> serverNames;
  private List<String> serverAddresses;
  private String address;
  private TextField serverField;
  private TextField serverName;
  private boolean makeServer;
  private boolean initializedTextFields = false;

  private TrueTypeFont headerFont;
  private TrueTypeFont entryFont;
  private TrueTypeFont searchFont;

  private static final int PADDING = 15;
  private int numberWidth;
  private int nameWidth;
  private float tableWidth;

  private float startingEntryIndex;
  private boolean canScrollUp;
  private boolean canScrollDown;

  private Image arrowImage;

  /**
   * Instantiates a TwoPlayerStartServer state.
   * @param conn Connection to database
   */
  public TwoPlayerStartServer(Connection conn) {
    this.conn = conn;

    Font font = new Font("Arial", Font.BOLD, 50);
    headerFont = Resources.getDefaultFont(40);

    Font font2 = new Font("Arial", Font.BOLD, 20);
    entryFont = Resources.getDefaultFont(10);

    Font font3 = new Font("Arial", Font.PLAIN, 18);
    searchFont = Resources.getDefaultFont(8);

    arrowImage = Resources.getImage("gray_arrow");
  }

  @Override
  public void init(GameContainer gc, StateBasedGame s)
      throws SlickException {
    this.serverNames = new ArrayList<>();
    this.serverAddresses = new ArrayList<>();

    // get the ipAddress of the player
    try {
      this.address = getIpAddress();
    } catch (SocketException e1) {
      this.address = "";
    }

    startingEntryIndex = 0;
    canScrollUp = false;
    canScrollDown = false;

    if (!initializedTextFields) {
      serverField = new TextField(gc
          , searchFont
          , (Window.width / 2 - entryFont.getWidth("Enter server number or name to join: ") - 200) / 2
          + entryFont.getWidth("Enter server number or name to join: "), 75
          + headerFont.getLineHeight() + 10, 200, entryFont.getLineHeight());

      serverName = new TextField(gc
          , searchFont
          , (int) (Window.width * 1.5 - entryFont.getWidth("Enter server name to create: ") - 200) / 2
          + entryFont.getWidth("Enter server name to create: "), 75
          + headerFont.getLineHeight() + 10, 200, entryFont.getLineHeight());

      initializedTextFields = true;
    }

    serverField.setAcceptingInput(true);
    serverField.setText("");
    serverField.setFocus(false);

    serverName.setAcceptingInput(true);
    serverName.setText("");
    serverName.setFocus(false);

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
    // select only servers with ip addresses in the same network
    String query = "SELECT * FROM servers WHERE ip LIKE '" +
        this.getSubnetStart() + "%" + "';";
    PreparedStatement prep = conn.prepareStatement(query);
    ResultSet rs = prep.executeQuery();

    nameWidth = 0;
    while(rs.next()) {
      // Get nameWidth
      String name = rs.getString("name");
      nameWidth = Math.max(nameWidth, entryFont.getWidth(name) + PADDING);

      // Add to arrayLists
      serverNames.add(name);
      serverAddresses.add(rs.getString("ip"));
    }

    // Set numberWidth
    numberWidth = entryFont.getWidth(Integer.toString(serverNames.size())) + PADDING;

    tableWidth = numberWidth + nameWidth;

    rs.close();
    prep.close();
  }

  /**
   * Returns the whole ipv4 ip address with x.x.x.x.
   * @return String ipv4 ip address
   */
  public String getAddress() {
    return this.address;
  }

  /**
   * Returns the portion of ipv4 addresss that represents the network.
   * @return String, network portion of ip (x.x.x.)
   */
  public String getSubnetStart() {
    StringBuilder subnet = new StringBuilder();
    String[] split = this.address.split(".");
    for (int i = 0; i < split.length - 1; i++) {
      subnet.append(split[i] + ".");
    }
    return subnet.toString();
  }

  @Override
  public void render(GameContainer gc, StateBasedGame s, Graphics g)
      throws SlickException {

    g.drawImage(Resources.getImage("background"), 0, 0);

    // Left Side

    // Draw title
    float currHeight = 75;
    headerFont.drawString((Window.width / 2 - headerFont.getWidth("Join Server")) / 2, currHeight, "Join Server", Color.white);
    currHeight += (10 + headerFont.getLineHeight());

    // Draw serverField
    entryFont.drawString((Window.width / 2 - entryFont.getWidth("Enter server number or name to join: ") - 200) / 2, currHeight, "Enter server number or name to join: ", Color.white);
    g.setColor(Color.white);
    serverField.render(gc, g);
    serverField.setBackgroundColor(Color.white);
    serverField.setBorderColor(Color.black);
    serverField.setTextColor(Color.black);

    // Get some variables
    float tableX = (Window.width / 2 - tableWidth) / 2;
    float tableY = currHeight + entryFont.getLineHeight() + 10;
    g.setColor(Color.black);

    // Check if there are no servers
    if (serverNames == null || serverNames.size() == 0) {
      String toDraw = "No Servers!";
      entryFont.drawString((Window.width / 2 - entryFont.getWidth(toDraw)) / 2, tableY, toDraw, Color.white);
    } else {

      float normalTableHeight = Window.height - tableY - entryFont.getLineHeight() - 27;
      if (Window.height == 700) {
        normalTableHeight = Window.height - tableY - entryFont.getLineHeight();
      }

      // Get table height
      float tableHeight = Math.min(normalTableHeight, (entryFont.getLineHeight() + 5) * serverNames.size());

      // Outline of table
      g.setColor(Color.gray);
      g.drawRect(tableX, tableY, tableWidth, tableHeight);

      // Separator line
      g.drawLine(tableX + numberWidth, tableY, tableX + numberWidth, tableY + tableHeight);

      // Get starting entry index
      int entryIndex = (int) startingEntryIndex;

      // Draw arrow(s)
      if (entryIndex > 0) {
        canScrollUp = true;
        arrowImage.setRotation(270);
        arrowImage.draw(tableX + tableWidth + 20, tableY, 30, 30);
      } else {
        canScrollUp = false;
      }
      if ((entryFont.getLineHeight() + 5) * (serverNames.size() - entryIndex) > normalTableHeight) {
        canScrollDown = true;
        arrowImage.setRotation(90);
        arrowImage.draw(tableX + tableWidth + 20, tableY + tableHeight - 30, 30, 30);
      } else {
        canScrollDown = false;
      }

      // Entries
      for (float y = tableY + 5; y < tableY + tableHeight && entryIndex < serverNames.size(); y += entryFont.getLineHeight() + 5) {
        // Draw number
        float x = tableX + (numberWidth - entryFont.getWidth(Integer.toString(entryIndex + 1))) / 2;
        entryFont.drawString(x, y, Integer.toString(entryIndex + 1), Color.white);

        // Draw name
        x = tableX + numberWidth + (nameWidth - entryFont.getWidth(serverNames.get(entryIndex))) / 2;
        entryFont.drawString(x, y, serverNames.get(entryIndex), Color.white);

        // Draw separating line
        if (y + entryFont.getLineHeight() + 5 < tableY + tableHeight) {
          g.drawLine(tableX, y + entryFont.getLineHeight(), tableX + tableWidth, y + entryFont.getLineHeight());
        }

        entryIndex++;
      }
    }

    // Right side

    // Draw title
    currHeight = 75;
    headerFont.drawString((Window.width * 1.5f - headerFont.getWidth("Create Server")) / 2, currHeight, "Create Server", Color.white);
    currHeight += (10 + headerFont.getLineHeight());

    // Draw serverField
    entryFont.drawString((Window.width * 1.5f - entryFont.getWidth("Enter server name to create: ") - 200) / 2, currHeight, "Enter server name to create: ", Color.white);
    g.setColor(Color.white);
    serverName.render(gc, g);
    serverName.setBackgroundColor(Color.white);
    serverName.setBorderColor(Color.black);
    serverName.setTextColor(Color.black);

    // Main menu button
    Resources.getImage("buttonMainMenu").draw(20, 20, BUTTON_WIDTH, BUTTON_HEIGHT);

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
          // this returns the first one found.... maybe it'll work for all cases?
          return addr.getHostAddress();
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

    // Moving up and down
    if (gc.getInput().isKeyDown(Input.KEY_UP) && canScrollUp) {
      startingEntryIndex -= .2;
    } else if (gc.getInput().isKeyDown(Input.KEY_DOWN) && canScrollDown) {
      startingEntryIndex += .2;
    }

    // Joining a server
    if (serverField.hasFocus() && gc.getInput().isKeyPressed(Input.KEY_ENTER)) {
      // Get input
      String input = serverField.getText();

      // Check if its a number, if it's not then check if its a name
      try {
        int serverNum = Integer.parseInt(input);
        if (serverNum - 1 < serverAddresses.size() && serverNum > 0) {
          address = serverAddresses.get(serverNum - 1);
          s.getState(States.TWO_PLAYER_CLIENT).init(gc, s);
          s.enterState(States.TWO_PLAYER_CLIENT);
        } else {
          Window.showMessage("Invalid Number", "Oops! That's not a valid number!");
        }
      } catch (NumberFormatException e) {
        if (serverNames.contains(input)) {
          address = serverAddresses.get(serverNames.indexOf(input));
          s.getState(States.TWO_PLAYER_CLIENT).init(gc, s);
          s.enterState(States.TWO_PLAYER_CLIENT);
        } else {
          Window.showMessage("Invalid Name", "Oops! That's not a valid name!");
        }
      }
    }

    // Create a server
    if (gc.getInput().isKeyPressed(Input.KEY_ENTER) && serverName.hasFocus()) {
      try {
        String input = serverName.getText();

        if (!serverNames.contains(input)) {
          address = getIpAddress();
          addServer(serverName.getText(), address);
          s.getState(States.TWO_PLAYER_HOST).init(gc, s);
          s.enterState(States.TWO_PLAYER_HOST);
        } else {
          Window.showMessage("Name Taken", "Oops! That server name is already taken!");
        }
      } catch (SocketException | SQLException e) {
        e.printStackTrace();
      }
    }

    // Get x and y mouse position coordinates
    int posX = gc.getInput().getMouseX();
    int posY = gc.getInput().getMouseY();

    // Back to main menu
    if (gc.getInput().isKeyPressed(Input.KEY_ESCAPE) || (gc.getInput().isMouseButtonDown(0) && posX >= 20 && posX <= 20 + BUTTON_WIDTH && posY >= 20 && posY <= 20 + BUTTON_HEIGHT)) {
      s.enterState(States.MENU);
    }

  }

  @Override
  public int getID() {
    // TODO Auto-generated method stub
    return States.TWO_PLAYER_START_SERVER;
  }

}
