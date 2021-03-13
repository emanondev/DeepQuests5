package emanondev.deepquests.implementations;

import java.util.*;

import org.bukkit.Bukkit;

import emanondev.core.YMLSection;
import emanondev.deepquests.events.QuestItemObtainEvent;
import emanondev.deepquests.interfaces.*;

public class AQuestBag<T extends User<T>> implements QuestBag<T> {

	private final T user;
	private final YMLSection section;

	public AQuestBag(T user,YMLSection section) {
		if (section==null || user == null)
			throw new NullPointerException();
		this.user = user;
		this.section = section;
	}
	
	public YMLSection getConfig() {
		return section;
	}
	
	@Override
	public T getUser() {
		return user;
	}

	@Override
	public void addQuestItem(String id, int amount) {
		if (amount<0)
			throw new IllegalArgumentException();
		if (amount==0)
			return;
		section.set(id, section.getInteger(id,0)+amount);
		Bukkit.getPluginManager().callEvent(new QuestItemObtainEvent<T>(user,id,amount));
	}

	@Override
	public boolean hasQuestItem(String id, int amount) {
		return section.getInteger(id, 0)>0;
	}

	@Override
	public int removeQuestItem(String id, int amount) {
		if (amount<0)
			throw new IllegalArgumentException();
		if (amount==0)
			return 0;
		int own = getQuestItemAmount(id);
		if (own<=amount) {
			section.set(id, null);
			return own;
		}
		section.set(id,own-amount);
		return amount;
	}
	
	@Override
	public int getQuestItemAmount(String id) {
		return section.getInteger(id,0);
	}

	@Override
	public Map<String, Integer> getQuestItems() {
		LinkedHashMap<String,Integer> items = new LinkedHashMap<>();
		for (String id:section.getKeys(false)) {
			int amount = getQuestItemAmount(id);
			if (amount>0)
				items.put(id, amount);
		}
		return items;
	}

	@Override
	public void reset() {
		for (String id:section.getKeys(false)) 
			section.set(id,null);
	}

}
