package emanondev.deepquests.data;

import emanondev.core.YMLSection;
import emanondev.deepquests.interfaces.Navigable;
import emanondev.deepquests.interfaces.QuestComponent;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.User;
import org.jetbrains.annotations.NotNull;

public abstract class QuestComponentData<T extends User<T>, E extends QuestComponent<T>> implements Navigable {
    private final E parent;
    private final YMLSection section;

    public QuestComponentData(@NotNull E parent,@NotNull YMLSection section) {
        this.parent = parent;
        this.section = section;
    }

    public @NotNull YMLSection getConfig() {
        return section;
    }

    public E getParent() {
        return parent;
    }

    public QuestManager<T> getQuestManager() {
        return parent.getManager();
    }

}