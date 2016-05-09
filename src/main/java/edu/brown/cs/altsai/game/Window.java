package edu.brown.cs.altsai.game;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Describes the window object in which the game is played.
 *
 * @author bl48
 *
 */
public class Window {
  // fields that describe width and height
  // Big size = 1390 x 859
  // Small size = 1132 x 700
  public static int width = 1132;
  public static int height = 700;

  // Automatically set width and height based on screen size
  static {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    double screenWidth = screenSize.getWidth();
    double screenHeight = screenSize.getHeight();
    if (screenWidth >= 1440 && screenHeight >= 909) {
      width = 1390;
      height = 859;
    } else {
      width = 1132;
      height = 700;
    }
  }
}
