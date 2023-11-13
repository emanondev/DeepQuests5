package emanondev.deepquests.command;

import emanondev.core.command.CoreCommand;
import emanondev.deepquests.Quests;
import emanondev.deepquests.gui.inventory.QuestsMenu;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuestsCommand extends CoreCommand {
    public QuestsCommand() {
        super("quests", Quests.get(), null, "Open available quests", Arrays.asList("quest", "q"));
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) {
            this.playerOnlyNotify(sender);
            return;
        }
        p.openInventory(new QuestsMenu(p, null, null).getInventory());
    }

    @Override
    public @Nullable List<String> onComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @Nullable Location location) {
        return Collections.emptyList();
    }
}
