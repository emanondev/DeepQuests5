package emanondev.deepquests.implementations;

import emanondev.core.YMLSection;
import emanondev.deepquests.interfaces.Navigable;
import emanondev.deepquests.interfaces.User;
import org.jetbrains.annotations.NotNull;

public abstract class AUserData<T extends User<T>> implements Navigable {

    protected final YMLSection section;
    private final T user;

    public AUserData(@NotNull T user, @NotNull YMLSection section) {
        this.user = user;
        this.section = section;
    }

    public @NotNull T getUser() {
        return user;
    }

    public @NotNull YMLSection getConfig() {
        return section;
    }

}
