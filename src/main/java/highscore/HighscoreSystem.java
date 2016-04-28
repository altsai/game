package highscore;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HighscoreSystem {

  private LocalHandler localHandler;
  private GlobalHandler globalHandler;

  /**
   * Creates a new local HighscoreSystem.
   *
   * @param filename - the name of the local highscore file
   * @param numScores - the number of scores to store
   * @throws IOException - if there was an error interfacing with the file
   */
  public HighscoreSystem(String filename, int numScores) throws IOException {
    localHandler = new LocalHandler(filename, numScores);
  }

  /**
   * Creates a new local and global HighscoreSystem.
   *
   * @param filename - the name of the local highscore file
   * @param numScores - the number of scores to store
   * @param conn - the connection to the database
   * @throws IOException - if there was an error interfacing with the file
   */
  public HighscoreSystem(String filename, int numScores, Connection conn) throws IOException, SQLException {
    this.localHandler = new LocalHandler(filename, numScores);
    this.globalHandler = new GlobalHandler(conn);
  }

  /**
   * Adds a given score to the local highscores.
   *
   * @param score - the score to be added
   * @return a boolean indicating whether or not this score is the new local best
   * @throws IOException - if there was an error interfacing with the file
   */
  public boolean addLocalScore(int score) throws IOException {
    return localHandler.addScore(score);
  }

  /**
   * Gets the current local highscore list.
   *
   * @return the current list of highscores (ordered - highest to lowest)
   */
  public List<Integer> getLocalScores() {
    return localHandler.getScores();
  }

  /**
   * Adds the given (name, score) pair to the global highscores.
   *
   * @param name - the given name
   * @param score - the given score
   * @return a boolean indicating whether or not the (name, score) was successfully added
   * @throws SQLException - if there was an error interfacing with the database
   */
  public boolean addGlobalScore(String name, int score) throws SQLException {
    return globalHandler.addScore(name, score);
  }

  private List<String[]> scoresToStrings(List<Score> scores) {
    // Fill the array with String[] of each Score
    List<String[]> toReturn = new ArrayList<>();
    for (Score score : scores) {
      toReturn.add(new String[]{Integer.toString(score.getPlace()), score.getName(), Integer.toString(score.getScore()), score.getDate().toString()});
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
      toReturn = new String[]{Integer.toString(score.getPlace()), score.getName(), Integer.toString(score.getScore()), score.getDate().toString()};
    }

    return toReturn;
  }
}
