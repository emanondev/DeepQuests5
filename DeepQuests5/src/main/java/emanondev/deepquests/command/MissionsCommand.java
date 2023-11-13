package emanondev.deepquests.command;

import emanondev.core.command.CoreCommand;
import emanondev.deepquests.Quests;
import emanondev.deepquests.gui.inventory.MissionsMenu;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MissionsCommand extends CoreCommand {
    public MissionsCommand() {
        super("missions", Quests.get(), null, "Open available missions", Arrays.asList("quest", "q"));
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) {
            this.playerOnlyNotify(sender);
            return;
        }
        p.openInventory(new MissionsMenu(p, null).getInventory());
    }

    @Override
    public @Nullable List<String> onComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @Nullable Location location) {
        return Collections.emptyList();
    }
}
