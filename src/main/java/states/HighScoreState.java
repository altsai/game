package states;

import java.util.List;

import javax.swing.JOptionPane;

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
import highscore.HighscoreSystem;

/**
 * Defines the high score game state.
 *
 * Contains menus for high score data.
 *
 * @author bl48
 *
 */
public class HighScoreState extends BasicGameState {

  private HighscoreSystem highscoreSystem;

  private List<String[]> globalHighscores;
  private List<String[]> globalHighscoresOfDay;
  private List<String[]> globalHighscoresOfMonth;
  private List<String[]> globalHighscoresOfYear;
  private List<String[]> localHighscores;

  private static final int BUTTON_WIDTH = 180;
  private static final int BUTTON_HEIGHT = 68;

  private static int HIGHEST_NUM = 28;
  static {
    if (Window.width == 1390) {
      HIGHEST_NUM = 28;
    } else {
      HIGHEST_NUM = 20;
    }
  }

  private int highlightedNum;
  private boolean invalidSearch;

  private TrueTypeFont headerFont;
  private TrueTypeFont entryFont;

  private static final int PADDING = 15;
  private int placeWidth;
  private int nameWidth;
  private int scoreWidth;
  private int timeWidth;
  private int dateWidth;
  private int tableWidth;

  private double startingEntryIndex = 0;
  private List<String[]> scoresInUse;
  private String inUseString;
  private boolean localScoresInUse;
  private boolean canScrollUp;
  private boolean canScrollDown;

  private Image arrowImage;

  private boolean initializedSearchField;
  private TextField searchField;

  public HighScoreState(HighscoreSystem highscoreSystem) {
    this.highscoreSystem = highscoreSystem;

    headerFont = Resources.getDefaultFont(40);

    entryFont = Resources.getDefaultFont(10);

    arrowImage = Resources.getImage("gray_arrow");

    scoreWidth = entryFont.getWidth("18446744073709551615") + PADDING;
    dateWidth = entryFont.getWidth("2016-12-31") + PADDING;
  }

  @Override
  public void init(GameContainer gc, StateBasedGame s) throws SlickException {
    // Get global highscores and local highscores
    if (highscoreSystem.isGlobal()) {
      globalHighscores = highscoreSystem.getGlobalScores();
      globalHighscoresOfDay = highscoreSystem.getGlobalScoresOfDay();
      globalHighscoresOfMonth = highscoreSystem.getGlobalScoresOfMonth();
      globalHighscoresOfYear = highscoreSystem.getGlobalScoresOfYear();
    }
    localHighscores = highscoreSystem.getLocalScores();

    if (!initializedSearchField) {
      TrueTypeFont searchFont = Resources.getDefaultFont(8);
      searchField = new TextField(gc, searchFont, Window.width / 2
          - (300 + entryFont.getWidth("Search for a name: ")) / 2
          + entryFont.getWidth("Search for a name: "), 20
          + headerFont.getLineHeight() + 10 + BUTTON_HEIGHT + 10, 300,
          entryFont.getLineHeight());
      initializedSearchField = true;
    }

    searchField.setAcceptingInput(false);
    searchField.setText("");

    setTable("local");
    canScrollUp = false;
    canScrollDown = false;
  }

  private void setTable(String type) {
    startingEntryIndex = 0;
    searchField.setText("");
    searchField.setFocus(false);
    highlightedNum = -1;
    invalidSearch = false;

    if (type.equals("local")) {
      localScoresInUse = true;
      searchField.setAcceptingInput(false);
      inUseString = "";

      // Set widths
      placeWidth = entryFont.getWidth(Integer.toString(localHighscores.size()))
          + PADDING;
      timeWidth = getTimeWidthLocal() + PADDING;
      tableWidth = placeWidth + scoreWidth + timeWidth + dateWidth;
    } else {
      localScoresInUse = false;
      searchField.setAcceptingInput(true);
      inUseString = type;

      // Set scoresInUse
      if (type.equals("year")) {
        scoresInUse = globalHighscoresOfYear;
      } else if (type.equals("month")) {
        scoresInUse = globalHighscoresOfMonth;
      } else if (type.equals("day")) {
        scoresInUse = globalHighscoresOfDay;
      } else {
        scoresInUse = globalHighscores;
      }

      // Get widths
      placeWidth = entryFont
          .getWidth(Integer.toString(globalHighscores.size())) + PADDING;
      setNameTimeWidths();
      tableWidth = placeWidth + nameWidth + scoreWidth + timeWidth + dateWidth;
    }
  }

  private int getTimeWidthLocal() {
    int max = 0;
    for (String[] score : localHighscores) {
      max = Math.max(max, entryFont.getWidth(score[3]));
    }

    return max;
  }

  private void setNameTimeWidths() {
    int maxLengthName = 0;
    int maxLengthTime = 0;
    for (String[] score : globalHighscores) {
      maxLengthName = Math.max(maxLengthName, entryFont.getWidth(score[1]));
      maxLengthTime = Math.max(maxLengthTime, entryFont.getWidth(score[3]));
    }

    nameWidth = maxLengthName + PADDING;
    timeWidth = maxLengthTime + PADDING;
  }

  @Override
  public void render(GameContainer gc, StateBasedGame s, Graphics g)
      throws SlickException {
    g.drawImage(Resources.getImage("background"), 0, 0);

    // Draw title
    headerFont.drawString(Window.width / 2 - headerFont.getWidth("High Scores")
        / 2, 20, "High Scores", Color.white);

    // Draw buttons
    float buttonsX = Window.width / 2 - (BUTTON_WIDTH * 5 + 20 * 5) / 2;
    float buttonsY = 20 + headerFont.getLineHeight() + 10;
    Resources.getImage("buttonLocal").draw(buttonsX, buttonsY, BUTTON_WIDTH,
        BUTTON_HEIGHT);
    Resources.getImage("buttonYear").draw(buttonsX + BUTTON_WIDTH + 20,
        buttonsY, BUTTON_WIDTH, BUTTON_HEIGHT);
    Resources.getImage("buttonMonth").draw(
        buttonsX + BUTTON_WIDTH * 2 + 20 * 2, buttonsY, BUTTON_WIDTH,
        BUTTON_HEIGHT);
    Resources.getImage("buttonToday").draw(
        buttonsX + BUTTON_WIDTH * 3 + 20 * 3, buttonsY, BUTTON_WIDTH,
        BUTTON_HEIGHT);
    Resources.getImage("buttonAllTime").draw(
        buttonsX + BUTTON_WIDTH * 4 + 20 * 4, buttonsY, BUTTON_WIDTH,
        BUTTON_HEIGHT);

    // Draw search bar
    entryFont.drawString(
        Window.width / 2 - (300 + entryFont.getWidth("Search for a name: "))
        / 2, 20 + headerFont.getLineHeight() + 10 + BUTTON_HEIGHT + 10,
        "Search for a name: ", Color.white);
    g.setColor(Color.white);
    searchField.render(gc, g);
    if (localScoresInUse) {
      searchField.setBackgroundColor(Color.gray);
    } else {
      searchField.setBackgroundColor(Color.white);
    }
    searchField.setBorderColor(Color.black);
    searchField.setTextColor(Color.black);

    // Draw 'invalid search' if applicable
    if (invalidSearch) {
      entryFont.drawString(
          Window.width / 2 - (300 + entryFont.getWidth("Search for a name: "))
          / 2 + entryFont.getWidth("Search for a name: ") + 300 + 10, 20
          + headerFont.getLineHeight() + 10 + BUTTON_HEIGHT + 10,
          "No Results!", Color.red);
    }

    // Get some variables
    float tableX = (Window.width - tableWidth) / 2;
    float tableY = 20 + headerFont.getLineHeight() + 10 + BUTTON_HEIGHT + 10
        + entryFont.getLineHeight() + 20;
    g.setColor(Color.black);

    float normalTableHeight = Window.height - tableY
        - entryFont.getLineHeight() - 15;
    if (Window.height == 700) {
      normalTableHeight = Window.height - tableY - entryFont.getLineHeight()
          * 2 - 15;
    }

    g.setColor(Color.gray);

    // Draw the highscores table (local or global)
    if ((localScoresInUse && (localHighscores == null || localHighscores.size() == 0))
        || (!localScoresInUse && (scoresInUse == null || scoresInUse.size() == 0))) {
      String toDraw = "No Highscores!";
      entryFont.drawString(Window.width / 2 - entryFont.getWidth(toDraw) / 2,
          tableY, toDraw, Color.white);
    } else if (localScoresInUse) {
      // Get table height
      float tableHeight = Math.min(normalTableHeight,
          (entryFont.getLineHeight() + 5) * localHighscores.size());

      // Outline of table
      g.drawRect(tableX, tableY, tableWidth, tableHeight);

      // Separator lines
      g.drawLine(tableX + placeWidth, tableY, tableX + placeWidth, tableY
          + tableHeight);
      g.drawLine(tableX + placeWidth + scoreWidth, tableY, tableX + placeWidth
          + scoreWidth, tableY + tableHeight);
      g.drawLine(tableX + placeWidth + scoreWidth + timeWidth, tableY, tableX
          + placeWidth + scoreWidth + timeWidth, tableY + tableHeight);

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
      if ((entryFont.getLineHeight() + 5)
          * (localHighscores.size() - entryIndex) > normalTableHeight) {
        canScrollDown = true;
        arrowImage.setRotation(90);
        arrowImage.draw(tableX + tableWidth + 20, tableY + tableHeight - 30,
            30, 30);
      } else {
        canScrollDown = false;
      }

      // Entries
      for (float y = tableY + 5; y < tableY + tableHeight
          && entryIndex < localHighscores.size(); y += entryFont
          .getLineHeight() + 5) {
        String[] score = localHighscores.get(entryIndex);

        // Draw place
        float x = tableX
            + (placeWidth - entryFont
                .getWidth(Integer.toString(entryIndex + 1))) / 2;
        entryFont.drawString(x, y, Integer.toString(entryIndex + 1),
            Color.white);

        // Draw score
        x = tableX + placeWidth + (scoreWidth - entryFont.getWidth(score[2]))
            / 2;
        entryFont.drawString(x, y, score[2], Color.white);

        // Draw time
        x = tableX + placeWidth + scoreWidth
            + (timeWidth - entryFont.getWidth(score[3])) / 2;
        entryFont.drawString(x, y, score[3], Color.white);

        // Draw date
        x = tableX + placeWidth + scoreWidth + timeWidth
            + (dateWidth - entryFont.getWidth(score[4])) / 2;
        entryFont.drawString(x, y, score[4], Color.white);

        // Draw separating line
        if (y + entryFont.getLineHeight() + 5 < tableY + tableHeight) {
          g.drawLine(tableX, y + entryFont.getLineHeight(),
              tableX + tableWidth, y + entryFont.getLineHeight());
        }

        entryIndex++;
      }
    } else {
      // Get table height
      float tableHeight = Math.min(normalTableHeight,
          (entryFont.getLineHeight() + 5) * scoresInUse.size());

      // Outline of table
      g.drawRect(tableX, tableY, tableWidth, tableHeight);

      // Separator lines
      g.drawLine(tableX + placeWidth, tableY, tableX + placeWidth, tableY
          + tableHeight);
      g.drawLine(tableX + placeWidth + nameWidth, tableY, tableX + placeWidth
          + nameWidth, tableY + tableHeight);
      g.drawLine(tableX + placeWidth + nameWidth + scoreWidth, tableY, tableX
          + placeWidth + nameWidth + scoreWidth, tableY + tableHeight);
      g.drawLine(tableX + placeWidth + nameWidth + scoreWidth + timeWidth,
          tableY, tableX + placeWidth + nameWidth + scoreWidth + timeWidth,
          tableY + tableHeight);
      g.drawLine(tableX + placeWidth + nameWidth + scoreWidth + timeWidth
          + dateWidth, tableY, tableX + placeWidth + nameWidth + scoreWidth
          + timeWidth + dateWidth, tableY + tableHeight);

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
      if ((entryFont.getLineHeight() + 5) * (scoresInUse.size() - entryIndex) > normalTableHeight) {
        canScrollDown = true;
        arrowImage.setRotation(90);
        arrowImage.draw(tableX + tableWidth + 20, tableY + tableHeight - 30,
            30, 30);
      } else {
        canScrollDown = false;
      }

      // Entries
      Color color = Color.black;
      for (float y = tableY + 5; y < tableY + tableHeight
          && entryIndex < scoresInUse.size(); y += entryFont.getLineHeight() + 5) {
        String[] score = scoresInUse.get(entryIndex);
        if (Integer.parseInt(score[0]) == highlightedNum) {
          color = Color.red;
        } else {
          color = Color.white;
        }

        // Draw place
        float x = tableX + (placeWidth - entryFont.getWidth(score[0])) / 2;
        entryFont.drawString(x, y, score[0], color);

        // Draw name
        x = tableX + placeWidth + (nameWidth - entryFont.getWidth(score[1]))
            / 2;
        entryFont.drawString(x, y, score[1], color);

        // Draw score
        x = tableX + placeWidth + nameWidth
            + (scoreWidth - entryFont.getWidth(score[2])) / 2;
        entryFont.drawString(x, y, score[2], color);

        // Draw time
        x = tableX + placeWidth + nameWidth + scoreWidth
            + (timeWidth - entryFont.getWidth(score[3])) / 2;
        entryFont.drawString(x, y, score[3], color);

        // Draw date
        x = tableX + placeWidth + nameWidth + scoreWidth + timeWidth
            + (dateWidth - entryFont.getWidth(score[4])) / 2;
        entryFont.drawString(x, y, score[4], color);

        // Draw separating line
        if (y + entryFont.getLineHeight() + 5 < tableY + tableHeight) {
          g.drawLine(tableX, y + entryFont.getLineHeight(),
              tableX + tableWidth, y + entryFont.getLineHeight());
        }

        entryIndex++;
      }
    }

    // Main menu button
    Resources.getImage("buttonMainMenu").draw(20, 20, BUTTON_WIDTH,
        BUTTON_HEIGHT);
  }

  @Override
  public void update(GameContainer gc, StateBasedGame s, int delta)
      throws SlickException {
    // Get x and y mouse position coordinates
    int posX = gc.getInput().getMouseX();
    int posY = gc.getInput().getMouseY();

    // Check if in the correct y coordinates
    boolean inY = false;
    if (gc.getInput().isMouseButtonDown(0)
        && posY >= 20 + headerFont.getLineHeight() + 10
        && posY <= 20 + headerFont.getLineHeight() + 10 + BUTTON_HEIGHT) {
      inY = true;
    }
    int buttonsX = Window.width / 2 - (BUTTON_WIDTH * 5 + 20 * 5) / 2;

    // Changing highscore displays
    if ((gc.getInput().isKeyPressed(Input.KEY_1) && !searchField.hasFocus())
        || (inY && posX >= buttonsX && posX <= buttonsX + BUTTON_WIDTH)) {
      setTable("local");
    }
    if ((gc.getInput().isKeyPressed(Input.KEY_2) && !searchField.hasFocus())
        || (inY && posX >= buttonsX + BUTTON_WIDTH + 20 && posX <= buttonsX
        + BUTTON_WIDTH * 2 + 20)) {
      if (highscoreSystem.isGlobal()) {
        setTable("year");
      } else {
        JOptionPane
        .showMessageDialog(
            null,
            "The game is running in offline mode.\nTo run in online mode, fix your internet connection and restart the game.",
            "Connection Error", JOptionPane.ERROR_MESSAGE);
      }
    }
    if ((gc.getInput().isKeyPressed(Input.KEY_3) && !searchField.hasFocus())
        || (inY && posX >= buttonsX + BUTTON_WIDTH * 2 + 20 * 2 && posX <= buttonsX
        + BUTTON_WIDTH * 3 + 20 * 2)) {
      if (highscoreSystem.isGlobal()) {
        setTable("month");
      } else {
        JOptionPane
        .showMessageDialog(
            null,
            "The game is running in offline mode.\nTo run in online mode, fix your internet connection and restart the game.",
            "Connection Error", JOptionPane.ERROR_MESSAGE);
      }
    }
    if ((gc.getInput().isKeyPressed(Input.KEY_4) && !searchField.hasFocus())
        || (inY && posX >= buttonsX + BUTTON_WIDTH * 3 + 20 * 3 && posX <= buttonsX
        + BUTTON_WIDTH * 4 + 20 * 3)) {
      if (highscoreSystem.isGlobal()) {
        setTable("day");
      } else {
        JOptionPane
        .showMessageDialog(
            null,
            "The game is running in offline mode.\nTo run in online mode, fix your internet connection and restart the game.",
            "Connection Error", JOptionPane.ERROR_MESSAGE);
      }
    }
    if ((gc.getInput().isKeyPressed(Input.KEY_5) && !searchField.hasFocus())
        || (inY && posX >= buttonsX + BUTTON_WIDTH * 4 + 20 * 4 && posX <= buttonsX
        + BUTTON_WIDTH * 5 + 20 * 4)) {
      if (highscoreSystem.isGlobal()) {
        setTable("all-time");
      } else {
        JOptionPane
        .showMessageDialog(
            null,
            "The game is running in offline mode.\nTo run in online mode, fix your internet connection and restart the game.",
            "Connection Error", JOptionPane.ERROR_MESSAGE);
      }
    }

    // Moving up and down
    if (gc.getInput().isKeyDown(Input.KEY_UP) && canScrollUp) {
      startingEntryIndex -= .2;
    } else if (gc.getInput().isKeyDown(Input.KEY_DOWN) && canScrollDown) {
      startingEntryIndex += .2;
    }

    // Searching
    if (gc.getInput().isKeyDown(Input.KEY_ENTER) && searchField.hasFocus()
        && !inUseString.equals("")) {
      String[] score = highscoreSystem.getScoreFromName(searchField.getText(),
          inUseString);
      if (score != null) {
        invalidSearch = false;
        int place = Integer.parseInt(score[0]);
        if (place <= HIGHEST_NUM) {
          startingEntryIndex = 0;
        } else {
          startingEntryIndex = place - HIGHEST_NUM;
        }

        highlightedNum = place;
      } else {
        highlightedNum = -1;
        invalidSearch = true;
      }
    }

    // Back to main menu
    if (gc.getInput().isKeyPressed(Input.KEY_ESCAPE)
        || (gc.getInput().isMouseButtonDown(0) && posX >= 20
        && posX <= 20 + BUTTON_WIDTH && posY >= 20 && posY <= 20 + BUTTON_HEIGHT)) {
      s.enterState(States.MENU);
    }
  }

  @Override
  public int getID() {
    return States.HIGH_SCORES;
  }

}
