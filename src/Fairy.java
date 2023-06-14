import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Fairy extends ActionEntity{
    public Fairy(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod) {
        super(id, position, images, animationPeriod, actionPeriod);
    }

    public void executeActivityAction(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = world.findNearest(this.position, new ArrayList<>(List.of(Stump.class)));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().position;

            if (moveToFairy(world, fairyTarget.get(), scheduler)) {
                // health equals 0, health limit is 5
                Sapling sapling = new Sapling(fairyTarget.get().id, tgtPos, imageStore.getImageList("sapling"), 1.000,1.000,0,5);

                world.addEntity(sapling);
                sapling.scheduleActions(world, imageStore, scheduler);
            }
        }

        scheduler.scheduleEvent(this, Action.createActivityAction(this, world, imageStore), this.actionPeriod);
    }

    public boolean moveToFairy(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.position.adjacent(target.position)) {
            world.removeEntity(scheduler, target);
            return true;
        } else {
            Point nextPos = this.nextPositionFairy(world, target.position);

            if (!this.position.equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    public Point nextPositionFairy(WorldModel world, Point destPos) {
        PathingStrategy pStrat = new AStarPathingStrategy();
        Predicate<Point> canPassThrough = (Point point) -> !(world.isOccupied(point) || !world.withinBounds(point));
        BiPredicate<Point, Point> withinReach = (Point point1, Point point2) -> point1.distance(point2) <= 1.1;
        List<Point> path = pStrat.computePath(this.position, destPos, canPassThrough, withinReach, PathingStrategy.CARDINAL_NEIGHBORS);

        if (path.size() <= 0) {
            return this.position;
        }
        else {
            return path.get(0);
        }

//        int horiz = Integer.signum(destPos.getX() - this.position.getX());
//        Point newPos = new Point(this.position.getX() + horiz, this.position.getY());
//
//        if ((horiz == 0 || world.isOccupied(newPos))  && (world.getOccupancyCell(newPos).getClass() != House.class)){
//            int vert = Integer.signum(destPos.getY() - this.position.getY());
//            newPos = new Point(this.position.getX(), this.position.getY() + vert);
//
//            if ((vert == 0 || world.isOccupied(newPos)) && (world.getOccupancyCell(newPos).getClass() != House.class)) {
//                newPos = this.position;
//            }
//        }
//
//        return newPos;
    }

}
