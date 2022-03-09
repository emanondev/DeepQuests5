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
		/* TODO removed
		if (CONN != null)
			try {
				CONN.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
    }

    PlayerInfoManager() {
		loadAll();
        Bukkit.getPluginManager().registerEvents(this, Quests.get());
    }

    public PlayerInfo getPlayerInfo(OfflinePlayer p) {
        //if (p == null)
        //	throw new NullPointerException();
        //if (users.containsKey(p.getUniqueId()))
        return users.get(p.getUniqueId());
        //users.put(p.getUniqueId(),val);

        //return new PlayerInfoSqlite(p);
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
	/* TODO removed
	private void load(PlayerInfoSqlite pInfo) throws SQLException {
		PS_LOAD.setString(1, pInfo.getOfflinePlayer().getUniqueId().toString());
		ResultSet rs = PS_LOAD.executeQuery();
		if (rs.next()) {
			for (DisplayState state : DisplayState.values()) {
				if ((rs.getInt("quest_" + state.toString().toLowerCase()) == 0 ? false : true) != pInfo
						.canSeeQuestState(state))
					pInfo.toggleCanSeeQuestState(state);
				if ((rs.getInt("mission_" + state.toString().toLowerCase()) == 0 ? false : true) != pInfo
						.canSeeMissionState(state))
					pInfo.toggleCanSeeMissionState(state);
			}

			ProgressBarType type = ProgressBarType.values()[rs.getInt("bartype") % ProgressBarType.values().length];
			if (pInfo.getProgressBarType() != type)
				pInfo.setProgressBarType(type);

			ProgressBarStyle style = ProgressBarStyle.values()[rs.getInt("barstyle")
					% ProgressBarStyle.values().length];
			if (pInfo.getProgressBarStyle() != style)
				pInfo.setProgressBarStyle(style);
		}
		rs.close();
	}*/

    private void save(PlayerInfoSqlite pInfo) throws SQLException {
        if (pInfo == null)
            return;
		/* TODO removed
		PS_SELECT.setString(1, pInfo.getUUID().toString());
		ResultSet rs = PS_SELECT.executeQuery();
		if (rs.next()) {
			for (DisplayState state : DisplayState.values()) {
				PS_UPDATE.setInt(state.ordinal() * 2 + 1, pInfo.canSeeQuestState(state) ? 1 : 0);
				PS_UPDATE.setInt(state.ordinal() * 2 + 2, pInfo.canSeeMissionState(state) ? 1 : 0);
			}
			PS_UPDATE.setInt(DisplayState.values().length * 2 + 1, pInfo.getProgressBarType().ordinal());
			PS_UPDATE.setInt(DisplayState.values().length * 2 + 2, pInfo.getProgressBarStyle().ordinal());
			PS_UPDATE.setString(DisplayState.values().length * 2 + 3, pInfo.getOfflinePlayer().getName());
			PS_UPDATE.setString(DisplayState.values().length * 2 + 4,
					pInfo.getOfflinePlayer().getUniqueId().toString());
			PS_UPDATE.executeUpdate();
		} else {
			PS_INSERT.setString(1, pInfo.getUUID().toString());
			PS_INSERT.setString(2, pInfo.getOfflinePlayer().getName().toLowerCase());
			for (DisplayState state : DisplayState.values()) {
				PS_INSERT.setInt(state.ordinal() * 2 + 3, pInfo.canSeeQuestState(state) ? 1 : 0);
				PS_INSERT.setInt(state.ordinal() * 2 + 4, pInfo.canSeeMissionState(state) ? 1 : 0);
			}
			PS_INSERT.setInt(DisplayState.values().length * 2 + 3, pInfo.getProgressBarType().ordinal());
			PS_INSERT.setInt(DisplayState.values().length * 2 + 4, pInfo.getProgressBarStyle().ordinal());
			PS_INSERT.executeUpdate();
		}
		*/
    }
	/* TODO removed
	private void createTable() throws SQLException {

		// SQL statement for creating a new table
		StringBuilder sql = new StringBuilder(
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (\n" + "	uuid TEXT PRIMARY KEY,\n" + "	name TEXT,\n");
		for (DisplayState state : DisplayState.values())
			sql = sql.append(" quest_" + state.toString().toLowerCase() + " INTEGER NOT NULL,\n");
		for (DisplayState state : DisplayState.values())
			sql = sql.append(" mission_" + state.toString().toLowerCase() + " INTEGER NOT NULL,\n");
		sql = sql.append(" bartype INTEGER NOT NULL,\n" + " barstyle INTEGER NOT NULL\n" + ");");

		CONN.createStatement().execute(sql.toString());
	}*/

    public static class PlayerInfoSqlite implements PlayerInfo {

        public PlayerInfoSqlite(UUID player) {
            if (player == null)
                throw new NullPointerException();
            this.player = player;
            for (int i = 0; i < DisplayState.values().length; i++) {
                canSeeQuestState[i] = true;
                canSeeMissionState[i] = true;
            }
			/* TODO removed
			new BukkitRunnable() {
				public void run() {
					try {
						PlayerInfoManager.this.load(PlayerInfoSqlite.this);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}.runTaskAsynchronously(Quests.get());*/
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
