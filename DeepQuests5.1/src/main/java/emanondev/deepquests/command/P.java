package emanondev.deepquests.command;

import org.bukkit.permissions.Permission;

import emanondev.core.PermissionBuilder;
import emanondev.deepquests.Quests;

public class P {

	public static final Permission COMMAND_DEEPQUESTS_HELP = new PermissionBuilder("deepquests.command.deepquests.help")
			.setDescription("Give access to help of admin quest command").buildAndRegister(Quests.get(),true);
	public static final Permission COMMAND_DEEPQUESTS_MANAGER = new PermissionBuilder("deepquests.command.deepquests.manager")
			.addChild(COMMAND_DEEPQUESTS_HELP, true)
			.buildAndRegister(Quests.get(),true);
	public static final Permission COMMAND_DEEPQUESTS_LISTMANAGERS = new PermissionBuilder("deepquests.command.deepquests.listmanagers")
			.addChild(COMMAND_DEEPQUESTS_HELP, true)
			.buildAndRegister(Quests.get(),true);
	public static final Permission COMMAND_DEEPQUESTS_EDITOR = new PermissionBuilder("deepquests.command.deepquests.editor")
			.addChild(COMMAND_DEEPQUESTS_HELP, true)
			.buildAndRegister(Quests.get(),true);
	//public static final Permission COMMAND_DEEPQUESTS_DATABASE = new PermissionBuilder("deepquests.command.deepquests.database")
	//		.addChild(COMMAND_DEEPQUESTS_HELP, true)
	//		.buildAndRegister(Quests.get(),true);
	public static final Permission COMMAND_DEEPQUESTS_OPENGUI = new PermissionBuilder("deepquests.command.deepquests.opengui")
			.addChild(COMMAND_DEEPQUESTS_HELP, true)
			.buildAndRegister(Quests.get(),true);
	public static final Permission COMMAND_DEEPQUESTS_RELOAD = new PermissionBuilder("deepquests.command.deepquests.reload")
			.addChild(COMMAND_DEEPQUESTS_HELP, true)
			.buildAndRegister(Quests.get(),true);
	public static final Permission COMMAND_DEEPQUESTS_SWAPPLAYERS = new PermissionBuilder("deepquests.command.deepquests.swapplayers")
			.addChild(COMMAND_DEEPQUESTS_HELP, true)
			.buildAndRegister(Quests.get(),true);
	public static final Permission COMMAND_DEEPQUESTS_QUESTBAG = new PermissionBuilder("deepquests.command.deepquests.questbag")
			.addChild(COMMAND_DEEPQUESTS_HELP, true)
			.buildAndRegister(Quests.get(),true);

	public static final Permission COMMAND_DEEPQUESTS = new PermissionBuilder("deepquests.command.deepquests")
			.addChild(COMMAND_DEEPQUESTS_MANAGER, true)
			.addChild(COMMAND_DEEPQUESTS_LISTMANAGERS, true)
			.addChild(COMMAND_DEEPQUESTS_EDITOR, true)
			.addChild(COMMAND_DEEPQUESTS_OPENGUI, true)
			.addChild(COMMAND_DEEPQUESTS_RELOAD, true)
			.addChild(COMMAND_DEEPQUESTS_SWAPPLAYERS, true)
			.addChild(COMMAND_DEEPQUESTS_QUESTBAG, true)
			.buildAndRegister(Quests.get(),true);
	/*public static final Permission COMMAND_DEEPQUESTS_DATABASE_MOVETOFLATFILE = new PermissionBuilder("deepquests.command.deepquests.database.movetoflatfile")
			.addChild(COMMAND_DEEPQUESTS_DATABASE, true)
			.buildAndRegister(Quests.get(),true);*/
	
}
