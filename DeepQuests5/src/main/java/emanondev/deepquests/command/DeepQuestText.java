package emanondev.deepquests.command;

import emanondev.deepquests.gui.button.TextEditorButton;
import emanondev.deepquests.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Deprecated
public class DeepQuestText extends ACommand implements TabExecutor {
    private static final HashMap<Player, TextEditorButton> map = new HashMap<>();

    public DeepQuestText() {
        super("deepquesttext", List.of("dqtext"), null);
        this.setPlayersOnly(true);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender arg0, @NotNull Command arg1, @NotNull String arg2, String[] arg3) {
        return new ArrayList<>();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command arg1, @NotNull String arg2, String[] args) {
        if (!(sender instanceof Player p)) {
            CmdUtils.playersOnly(sender);
            return true;
        }
        if (!map.containsKey(p)) {
            p.sendMessage(Utils.fixString("&cNo text is requested", null, true));
            return true;
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
        return true;
    }

    /**
     * use \n on description for multiple lines
     *
     * @param p
     * @param textBase
     * @param description
     * @param button
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
            comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                    ChatColor.translateAlternateColorCodes('&', description)).create()));

        p.spigot().sendMessage(comp.create());
    }


    /**
     * completely override reiceved text with message
     * the command is /dqtext
     *
     * @param p
     * @param message
     * @param button
     */
    public static void requestText(Player p, BaseComponent[] message, TextEditorButton button) {
        map.put(p, button);
        p.closeInventory();
        p.spigot().sendMessage(message);

    }

}
