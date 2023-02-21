package emanondev.deepquests;

import emanondev.deepquests.utils.DisplayState;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerInfoManager implements Listener {

    private final Map<UUID, PlayerInfoSqlite> users = new HashMap<>();

    public void disable() {
        for (PlayerInfoSqlite user : users.values())
            try {
                save(user);
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    PlayerInfoManager() {
        loadAll();
        Bukkit.getPluginManager().registerEvents(this, Quests.get());
    }

    public PlayerInfo getPlayerInfo(OfflinePlayer p) {
        return users.get(p.getUniqueId());
    }

    private void loadAll() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!users.containsKey(p.getUniqueId())) {
                PlayerInfoSqlite user = new PlayerInfoSqlite(p.getUniqueId());
                users.put(p.getUniqueId(), user);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerJoin(PlayerLoginEvent event) {
        if (event.getResult() == PlayerLoginEvent.Result.ALLOWED)
            if (!users.containsKey(event.getPlayer().getUniqueId()))
                users.put(event.getPlayer().getUniqueId(), new PlayerInfoSqlite(event.getPlayer().getUniqueId()));

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerQuit(PlayerQuitEvent event) {
        new BukkitRunnable() {
            public void run() {
                try {
                    save(users.get(event.getPlayer().getUniqueId()));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(Quests.get());
    }

    private void save(PlayerInfoSqlite pInfo) throws SQLException {
        if (pInfo == null)
            return;
    }

    public static class PlayerInfoSqlite implements PlayerInfo {

        public PlayerInfoSqlite(UUID player) {
            if (player == null)
                throw new NullPointerException();
            this.player = player;
            for (int i = 0; i < DisplayState.values().length; i++) {
                canSeeQuestState[i] = true;
                canSeeMissionState[i] = true;
            }
        }

        public ProgressBarType getProgressBarType() {
            return type;
        }

        public void setProgressBarType(ProgressBarType type) {
            this.type = type;
        }

        private final UUID player;
        private ProgressBarType type = ProgressBarType.BOSSBAR;
        private ProgressBarStyle style = ProgressBarStyle.NUMERIC;

        public Player getPlayer() {
            return Bukkit.getPlayer(player);
        }

        public UUID getUUID() {
            return player;
        }

        public OfflinePlayer getOfflinePlayer() {
            return Bukkit.getOfflinePlayer(player);
        }

        private final boolean[] canSeeQuestState = new boolean[DisplayState.values().length];
        private final boolean[] canSeeMissionState = new boolean[DisplayState.values().length];

        public boolean canSeeQuestState(DisplayState state) {
            return canSeeQuestState[state.ordinal()];
        }

        public boolean canSeeMissionState(DisplayState state) {
            return canSeeMissionState[state.ordinal()];
        }

        public void toggleCanSeeQuestState(DisplayState state) {
            canSeeQuestState[state.ordinal()] = !canSeeQuestState[state.ordinal()];
        }

        public void toggleCanSeeMissionState(DisplayState state) {
            canSeeMissionState[state.ordinal()] = !canSeeMissionState[state.ordinal()];
        }

        public ProgressBarStyle getProgressBarStyle() {
            return style;
        }

        public void setProgressBarStyle(ProgressBarStyle style) {
            this.style = style;
        }

    }

}
