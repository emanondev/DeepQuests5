package emanondev.deepquests.player;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import emanondev.deepquests.implementations.AUser;
import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.Task;
import emanondev.deepquests.interfaces.UserManager;

public class QuestPlayer extends AUser<QuestPlayer> {

	private final UUID uuid;

	public QuestPlayer(UserManager<QuestPlayer> manager, OfflinePlayer p) {
		super(manager, p.getUniqueId().toString());
		this.uuid = p.getUniqueId();
		getConfig().set("player_name", getOfflinePlayer().getName());
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

	public OfflinePlayer getOfflinePlayer() {
		return Bukkit.getOfflinePlayer(uuid);
	}

	@Override
	public Collection<Player> getPlayers() {
		HashSet<Player> set = new HashSet<>();
		if (getPlayer() != null)
			set.add(getPlayer());
		return set;
	}

	@Override
	public boolean canProgress(Task<QuestPlayer> task, Player player) {
		return true;
	}

	@Override
	public boolean canStart(Mission<QuestPlayer> mission, Player targetPlayer) {
		return true;
	}

}
