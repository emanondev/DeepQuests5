package emanondev.deepquests.command;

import emanondev.core.PermissionBuilder;
import emanondev.deepquests.Quests;
import org.bukkit.permissions.Permission;

public class P {

    public static final Permission COMMAND_DEEPQUESTS_HELP = new PermissionBuilder("deepquests.command.deepquests.help")
            .setDescription("Give access to help of admin quest command").buildAndRegister(Quests.get());
    public static final Permission COMMAND_DEEPQUESTS_MANAGER = new PermissionBuilder("deepquests.command.deepquests.manager")
            .addChild(COMMAND_DEEPQUESTS_HELP, true)
            .buildAndRegister(Quests.get());
    public static final Permission COMMAND_DEEPQUESTS_LISTMANAGERS = new PermissionBuilder("deepquests.command.deepquests.listmanagers")
            .addChild(COMMAND_DEEPQUESTS_HELP, true)
            .buildAndRegister(Quests.get());
    public static final Permission COMMAND_DEEPQUESTS_EDITOR = new PermissionBuilder("deepquests.command.deepquests.editor")
            .addChild(COMMAND_DEEPQUESTS_HELP, true)
            .buildAndRegister(Quests.get());
    public static final Permission COMMAND_DEEPQUESTS_OPENGUI = new PermissionBuilder("deepquests.command.deepquests.opengui")
            .addChild(COMMAND_DEEPQUESTS_HELP, true)
            .buildAndRegister(Quests.get());
    public static final Permission COMMAND_DEEPQUESTS_RELOAD = new PermissionBuilder("deepquests.command.deepquests.reload")
            .addChild(COMMAND_DEEPQUESTS_HELP, true)
            .buildAndRegister(Quests.get());
    public static final Permission COMMAND_DEEPQUESTS_SWAPPLAYERS = new PermissionBuilder("deepquests.command.deepquests.swapplayers")
            .addChild(COMMAND_DEEPQUESTS_HELP, true)
            .buildAndRegister(Quests.get());
    public static final Permission COMMAND_DEEPQUESTS_QUESTBAG = new PermissionBuilder("deepquests.command.deepquests.questbag")
            .addChild(COMMAND_DEEPQUESTS_HELP, true)
            .buildAndRegister(Quests.get());
    public static final Permission COMMAND_DEEPQUESTS_SEEQUESTBAG = new PermissionBuilder("deepquests.command.deepquests.seequestbag")
            .addChild(COMMAND_DEEPQUESTS_HELP, true)
            .buildAndRegister(Quests.get());

    public static final Permission COMMAND_DEEPQUESTS = new PermissionBuilder("deepquests.command.deepquests")
            .addChild(COMMAND_DEEPQUESTS_MANAGER, true)
            .addChild(COMMAND_DEEPQUESTS_LISTMANAGERS, true)
            .addChild(COMMAND_DEEPQUESTS_EDITOR, true)
            .addChild(COMMAND_DEEPQUESTS_OPENGUI, true)
            .addChild(COMMAND_DEEPQUESTS_RELOAD, true)
            .addChild(COMMAND_DEEPQUESTS_SWAPPLAYERS, true)
            .addChild(COMMAND_DEEPQUESTS_QUESTBAG, true)
            .addChild(COMMAND_DEEPQUESTS_SEEQUESTBAG, true)
            .buildAndRegister(Quests.get());

}
