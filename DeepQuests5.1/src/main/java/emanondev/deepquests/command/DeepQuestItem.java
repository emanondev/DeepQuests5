package emanondev.deepquests.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import emanondev.deepquests.gui.button.ItemEditorButton;
import emanondev.deepquests.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class DeepQuestItem extends ACommand {
	private static final HashMap<Player,ItemEditorButton> map = new HashMap<>();
	
	public DeepQuestItem() {
		super("deepquestitem",Arrays.asList("dqitem"),null);
		this.setPlayersOnly(true);
	}

	
	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		return new ArrayList<String>();
	}

	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender,String label,String[] args) {
		Player p = (Player) sender;
		if (!map.containsKey(p)){
			p.sendMessage(Utils.fixString("&cNo item is requested",null,true));
			return;
		}
		p.openInventory(map.get(p).getGui().getInventory());
		ItemStack item = p.getInventory().getItemInMainHand();
		if (item == null || item.getType()==Material.AIR) {
			map.get(p).onReicevedItem(null);
		}
		else
			map.get(p).onReicevedItem(new ItemStack(item));
		map.remove(p);
		return;
	}

	private static void requestItem(Player p, BaseComponent[] description,ItemEditorButton button) {
		map.put(p,button);
		p.closeInventory();
		ComponentBuilder comp = new ComponentBuilder(
				ChatColor.GOLD+"*******************************\n"+
				ChatColor.GOLD+"Keep the item in Hand and Click Me\n"+
				ChatColor.GOLD+"*******************************");
			comp.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
					"/deepquestitem"));
		if (description!=null)
			comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,description));
		
		p.spigot().sendMessage(comp.create());
	}
	/**
	 * use \n on description for multiple lines
	 * @param p
	 * @param description
	 * @param button
	 */
	public static void requestItem(Player p, String description,ItemEditorButton button) {
		requestItem(p,new ComponentBuilder(
				ChatColor.translateAlternateColorCodes('&',description)).create(),button);
	}

}
