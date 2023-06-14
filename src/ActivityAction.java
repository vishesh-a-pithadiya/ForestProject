public class ActivityAction extends Action{
    private final ImageStore imageStore;
    private final WorldModel world;

    public ActivityAction(ActionEntity entity, WorldModel world, ImageStore imageStore) {
        super(entity);
        this.imageStore = imageStore;
        this.world = world;
    }
    @Override
    public void executeAction(EventScheduler scheduler) {
        ((ActionEntity) entity).executeActivityAction(world, imageStore, scheduler);
    }
}
