package emanondev.deepquests.command;

import emanondev.core.command.CoreCommand;
import emanondev.deepquests.Quests;
import emanondev.deepquests.gui.GuiHandler;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class DeepQuestBack extends CoreCommand {

    public DeepQuestBack() {
        super("deepquestback", Quests.get(), P.COMMAND_DEEPQUESTS_DQBACK, "Allow to reopen an editor gui", List.of("dqback"));
    }


    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p))
            this.playerOnlyNotify(sender);
        else if (GuiHandler.getLastUsedGui(p) != null)
            p.openInventory(GuiHandler.getLastUsedGui(p).getInventory());
    }

    @Override
    public @Nullable List<String> onComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @Nullable Location location) {
        return Collections.emptyList();
    }
}
