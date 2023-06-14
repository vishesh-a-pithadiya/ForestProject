/**
 * An action that can be taken by an entity
 */
public abstract class Action {
    protected AnimationEntity entity;
    public Action(AnimationEntity entity) {
        this.entity = entity;
    }

    protected abstract void executeAction(EventScheduler scheduler);
    public static Action createActivityAction(ActionEntity entity, WorldModel world, ImageStore imageStore) {
        return new ActivityAction(entity, world, imageStore);
    }
    public static Action createAnimationAction(AnimationEntity entity, int repeatCount) {
        return new AnimationAction(entity, repeatCount);
    }
}
