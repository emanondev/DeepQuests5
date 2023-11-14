package emanondev.deepquests.implementations;

import emanondev.core.PermissionBuilder;
import emanondev.core.YMLConfig;
import emanondev.core.YMLSection;
import emanondev.deepquests.Quests;
import emanondev.deepquests.interfaces.*;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ARewardProvider<T extends User<T>> implements RewardProvider<T> {

    private final QuestManager<T> manager;
    private final Map<String, RewardType<T>> missionTypes = new LinkedHashMap<>();
    private final Map<String, RewardType<T>> questTypes = new LinkedHashMap<>();
    private final Map<String, RewardType<T>> taskTypes = new LinkedHashMap<>();
    private final Map<String, RewardType<T>> types = new LinkedHashMap<>();
    private final YMLConfig config;
    private final Permission editPermission;

    public ARewardProvider(QuestManager<T> manager) {
        if (manager == null)
            throw new NullPointerException();
        this.manager = manager;
        editPermission = new PermissionBuilder("deepquests.editor." + this.getManager().getName() + ".rewardtype.*")
                .setDescription("Allows to edit any reward with type for manager " + this.getManager().getName())
                .buildAndRegister(manager.getPlugin(), true);
        config = getManager().getConfig("rewardtype_config.yml");
    }

    public void reload() {
        config.reload();
    }

    public @NotNull YMLSection getTypeConfig(@NotNull RewardType<T> t) {
        if (t == null)
            throw new NullPointerException();
        return config.loadSection(t.getKeyID());
    }

    public void saveConfig() {
        config.save();
    }

    @Override
    public @NotNull QuestManager<T> getManager() {
        return manager;
    }

    @Override
    public RewardType<T> getType(@NotNull String id) {
        return types.get(id);
    }

    @Override
    public @NotNull Collection<RewardType<T>> getTypes() {
        return types.values();
    }

    @Override
    public void registerQuestType(@NotNull RewardType<T> type) {
        if (type == null)
            throw new NullPointerException();
        if (type.getManager() != manager)
            throw new IllegalArgumentException();
        types.put(type.getKeyID(), type);
        questTypes.put(type.getKeyID(), type);

        editPermission.getChildren().put(type.getEditorPermission().getName(), true);
        this.getManager().getPlugin().registerPermission(editPermission);
    }

    @Override
    public void registerMissionType(@NotNull RewardType<T> type) {
        if (type == null)
            throw new NullPointerException();
        if (type.getManager() != manager)
            throw new IllegalArgumentException();
        types.put(type.getKeyID(), type);
        missionTypes.put(type.getKeyID(), type);

        editPermission.getChildren().put(type.getEditorPermission().getName(), true);
        this.getManager().getPlugin().registerPermission(editPermission);
    }

    @Override
    public void registerTaskType(@NotNull RewardType<T> type) {
        if (type == null)
            throw new NullPointerException();
        if (type.getManager() != manager)
            throw new IllegalArgumentException();
        types.put(type.getKeyID(), type);
        taskTypes.put(type.getKeyID(), type);

        editPermission.getChildren().put(type.getEditorPermission().getName(), true);
        this.getManager().getPlugin().registerPermission(editPermission);
    }

    @Override
    public void registerType(@NotNull RewardType<T> type) {
        if (type == null)
            throw new NullPointerException();
        if (type.getManager() != manager)
            throw new IllegalArgumentException();
        types.put(type.getKeyID(), type);
        questTypes.put(type.getKeyID(), type);
        missionTypes.put(type.getKeyID(), type);
        taskTypes.put(type.getKeyID(), type);

        editPermission.getChildren().put(type.getEditorPermission().getName(), true);
        this.getManager().getPlugin().registerPermission(editPermission);
    }

    @Override
    public @NotNull Collection<RewardType<T>> getQuestTypes() {
        return Collections.unmodifiableCollection(questTypes.values());
    }

    @Override
    public @NotNull Collection<RewardType<T>> getMissionTypes() {
        return Collections.unmodifiableCollection(missionTypes.values());
    }

    @Override
    public @NotNull Collection<RewardType<T>> getTaskTypes() {
        return Collections.unmodifiableCollection(taskTypes.values());
    }

    @Override
    public @NotNull Reward<T> getInstance(int id, @NotNull YMLSection section) {
        if (manager == null)
            throw new NullPointerException();
        try {
            Reward<T> reward = getType(section.getString(Paths.TYPE_NAME, null)).getInstance(id, manager, section);
            reward.getRawFeedback();// preload performance boost
            /*
             * Quests.get().logDone("Loaded reward &e" + id + " &f(Type: &e" +
             * section.getString(Paths.TYPE_NAME, null) + "&f) for manager &e" +
             * this.getManager().getName());
             */
            return reward;
        } catch (Exception e) {
            Quests.get()
                    .logIssue("Couldn't load Reward &e" + id + " &fcan't find RewardType &e"
                            + section.getString(Paths.TYPE_NAME, null)
                            + "&f, setting a default value, check if all dependency plugin loaded correctly");
        }
        return new ErrorReward<>(id, manager, section);
    }

    @Override
    public final @NotNull Permission getEditorPermission() {
        return editPermission;
    }

}
