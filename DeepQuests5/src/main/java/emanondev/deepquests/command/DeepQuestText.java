package emanondev.deepquests.command;

import emanondev.core.command.CoreCommand;
import emanondev.deepquests.Quests;
import emanondev.deepquests.gui.button.TextEditorButton;
import emanondev.deepquests.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DeepQuestText extends CoreCommand {
    private static final HashMap<Player, TextEditorButton> map = new HashMap<>();

    public DeepQuestText() {
        super("deepquesttext", Quests.get(), P.COMMAND_DEEPQUESTS_DQTEXT,"Used to set text",List.of("dqtext"));
    }

    /**
     * use \n on description for multiple lines
     */
    public static void requestText(Player p, String textBase, String description,
                                   TextEditorButton button) {
        map.put(p, button);
        p.closeInventory();
        ComponentBuilder comp = new ComponentBuilder(
                ChatColor.GOLD + "****************************\n" +
                        ChatColor.GOLD + "         Click Me\n" +
                        ChatColor.GOLD + "****************************");
        if (textBase == null)
            comp.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                    "/dqtext "));
        else
            comp.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                    "/dqtext " + textBase));
        if (description != null)
            comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                    ChatColor.translateAlternateColorCodes('&', description))));

        p.spigot().sendMessage(comp.create());
    }


    /**
     * completely override reiceved text with message
     * the command is /dqtext
     */
    public static void requestText(Player p, BaseComponent[] message, TextEditorButton button) {
        map.put(p, button);
        p.closeInventory();
        p.spigot().sendMessage(message);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) {
            this.playerOnlyNotify(sender);
            return;
        }
        if (!map.containsKey(p)) {
            p.sendMessage(Utils.fixString("&cNo text is requested", null, true));
            return;
        }
        p.openInventory(map.get(p).getGui().getInventory());
        if (args.length > 0) {
            StringBuilder text = new StringBuilder();
            for (String arg : args)
                text.append(" ").append(arg);
            map.get(p).onReicevedText(text.toString().replaceFirst(" ", ""));
        } else
            map.get(p).onReicevedText(null);
        map.remove(p);
    }

    @Override
    public @Nullable List<String> onComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @Nullable Location location) {
        return Collections.emptyList();
    }
}
