package emanondev.deepquests.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import emanondev.deepquests.Perms;
import emanondev.deepquests.gui.GuiHandler;

public class DeepQuestBack extends ACommand {

	public DeepQuestBack() {
		super("deepquestback", Arrays.asList("dqback"), Perms.ADMIN_EDITOR);
		this.setPlayersOnly(true);
	}
	
	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		return new ArrayList<String>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (!(sender instanceof Player)) {
			CmdUtils.playersOnly(sender);
			return true;
		}
		Player p = (Player) sender;
		if (GuiHandler.getLastUsedGui(p)!=null)
			p.openInventory(GuiHandler.getLastUsedGui(p).getInventory());
		return true;
	}
	

}
