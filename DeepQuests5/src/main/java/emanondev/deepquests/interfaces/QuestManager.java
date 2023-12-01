package emanondev.deepquests.interfaces;

import emanondev.core.CorePlugin;
import emanondev.core.YMLConfig;
import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

public interface QuestManager<T extends User<T>> extends GuiElement {

    /**
     * name of this, must be unique and immutable, used for folder name
     *
     */
    @NotNull String getName();

    /**
     * @return require manager of this
     */
    @NotNull RequireProvider<T> getRequireProvider();

    /**
     * @return reward manager of this
     */
    @NotNull RewardProvider<T> getRewardProvider();

    /**
     * @return task manager of this
     */
    @NotNull TaskProvider<T> getTaskProvider();

    /**
     * @return user manager of this
     */
    @NotNull UserManager<T> getUserManager();

    void save();

    @NotNull YMLConfig getConfig();

    @NotNull YMLConfig getConfig(String fileName);

    @NotNull File getFolder();

    @NotNull BossBarManager<T> getBossBarManager();

    int getPriority();

    @NotNull Collection<Quest<T>> getQuests();

    Quest<T> getQuest(int id);

    @NotNull Collection<Reward<T>> getRewards();

    @NotNull Collection<Require<T>> getRequires();

    @NotNull Quest<T> createQuest(OfflinePlayer author);

    @NotNull Mission<T> createMission(Quest<T> quest, OfflinePlayer author);

    @NotNull Task<T> createTask(Mission<T> mission, TaskType<T> type, OfflinePlayer author);

    @NotNull Reward<T> createReward(RewardType<T> type, OfflinePlayer author);

    @NotNull Require<T> createRequire(RequireType<T> type, OfflinePlayer author);

    void linkRequire(@NotNull Require<T> require, @NotNull Quest<T> quest);

    void unlinkRequire(@NotNull Require<T> require, @NotNull Quest<T> quest);

    void linkRequire(@NotNull Require<T> require, @NotNull Mission<T> mission);

    void unlinkRequire(@NotNull Require<T> require, @NotNull Mission<T> mission);

    void linkCompleteReward(@NotNull Reward<T> reward, @NotNull Mission<T> mission);

    void unlinkCompleteReward(@NotNull Reward<T> reward, @NotNull Mission<T> mission);

    void linkStartReward(@NotNull Reward<T> reward, @NotNull Mission<T> mission);

    void unlinkStartReward(@NotNull Reward<T> reward, @NotNull Mission<T> mission);

    void linkFailReward(@NotNull Reward<T> reward, @NotNull Mission<T> mission);

    void unlinkFailReward(@NotNull Reward<T> reward, @NotNull Mission<T> mission);

    void linkProgressReward(@NotNull Reward<T> reward, @NotNull Task<T> task);

    void unlinkProgressReward(@NotNull Reward<T> reward, @NotNull Task<T> task);

    void linkCompleteReward(@NotNull Reward<T> reward, @NotNull Task<T> task);

    void unlinkCompleteReward(@NotNull Reward<T> reward, @NotNull Task<T> task);

    void delete(@NotNull Reward<T> reward);

    void delete(@NotNull Require<T> require);

    void delete(@NotNull Mission<T> mission);

    void delete(@NotNull Quest<T> quest);

    void delete(@NotNull Task<T> task);

    void reload();

    /**
     * @return null if no limit
     */
    @Nullable Integer getDefaultMissionLimit(T user);

    @Nullable Mission<T> getMission(int id);

    @Nullable Task<T> getTask(int id);

    @Nullable Reward<T> getReward(int id);

    @Nullable Require<T> getRequire(int id);

    @NotNull Collection<Mission<T>> getMissions();

    @NotNull Collection<Task<T>> getTasks();

    void disable();

    /**
     * Utility for command to manipulate user, user must be returned from string
     * the string may not represent the user id, usually it's something human readable
     * like player name for players instead of their uuid
     *
     * @return the related user, may be null
     */
    @Nullable T getArgomentUser(String argument);

    /**
     * Utility for command to autocomplete user, must return a collection of users arguments taken by command
     *
     * @return collection of users arguments taken by command
     */
    @NotNull Collection<String> getUsersArguments();

    @NotNull CorePlugin getPlugin();

    @NotNull Permission getEditorPermission();

    default void debugUnused() {
        SortedSet<Integer> rewardIds = new TreeSet<>();
        SortedSet<Integer> requireIds = new TreeSet<>();
        getRewards().forEach(r->rewardIds.add(r.getID()));
        getRequires().forEach(r->requireIds.add(r.getID()));
        for (Task<T> task : getTasks()) {
            task.getCompleteRewards();
            for (Reward<T> rew : task.getCompleteRewards())
                rewardIds.remove(rew.getID());
            task.getProgressRewards();
            for (Reward<T> rew : task.getProgressRewards())
                rewardIds.remove(rew.getID());
        }
        for (Mission<T> m : getMissions()) {
            for (Reward<T> rew : m.getCompleteRewards())
                rewardIds.remove(rew.getID());
            for (Reward<T> rew : m.getFailRewards())
                rewardIds.remove(rew.getID());
            for (Reward<T> rew : m.getStartRewards())
                rewardIds.remove(rew.getID());
            for (Require<T> rew : m.getRequires())
                requireIds.remove(rew.getID());
        }
        for (Quest<T> q : getQuests()) {
            q.getRequires();
            for (Require<T> rew : q.getRequires())
                requireIds.remove(rew.getID());
        }

        for (int id : rewardIds)
            getPlugin().logInfo("on manager &e"+getName()+ "&f reward &e" + id + "&f is unused");
        for (int id : requireIds)
            getPlugin().logInfo("on manager &e"+getName()+ "&f require &e" + id + "&f is unused");
    }

}