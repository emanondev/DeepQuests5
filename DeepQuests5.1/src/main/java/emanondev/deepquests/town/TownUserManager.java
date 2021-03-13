package emanondev.deepquests.town;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.DeleteTownEvent;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import emanondev.deepquests.Quests;
import emanondev.deepquests.implementations.AUserManager;

public class TownUserManager extends AUserManager<QuestTown> implements Listener {

	private Map<Town, QuestTown> users = new HashMap<>();

	public TownUserManager(TownQuestManager questManager) {
		super(questManager);
		Bukkit.getPluginManager().registerEvents(this, Quests.get());
	}

	@Override
	public TownQuestManager getManager() {
		return (TownQuestManager) super.getManager();
	}

	@Override
	public Collection<QuestTown> getUsers() {
		return Collections.unmodifiableCollection(users.values());
	}

	@Override
	public QuestTown getUser(String uuid) {
		for (Town town : users.keySet())
			if (town.getUuid().toString().equals(uuid))
				return users.get(town);
		return null;
	}

	@Override
	public QuestTown getUser(Player p) {
		try {
			Resident r = TownyAPI.getInstance().getDataSource().getResident(p.getName());
			return r.hasTown() ? users.get(r.getTown()) : null;
		} catch (Exception e) {
			return null;
		}
	}
	
	public QuestTown getUser(Town t) {
		return t == null? null : users.get(t);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onTownCreate(NewTownEvent event) {
		QuestTown questUser = new QuestTown(this, event.getTown());
		users.put(event.getTown(), questUser);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onTownDelete(DeleteTownEvent event) {
		for (Town town : users.keySet()) {
			if (town.getName().equals(event.getTownName())) {
				QuestTown user = users.remove(town);
				if (user!=null)
					user.saveOnDisk();
			}
		}
	}

	@Override
	public void reload() {
		saveAll();
		users.clear();
		for (Town town : TownyAPI.getInstance().getDataSource().getTowns()) {
			if (!users.containsKey(town)) {
				QuestTown questUser = new QuestTown(this, town);
				users.put(town, questUser);
			}
		}

	}

}