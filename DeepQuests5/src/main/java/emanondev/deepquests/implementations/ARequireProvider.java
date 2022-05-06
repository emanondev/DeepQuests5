package emanondev.deepquests.implementations;

import emanondev.core.PermissionBuilder;
import emanondev.core.YMLConfig;
import emanondev.core.YMLSection;
import emanondev.deepquests.Quests;
import emanondev.deepquests.interfaces.*;
import org.bukkit.permissions.Permission;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ARequireProvider<T extends User<T>> implements RequireProvider<T> {

    private final QuestManager<T> manager;
    private final Map<String, RequireType<T>> missionTypes = new LinkedHashMap<>();
    private final Map<String, RequireType<T>> questTypes = new LinkedHashMap<>();
    private final Map<String, RequireType<T>> taskTypes = new LinkedHashMap<>();
    private final Map<String, RequireType<T>> types = new LinkedHashMap<>();
    private final YMLConfig config;

    public ARequireProvider(QuestManager<T> manager) {
        if (manager == null)
            throw new NullPointerException();
        this.manager = manager;
        editPermission = new PermissionBuilder("deepquests.editor." + this.getManager().getName() + ".requiretype.*")
                .setDescription("Allows to edit any require with type for manager " + this.getManager().getName())
                .buildAndRegister(manager.getPlugin(), true);
        config = getManager().getConfig("requiretype_config.yml");
    }

    public void reload() {
        config.reload();
    }

    public YMLSection getTypeConfig(RequireType<T> t) {
        if (t == null)
            throw new NullPointerException();
        return config.loadSection(t.getKeyID());
    }

    public void saveConfig() {
        config.save();
    }

    @Override
    public QuestManager<T> getManager() {
        return manager;
    }

    @Override
    public RequireType<T> getType(String id) {
        return types.get(id);
    }

    @Override
    public Collection<RequireType<T>> getTypes() {
        return types.values();
    }

    private final Permission editPermission;

    @Override
    public void registerQuestType(RequireType<T> type) {
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
    public void registerMissionType(RequireType<T> type) {
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
    public void registerTaskType(RequireType<T> type) {
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
    public void registerType(RequireType<T> type) {
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
    public Collection<RequireType<T>> getQuestTypes() {
        return Collections.unmodifiableCollection(questTypes.values());
    }

    @Override
    public Collection<RequireType<T>> getMissionTypes() {
        return Collections.unmodifiableCollection(missionTypes.values());
    }

    @Override
    public Collection<RequireType<T>> getTaskTypes() {
        return Collections.unmodifiableCollection(taskTypes.values());
    }

    @Override
    public Require<T> getInstance(int id, QuestManager<T> manager, YMLSection section) {
        if (manager == null)
            throw new NullPointerException();
        try {
            return getType(section.getString(Paths.TYPE_NAME, null)).getInstance(id, manager, section);
        } catch (Exception e) {
            Quests.get()
                    .logIssue("Couldn't load Require &e" + id + " &fcan't find RequireType &e"
                            + section.getString(Paths.TYPE_NAME, null)
                            + "&f, setting a default value, check if all dependency plugin loaded correctly");
        }
        return new ErrorRequire<>(id, manager, section);
    }

    @Override
    public final Permission getEditorPermission() {
        return editPermission;
    }

}