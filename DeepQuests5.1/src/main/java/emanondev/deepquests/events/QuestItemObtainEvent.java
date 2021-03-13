package emanondev.deepquests.events;

import org.bukkit.event.HandlerList;

import emanondev.deepquests.interfaces.User;

public class QuestItemObtainEvent<T extends User<T>> extends UserEvent<T> {
	private static final HandlerList handlers = new HandlerList();
	public HandlerList getHandlers() {
	    return handlers;
	}
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	private final String id;
	private final int amount;
	public QuestItemObtainEvent(T user,String id,int amount) {
		super(user);
		this.id = id;
		this.amount = amount;
	}
	public String getID(){
		return id;
	}
	public int getAmount(){
		return amount;
	}
}
