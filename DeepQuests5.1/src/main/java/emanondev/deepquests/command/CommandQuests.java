package emanondev.deepquests.command;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import emanondev.deepquests.gui.inventory.QuestsMenu;

public class CommandQuests extends ACommand {
	public CommandQuests() {
		super("quests",Arrays.asList("quest","q"),null);
		this.setPlayersOnly(true);
	}

	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		Player p = (Player) sender;
		p.openInventory(new QuestsMenu(p,null,null).getInventory());
	}
}
