package emanondev.deepquests.interfaces;

import emanondev.core.CorePlugin;
import emanondev.core.YMLConfig;
import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.Permission;

import java.io.File;
import java.util.Collection;

public interface QuestManager<T extends User<T>> extends GuiElement {

    /**
     * name of this, must be unique and immutable, used for folder name
     *
     * @return
     */
    String getName();

    /**
     * @return require manager of this
     */
    RequireProvider<T> getRequireProvider();

    /**
     * @return reward manager of this
     */
    RewardProvider<T> getRewardProvider();

    /**
     * @return task manager of this
     */
    TaskProvider<T> getTaskProvider();

    /**
     * @return user manager of this
     */
    UserManager<T> getUserManager();

    void save();

    YMLConfig getConfig();

    YMLConfig getConfig(String fileName);

    File getFolder();

    BossBarManager<T> getBossBarManager();

    int getPriority();

    Collection<Quest<T>> getQuests();

    Quest<T> getQuest(int id);

    Collection<Reward<T>> getRewards();

    Collection<Require<T>> getRequires();

    Quest<T> createQuest(OfflinePlayer author);

    Mission<T> createMission(Quest<T> quest, OfflinePlayer author);

    Task<T> createTask(Mission<T> mission, TaskType<T> type, OfflinePlayer author);

    Reward<T> createReward(RewardType<T> type, OfflinePlayer author);

    Require<T> createRequire(RequireType<T> type, OfflinePlayer author);

    void linkRequire(Require<T> require, Quest<T> quest);

    void unlinkRequire(Require<T> require, Quest<T> quest);

    void linkRequire(Require<T> require, Mission<T> mission);

    void unlinkRequire(Require<T> require, Mission<T> mission);

    void linkCompleteReward(Reward<T> reward, Mission<T> mission);

    void unlinkCompleteReward(Reward<T> reward, Mission<T> mission);

    void linkStartReward(Reward<T> reward, Mission<T> mission);

    void unlinkStartReward(Reward<T> reward, Mission<T> mission);

    void linkFailReward(Reward<T> reward, Mission<T> mission);

    void unlinkFailReward(Reward<T> reward, Mission<T> mission);

    void linkProgressReward(Reward<T> reward, Task<T> task);

    void unlinkProgressReward(Reward<T> reward, Task<T> task);

    void linkCompleteReward(Reward<T> reward, Task<T> task);

    void unlinkCompleteReward(Reward<T> reward, Task<T> task);

    void delete(Reward<T> reward);

    void delete(Require<T> require);

    void delete(Mission<T> mission);

    void delete(Quest<T> quest);

    void delete(Task<T> task);

    void reload();

    /**
     * @param user
     * @return
     */
    Integer getDefaultMissionLimit(T user);

    Mission<T> getMission(int id);

    Task<T> getTask(int id);

    Reward<T> getReward(int id);

    Require<T> getRequire(int id);

    Collection<Mission<T>> getMissions();

    Collection<Task<T>> getTasks();

    void disable();

    /**
     * Utility for command to manipulate user, user must be returned from string
     * the string may not represent the user id, usually it's something human readable
     * like player name for players instead of their uuid
     *
     * @param argument
     * @return the related user, may be null
     */
    T getArgomentUser(String argument);

    /**
     * Utility for command to autocomplete user, must return a collection of users arguments taken by command
     *
     * @return collection of users arguments taken by command
     */
    Collection<String> getUsersArguments();

    CorePlugin getPlugin();

    Permission getEditorPermission();


}