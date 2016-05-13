package highscore;

import java.util.Date;

/**
 * Score object that represents an instanc of a game score.
 *
 */
public class Score {

  private String name;
  private int score;
  private int place;
  private int time;
  private Date date;

  /**
   * Creates a new Score.
   *
   * @param name - the name
   * @param score - the score
   * @param place - the place
   * @param time - the time
   * @param date - the date
   */
  protected Score(String name, int score, int place, int time, Date date) {
    this.name = name;
    this.score = score;
    this.place = place;
    this.time = time;
    this.date = date;
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  protected String getName() {
    return this.name;
  }

  /**
   * Gets the score.
   *
   * @return the score
   */
  protected int getScore() {
    return this.score;
  }

  /**
   * Gets the place.
   *
   * @return the place
   */
  protected int getPlace() {
    return this.place;
  }

  /**
   * Gets the time.
   *
   * @return the time
   */
  protected int getTime() {
    return this.time;
  }

  /**
   * Gets the date.
   *
   * @return the date
   */
  protected Date getDate() {
    return this.date;
  }
}
