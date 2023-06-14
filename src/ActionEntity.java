import processing.core.PImage;

import java.util.List;

public abstract class ActionEntity extends AnimationEntity{
    protected double actionPeriod;

    public ActionEntity(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod) {
        super(id, position, images, animationPeriod);
        this.actionPeriod = actionPeriod;
    }
    public double getActionPeriod() {
        return actionPeriod;
    }
    @Override
    public void scheduleActions(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler) {
        eventScheduler.scheduleEvent(this, Action.createActivityAction(this, world, imageStore), getActionPeriod());
        eventScheduler.scheduleEvent(this, Action.createAnimationAction(this,0), getAnimationPeriod());
    }

    public abstract void executeActivityAction( WorldModel world,ImageStore imageStore, EventScheduler scheduler);
}
