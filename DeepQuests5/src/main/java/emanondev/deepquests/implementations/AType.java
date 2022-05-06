package emanondev.deepquests.implementations;

import emanondev.deepquests.interfaces.QuestComponent;
import emanondev.deepquests.interfaces.QuestComponentType;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.User;
import org.jetbrains.annotations.NotNull;

public abstract class AType<T extends User<T>, E extends QuestComponent<T>> implements QuestComponentType<T, E> {

    private final String ID;

    private final QuestManager<T> manager;

    public AType(String id, QuestManager<T> manager) {
        if (id == null || manager == null)
            throw new NullPointerException();
        if (id.isEmpty() || !Paths.ALPHANUMERIC.matcher(id).matches())
            throw new IllegalArgumentException("Invalid Id");
        this.ID = id;
        this.manager = manager;
    }

    @Override
    public final @NotNull String getKeyID() {
        return ID;
    }

    @Override
    public QuestManager<T> getManager() {
        return manager;
    }
}