package emanondev.deepquests.implementations;

import emanondev.deepquests.interfaces.Task;
import emanondev.deepquests.interfaces.Task.Phase;
import emanondev.deepquests.interfaces.TaskData;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Date;

public class TaskBossBar<T extends User<T>> {

    private final BossBar bar;
    private final TaskData<T> taskData;
    private long lastUpdate = 0;

    public TaskBossBar(@NotNull TaskData<T> taskData) {
        this.taskData = taskData;
        bar = Bukkit.createBossBar("", taskData.getTask().getBossBarColor(), taskData.getTask().getBossBarStyle());
        updateProgress();
    }

    public void addPlayers(@NotNull Collection<Player> players) {
        for (Player p : players)
            bar.addPlayer(p);
    }

    public void dispose() {
        bar.removeAll();
    }

    public @NotNull BarColor getColor() {
        return bar.getColor();
    }

    public double getProgress() {
        return bar.getProgress();
    }

    public @NotNull BarStyle getStyle() {
        return bar.getStyle();
    }

    public String getTitle() {
        return bar.getTitle();
    }

    public boolean isVisible() {
        return bar.isVisible();
    }

    public void setColor(@NotNull BarColor color) {
        bar.setColor(color);
    }

    public void updateProgress() {
        int progress = taskData.getProgress();
        int max = taskData.getTask().getMaxProgress();
        bar.setProgress(((double) progress) / max);
        if (progress >= max)
            bar.setTitle(
                    Utils.fixString(getTask().getPhaseDescription(taskData.getUser(), Phase.COMPLETE), null, true));
        else
            bar.setTitle(
                    Utils.fixString(getTask().getPhaseDescription(taskData.getUser(), Phase.PROGRESS), null, true));
        lastUpdate = new Date().getTime();
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public Task<T> getTask() {
        return taskData.getTask();
    }

    public void setStyle(@NotNull BarStyle style) {
        bar.setStyle(style);
    }

    public void setVisible(boolean visible) {
        bar.setVisible(visible);
    }
}
