package emanondev.deepquests.events;

import emanondev.deepquests.interfaces.Quest;
import emanondev.deepquests.interfaces.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class QuestCompleteEvent<T extends User<T>> extends UserEvent<T> {
    private static final HandlerList handlers = new HandlerList();

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final Quest<T> quest;

    public QuestCompleteEvent(T user, Quest<T> quest) {
        super(user);
        this.quest = quest;
    }

    public Quest<T> getQuest() {
        return quest;
    }
}