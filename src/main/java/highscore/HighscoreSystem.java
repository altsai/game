package highscore;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HighscoreSystem {

  public static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

  private LocalHandler localHandler;
  private GlobalHandler globalHandler;

  private boolean isGlobal;

  /**
   * Creates a new local HighscoreSystem.
   *
   * @param filename - the name of the local highscore file
   * @param numScores - the number of scores to store
   * @throws IOException - if there was an error interfacing with the file
   * @throws ParseException
   * @throws NumberFormatException
   */
  public HighscoreSystem(String filename, int numScores) throws IOException, NumberFormatException, ParseException {
    localHandler = new LocalHandler(filename, numScores);
    isGlobal = false;
  }

  /**
   * Creates a new local and global HighscoreSystem.
   *
   * @param filename - the name of the local highscore file
   * @param numScores - the number of scores to store
   * @param conn - the connection to the database
   * @throws IOException - if there was an error interfacing with the file
   * @throws ParseException
   * @throws NumberFormatException
   */
  public HighscoreSystem(String filename, int numScores, Connection conn) throws IOException, SQLException, NumberFormatException, ParseException {
    this.localHandler = new LocalHandler(filename, numScores);
    this.globalHandler = new GlobalHandler(conn);
    isGlobal = true;
  }

  /**
   * Gets if this HighscoreSystem is a global one or not.
   *
   * @return a boolean indicating whether or not this HighscoreSystem is global
   */
  public boolean isGlobal() {
    return isGlobal;
  }

  /**
   * Adds a given score to the local highscores.
   *
   * @param score - the score to be added
   * @return a boolean indicating whether or not this score is the new local best
   * @throws IOException - if there was an error interfacing with the file
   * @throws ParseException
   */
  public boolean addLocalScore(int score, int time) throws IOException, ParseException {
    return localHandler.addScore(score, time);
  }

  /**
   * Gets the current local highscore list.
   *
   * @return the current list of highscores (ordered - highest to lowest)
   */
  public List<String[]> getLocalScores() {
    return scoresToStrings(localHandler.getScores());
  }

  /**
   * Adds the given (name, score) pair to the global highscores.
   *
   * @param name - the given name
   * @param score - the given score
   * @param time - the given time
   * @return a boolean indicating whether or not the (name, score) was successfully added
   * @throws SQLException - if there was an error interfacing with the database
   */
  public boolean addGlobalScore(String name, int score, int time) throws SQLException {
    return globalHandler.addScore(name, score, time);
  }

  public static String timeToString(int time) {
    String timeString = "Unrecorded Time";
    if (time >= 60000 * 2) {
      long seconds = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));
      if (seconds >= 2 || seconds < 1) {
        timeString = String.format("%d minutes, %d seconds", TimeUnit.MILLISECONDS.toMinutes(time), seconds);
      } else {
        timeString = String.format("%d minutes, %d second", TimeUnit.MILLISECONDS.toMinutes(time), seconds);
      }
    } else if (time >= 60000) {
      long seconds = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));
      if (seconds >= 2 || seconds < 1) {
        timeString = String.format("%d minute, %d seconds", TimeUnit.MILLISECONDS.toMinutes(time), seconds);
      } else {
        timeString = String.format("%d minute, %d second", TimeUnit.MILLISECONDS.toMinutes(time), seconds);
      }
    } else if (time >= 1000 * 2) {
      timeString = String.format("%d seconds", TimeUnit.MILLISECONDS.toSeconds(time));
    } else if (time >= 1000) {
      timeString = "1 second";
    } else {
      timeString = "0 seconds";
    }

    return timeString;
  }

  private List<String[]> scoresToStrings(List<Score> scores) {
    // Fill the array with String[] of each Score
    List<String[]> toReturn = new ArrayList<>();
    for (Score score : scores) {
      toReturn.add(new String[]{Integer.toString(score.getPlace()), score.getName(), Integer.toString(score.getScore()), timeToString(score.getTime()), FORMAT.format(score.getDate())});
    }

    return toReturn;
  }

  /**
   * Get the list of global scores.
   *
   * @return The list of global highscores as a list of string arrays of size 3
   */
  public List<String[]> getGlobalScores() {
    return scoresToStrings(globalHandler.getScores());
  }

  /**
   * Get the list of global scores of the current day.
   *
   * @return The list of global highscores of the current day as a list of string arrays of size 3
   */
  public List<String[]> getGlobalScoresOfDay() {
    return scoresToStrings(globalHandler.getScoresOfDay());
  }

  /**
   * Get the list of global scores of the current month.
   *
   * @return The list of global highscores of the current month as a list of string arrays of size 3
   */
  public List<String[]> getGlobalScoresOfMonth() {
    return scoresToStrings(globalHandler.getScoresOfMonth());
  }

  /**
   * Get the list of global scores of the current year.
   *
   * @return The list of global highscores of the current year as a list of string arrays of size 3
   */
  public List<String[]> getGlobalScoresOfYear() {
    return scoresToStrings(globalHandler.getScoresOfYear());
  }

  /**
   * Gets the score associated with the given name.
   *
   * @param name - the given name
   * @param time - the time period to look in
   * @return the score associated with the given name as a string array of size 3 (or maybe null if the name doesn't have an associated global score)
   */
  public String[] getScoreFromName(String name, String time) {
    Score score = globalHandler.getScoreFromName(name, time);

    String[] toReturn = null;
    if (score != null) {
      toReturn = new String[]{Integer.toString(score.getPlace()), score.getName(), Integer.toString(score.getScore()), timeToString(score.getTime()), FORMAT.format(score.getDate())};
    }

    return toReturn;
  }
}
