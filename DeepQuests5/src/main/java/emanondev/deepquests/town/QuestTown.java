package emanondev.deepquests.town;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import emanondev.deepquests.implementations.AUser;
import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.Task;
import emanondev.deepquests.interfaces.UserManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class QuestTown extends AUser<QuestTown> {

    private final Town town;

    public QuestTown(UserManager<QuestTown> manager, Town town) {
        super(manager, town.getUUID().toString());
        this.town = town;
    }

    @Override
    public @NotNull Collection<Player> getPlayers() {
        return TownyAPI.getInstance().getOnlinePlayers(town);
    }

    @Override
    public boolean canProgress(@NotNull Task<QuestTown> task, Player player) {
        return true;
    }

    @Override
    public boolean canStart(@NotNull Mission<QuestTown> mission, Player targetPlayer) {
        return true;
    }

    public Town getTown() {
        return town;
    }

}
