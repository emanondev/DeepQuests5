package emanondev.deepquests.nation;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import emanondev.deepquests.implementations.AUser;
import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.Task;
import emanondev.deepquests.interfaces.UserManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class QuestNation extends AUser<QuestNation> {

    private final Nation nation;

    public QuestNation(UserManager<QuestNation> manager, Nation nation) {
        super(manager, nation.getUUID().toString());
        this.nation = nation;
    }

    @Override
    public @NotNull Collection<Player> getPlayers() {
        return TownyAPI.getInstance().getOnlinePlayers(nation);
    }

    @Override
    public boolean canProgress(@NotNull Task<QuestNation> task, Player player) {
        return true;
    }

    @Override
    public boolean canStart(@NotNull Mission<QuestNation> mission, Player targetPlayer) {
        return true;
    }

    public Nation getNation() {
        return nation;
    }

}
