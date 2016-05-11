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
import java.util.UUID;

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

import edu.brown.cs.altsai.game.Main;
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
  private List<Integer> serverWidths;
  private List<Integer> serverHeights;
  private List<String> serverIds;
  private String address;
  private String serverId;
  private TextField serverField1390;
  private TextField serverName1390;
  private TextField serverField1132;
  private TextField serverName1132;
  private TextField serverFieldUsing;
  private TextField serverNameUsing;
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

    headerFont = Resources.getDefaultFont(40);

    entryFont = Resources.getDefaultFont(10);

    searchFont = Resources.getDefaultFont(8);

    arrowImage = Resources.getImage("gray_arrow");
  }

  @Override
  public void init(GameContainer gc, StateBasedGame s)
      throws SlickException {

    gc.getInput().clearControlPressedRecord();
    gc.getInput().clearKeyPressedRecord();
    gc.getInput().clearMousePressedRecord();

    this.serverNames = new ArrayList<>();
    this.serverAddresses = new ArrayList<>();
    this.serverWidths = new ArrayList<>();
    this.serverHeights = new ArrayList<>();
    this.serverIds = new ArrayList<>();

    // get the ipAddress of the player
    serverId = "";
    try {
      this.address = getIpAddress();
    } catch (SocketException e1) {
      this.address = "";
    }

    startingEntryIndex = 0;
    canScrollUp = false;
    canScrollDown = false;

    if (!initializedTextFields) {
      serverField1390 = new TextField(gc
          , searchFont
          , (1390 / 2 - entryFont.getWidth("Enter server number/name to join: ") - 200) / 2
          + entryFont.getWidth("Enter server number/name to join: "), 75
          + headerFont.getLineHeight() + 10, 200, entryFont.getLineHeight());

      serverName1390 = new TextField(gc
          , searchFont
          , (int) (1390 * 1.5 - entryFont.getWidth("Enter server name to create: ") - 200) / 2
          + entryFont.getWidth("Enter server name to create: "), 75
          + headerFont.getLineHeight() + 10, 200, entryFont.getLineHeight());

      serverField1132 = new TextField(gc
          , searchFont
          , (1132 / 2 - entryFont.getWidth("Enter server number/name to join: ") - 200) / 2
          + entryFont.getWidth("Enter server number/name to join: "), 75
          + headerFont.getLineHeight() + 10, 200, entryFont.getLineHeight());

      serverName1132 = new TextField(gc
          , searchFont
          , (int) (1132 * 1.5 - entryFont.getWidth("Enter server name to create: ") - 200) / 2
          + entryFont.getWidth("Enter server name to create: "), 75
          + headerFont.getLineHeight() + 10, 200, entryFont.getLineHeight());

      initializedTextFields = true;
    }

    if (Window.width == 1390) {
      serverFieldUsing = serverField1390;
      serverNameUsing = serverName1390;
    } else if (Window.width == 1132) {
      serverFieldUsing = serverField1132;
      serverNameUsing = serverName1132;
    }

    serverFieldUsing.setAcceptingInput(true);
    serverFieldUsing.setText("");
    serverFieldUsing.setFocus(false);

    serverNameUsing.setAcceptingInput(true);
    serverNameUsing.setText("");
    serverNameUsing.setFocus(false);

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
      serverWidths.add(rs.getInt("width"));
      serverHeights.add(rs.getInt("height"));
      serverIds.add(rs.getString("id"));
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

  public String getServerId() {
    return this.serverId;
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
    entryFont.drawString((Window.width / 2 - entryFont.getWidth("Enter server number/name to join: ") - 200) / 2, currHeight, "Enter server number/name to join: ", Color.white);
    g.setColor(Color.white);
    serverFieldUsing.render(gc, g);
    serverFieldUsing.setBackgroundColor(Color.white);
    serverFieldUsing.setBorderColor(Color.black);
    serverFieldUsing.setTextColor(Color.black);

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
    serverNameUsing.render(gc, g);
    serverNameUsing.setBackgroundColor(Color.white);
    serverNameUsing.setBorderColor(Color.black);
    serverNameUsing.setTextColor(Color.black);

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

  private void addServer(String name, String localAddr, int width, int height, String id) throws SQLException, SocketException {
    String add = "INSERT INTO servers VALUES (?, ?, ?, ?, ?)";
    PreparedStatement prep = conn.prepareStatement(add);
    prep.setString(1, name);
    prep.setString(2, localAddr);
    prep.setInt(3, width);
    prep.setInt(4, height);
    prep.setString(5, id);

    prep.execute();

    prep.close();


  }

  private void handleSizing(int width, int height, String id) {
    // Add this width and height
    try {
      String add = "INSERT INTO screenSize VALUES (?, ?, ?)";
      PreparedStatement prep;
      prep = conn.prepareStatement(add);
      prep.setString(1, id);
      prep.setInt(2, Window.width);
      prep.setInt(3, Window.height);

      prep.execute();

      prep.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    // Set new width, height if necessary
    if (Window.width > width || Window.height > height) {
      Main.setWidthHeight(width, height);
    }
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
    if (serverFieldUsing.hasFocus() && gc.getInput().isKeyPressed(Input.KEY_ENTER)) {
      // Get input
      String input = serverFieldUsing.getText();

      // Check if its a number, if it's not then check if its a name
      try {
        int serverNum = Integer.parseInt(input);
        if (serverNum - 1 < serverAddresses.size() && serverNum > 0) {
          address = serverAddresses.get(serverNum - 1);
          handleSizing(serverWidths.get(serverNum - 1), serverHeights.get(serverNum - 1), serverIds.get(serverNum - 1));
          s.getState(States.TWO_PLAYER_CLIENT).init(gc, s);
          s.enterState(States.TWO_PLAYER_CLIENT);
        } else {
          Window.showMessage("Invalid Number", "Oops! That's not a valid number!");
        }
      } catch (NumberFormatException e) {
        if (serverNames.contains(input)) {
          address = serverAddresses.get(serverNames.indexOf(input));
          handleSizing(serverWidths.get(serverNames.indexOf(input)), serverHeights.get(serverNames.indexOf(input)), serverIds.get(serverNames.indexOf(input)));
          s.getState(States.TWO_PLAYER_CLIENT).init(gc, s);
          s.enterState(States.TWO_PLAYER_CLIENT);
        } else {
          Window.showMessage("Invalid Name", "Oops! That's not a valid name!");
        }
      }
    }

    // Create a server
    if (gc.getInput().isKeyPressed(Input.KEY_ENTER) && serverNameUsing.hasFocus()) {
      try {
        String input = serverNameUsing.getText();

        if (!serverNames.contains(input)) {
          address = getIpAddress();
          serverId = UUID.randomUUID().toString().substring(10);
          addServer(serverNameUsing.getText(), address, Window.width, Window.height, serverId);
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
    if (gc.getInput().isKeyPressed(Input.KEY_ESCAPE)
        || (gc.getInput().isMouseButtonDown(0) && posX >= 20 && posX <= 20 + BUTTON_WIDTH && posY >= 20 && posY <= 20 + BUTTON_HEIGHT)) {
      s.getState(States.MENU).init(gc, s);
      s.enterState(States.MENU);
    }

  }

  @Override
  public int getID() {
    // TODO Auto-generated method stub
    return States.TWO_PLAYER_START_SERVER;
  }

}
