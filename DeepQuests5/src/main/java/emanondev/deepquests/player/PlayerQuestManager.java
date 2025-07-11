package emanondev.deepquests.player;

import emanondev.core.CorePlugin;
import emanondev.core.Hooks;
import emanondev.deepquests.gui.button.GuiElementButton;
import emanondev.deepquests.gui.button.SortableButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.AQuestManager;
import emanondev.deepquests.player.requiretypes.*;
import emanondev.deepquests.player.rewardtypes.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class PlayerQuestManager extends AQuestManager<QuestPlayer> {

    public final static String NAME = "players";
    private final PlayerUserManager userManager;

    public PlayerQuestManager(String name, CorePlugin plugin) {
        super(name, plugin);
        userManager = new PlayerUserManager(this);

        // TODO types
        getRequireProvider().registerType(new PermissionRequireType(this));
        getRewardProvider().registerType(new ItemStackRewardType(this));
        getRewardProvider().registerType(new ConsoleCommandRewardType(this));
        if (Hooks.isMcmmoEnabled()) {
            getRequireProvider().registerType(new McmmoLevelRequireType(this));
            getRewardProvider().registerType(new McmmoExpRewardType(this));
        }
        if (Hooks.isJobsEnabled()) {
            getRequireProvider().registerType(new JobLevelRequireType(this));
            getRewardProvider().registerType(new JobsExpRewardType(this));
        }
        if (Hooks.isSkillAPIEnabled()) {
            getRewardProvider().registerType(new SkillAPIExpRewardType(this));
            getRequireProvider().registerType(new SkillAPILevelRequireType(this));
        }
        if (Hooks.isEnabled("CMI")) {//TODO add to core
            getRequireProvider().registerType(new CMIPlayTimeRequireType(this));
        }

    }

    @Override
    public @NotNull PlayerUserManager getUserManager() {
        return userManager;
    }

    public void reload() {
        super.reload();
        long inactivityDays = PlayerQuestManager.this.getConfig().loadLong("database.remove_inactive_user_after_days",
                365L);
        if (inactivityDays > 0)
            new BukkitRunnable() {

                @Override
                public void run() {
                    try {
                        getPlugin().logTetraStar(ChatColor.BLUE,
                                "Removing players offline by &e" + inactivityDays + "&f days or more");
                        Collection<String> users = PlayerQuestManager.this.getUserManager().getUsersUIDs();
                        int counter = 0;
                        long now = System.currentTimeMillis();
                        long removeBefore = now - (inactivityDays * 24L * 3600L * 1000L);
                        for (String uuid : users) {
                            OfflinePlayer off = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                            if (off.getLastPlayed() > removeBefore || off.isOnline())
                                continue;

                            File file = PlayerQuestManager.this.getUserManager().getUserFile(uuid);
                            if (file.delete()) {
                                counter++;
                                getPlugin().logDone("Removed player &e" + uuid + "&f (&e" + off.getName()
                                        + "&f) from database for inactivity");
                            } else
                                getPlugin().logProblem("Unable to delete &e" + uuid + "&f (&e" + off.getName()
                                        + "&f) File: &e" + file.getAbsolutePath());

                        }
                        getPlugin().logDone("Removed &e" + counter
                                + "&f player(s) from database for inactivity (more than &e" + inactivityDays
                                + "&f days) , took &e" + (System.currentTimeMillis() - now) + "&f ms");
                        /*
                         * now = System.currentTimeMillis();
                         * getPlugin().logTetraStar(ChatColor.BLUE,"Cleanup task examining &e"+users.
                         * size()+" &fusers"); for (String uuid : users) { OfflinePlayer off =
                         * Bukkit.getOfflinePlayer(UUID.fromString(uuid)); QuestPlayer qPlayer =
                         * userManager.loadPlayer(off); for (TaskData<QuestPlayer> data :
                         * qPlayer.getTaskDatas()) if (data.getProgress() == 0) data.setProgress(0); for
                         * (MissionData<QuestPlayer> data : qPlayer.getMissionDatas()) { if
                         * (data.getLastCompleted() == 0) data.setLastCompleted(0); if
                         * (data.getLastFailed() == 0) data.setLastFailed(0); if (data.getLastStarted()
                         * == 0) data.setLastStarted(0); if (data.failedTimes() == 0)
                         * data.setFailedTimes(0); if (data.successfullyCompletedTimes() == 0)
                         * data.setCompletedTimes(0); } for (QuestData<QuestPlayer> data :
                         * qPlayer.getQuestDatas()) { if (data.getLastCompleted() == 0)
                         * data.setLastCompleted(0); if (data.getLastFailed() == 0)
                         * data.setLastFailed(0); if (data.getLastStarted() == 0)
                         * data.setLastStarted(0); if (data.failedTimes() == 0) data.setFailedTimes(0);
                         * if (data.successfullyCompletedTimes() == 0) data.setCompletedTimes(0); if
                         * (data.getPoints() == 0) data.setPoints(0); } qPlayer.saveOnDisk();
                         *
                         * }
                         *
                         * getPlugin().logDone("Cleanup done, took &e" + (System.currentTimeMillis() -
                         * now) + "&f ms");
                         */

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }.runTaskAsynchronously(getPlugin());
    }

    @Override
    public @NotNull SortableButton getEditorButton(@NotNull Gui parent) {
        return new GuiElementButton<>(parent, this);
    }

    @Override
    public @NotNull Gui getEditorGui(Player target, Gui parent) {
        return new EditorGui(target, parent);
    }

    @Override
    public List<String> getInfo() {
        return Arrays.asList("&6&lPlayers", "&6Quests related to players");
    }

    @Override
    public @NotNull Material getGuiMaterial() {
        return Material.PLAYER_HEAD;
    }

    @Override
    public QuestPlayer getArgomentUser(String argument) {
        @SuppressWarnings("deprecation")
        OfflinePlayer player = Bukkit.getOfflinePlayer(argument);
        if (player.getFirstPlayed() <= 0)
            return null;
        if (player.isOnline()) {
            QuestPlayer user = getUserManager().getUser(player.getPlayer());
            if (user != null)
                return user;
        }
        return new QuestPlayer(getUserManager(), player);
    }

    @Override
    public @NotNull Collection<String> getUsersArguments() {
        HashSet<String> names = new HashSet<>();
        for (Player p : Bukkit.getOnlinePlayers())
            names.add(p.getName());
        return names;
    }

    protected class EditorGui extends AQuestManager<QuestPlayer>.EditorGui {

        public EditorGui(Player player, Gui previusHolder) {
            super(player, previusHolder);
        }

    }
}
