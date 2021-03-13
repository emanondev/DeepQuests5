package emanondev.deepquests.parties;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.alessiodp.parties.api.interfaces.Party;

import emanondev.deepquests.implementations.AUser;
import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.Task;
import emanondev.deepquests.interfaces.UserManager;

public class QuestParty extends AUser<QuestParty> {
	
	private final Party party;

	public QuestParty(UserManager<QuestParty> manager,Party party) {
		super(manager,party.getName());
		this.party = party;
	}

	@Override
	public Collection<Player> getPlayers() {
		HashSet<Player> players = new HashSet<>();
		for (UUID uuid:party.getMembers())
			Bukkit.getPlayer(uuid);
		return players;
	}

	@Override
	public boolean canProgress(Task<QuestParty> task, Player player) {
		return true;
	}

	@Override
	public boolean canStart(Mission<QuestParty> mission, Player targetPlayer) {
		return true;
	}

	public Party getParty() {
		return party;
	}

}
