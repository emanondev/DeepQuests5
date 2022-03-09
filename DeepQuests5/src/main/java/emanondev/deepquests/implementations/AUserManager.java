package emanondev.deepquests.implementations;

import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.interfaces.UserManager;

public abstract class AUserManager<T extends User<T>> implements UserManager<T> {

    private final QuestManager<T> manager;

    public AUserManager(QuestManager<T> manager) {
        if (manager == null)
            throw new NullPointerException();
        this.manager = manager;
    }

    @Override
    public QuestManager<T> getManager() {
        return manager;
    }

}
