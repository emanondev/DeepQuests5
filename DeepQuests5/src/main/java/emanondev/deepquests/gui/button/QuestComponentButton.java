package emanondev.deepquests.gui.button;

import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.interfaces.QuestComponent;
import org.jetbrains.annotations.NotNull;

public abstract class QuestComponentButton<T extends QuestComponent<?>> extends AButton implements Comparable<QuestComponentButton<T>> {

    private final T questComponent;

    public QuestComponentButton(Gui parent, T questComponent) {
        super(parent);
        this.questComponent = questComponent;
    }

    public T getQuestComponent() {
        return questComponent;
    }

    @Override
    public int compareTo(@NotNull QuestComponentButton<T> o) {
        if (o == null)
            return -questComponent.getPriority();
        if (o.questComponent.getPriority() - questComponent.getPriority() != 0)
            return o.questComponent.getPriority() - questComponent.getPriority();
        return questComponent.getID() - o.questComponent.getID();
    }


}