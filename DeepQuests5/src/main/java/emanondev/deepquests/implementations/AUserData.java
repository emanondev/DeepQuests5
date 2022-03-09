package emanondev.deepquests.implementations;

import emanondev.core.YMLSection;
import emanondev.deepquests.interfaces.Navigable;
import emanondev.deepquests.interfaces.User;

public abstract class AUserData<T extends User<T>> implements Navigable {

    private final T user;
    protected final YMLSection section;

    public AUserData(T user, YMLSection section) {
        if (user == null || section == null)
            throw new NullPointerException();
        this.user = user;
        this.section = section;
    }

    public T getUser() {
        return user;
    }

    public YMLSection getConfig() {
        return section;
    }

}
