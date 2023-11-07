package emanondev.deepquests.command;

import emanondev.core.command.CoreCommand;
import emanondev.deepquests.Quests;
import emanondev.deepquests.gui.button.ItemEditorButton;
import emanondev.deepquests.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DeepQuestItem extends CoreCommand {
    private static final HashMap<Player, ItemEditorButton> map = new HashMap<>();

    public DeepQuestItem() {
        super("deepquestitem", Quests.get(), P.COMMAND_DEEPQUESTS_DQITEM, null, List.of("dqitem"));
    }

    private static void requestItem(Player p, Text description, ItemEditorButton button) {
        map.put(p, button);
        p.closeInventory();
        ComponentBuilder comp = new ComponentBuilder(
                ChatColor.GOLD + "*******************************\n" +
                        ChatColor.GOLD + "Keep the item in Hand and Click Me\n" +
                        ChatColor.GOLD + "*******************************");
        comp.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                "/deepquestitem"));
        if (description != null)
            comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, description));

        p.spigot().sendMessage(comp.create());
    }

    /**
     * use \n on description for multiple lines
     */
    public static void requestItem(Player p, String description, ItemEditorButton button) {
        requestItem(p, new Text(
                ChatColor.translateAlternateColorCodes('&', description)), button);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) {
            this.playerOnlyNotify(sender);
            return;
        }
        if (!map.containsKey(p)) {
            p.sendMessage(Utils.fixString("&cNo item is requested", null, true));
            return;
        }
        p.openInventory(map.get(p).getGui().getInventory());
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            map.get(p).onReicevedItem(null);
        } else
            map.get(p).onReicevedItem(new ItemStack(item));
        map.remove(p);
    }

    @Override
    public @Nullable List<String> onComplete(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings, @Nullable Location location) {
        return Collections.emptyList();
    }
}
