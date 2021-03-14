package emanondev.deepquests.command;

import java.util.*;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;

import emanondev.core.CoreCommand;
import emanondev.core.ItemBuilder;
import emanondev.core.PermissionBuilder;
import emanondev.core.UtilsMessages;
import emanondev.core.UtilsString;
import emanondev.deepquests.Quests;
import emanondev.deepquests.gui.button.Button;
import emanondev.deepquests.gui.button.StaticButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.gui.inventory.ListGui;
import emanondev.deepquests.interfaces.*;
import emanondev.itemedit.ItemEdit;

public class QuestBagCommand<T extends User<T>> extends CoreCommand {

	private final QuestManager<T> manager;

	private final Permission SEE_OTHERS;

	public QuestBagCommand(QuestManager<T> manager) {
		super(manager.getName() + "questbag", Quests.get(),
				new PermissionBuilder("deepquests.command." + manager.getName() + "questbag")
				.setDescription("Allows to open and inspect your own questbag for manager "+manager.getName())
						.buildAndRegister(Quests.get(),true),
				"allow to use /" + manager.getName() + "questbag command");
		SEE_OTHERS = new PermissionBuilder("deepquests.command." + manager.getName() + "questbag.others")
				.addChild(this.getCommandPermission(), true)
				.setDescription("Allows to open and inspect any questbag for manager "+manager.getName()).buildAndRegister(Quests.get(),true);
		this.manager = manager;
	}

	@Override
	public void onExecute(CommandSender sender, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			this.playerOnlyNotify(sender);
			return;
		}
		Player s = (Player) sender;
		switch (args.length) {
		case 0: {
			T u = manager.getUserManager().getUser(s);
			if (u == null) {
				// no user
				UtilsMessages.sendMessage(sender, this.getPlugin().getLanguageConfig(sender)
						.loadMessage("command." + this.getID() + ".no-user", "&cNo bag found"));
				return;
			}
			if (u.getQuestBag() == null) {
				// nobag
				UtilsMessages.sendMessage(sender, this.getPlugin().getLanguageConfig(sender)
						.loadMessage("command." + this.getID() + ".no-bag", "&cNo bag found"));
				return;
			}
			Map<String, Integer> map = u.getQuestBag().getQuestItems();
			if (map.isEmpty()) {
				// no items
				UtilsMessages.sendMessage(sender, this.getPlugin().getLanguageConfig(sender)
						.loadMessage("command." + this.getID() + ".no-items", "&cThis bag has no items"));
				return;
			}
			Gui gui = craftGui(u, map, s);
			s.openInventory(gui.getInventory());
			return;
		}
		case 1: {
			if (!s.hasPermission(SEE_OTHERS)) {
				this.permissionLackNotify(s, SEE_OTHERS);
				return;
			}
			Player t = this.readPlayer(sender,args[0]);
			if (t == null) {
				// unexisting player
				UtilsMessages.sendMessage(sender,
						this.getPlugin().getLanguageConfig(sender).loadMessage(
								"command." + this.getID() + ".wrong-player-name",
								"&cNo user with name &6%name% &cwas found", "%name%", args[0]));
				return;
			}
			T u = manager.getUserManager().getUser(t);
			if (u == null) {
				// no user
				UtilsMessages.sendMessage(sender, this.getPlugin().getLanguageConfig(sender)
						.loadMessage("command." + this.getID() + ".no-user", "&cNo user found"));
				return;
			}
			if (u.getQuestBag() == null) {
				// nobag
				UtilsMessages.sendMessage(sender, this.getPlugin().getLanguageConfig(sender)
						.loadMessage("command." + this.getID() + ".no-bag", "&cNo bag found"));
				return;
			}
			Map<String, Integer> map = u.getQuestBag().getQuestItems();
			if (map.isEmpty()) {
				// noitems
				UtilsMessages.sendMessage(sender, this.getPlugin().getLanguageConfig(sender)
						.loadMessage("command." + this.getID() + ".no-items", "&cThis bag has no items"));
				return;
			}
			Gui gui = craftGui(u, map, s);
			s.openInventory(gui.getInventory());
			return;
		}
		default:
			UtilsMessages.sendMessage(sender, this.getPlugin().getLanguageConfig(sender)
					.loadMessage("command." + this.getID() + ".help", "&c/%alias% [player]", "%alias%", alias));
		}
	}

	@Override
	public List<String> onComplete(CommandSender sender, String alias, String[] args, Location loc) {
		if (args.length != 1 || !sender.hasPermission(SEE_OTHERS))
			return new ArrayList<>();
		return this.completePlayerNames(sender, args[0]);
	}

	public Gui craftGui(T target, Map<String, Integer> map, Player s) {
		LinkedHashMap<String, ItemStack> itemMap = new LinkedHashMap<>();
		for (String id : map.keySet()) {
			try {
				ItemStack stack = ItemEdit.get().getServerStorage().getItem(id);
				if (stack==null)
					stack = new ItemBuilder(Material.STONE).setDisplayName("???").setGuiProperty().build();
				itemMap.put(id,
						new ItemBuilder(stack)
								.setAmount(map.get(id) > 100 ? 101 : map.get(id))
								.setDisplayName(stack.getItemMeta().getDisplayName() + UtilsString.fix(" &7[x &f" + map.get(id) + "&7]",null,true))
								.build());

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ListGui<Button> gui = new ListGui<Button>(this.getPlugin().getLanguageConfig(s)
				.loadMessage("command." + this.getID() + ".gui-title", "&9Quest Bag"), 6, s, null);
		for (ItemStack item : itemMap.values())
			gui.addButton(new StaticButton(item, gui));
		return gui;
	}

}
