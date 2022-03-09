package emanondev.deepquests.command;

import emanondev.deepquests.Holders;
import emanondev.deepquests.Translations;
import org.bukkit.command.CommandSender;

public class CmdUtils {

    public static void lackPermission(CommandSender s, String permission) {
        s.sendMessage(Translations.Command.LACK_PERMISSION.replace(Holders.PERMISSION, permission));
    }

    public static void notImplemented(CommandSender s) {
        s.sendMessage(Translations.Command.NOT_IMPLEMENTED);
    }

    public static void success(CommandSender s) {
        s.sendMessage(Translations.Command.SUCCESS);
    }

    public static void fail(CommandSender s) {
        s.sendMessage(Translations.Command.FAIL);
    }

    public static void playersOnly(CommandSender s) {
        s.sendMessage(Translations.Command.PLAYERS_ONLY);
    }
}
