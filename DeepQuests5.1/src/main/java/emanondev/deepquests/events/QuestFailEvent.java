package emanondev.deepquests.events;

import org.bukkit.event.HandlerList;

import emanondev.deepquests.interfaces.Quest;
import emanondev.deepquests.interfaces.User;

public class QuestFailEvent<T extends User<T>> extends UserEvent<T> {
	private static final HandlerList handlers = new HandlerList();
	public HandlerList getHandlers() {
	    return handlers;
	}
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	private final Quest<T> quest;
	public QuestFailEvent(T user, Quest<T> quest) {
		super(user);
		this.quest = quest;
	}
	public Quest<T> getQuest(){
		return quest;
	}
}
