package highscore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class LocalHandler {

  private File highscoresFile;
  private int numScores;
  private List<Score> highscores;

  public static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

  /**
   * Creates a new LocalHandler.
   *
   * @param filename - the name of the local highscore file
   * @param numScores - the number of scores to store
   * @throws IOException - if there was an error interfacing with the file
   * @throws ParseException
   * @throws NumberFormatException
   */
  protected LocalHandler(String filename, int numScores) throws IOException, NumberFormatException, ParseException {
    this.highscoresFile = new File(filename);
    this.numScores = numScores;

    // Check that numScores is greater than 0
    if (numScores <= 0) {
      throw new IllegalArgumentException("Number of scores must be greater than 0.");
    }

    this.highscores = new ArrayList<>(numScores);

    instantiateFile();
  }

  /**
   * Creates the file given by the highscores file, or reads in the data if it already exists.
   *
   * @throws IOException - if there was a problem interacting with the file
   * @throws ParseException
   * @throws NumberFormatException
   */
  private void instantiateFile() throws IOException, NumberFormatException, ParseException {
    if (!highscoresFile.createNewFile()) {
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(highscoresFile), "UTF-8"));
      readInData(br);
    }
  }

  /**
   * Sorts the highscore list and makes sure it has the correct number of elements.
   */
  private void sortAndClean() {
    // Sort the list
    highscores.sort((x, y) -> -(new Integer(x.getScore()).compareTo(new Integer(y.getScore()))));

    // localHighscores should contain the top numScores scores only
    if (highscores.size() > numScores) {
      highscores = new ArrayList<>(highscores.subList(0, numScores));
    }
  }

  /**
   * Gets the current date
   *
   * @return the current date
   */
  private Date getCurrentDate() {
    return new Date();
  }

  /**
   * Reads in the data in the file (if it existed).
   *
   * @param br - a BufferedReader for the file
   * @throws IOException - if there was an error interfacing with the file
   * @throws NumberFormatException - if there was an error parsing the file
   * @throws ParseException
   */
  private void readInData(BufferedReader br) throws IOException, NumberFormatException, ParseException {
    // Read through and store each score
    String line = br.readLine();
    while (line != null && !line.equals("")) {
      String[] lineSplit = line.split("\\*\\*\\*");
      int score = Integer.parseInt(lineSplit[0]);
      int time = Integer.parseInt(lineSplit[1]);
      DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
      Date date = format.parse(lineSplit[2]);
      highscores.add(new Score("", score, 0, time, date));
      line = br.readLine();
    }

    br.close();
    sortAndClean();
  }

  /**
   * Writes the current highscore list to the file.
   *
   * @throws IOException - if there was an error interfacing with the file
   */
  private void writeScores() throws IOException {
    // Create the string to write
    StringBuilder scoreString = new StringBuilder();
    for (Score score : highscores) {
      scoreString.append(score.getScore() + "***" + score.getTime() + "***" + FORMAT.format(score.getDate()) + "\n");
    }

    // Write to the file and close the writer
    BufferedWriter writer = new BufferedWriter(new FileWriter(highscoresFile, false));
    writer.write(scoreString.toString());
    writer.flush();
    writer.close();
  }

  /**
   * Adds a given score to the local highscores.
   *
   * @param score - the score to be added
   * @return a boolean indicating whether or not this score is the new best
   * @throws IOException - if there was an error interfacing with the file
   * @throws ParseException
   */
  protected boolean addScore(int score, int time) throws IOException, ParseException {
    // Check if this score is the new best
    boolean toReturn = false;
    if (highscores.size() == 0 || score > highscores.get(0).getScore()) {
      toReturn = true;
    }

    // Add the score and clean/write
    highscores.add(new Score("", score, 0, time, getCurrentDate()));
    sortAndClean();
    writeScores();

    return toReturn;
  }

  /**
   * Gets the current highscore list.
   *
   * @return the current list of highscores (ordered - highest to lowest)
   */
  protected List<Score> getScores() {
    return Collections.unmodifiableList(highscores);
  }
}
