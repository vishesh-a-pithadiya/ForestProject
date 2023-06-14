public class AnimationAction extends Action {
    private final int repeatCount;

    public AnimationAction( AnimationEntity entity, int repeatCount) {
        super(entity);
        this.repeatCount = repeatCount;
    }
    @Override
    protected void executeAction(EventScheduler scheduler) {
        this.entity.nextImage();

        if (this.repeatCount != 1) {
            scheduler.scheduleEvent(entity, createAnimationAction(entity, Math.max(repeatCount - 1, 0)), entity.getAnimationPeriod());
        }
    }
}
