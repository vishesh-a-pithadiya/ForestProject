import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class DudeFull extends ActionEntity implements Dude{
    protected int resourceLimit;
    protected int resourceCount;

    public DudeFull(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int resourceLimit) {
        super(id, position, images, animationPeriod, actionPeriod);
        this.resourceCount = 0;
        this.resourceLimit = resourceLimit;
    }
    @Override
    public void executeActivityAction( WorldModel world,ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget = world.findNearest(this.position, new ArrayList<>(List.of(House.class)));

        if (fullTarget.isPresent() && moveToFull(world, fullTarget.get(), scheduler)) {
            transformFull(world, scheduler, imageStore);
        } else {
            scheduler.scheduleEvent(this, Action.createActivityAction(this, world, imageStore), this.actionPeriod);
        }
    }
    public void transformFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        DudeNotFull dude = new DudeNotFull(this.id, this.position, this.images, this.animationPeriod, this.actionPeriod, this.resourceLimit,this.resourceCount);
        world.removeEntity(scheduler, this);

        world.addEntity(dude);
        dude.scheduleActions(world, imageStore, scheduler);
    }
    public Point nextPositionDude(WorldModel world, Point destPos) {
        PathingStrategy pStrat = new AStarPathingStrategy();
        Predicate<Point>  canPassThrough= (Point point) -> !((world.isOccupied(point) && world.getOccupancyCell(point).getClass() != Stump.class) || (!world.withinBounds(point)));
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
    public boolean moveToFull(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.position.adjacent(target.position)) {
            return true;
        } else {
            Point nextPos = this.nextPositionDude(world, target.position);

            if (!this.position.equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

}
