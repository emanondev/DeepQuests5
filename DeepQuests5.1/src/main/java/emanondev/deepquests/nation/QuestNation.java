package emanondev.deepquests.nation;

import java.util.Collection;

import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;

import emanondev.deepquests.implementations.AUser;
import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.Task;
import emanondev.deepquests.interfaces.UserManager;

public class QuestNation extends AUser<QuestNation> {
	
	private final Nation nation;

	public QuestNation(UserManager<QuestNation> manager,Nation nation) {
		super(manager,nation.getUuid().toString());
		this.nation = nation;
	}

	@Override
	public Collection<Player> getPlayers() {
		return TownyAPI.getInstance().getOnlinePlayers(nation);
	}

	@Override
	public boolean canProgress(Task<QuestNation> task, Player player) {
		return true;
	}

	@Override
	public boolean canStart(Mission<QuestNation> mission, Player targetPlayer) {
		return true;
	}

	public Nation getNation() {
		return nation;
	}

}
