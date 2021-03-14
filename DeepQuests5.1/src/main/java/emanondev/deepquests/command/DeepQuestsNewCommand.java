package emanondev.deepquests.command;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import emanondev.core.CoreCommand;
import emanondev.core.UtilsMessages;
import emanondev.core.YMLConfig;
import emanondev.deepquests.Quests;
import emanondev.deepquests.gui.inventory.QuestsMenu;
import emanondev.deepquests.implementations.AQuestManager;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.player.PlayerQuestManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;

public class DeepQuestsNewCommand extends CoreCommand {

	public DeepQuestsNewCommand() {
		// TODO temp name
		super("deepquestsnew", Quests.get(), P.COMMAND_DEEPQUESTS_HELP, "admin main command");
	}

	/*
	 * command database movetoflatfile swapplayers <player1> <player2> manager
	 * <name> editor listmanagers opengui <player> editor reload
	 * 
	 * 
	 * 
	 * 
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void onExecute(CommandSender sender, String alias, String[] args) {
		if (args.length == 0) {
			help(sender, alias, args);
			return;
		}
		switch (args[0].toLowerCase()) {

		case "swapplayers":
			if (!sender.hasPermission(P.COMMAND_DEEPQUESTS_SWAPPLAYERS))
				this.permissionLackNotify(sender, P.COMMAND_DEEPQUESTS_SWAPPLAYERS);
			else
				this.swapplayers(sender, alias, args);
			return;
		case "manager":
			if (!sender.hasPermission(P.COMMAND_DEEPQUESTS_MANAGER))
				this.permissionLackNotify(sender, P.COMMAND_DEEPQUESTS_MANAGER);
			else
				this.manager(sender, alias, args);
			return;
		case "listmanagers":
			if (!sender.hasPermission(P.COMMAND_DEEPQUESTS_LISTMANAGERS))
				this.permissionLackNotify(sender, P.COMMAND_DEEPQUESTS_LISTMANAGERS);
			else
				this.listmanagers(sender, alias, args);
			return;
		case "opengui":
			if (!sender.hasPermission(P.COMMAND_DEEPQUESTS_OPENGUI))
				this.permissionLackNotify(sender, P.COMMAND_DEEPQUESTS_OPENGUI);
			else
				this.opengui(sender, alias, args);
			return;
		case "editor":
			if (!sender.hasPermission(P.COMMAND_DEEPQUESTS_EDITOR))
				this.permissionLackNotify(sender, P.COMMAND_DEEPQUESTS_EDITOR);
			else
				this.editor(sender, alias, args);
			return;
		case "reload":
			if (!sender.hasPermission(P.COMMAND_DEEPQUESTS_RELOAD))
				this.permissionLackNotify(sender, P.COMMAND_DEEPQUESTS_RELOAD);
			else
				this.reload(sender, alias, args);
			return;
		case "questbag":
			if (!sender.hasPermission(P.COMMAND_DEEPQUESTS_QUESTBAG))
				this.permissionLackNotify(sender, P.COMMAND_DEEPQUESTS_QUESTBAG);
			else
				this.questbag(sender, alias, args);
			return;
		case "debugunused":
			for (QuestManager<?> qManager : Quests.get().getManagers()) {
				getPlugin().logDone("checking manager &e" + qManager.getName());
				((AQuestManager) qManager).debugUnused();
			}
			return;
		}
		help(sender, alias, args);
		return;
	}

	// qa questbag %manager% %user% add/remove %id%
	@SuppressWarnings("rawtypes")
	private void questbag(CommandSender sender, String alias, String[] args) {
		if (args.length != 5 && args.length != 6) {
			UtilsMessages.sendMessage(sender,
					this.getPlugin().getLanguageConfig(sender).loadMessage("command.deepquests.questbag.wrong_params",
							"&4[&c✘&4] &c/%alias% questbag <manager> <user> <add/remove> <id> [amount]", "%alias%",
							alias));
			return;
		}
		QuestManager qm = Quests.get().getQuestManager(args[1]);
		if (qm == null) {
			UtilsMessages.sendMessage(sender,
					this.getPlugin().getLanguageConfig(sender).loadMessage(
							"command.deepquests.questbag.invalid_manager", "&4[&c✘&4] &cInvalid manager name",
							"%alias%", alias));
			return;
		}
		User u = qm.getArgomentUser(args[2]);
		if (u == null) {

			UtilsMessages.sendMessage(sender, this.getPlugin().getLanguageConfig(sender).loadMessage(
					"command.deepquests.questbag.invalid_user", "&4[&c✘&4] &cInvalid user name", "%alias%", alias));
			return;
		}
		boolean add;
		switch (args[3].toLowerCase()) {
		case "add":
			add = true;
			break;
		case "remove":
			add = false;
			break;
		default:

			UtilsMessages.sendMessage(sender,
					this.getPlugin().getLanguageConfig(sender).loadMessage(
							"command.deepquests.questbag.invalid_operation",
							"&4[&c✘&4] &cInvalid operation name, user &eadd &cor &eremove", "%alias%", alias));
			return;
		}
		int amount;
		try {
			amount = args.length >= 6 ? Integer.valueOf(args[5]) : 1;
			if (amount <= 0)
				throw new IllegalArgumentException();
		} catch (NumberFormatException e) {

			return;

		} catch (IllegalArgumentException e) {

			return;
		}
		String id = args[4].toLowerCase();
		if (add) {
			u.getQuestBag().addQuestItem(id, amount);
			UtilsMessages.sendMessage(sender,
					this.getPlugin().getLanguageConfig(sender).loadMessage("command.deepquests.questbag.success.add",
							"&2[&a✔&2] &aAdded &e%amount% &aof &e%id% &ato &e%user% &aquestbag", "%amount%",
							String.valueOf(amount), "%id%", id, "%user%", args[2]));
			return;
		}

		if (u.getQuestBag().getQuestItemAmount(id) == 0) {
			UtilsMessages.sendMessage(sender,
					this.getPlugin().getLanguageConfig(sender).loadMessage("command.deepquests.questbag.has_no_items",
							"&2[&a✔&2] &e%user% &ahas no &e%id% &aon his questbag", "%id%", id, "%user%", args[2]));
			return;
		}
		int removed = u.getQuestBag().removeQuestItem(id, amount);
		UtilsMessages.sendMessage(sender,
				this.getPlugin().getLanguageConfig(sender).loadMessage("command.deepquests.questbag.success.remove",
						"&2[&a✔&2] &aRemoved &e%amount% &aof &e%id% &ato &e%user% &aquestbag", "%amount%",
						String.valueOf(removed), "%id%", id, "%user%", args[2]));
	}

	private void reload(CommandSender sender, String alias, String[] args) {
		if (args.length != 1) {
			UtilsMessages.sendMessage(sender, this.getPlugin().getLanguageConfig(sender).loadMessage(
					"command.deepquests.reload.wrong_params", "&4[&c✘&4] &c/%alias% reload", "%alias%", alias));
			return;
		}
		Quests.get().onReload();
		if (sender instanceof Player)
			UtilsMessages.sendMessage(sender, this.getPlugin().getLanguageConfig(sender)
					.loadMessage("command.deepquests.reload.success", "&2[&a✔&2] &aPlugin reloaded"));
	}

	private void editor(CommandSender sender, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			this.playerOnlyNotify(sender);
			return;
		}
		((Player) sender).openInventory(Quests.get().getEditorGui((Player) sender, null).getInventory());
	}

	// opengui <player>
	private void opengui(CommandSender sender, String alias, String[] args) {
		if (args.length != 2) {
			UtilsMessages.sendMessage(sender,
					this.getPlugin().getLanguageConfig(sender).loadMessage("command.deepquests.opengui.wrong_params",
							"&4[&c✘&4] &c/%alias% opengui &6<player>", "%alias%", alias));
			return;
		}
		Player target = this.readPlayer(sender, args[1]);
		if (target == null) {
			UtilsMessages.sendMessage(sender,
					this.getPlugin().getLanguageConfig(sender).loadMessage("command.deepquests.opengui.wrong_target",
							"&4[&c✘&4] &cTarget player is offline", "%alias%", alias));
			return;
		}
		((Player) sender).openInventory(new QuestsMenu(target, null, null).getInventory());
	}

	private void listmanagers(CommandSender sender, String alias, String[] args) {
		if (args.length != 1) {
			UtilsMessages.sendMessage(sender,
					this.getPlugin().getLanguageConfig(sender).loadMessage(
							"command.deepquests.listmanagers.wrong_params", "&4[&c✘&4] &c/%alias% listmanagers",
							"%alias%", alias));
			return;
		}
		YMLConfig lang = this.getPlugin().getLanguageConfig(sender);
		ComponentBuilder message = new ComponentBuilder(
				lang.loadMessage("command.deepquests.listmanagers.success.prefix", "&c<> &6<> &eManagers &6<> &c<>"));
		message.append("\n");

		for (QuestManager<?> manager : Quests.get().getManagers()) {
			String[] holders = new String[] { "%alias%", alias, "%name%", manager.getName(), "%description%",
					String.join("\n", manager.getInfo()) };
			message.append(lang.loadMessage("command.deepquests.listmanagers.success.manager_edit", "&9✎ ",
					"&6Click to edit &e%name% &6quest manager", "/%alias% manager %name%",
					ClickEvent.Action.RUN_COMMAND, holders).create(), FormatRetention.NONE)
					.append(lang.loadMessage("command.deepquests.listmanagers.success.manager_display", "&e%name% ",
							"%description%", null, ClickEvent.Action.SUGGEST_COMMAND, true, holders).create(),
							FormatRetention.NONE)
					.append("\n");
		}
		message.append(
				lang.loadMessage("command.deepquests.listmanagers.success.postfix", "&c<> &6<> &eManagers &6<> &c<>"),
				FormatRetention.NONE);
		UtilsMessages.sendMessage(sender, message.create());
		// TODO Auto-generated method stub

	}

	// manager <manager>
	private void manager(CommandSender sender, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			this.playerOnlyNotify(sender);
			return;
		}
		if (args.length == 2) {
			QuestManager<?> manager = Quests.get().getQuestManager(args[1]);
			if (manager == null) {
				UtilsMessages.sendMessage(sender,
						this.getPlugin().getLanguageConfig(sender).loadMessage(
								"command.deepquests.manager.invalid_manager",
								"&4[&c✘&4] &cUnable to find manager &e%name%", "%name%", args[1]));
				return;
			}
			((Player) sender).openInventory(manager
					.getEditorGui((Player) sender, Quests.get().getEditorGui((Player) sender, null)).getInventory());
			return;
		}

		UtilsMessages.sendMessage(sender, this.getPlugin().getLanguageConfig(sender).loadMessage(
				"command.deepquests.manager.wrong_params", "&4[&c✘&4] &c/%alias% manager <manager>", "%alias%", alias));
	}

	// swapplayers <player1> <player2>
	@SuppressWarnings("deprecation")
	private void swapplayers(CommandSender sender, String alias, String[] args) {
		if (args.length != 3) {
			UtilsMessages.sendMessage(sender,
					this.getPlugin().getLanguageConfig(sender).loadMessage(
							"command.deepquests.swapplayers.wrong_params", "&4[&c✘&4] &c/%alias% listmanagers",
							"%alias%", alias));
			return;
		}

		OfflinePlayer p1 = Bukkit.getOfflinePlayer(args[1]);
		if (p1.getFirstPlayed() == 0 && !p1.isOnline()) {
			UtilsMessages.sendMessage(sender,
					this.getPlugin().getLanguageConfig(sender).loadMessage(
							"command.deepquests.database.swapplayers.invalid_player",
							"&4[&c✘&4] &cTarget &6%player% &cnever joined the server", "%alias%", alias, "%player%",
							p1.getName()));
			return;
		}

		OfflinePlayer p2 = Bukkit.getOfflinePlayer(args[2]);
		if (p2.getFirstPlayed() == 0 && !p2.isOnline()) {
			UtilsMessages.sendMessage(sender,
					this.getPlugin().getLanguageConfig(sender).loadMessage(
							"command.deepquests.database.swapplayers.invalid_player",
							"&4[&c✘&4] &cTarget &6%player% &cnever joined the server", "%alias%", alias, "%player%",
							p2.getName()));
			return;
		}

		((PlayerQuestManager) Quests.get().getQuestManager(PlayerQuestManager.NAME)).getUserManager().swapPlayers(p1,
				p2, sender);
	}

	private void help(CommandSender sender, String alias, String[] args) {

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<String> onComplete(CommandSender sender, String alias, String[] args, Location loc) {
		switch (args.length) {
		case 1:
			return this.complete(args[0],
					Arrays.asList("swapplayers", "manager", "listmanagers", "opengui", "editor", "reload", "questbag"));
		case 2:
			switch (args[0].toLowerCase()) {
			case "swapplayers":
				if (sender.hasPermission(P.COMMAND_DEEPQUESTS_SWAPPLAYERS))
					return this.completePlayerNames(sender, args[1]);
				return Collections.emptyList();
			case "opengui":
				if (sender.hasPermission(P.COMMAND_DEEPQUESTS_OPENGUI))
					return this.completePlayerNames(sender, args[1]);
				return Collections.emptyList();
			case "questbag":
				if (sender.hasPermission(P.COMMAND_DEEPQUESTS_QUESTBAG))
					return this.complete(args[1], Quests.get().getManagersNames());
				return Collections.emptyList();
			case "manager":
				if (sender.hasPermission(P.COMMAND_DEEPQUESTS_MANAGER))
					return this.complete(args[1], Quests.get().getManagersNames());
				return Collections.emptyList();
			}
			return Collections.emptyList();
		case 3:
			switch (args[0].toLowerCase()) {
			case "swapplayers":
				if (sender.hasPermission(P.COMMAND_DEEPQUESTS_SWAPPLAYERS))
					return this.completePlayerNames(sender, args[2]);
				return Collections.emptyList();
			case "questbag":
				if (!sender.hasPermission(P.COMMAND_DEEPQUESTS_QUESTBAG))
					return Collections.emptyList();
				QuestManager qm = Quests.get().getQuestManager(args[1]);
				if (qm == null)
					return Collections.emptyList();
				return this.complete(args[2], qm.getUsersArguments());
			}
			return Collections.emptyList();
		case 4:
			switch (args[0].toLowerCase()) {
			case "questbag":
				if (sender.hasPermission(P.COMMAND_DEEPQUESTS_QUESTBAG))
					return this.complete(args[3], new String[] { "add", "remove" });
				return Collections.emptyList();
			}
			return Collections.emptyList();
		case 5:
			switch (args[0].toLowerCase()) {
			case "questbag":
				if (!sender.hasPermission(P.COMMAND_DEEPQUESTS_QUESTBAG))
					return Collections.emptyList();
				// TODO
				return Collections.emptyList();
			/*
			 * QuestManager qm = Quests.get().getQuestManager(args[1]); if (qm==null) return
			 * Collections.emptyList(); User u = qm.getArgomentUser(args[2]); if (u==null)
			 * return Collections.emptyList(); QuestBag qb = u.getQuestBag(); qb. return
			 * this.complete(args[4], qm.getUsersArguments());
			 */
			}
			return Collections.emptyList();
		case 6:
			switch (args[0].toLowerCase()) {
			case "questbag":
				if (sender.hasPermission(P.COMMAND_DEEPQUESTS_QUESTBAG))
					return this.complete(args[5], new String[] { "1", "5", "10" });
				return Collections.emptyList();
			}
			return Collections.emptyList();
		}
		return Collections.emptyList();
	}

}
