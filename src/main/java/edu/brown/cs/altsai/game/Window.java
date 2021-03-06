package edu.brown.cs.altsai.game;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

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
  /**
   * Width of window.
   */
  public static int width = 1390;

  /**
   * Height of window.
   */
  public static int height = 859;

  /**
   * Width of screen.
   */
  public static double screenWidth;

  /**
   * Height of screen.
   */
  public static double screenHeight;

  /**
   * Screen dot per inch.
   */
  public static double screenDPI;

  /**
   * Pixel to feet conversion.
   */
  public static double feetPerPixel;

  // Automatically set width and height based on screen size
  static {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    screenWidth = screenSize.getWidth();
    screenHeight = screenSize.getHeight();
    if (screenWidth >= 1440 && screenHeight >= 909) {
      // Changed to make same screen size always
      width = 1132;
      height = 700;
    } else {
      width = 1132;
      height = 700;
    }

    screenDPI = Toolkit.getDefaultToolkit().getScreenResolution();
    feetPerPixel = 1 / (Math.sqrt(screenDPI) * 12 * 12);
  }

  /**
   * Displays message.
   *
   * @param title
   *          title of the message
   * @param message
   *          the message
   */
  public static void showMessage(String title, String message) {

    JFrame frmOpt = new JFrame();
    frmOpt.setVisible(true);
    frmOpt.setLocation((int) screenWidth / 2, (int) screenHeight / 2);
    frmOpt.setAlwaysOnTop(true);
    String[] options = { "Okay" };
    int response = JOptionPane.showOptionDialog(frmOpt, message, title,
        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options,
        "Okay");
    if (response == JOptionPane.YES_OPTION) {
      frmOpt.dispose();
    }

  }

  /**
   * Resize window dimensions.
   * 
   * @param widthNew
   *          the new width
   * @param heightNew
   *          the new height
   */
  public static void setWidthHeight(int widthNew, int heightNew) {
    width = widthNew;
    height = heightNew;
  }
}
