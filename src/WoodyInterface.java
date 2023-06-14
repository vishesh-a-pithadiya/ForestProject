public interface WoodyInterface {
    int getHealth();
    Point getPosition();
    void setHealth(int health);
    boolean transformPlant(WorldModel world, EventScheduler scheduler, ImageStore imageStore);
}
