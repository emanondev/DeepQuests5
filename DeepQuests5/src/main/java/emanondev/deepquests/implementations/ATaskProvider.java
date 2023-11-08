package emanondev.deepquests.implementations;

import emanondev.core.PermissionBuilder;
import emanondev.core.YMLConfig;
import emanondev.core.YMLSection;
import emanondev.deepquests.Quests;
import emanondev.deepquests.interfaces.*;
import emanondev.deepquests.interfaces.Task.Phase;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ATaskProvider<T extends User<T>> implements TaskProvider<T> {

    private final QuestManager<T> manager;
    private final Map<String, TaskType<T>> types = new LinkedHashMap<>();
    private final YMLConfig config;

    public ATaskProvider(QuestManager<T> manager) {
        if (manager == null)
            throw new NullPointerException();
        this.manager = manager;
        editPermission = new PermissionBuilder("deepquests.editor." + this.getManager().getName() + ".tasktype.*")
                .setDescription("Allows to edit any task with type for manager " + this.getManager().getName())
                .buildAndRegister(manager.getPlugin(), true);
        config = getManager().getConfig("tasktype_config.yml");
    }

    public void reload() {
        config.reload();
    }

    public YMLSection getTypeConfig(TaskType<T> t) {
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
    public TaskType<T> getType(String id) {
        return types.get(id);
    }

    @Override
    public void registerType(TaskType<T> type) {
        if (type == null)
            throw new NullPointerException();
        if (types.containsKey(type.getKeyID()))
            throw new IllegalArgumentException();
        if (type.getManager() != manager)
            throw new IllegalArgumentException();
        types.put(type.getKeyID(), type);
        Bukkit.getPluginManager().registerEvents(type, Quests.get());

        editPermission.getChildren().put(type.getEditorPermission().getName(), true);
        this.getManager().getPlugin().registerPermission(editPermission);
    }

    private final Permission editPermission;

    @Override
    public Collection<TaskType<T>> getTypes() {
        return Collections.unmodifiableCollection(types.values());
    }

    @Override
    public @NotNull Task<T> getInstance(int id, Mission<T> mission, YMLSection section) {
        if (mission == null)
            throw new NullPointerException();
        try {
            TaskType<T> type = getType(section.getString(Paths.TYPE_NAME, null));
            if (type == null) {
                Quests.get()
                        .logIssue("Couldn't load Task &e" + id + " &fcan't find TaskType &e"
                                + section.getString(Paths.TYPE_NAME, null)
                                + "&f, setting a default value, check if all dependency plugin loaded correctly");
                return new ErrorTask<>(id, mission, section);
            }
            Task<T> task = type.getInstance(id, mission, section);
            for (Phase phase : Phase.values())
                task.getRawPhaseDescription(phase);// TODO ? preload performance boost

            /*
             * Quests.get().logDone("Loaded task &e" + id + " &f(Type: &e" +
             * section.getString(Paths.TYPE_NAME, null) + "&f) for manager &e" +
             * this.getManager().getName());
             */

            return task;
        } catch (Exception e) {
            e.printStackTrace();
            Quests.get().logIssue("Couldn't load Task &e" + id + " &fwith Key Id &e"
                    + section.getString(Paths.TYPE_NAME, null) + "&f, setting a default value");
        }
        return new ErrorTask<>(id, mission, section);
    }

    @Override
    public Permission getEditorPermission() {
        return editPermission;
    }

}
