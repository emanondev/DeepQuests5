package emanondev.deepquests.implementations;

import emanondev.core.YMLConfig;
import emanondev.deepquests.Perms;
import emanondev.deepquests.Quests;
import emanondev.deepquests.interfaces.*;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ABossBarManager<T extends User<T>> implements BossBarManager<T> {
    private final YMLConfig config;
    private final QuestManager<T> questManager;

    private final HashMap<T, HashMap<Task<T>, TaskBossBar<T>>> map = new HashMap<>();
    private final HashMap<TaskType<T>, BarStyle> tasksStyle = new HashMap<>();
    private final HashMap<TaskType<T>, BarColor> tasksColor = new HashMap<>();
    private final HashMap<TaskType<T>, Boolean> tasksShowBossBar = new HashMap<>();
    private long duration = 70;
    private BarColor defaultColor = BarColor.BLUE;
    private BarStyle defaultStyle = BarStyle.SEGMENTED_20;
    private boolean defaultShowBossBar = true;

    public ABossBarManager(QuestManager<T> questManager) {
        this.questManager = questManager;
        config = getManager().getConfig("bossbar-config.yml");
        reload();
    }

    public void save() {
        config.save();
    }

    public void reload() {
        duration = Math.max(10L, config.loadLong(Paths.BOSSBAR_MANAGER_DURATION, 70L));

        defaultColor = config.loadEnum(Paths.BOSSBAR_MANAGER_DEFAULT_COLOR,
                BarColor.BLUE, BarColor.class);

        defaultStyle = config.loadEnum(Paths.BOSSBAR_MANAGER_DEFAULT_STYLE,
                BarStyle.SEGMENTED_20, BarStyle.class);

        defaultShowBossBar = config.loadBoolean(Paths.BOSSBAR_MANAGER_DEFAULT_SHOWBOSSBAR, true);

        for (HashMap<Task<T>, TaskBossBar<T>> tMap : map.values())
            for (TaskBossBar<T> bar : tMap.values())
                bar.dispose();
        tasksStyle.clear();
        tasksColor.clear();
        map.clear();
    }

    public void onProgress(@NotNull T user, @NotNull Task<T> task) {
        //TODO ignore if task doesn't showboss bar
        if (!task.showBossBar())
            return;
        Collection<Player> targets = getPlayers(user, task);
        if (targets.isEmpty())
            return;
        List<Player> players = new ArrayList<>(targets);
        for (Player target : targets)
            if (target.hasPermission(Perms.SEE_TASK_BOSSBAR))
                players.add(target);
        if (players.isEmpty())
            return;
        if (!map.containsKey(user)) {
            HashMap<Task<T>, TaskBossBar<T>> tMap = new HashMap<>();
            tMap.put(task, new TaskBossBar<>(user.getTaskData(task)));
            map.put(user, tMap);
        } else if (!map.get(user).containsKey(task)) {
            map.get(user).put(task, new TaskBossBar<>(user.getTaskData(task)));
        }
        TaskBossBar<T> taskBar = map.get(user).get(task);
        taskBar.addPlayers(players);
        taskBar.updateProgress();
        long lastUpdate = taskBar.getLastUpdate();
        Bukkit.getScheduler().runTaskLater(Quests.get(), () -> {
            if (lastUpdate != taskBar.getLastUpdate())
                return;
            if (map.containsKey(user))
                map.get(user).remove(task);
            taskBar.dispose();
        }, duration);
    }

    public @NotNull BarColor getBarColor(@NotNull TaskType<T> type) {
        if (tasksColor.containsKey(type))
            return tasksColor.get(type);

        String path = "type." + type.getKeyID() + ".color";
        BarColor color = config.getEnum(path, defaultColor, BarColor.class);
        config.set(path, color.name());
        config.save();
        tasksColor.put(type, color);
        return color;
    }

    public @NotNull BarStyle getBarStyle(@NotNull TaskType<T> type) {
        if (tasksStyle.containsKey(type))
            return tasksStyle.get(type);

        String path = "type." + type.getKeyID() + ".style";
        BarStyle style = config.getEnum(path, defaultStyle, BarStyle.class);
        config.set(path, style.name());
        config.save();
        tasksStyle.put(type, style);
        return style;
    }

    public boolean getShowBossBar(@NotNull TaskType<T> type) {
        if (tasksShowBossBar.containsKey(type))
            return tasksShowBossBar.get(type);

        String path = "type." + type.getKeyID() + ".showBossBar";
        boolean result = config.getBoolean(path, defaultShowBossBar);
        config.set(path, result);
        config.saveAsync();
        tasksShowBossBar.put(type, result);
        return result;
    }

    @Override
    public @NotNull Collection<Player> getPlayers(@NotNull T user, @NotNull Task<T> task) {
        return user.getPlayers();
    }

    public final QuestManager<T> getManager() {
        return questManager;
    }

}