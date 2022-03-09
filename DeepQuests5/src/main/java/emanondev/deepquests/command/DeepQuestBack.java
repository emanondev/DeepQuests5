package emanondev.deepquests.command;

import emanondev.deepquests.Perms;
import emanondev.deepquests.gui.GuiHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DeepQuestBack extends ACommand {

    public DeepQuestBack() {
        super("deepquestback", List.of("dqback"), Perms.ADMIN_EDITOR);
        this.setPlayersOnly(true);
    }

    @Override
    public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
        return new ArrayList<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
        if (!(sender instanceof Player p)) {
            CmdUtils.playersOnly(sender);
            return true;
        }
        if (GuiHandler.getLastUsedGui(p) != null)
            p.openInventory(GuiHandler.getLastUsedGui(p).getInventory());
        return true;
    }


}
