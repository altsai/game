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
      images.put("player", loadImage("img/wasd_to_live_hero_small.png"));
      images.put("player2", loadImage("img/player2.png"));
      images.put("zombie", loadImage("img/wasd_zombie_v3_small.png"));
      images.put("life", loadImage("img/wasd_life.png"));
      images.put("invinciblePlayer", loadImage("img/PlayerInvincible.png"));
      images.put("bomb", loadImage("img/wasd_bomb.png"));
      images.put("speed", loadImage("img/wasd_speed.png"));
      images.put("timestop", loadImage("img/wasd_timestop.png"));
      images.put("background", loadImage("img/wasd_background_blue_2.png"));
      images.put("key", loadImage("img/blank_key.png"));
      images.put("w_key", loadImage("img/W_key.png"));
      images.put("a_key", loadImage("img/A_key.png"));
      images.put("s_key", loadImage("img/S_key.png"));
      images.put("d_key", loadImage("img/D_key.png"));
      images.put("blue_arrow", loadImage("img/wasd_blue_arrow.png"));
      images
          .put("playerLarge", loadImage("img/wasd_to_live_hero_large_70.png"));
      images.put("button", loadImage("img/wasd_button.png"));
      images.put("buttonSingle", loadImage("img/wasd_button_singleplayer.png"));
      images.put("buttonMulti", loadImage("img/wasd_button_multiplayer.png"));
      images.put("buttonRank", loadImage("img/wasd_button_rankings.png"));
      images.put("buttonAbout", loadImage("img/wasd_button_about.png"));
      images.put("buttonLocal", loadImage("img/wasd_button_local.png"));
      images.put("buttonYear", loadImage("img/wasd_button_year.png"));
      images.put("buttonMonth", loadImage("img/wasd_button_month.png"));
      images.put("buttonToday", loadImage("img/wasd_button_today.png"));
      images.put("buttonAllTime", loadImage("img/wasd_button_alltime.png"));
      sprites.put("bomb_explosion",
          loadSprite("img/bomb_explosion.png", 196, 190));
      sprites.put("injuredAnimation",
          loadSprite("img/wasd_injured_sprite.png", 30, 30));
      images.put("blackhole", loadImage("img/wasd_blackhole.png"));
      images.put("blackholeLarge", loadImage("img/wasd_blackhole_large_2.png"));
      images.put("fire", loadImage("img/wasd_fire.png"));
      images.put("jail", loadImage("img/jail.png"));
      images.put("firezombie", loadImage("img/firezombie.png"));
      images.put("laserbeam", loadImage("img/laserbeam.png"));
      images.put("particle", loadImage("img/particle.png"));
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
