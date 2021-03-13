package emanondev.deepquests.interfaces;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.entity.Player;

import emanondev.core.YMLConfig;

public interface UserManager<T extends User<T>> {
	/**
	 * 
	 * @return an immutable Collection of all loaded Users
	 */
	public Collection<T> getUsers();
	/**
	 * 
	 * @param id
	 * @return the user with user.getUID().equals(id)
	 */
	public T getUser(String id);
	
	/**
	 * save on disk all users progress
	 */
	public default void saveAll() {
		for(T user:new ArrayList<>(getUsers()))
			user.saveOnDisk();
	}
	
	/**
	 * 
	 * @return the quest manager
	 */
	public QuestManager<T> getManager();
	
	/**
	 * save all users progress
	 * and reload them from disk
	 */
	public void reload();
	
	/**
	 * 
	 * @param p
	 * @return the user with user.getPlayers().contains(p) == true or null
	 */
	public T getUser(Player p);
	

	public default List<String> getUsersUIDs() throws Exception {
		File folder = new File(getManager().getFolder(), "users_database");
		ArrayList<String> names = new ArrayList<>();
		for (File file : folder.listFiles()) {
			if (file.isDirectory())
				continue;
			if (!file.getName().endsWith(".yml"))
				continue;
			names.add(file.getName().replace(".yml", ""));
		}
		return names;
	}

	public default File getUserFile(String uid) {
		return new File(getManager().getFolder(), "users_database" + File.separator + uid + ".yml");
	}

	public default YMLConfig getUserConfig(String uid) {
		return getManager().getConfig("users_database" + File.separator + uid + ".yml");
	}
	/*
	@Deprecated
	public default Navigator getUserNavigator(String uid) {
		return new ConfigFile(new File(getManager().getFolder(), "users_database" + File.separator + uid + ".yml")).getNavigator();
	}*/

	@Deprecated
	public default void saveUser(T user) {
		user.getConfig().save();
	}

}
