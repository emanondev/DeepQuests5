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

import java.util.Collection;
import java.util.Date;

public class TaskBossBar<T extends User<T>> {

    private final BossBar bar;
    private final TaskData<T> taskData;

    public TaskBossBar(TaskData<T> taskData) {
        this.taskData = taskData;
        bar = Bukkit.createBossBar("", taskData.getTask().getBossBarColor(), taskData.getTask().getBossBarStyle());
        updateProgress();
    }

    public void addPlayers(Collection<Player> players) {
        for (Player p : players)
            bar.addPlayer(p.getPlayer());
    }

    public void dispose() {
        bar.removeAll();
    }

    public BarColor getColor() {
        return bar.getColor();
    }

    public double getProgress() {
        return bar.getProgress();
    }

    public BarStyle getStyle() {
        return bar.getStyle();
    }

    public String getTitle() {
        return bar.getTitle();
    }

    public boolean isVisible() {
        return bar.isVisible();
    }

    public void setColor(BarColor color) {
        bar.setColor(color);
    }

    private long lastUpdate = 0;

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

    public void setStyle(BarStyle style) {
        bar.setStyle(style);
    }

    public void setVisible(boolean visible) {
        bar.setVisible(visible);
    }
}
