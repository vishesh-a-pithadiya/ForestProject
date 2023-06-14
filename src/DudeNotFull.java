import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class DudeNotFull extends ActionEntity implements Dude{
    private final int resourceLimit;
    private int resourceCount;

    public DudeNotFull(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int resourceLimit, int resourceCount){
        super(id, position, images, animationPeriod, actionPeriod);
        this.resourceCount = resourceCount;
        this.resourceLimit = resourceLimit;
    }
    public void executeActivityAction( WorldModel world,ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = world.findNearest(this.position, new ArrayList<>(Arrays.asList(Tree.class))); // , Sapling.class) removed sapling from the things to be search so saplings aren't cut down


        if (target.isEmpty() || !moveToNotFull(world, (WoodyInterface) target.get(), scheduler) || !transformNotFull(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, Action.createActivityAction(this, world, imageStore), this.actionPeriod);
        }
    }
    public boolean transformNotFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.resourceCount >= this.resourceLimit) {
            DudeFull dude =  new DudeFull(id, position, images, animationPeriod, actionPeriod, resourceLimit);

            world.removeEntity(scheduler, this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(dude);
            dude.scheduleActions(world, imageStore, scheduler);
            return true;
        }
        return false;
    }
    public Point nextPositionDude(WorldModel world, Point destPos) {
        PathingStrategy pStrat = new AStarPathingStrategy();
        Predicate<Point> canPassThrough= (Point point) -> !((world.isOccupied(point) && world.getOccupancyCell(point).getClass() != Stump.class) || (!world.withinBounds(point)));
        BiPredicate<Point, Point> withinReach = (Point point1, Point point2) -> point1.distance(point2) <= 1.1;
        List<Point> path = pStrat.computePath( this.position, destPos, canPassThrough, withinReach, PathingStrategy.CARDINAL_NEIGHBORS);
        if (path.size() == 0) {
            return this.position;
        }
        else {
//            System.out.println(path.get(0));
            return path.get(0);
        }

//        int horiz = Integer.signum(destPos.getX() - this.position.getX());
//        Point newPos = new Point(this.position.getX() + horiz, this.position.getY());
//
//        if (horiz == 0 || world.isOccupied(newPos) && world.getOccupancyCell(newPos).getClass() != Stump.class) {
//            int vert = Integer.signum(destPos.getY() - this.position.getY());
//            newPos = new Point(this.position.getX(), this.position.getY() + vert);
//
//            if (vert == 0 || world.isOccupied(newPos) &&  world.getOccupancyCell(newPos).getClass() != Stump.class) {
//                newPos = this.position;
//            }
//        }
//        return newPos;
    }
    public boolean moveToNotFull(WorldModel world, WoodyInterface target, EventScheduler scheduler) {
        if (this.position.adjacent(target.getPosition())) {
            this.resourceCount += 1;
            target.setHealth(target.getHealth()-1);
            return true;
        } else {
            Point nextPos = this.nextPositionDude(world, target.getPosition());

            if (!this.position.equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }


}