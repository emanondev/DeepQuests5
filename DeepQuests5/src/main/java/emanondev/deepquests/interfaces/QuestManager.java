package emanondev.deepquests.interfaces;

import emanondev.core.CorePlugin;
import emanondev.core.YMLConfig;
import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;

public interface QuestManager<T extends User<T>> extends GuiElement {

    /**
     * name of this, must be unique and immutable, used for folder name
     *
     */
    String getName();

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
     * @param user
     * @return
     */
    Integer getDefaultMissionLimit(T user);

    Mission<T> getMission(int id);

    Task<T> getTask(int id);

    Reward<T> getReward(int id);

    Require<T> getRequire(int id);

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


}