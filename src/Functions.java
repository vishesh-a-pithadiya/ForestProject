import java.util.*;

import processing.core.PImage;
import processing.core.PApplet;

/**
 * This class contains many functions written in a procedural style.
 * You will reduce the size of this class over the next several weeks
 * by refactoring this codebase to follow an OOP style.
 */
public final class Functions {
//    private static final int COLOR_MASK = 0xffffff;
//    private static final int KEYED_IMAGE_MIN = 5;
//    private static final int KEYED_RED_IDX = 2;
//    private static final int KEYED_GREEN_IDX = 3;
//    private static final int KEYED_BLUE_IDX = 4;
//
//    private static final double SAPLING_ACTION_ANIMATION_PERIOD = 1.000; // have to be in sync since grows and gains health at same time
//    private static final int SAPLING_HEALTH_LIMIT = 5;
//
//    private static final int PROPERTY_KEY = 0;
//    public static final int PROPERTY_ID = 1;
//    public static final int PROPERTY_COL = 2;
//    public static final int PROPERTY_ROW = 3;
////    public static final int ENTITY_NUM_PROPERTIES = 4;
//
//    private static final String STUMP_KEY = "stump";
//    private static final int STUMP_NUM_PROPERTIES = 0;
//    private static final String SAPLING_KEY = "sapling";
//    private static final int SAPLING_HEALTH = 0;
//    private static final int SAPLING_NUM_PROPERTIES = 1;
//    private static final String OBSTACLE_KEY = "obstacle";
//    private static final int OBSTACLE_ANIMATION_PERIOD = 0;
//    private static final int OBSTACLE_NUM_PROPERTIES = 1;
//
//    private static final String DUDE_KEY = "dude";
//    private static final int DUDE_ACTION_PERIOD = 0;
//    private static final int DUDE_ANIMATION_PERIOD = 1;
//    private static final int DUDE_LIMIT = 2;
//    private static final int DUDE_NUM_PROPERTIES = 3;
//
//    private static final String HOUSE_KEY = "house";
//    private static final int HOUSE_NUM_PROPERTIES = 0;

//    private static final String FAIRY_KEY = "fairy";
//    private static final int FAIRY_ANIMATION_PERIOD = 0;
//    private static final int FAIRY_ACTION_PERIOD = 1;
//    private static final int FAIRY_NUM_PROPERTIES = 2;
//
//    private static final String TREE_KEY = "tree";
//    private static final int TREE_NUM_PROPERTIES = 3;
//
//    private static final double TREE_ANIMATION_MAX = 0.600;
//    private static final double TREE_ANIMATION_MIN = 0.050;
//    private static final double TREE_ACTION_MAX = 1.400;
//    private static final double TREE_ACTION_MIN = 1.000;
//    private static final int TREE_HEALTH_MAX = 3;
//    private static final int TREE_HEALTH_MIN = 1;

    public static int getIntFromRange(int max, int min) {
        Random rand = new Random();
        return min + rand.nextInt(max-min);
    }
    public static double getNumFromRange(double max, double min) {
        Random rand = new Random();
        return min + rand.nextDouble() * (max - min);
    }
    public static int clamp(int value, int low, int high) {
        return Math.min(high, Math.max(value, low));
    }
    public static void processImageLine(Map<String, List<PImage>> images, String line, PApplet screen) {
        String[] attrs = line.split("\\s");
        if (attrs.length >= 2) {
            String key = attrs[0];
            PImage img = screen.loadImage(attrs[1]);
            if (img != null && img.width != -1) {
                List<PImage> imgs = getImages(images, key);
                imgs.add(img);

                if (attrs.length >= 5) {
                    int r = Integer.parseInt(attrs[2]);
                    int g = Integer.parseInt(attrs[3]);
                    int b = Integer.parseInt(attrs[4]);
                    setAlpha(img, screen.color(r, g, b), 0);
                }
            }
        }
    }
    public static List<PImage> getImages(Map<String, List<PImage>> images, String key) {
        return images.computeIfAbsent(key, k -> new LinkedList<>());
    }

    /*
      Called with color for which alpha should be set and alpha value.
      setAlpha(img, color(255, 255, 255), 0));
    */
    public static void setAlpha(PImage img, int maskColor, int alpha) {
        int alphaValue = alpha << 24;
        int nonAlpha = maskColor & 0xffffff;
        img.format = PApplet.ARGB;
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++) {
            if ((img.pixels[i] & 0xffffff) == nonAlpha) {
                img.pixels[i] = alphaValue | nonAlpha;
            }
        }
        img.updatePixels();
    }

}
