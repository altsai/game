package highscore;

import java.util.Date;

public class Score {

  private String name;
  private int score;
  private int place;
  private Date date;

  /**
   * Creates a new Score.
   *
   * @param name - the name
   * @param score - the score
   * @param year - the year
   * @param month - the month
   * @param day - the day
   */
  protected Score(String name, int score, int place, Date date) {
    this.name = name;
    this.score = score;
    this.place = place;
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
   * Gets the date.
   *
   * @return the date
   */
  protected Date getDate() {
    return this.date;
  }
}
