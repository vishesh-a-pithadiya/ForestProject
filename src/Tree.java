import processing.core.PImage;

import java.util.List;

public class Tree extends ActionEntity implements WoodyInterface{
    private int health;

    public Tree(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int health) {
        super(id, position, images, animationPeriod, actionPeriod);
        this.health = health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getHealth() {
        return health;
    }
    @Override
    public Point getPosition() {
        return this.position;
    }
    public boolean transformPlant(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.health <= 0) {
            Entity stump = new Stump(this.id, this.position, imageStore.getImageList("stump"));

            world.removeEntity(scheduler, this);

            world.addEntity(stump);

            return true;
        }

        return false;
    }
    @Override
    public void executeActivityAction(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        if (!transformPlant(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, Action.createActivityAction(this, world, imageStore), this.actionPeriod);
        }
    }
}
