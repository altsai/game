package highscore;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalHandler {

  private Connection conn;
  private List<Score> highscores;
  private Map<String, Score> nameMap;

  /**
   * Creates a new GlobalHandler.
   *
   * @param conn - the connection to the db
   * @throws SQLException - if there was an error interfacing with the database
   */
  protected GlobalHandler(Connection conn) throws SQLException {
    this.conn = conn;
    this.highscores = new ArrayList<>();
    this.nameMap = new HashMap<>();

    collectScores();
  }

  /**
   * Gets the current highscore list.
   *
   * @return the current list of highscores (ordered - highest to lowest)
   */
  protected List<Score> getScores() {
    return Collections.unmodifiableList(highscores);
  }

  /**
   * Gets the current highscore list, including only scores of the current day.
   *
   * @return the current list of highscores, only of scores of the current day
   */
  protected List<Score> getScoresOfDay() {
    // Set variables
    List<Score> toReturn = new ArrayList<>();
    int currPlace = 1;

    // Get current date
    Date currentDate = getCurrentDate();
    Calendar cal = Calendar.getInstance();
    cal.setTime(currentDate);
    int currYear = cal.get(Calendar.YEAR);
    int currMonth = cal.get(Calendar.MONTH);
    int currDay = cal.get(Calendar.DAY_OF_MONTH);

    for (Score score : highscores) {
      // Get score variables
      cal.setTime(score.getDate());
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH);
      int day = cal.get(Calendar.DAY_OF_MONTH);

      if (year == currYear && month == currMonth && day == currDay) {
        toReturn.add(new Score(score.getName(), score.getScore(), currPlace, score.getDate()));
        currPlace++;
      }
    }

    return toReturn;
  }

  /**
   * Gets the current highscore list, including only scores of the current month.
   *
   * @return the current list of highscores, only of scores of the current month
   */
  protected List<Score> getScoresOfMonth() {
    List<Score> toReturn = new ArrayList<>();
    int currPlace = 1;

    // Get current date
    Date currentDate = getCurrentDate();
    Calendar cal = Calendar.getInstance();
    cal.setTime(currentDate);
    int currYear = cal.get(Calendar.YEAR);
    int currMonth = cal.get(Calendar.MONTH);

    for (Score score : highscores) {
      // Get score variables
      cal.setTime(score.getDate());
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH);

      if (year == currYear && month == currMonth) {
        toReturn.add(new Score(score.getName(), score.getScore(), currPlace, score.getDate()));
        currPlace++;
      }
    }

    return toReturn;
  }

  /**
   * Gets the current highscore list, including only scores of the current year.
   *
   * @return the current list of highscores, only of scores of the current year
   */
  protected List<Score> getScoresOfYear() {
    List<Score> toReturn = new ArrayList<>();
    int currPlace = 1;

    // Get current date
    Date currentDate = getCurrentDate();
    Calendar cal = Calendar.getInstance();
    cal.setTime(currentDate);
    int currYear = cal.get(Calendar.YEAR);

    for (Score score : highscores) {
      // Get score variables
      cal.setTime(score.getDate());
      int year = cal.get(Calendar.YEAR);

      if (year == currYear) {
        toReturn.add(new Score(score.getName(), score.getScore(), currPlace, score.getDate()));
        currPlace++;
      }
    }

    return toReturn;
  }

  /**
   * Gets the sorted scores from the database.
   *
   * @throws SQLException - if there was an error interfacing with the database
   */
  private void collectScores() throws SQLException {
    // Query to get all (name, score) pairs
    String query = "SELECT * FROM highscores ORDER BY score DESC";
    PreparedStatement prep = conn.prepareStatement(query);
    ResultSet rs = prep.executeQuery();

    // Add all Scores to the list, and (name -> Score) map
    highscores = new ArrayList<>();
    for (int i = 1; rs.next(); i++) {
      // Create the Score
      String name = rs.getString(1);
      int score = rs.getInt(2);
      Date date = rs.getDate(3);
      Score scoreObj = new Score(name, score, i, date);

      // Add it to the data structures
      highscores.add(scoreObj);
      nameMap.put(name, scoreObj);
    }
  }

  /**
   * Gets the current date as a java.sql.Date.
   *
   * @return the current date
   */
  private Date getCurrentDate() {
    return new Date(new java.util.Date().getTime());
  }

  /**
   * Adds a given (name, score) pair to the database, if the name isn't already there.
   *
   * @param name - the given name
   * @param score - the given score
   * @return a boolean indicating whether or not the addition was successful
   * @throws SQLException - if there was an error interfacing with the database
   */
  protected boolean addScore(String name, int score) throws SQLException {
    // Query to check if name is already in the highscores
    String query = "SELECT * FROM highscores WHERE name = ?";
    PreparedStatement prep = conn.prepareStatement(query);
    prep.setString(1, name);
    ResultSet rs = prep.executeQuery();

    // If name isn't there, then insert it
    boolean toReturn = false;
    if (!rs.next()) {
      // SQL to insert name and score
      String insert = "INSERT INTO highscores VALUES (?, ?, ?)";
      PreparedStatement prep2 = conn.prepareStatement(insert);
      prep2.setString(1, name);
      prep2.setInt(2, score);
      prep2.setDate(3, getCurrentDate());
      prep2.execute();
      prep2.close();

      collectScores();

      toReturn = true;
    }

    // Close rs and prep
    rs.close();
    prep.close();

    return toReturn;
  }

  /**
   * Gets the score corresponding to the given name (will be null if name isn't in the scores).
   *
   * @param name - the given name
   * @return the Score relating to the given name (or null)
   */
  protected Score getScoreFromName(String name) {
    return nameMap.get(name);
  }
}
