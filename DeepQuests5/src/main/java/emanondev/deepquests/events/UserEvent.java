package emanondev.deepquests.events;

import emanondev.deepquests.interfaces.User;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public abstract class UserEvent<T extends User<T>> extends Event {
    private final T user;

    public UserEvent(@NotNull T user) {
        this.user = user;
    }

    public @NotNull T getUser() {
        return user;
    }

}
