package emanondev.deepquests.events;

import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MissionFailEvent<T extends User<T>> extends UserEventWithRewards<T> {
    private static final HandlerList handlers = new HandlerList();
    private final Mission<T> mission;

    public MissionFailEvent(T user, Mission<T> mission) {
        super(user, mission.getFailRewards());
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
}