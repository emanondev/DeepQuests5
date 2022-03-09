package emanondev.deepquests.events;

import emanondev.deepquests.interfaces.User;
import org.bukkit.event.Event;

public abstract class UserEvent<T extends User<T>> extends Event {
    private final T user;

    public UserEvent(T user) {
        this.user = user;
    }

    public T getUser() {
        return user;
    }

}
