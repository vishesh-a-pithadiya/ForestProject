import processing.core.PImage;

import java.util.List;

public class Sapling extends ActionEntity implements WoodyInterface{

    private int health;
    private final int healthLimit;

    public Sapling(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int health, int healthLimit) {
        super(id, position, images, animationPeriod, actionPeriod);
        this.health = health;
        this.healthLimit = healthLimit;
    }

    public boolean transformPlant(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.health <= 0) {
            Entity stump = new Stump(this.id, this.position, imageStore.getImageList("stump"));

            world.removeEntity(scheduler, this);

            world.addEntity(stump);

            return true;
        } else if (this.health >= this.healthLimit) {
            Tree tree = new Tree(this.id, this.position, imageStore.getImageList("tree"), Functions.getNumFromRange(0.600, 0.050), Functions.getNumFromRange(1.400, 1.000),  Functions.getIntFromRange(3, 1));

            world.removeEntity(scheduler, this);

            world.addEntity(tree);
            tree.scheduleActions(world, imageStore, scheduler);

            return true;
        }

        return false;
    }

    public int getHealth() {
        return health;
    }
    @Override
    public Point getPosition() {
        return this.position;
    }
    @Override
    public void setHealth(int health) {
        this.health = health;

    }
    @Override
    public void executeActivityAction(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        this.health++;
        if (!transformPlant(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, Action.createActivityAction(this, world, imageStore), this.actionPeriod);
        }
    }
}
