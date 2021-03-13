package emanondev.deepquests.nation;

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
import com.palmergames.bukkit.towny.event.DeleteNationEvent;
import com.palmergames.bukkit.towny.event.NewNationEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Nation;

import emanondev.deepquests.Quests;
import emanondev.deepquests.implementations.AUserManager;

public class NationUserManager extends AUserManager<QuestNation> implements Listener {

	private Map<Nation, QuestNation> users = new HashMap<>();

	public NationUserManager(NationQuestManager questManager) {
		super(questManager);
		Bukkit.getPluginManager().registerEvents(this, Quests.get());
	}

	@Override
	public NationQuestManager getManager() {
		return (NationQuestManager) super.getManager();
	}

	@Override
	public Collection<QuestNation> getUsers() {
		return Collections.unmodifiableCollection(users.values());
	}

	@Override
	public QuestNation getUser(String uuid) {
		for (Nation nation : users.keySet())
			if (nation.getUuid().toString().equals(uuid))
				return users.get(nation);
		return null;
	}

	@Override
	public QuestNation getUser(Player p) {
		try {
			Resident r = TownyAPI.getInstance().getDataSource().getResident(p.getName());
			return r.hasNation() ? users.get(r.getTown().getNation()) : null;
		} catch (Exception e) {
			return null;
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onNationCreate(NewNationEvent event) {
		QuestNation questUser = new QuestNation(this, event.getNation());
		users.put(event.getNation(), questUser);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onNationDelete(DeleteNationEvent event) {
		for (Nation nation : users.keySet()) {
			if (nation.getName().equals(event.getNationName())) {
				QuestNation user = users.remove(nation);
				if (user!=null)
					user.saveOnDisk();
			}
		}
	}

	@Override
	public void reload() {
		saveAll();
		users.clear();
		for (Nation nation : TownyAPI.getInstance().getDataSource().getNations()) {
			if (!users.containsKey(nation)) {
				QuestNation questUser = new QuestNation(this, nation);
				users.put(nation, questUser);
			}
		}

	}

	public QuestNation getUser(Nation n) {
		return n == null? null : users.get(n);
	}

}