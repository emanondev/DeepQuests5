package emanondev.deepquests.events;

import emanondev.deepquests.interfaces.Task;
import emanondev.deepquests.interfaces.User;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TaskProgressEvent<T extends User<T>> extends UserEventWithRewards<T> implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Task<T> task;
    private final int limit;
    @Getter
    private int progress;
    @Setter
    @Getter
    private boolean cancelled = false;
    public TaskProgressEvent(@NotNull T user, @NotNull Task<T> task, int progress, int limit) {
        super(user, task.getProgressRewards());
        this.task = task;
        this.progress = progress;
        this.limit = limit;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public void setProgress(int amount) {
        progress = Math.clamp(amount, 0, limit);
    }

    public @NotNull Task<T> getTask() {
        return task;
    }

}
