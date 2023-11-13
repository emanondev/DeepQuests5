package emanondev.deepquests.events;

import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.User;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MissionStartEvent<T extends User<T>> extends UserEventWithRewards<T> implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Mission<T> mission;
    private boolean cancelled = false;

    public MissionStartEvent(T user, Mission<T> mission) {
        super(user, mission.getStartRewards());
        this.mission = mission;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public Mission<T> getMission() {
        return mission;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}