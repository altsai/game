package highscore;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class Main {
  public static void main (String args[]) {
    try {
      //			HighscoreSystem system = new HighscoreSystem("test.txt", 2);
      //			System.out.println(system.getLocalScores().toString());
      //			system.addLocalScore(801);
      //			System.out.println(system.getLocalScores().toString());


      String url = "jdbc:mysql://cs32db.csox7dghpjsn.us-east-1.rds.amazonaws.com:3306/";
      String userName = "cs32user";
      String password = "cs32pass";
      String dbName = "cs32db";
      String driver = "com.mysql.jdbc.Driver";
      Class.forName(driver);
      Connection connection = DriverManager.getConnection(url + dbName, userName, password);
      HighscoreSystem system = new HighscoreSystem("test.txt", 2, connection);
      printGlobalScores(system.getGlobalScores(), 0);
      system.addGlobalScore("KingBen", 20000);
      printGlobalScores(system.getGlobalScoresOfDay(), 1);
      printGlobalScores(system.getGlobalScoresOfMonth(), 2);
      printGlobalScores(system.getGlobalScoresOfYear(), 3);
      //printScore(system.getScoreFromName("Ben2"));
    } catch (IOException | SQLException | ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private static void printGlobalScores(List<String[]> list, int timeFrame) {
    if (timeFrame == 0) {
      System.out.println("-------Current Global High Scores------");
    } else if (timeFrame == 1) {
      System.out.println("-------Current Global High Scores Of The Day------");
    } else if (timeFrame == 2) {
      System.out.println("-------Current Global High Scores Of The Month------");
    } else if (timeFrame == 3) {
      System.out.println("-------Current Global High Scores Of The Year------");
    }
    for (String[] score : list) {
      System.out.println(score[0] + ": " + score[1] + " - " + score[2] + " AT " + score[3]);
    }
    System.out.println("--------------");
  }

  private static void printScore(String[] score) {
    if (score != null) {
      System.out.println(score[0] + ": " + score[1] + " - " + score[2] + " AT " + score[3]);
    }
  }
}
