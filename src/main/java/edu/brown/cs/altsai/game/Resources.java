package edu.brown.cs.altsai.game;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;

/**
 * Groups together graphics and sounds that the game uses.
 *
 * Resources is initialized as the beginning of the game with static fields that
 * provide easy access to specific files.
 *
 * @author bl48
 *
 */
public class Resources {
  private static Map<String, Image> images;
  private static Map<String, SpriteSheet> sprites;
  private static Map<String, Sound> sounds;

  /**
   * Constructor for a resources object.
   */
  public Resources() {
    // instantitate map of names to files
    Resources.images = new HashMap<>();
    Resources.sprites = new HashMap<>();
    Resources.sounds = new HashMap<>();

    // load in all the files that are initially needed
    try {
      images.put("player", loadImage("img/player1-2.png"));
      images.put("player2", loadImage("img/player2-2.png"));
      images.put("zombie", loadImage("img/enemyROUNDRED1.png"));
      images.put("life", loadImage("img/heart.png"));
      // images.put("invinciblePlayer", loadImage("img/PlayerInvincible.png"));
      images.put("bomb", loadImage("img/powerup-icon-bombred1.png"));
      images.put("speed", loadImage("img/powerup-icon-speedwhite2.png"));
      images.put("timestop", loadImage("img/powerup-icon-timestopwhite1.png"));
      images.put("blackhole", loadImage("img/powerup-icon-blkholewhite1.png"));
      images.put("fire", loadImage("img/powerup-icon-firewhite3.png"));
      images.put("jail", loadImage("img/powerup-icon-jailwhite2.png"));
      // images.put("firezombie", loadImage("img/firezombie.png"));
      images.put("laserbeam", loadImage("img/powerup-icon-laserwhite2.png"));
      images.put("button", loadImage("img/buttons/buttonrounded1.png"));
      images.put("buttonSingle", loadImage("img/buttons/spg_button.png"));
      images.put("buttonMulti", loadImage("img/buttons/omulti_button.png"));
      images.put("buttonRank", loadImage("img/buttons/rankings_button.png"));
      images.put("buttonAbout", loadImage("img/buttons/about_button.png"));
      images.put("buttonLocal", loadImage("img//buttons/local_button.png"));
      images.put("buttonYear", loadImage("img/buttons/gyear_button.png"));
      images.put("buttonMonth", loadImage("img/buttons/gmonth_button.png"));
      images.put("buttonToday", loadImage("img/buttons/gtoday_button.png"));
      images.put("buttonAllTime", loadImage("img/buttons/gall_button.png"));
      images.put("buttonMainMenu",
          loadImage("img/buttons/main_small_button.png"));
      images.put("buttonMainMenuLarge",
          loadImage("img/buttons/main_button.png"));
      images.put("buttonResume", loadImage("img/buttons/resume_button.png"));
      images.put("buttonMultiOffline",
          loadImage("img/buttons/offmulti_button.png"));
      images.put("background", loadImage("/img/background1-.png"));
      images.put("background-main", loadImage("img/preview1.png"));
      images.put("logo", loadImage("img/logo-4-1.png"));

      images.put("blue_arrow", loadImage("img/wasd_blue_arrow.png"));
      images.put("blackholeLarge", loadImage("img/wasd_blackhole_large_2.png"));
      images.put("particle", loadImage("img/particle.png"));
      images
          .put("playerLarge", loadImage("img/wasd_to_live_hero_large_70.png"));
      sprites.put("bomb_explosion",
          loadSprite("img/bomb_explosion.png", 196, 190));
      sprites.put("injuredAnimation",
          loadSprite("img/wasd_injured_sprite.png", 30, 30));
      images.put("particle_black", loadImage("img/particle_black.png"));
      sprites.put("bomb_explosion",
          loadSprite("img/bomb_explosion.png", 196, 190));
      sprites.put("bomb_explosion_2",
          loadSprite("img/wasd_bomb_animation_large_2.png", 500, 281));
      sprites.put("laser", loadSprite("img/wasd_laser_4_small.png", 75, 75));
      sprites.put("blackholeAnimation",
          loadSprite("img/wasd_blackhole_animation.png", 200, 200));
    } catch (SlickException e) {
      System.out.println("ERROR: No player.png found");
    }
  }

  /**
   * Method to load in a new image to be used.
   *
   * @param path
   *          String, filepath to the image
   * @return Image object
   * @throws SlickException
   *           On error accessing file
   */
  public static Image loadImage(String path) throws SlickException {
    return new Image(path, false, Image.FILTER_NEAREST);
  }

  /**
   * Method to load a sprite sheet into resources.
   *
   * @param path
   *          String filepath
   * @param tw
   *          Int, tile width
   * @param th
   *          Int, tile height
   * @return SpriteSheet
   * @throws SlickException
   *           On error accessing file
   */
  public static SpriteSheet loadSprite(String path, int tw, int th)
      throws SlickException {
    return new SpriteSheet(path, tw, th);
  }

  /**
   * Method to get a specific spritesheet from Resources.
   *
   * @param sprite
   *          String, identifier of a spritesheet
   * @return SpriteSheet
   */
  public static SpriteSheet getSprite(String sprite) {
    return sprites.get(sprite);
  }

  /**
   * Method to get a specific spritesheet tile from resources.
   *
   * @param sprite
   *          String, identifier of a spritesheet
   * @param x
   *          int, the tile column number
   * @param y
   *          int, the tile row number
   * @return Image, the specified tile of the spriteSheet
   */
  public static Image getSpriteImage(String sprite, int x, int y) {
    return sprites.get(sprite).getSubImage(x, y);
  }

  /**
   * Returns a specific image from the Resources.
   *
   * @param name
   *          String, identifier for an Image
   * @return Image
   */
  public static Image getImage(String name) {
    return images.get(name);
  }

  /**
   * Returns a specific sound from Resources.
   *
   * @param name
   *          String, name identifier of the sound
   * @return Sound
   */
  public static Sound getSound(String name) {
    return sounds.get(name);
  }

}
