import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public abstract class Entity {
    protected String id;
    protected Point position;
    protected List<PImage> images;
    protected int imageIndex;
//    private double actionPeriod;
//    protected double animationPeriod;
//    private int health;
//    private int healthLimit;

    public Entity(String id, Point position, List<PImage> images) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
    }

    protected String getId() {
        return id;
    }
    protected Point getPosition() {
        return position;
    }
    protected void setPosition(Point position) {
        this.position = position;
    }
    public void nextImage() {
        this.imageIndex = this.imageIndex + 1;
    }
    public void tryAddEntity(WorldModel world) {
        if (world.isOccupied(this.position)) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        world.addEntity(this);
    }

    /**
     * Helper method for testing. Preserve this functionality while refactoring.
     */
    public String log(){
        return this.id.isEmpty() ? null :
                String.format("%s %d %d %d", this.id, this.position.getX(), this.position.getY(), this.imageIndex);
    }
    public PImage getCurrentImage() {
        return images.get(imageIndex % images.size());
    }
}
