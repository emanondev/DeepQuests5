package emanondev.deepquests.interfaces;

import emanondev.core.YMLConfig;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface UserManager<T extends User<T>> {
    /**
     * @return an immutable Collection of all loaded Users
     */
    Collection<T> getUsers();

    /**
     * @param id
     * @return the user with user.getUID().equals(id)
     */
    T getUser(String id);

    /**
     * save on disk all users progress
     */
    default void saveAll() {
        for (T user : new ArrayList<>(getUsers()))
            user.saveOnDisk();
    }

    /**
     * @return the quest manager
     */
    QuestManager<T> getManager();

    /**
     * save all users progress
     * and reload them from disk
     */
    void reload();

    /**
     * @param p
     * @return the user with user.getPlayers().contains(p) == true or null
     */
    T getUser(Player p);


    default List<String> getUsersUIDs() throws Exception {
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

    default File getUserFile(String uid) {
        return new File(getManager().getFolder(), "users_database" + File.separator + uid + ".yml");
    }

    default YMLConfig getUserConfig(String uid) {
        return getManager().getConfig("users_database" + File.separator + uid + ".yml");
    }

    @Deprecated
    default void saveUser(T user) {
        user.getConfig().save();
    }

}
