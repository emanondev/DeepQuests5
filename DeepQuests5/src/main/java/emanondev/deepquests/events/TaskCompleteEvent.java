package emanondev.deepquests.events;

import emanondev.deepquests.interfaces.Task;
import emanondev.deepquests.interfaces.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TaskCompleteEvent<T extends User<T>> extends UserEventWithRewards<T> {
    private static final HandlerList handlers = new HandlerList();

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final Task<T> task;

    public TaskCompleteEvent(T user, Task<T> task) {
        super(user, task.getCompleteRewards());
        this.task = task;
    }

    public Task<T> getTask() {
        return task;
    }
}