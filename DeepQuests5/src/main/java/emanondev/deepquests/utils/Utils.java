package emanondev.deepquests.utils;

import emanondev.deepquests.hooks.Hooks;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    /**
     * Update the item with the description, covering both title and lore
     *
     * @param item    - item to update
     * @param desc    - raw text
     * @param p       - player or null for placeHolderApi use
     * @param color   - translate colors
     * @param holders - additional Holders, must be even number with the format "to replace","replacer","to replace 2","replacer 2"....
     */
    public static void updateDescription(ItemStack item, List<String> desc, Player p, boolean color,
                                         String... holders) {
        if (item == null)
            return;

        // prepare title and lore
        String title;
        ArrayList<String> lore;
        if (desc == null || desc.isEmpty()) {
            title = " ";
            lore = null;
        } else if (desc.size() == 1) {
            if (desc.get(0) != null)
                if (!desc.get(0).startsWith(ChatColor.RESET + ""))
                    title = ChatColor.RESET + desc.get(0);
                else
                    title = desc.get(0);
            else
                title = null;
            lore = null;
        } else {
            if (!desc.get(0).startsWith(ChatColor.RESET + ""))
                title = ChatColor.RESET + desc.get(0);
            else
                title = desc.get(0);
            lore = new ArrayList<String>();
            for (int i = 1; i < desc.size(); i++)
                if (desc.get(i) != null)
                    if (!desc.get(i).startsWith(ChatColor.RESET + ""))
                        lore.add(ChatColor.RESET + desc.get(i));
                    else
                        lore.add(desc.get(i));
                else
                    lore.add("");
        }

        // apply holders and colors for title and lore
        title = fixString(title, p, color, holders);
        updateList(lore, p, color, holders);

        // apply title and lore to item
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(title);
        meta.setLore(lore);
        item.setItemMeta(meta);

    }

    /**
     * @param desc    - raw text
     * @param p       - player or null for placeHolderApi use
     * @param color   - translate colors
     * @param holders - additional place Holders, must be even number with the format "to replace","replacer","to replace 2","replacer 2"....
     * @return a new list, with fixed text
     */
    public static List<String> fixList(List<String> desc, Player p, boolean color, String... holders) {
        if (desc == null)
            return null;
        List<String> list = new ArrayList<>();
        for (String line : desc)
            list.add(fixString(line, p, color, holders));
        return list;
    }

    /**
     * @param item    how the items is, original item is unmodified
     * @param desc    - raw text
     * @param p       - player or null for placeHolderApi use
     * @param color   - translate colors
     * @param holders - additional place Holders, must be even number with the format "to replace","replacer","to replace 2","replacer 2"....
     * @return an item clone with description and replaced holders on both title and lore
     */
    public static ItemStack setDescription(ItemStack item, List<String> desc, Player p, boolean color,
                                           String... holders) {
        if (item == null || item.getType() == Material.AIR)
            return null;

        ItemStack itemCopy = new ItemStack(item);
        updateDescription(itemCopy, desc, p, color, holders);
        return itemCopy;
    }

    private static void updateList(ArrayList<String> list, Player p, boolean color, String... stuffs) {
        if (list == null || list.isEmpty())
            return;
        for (int i = 0; i < list.size(); i++) {
            list.set(i, fixString(list.get(i), p, color, stuffs));
        }
    }

    public static String fixString(String text, Player p, boolean color, String... stuffs) {
        if (text == null)
            return null;

        // holders
        if (stuffs != null && stuffs.length % 2 != 0)
            throw new IllegalArgumentException("holder withouth replacer");
        if (stuffs != null && stuffs.length > 0) {
            //new IllegalStateException("Debug text '"+text+"' holders "+Arrays.toString(stuffs)).printStackTrace();
            for (int i = 0; i < stuffs.length; i += 2)
                text = text.replace(stuffs[i], stuffs[i + 1]);
        }

        // papi
        if (p != null && Hooks.isPAPIEnabled())
            text = PlaceholderAPI.setPlaceholders(p, text);

        // colore
        if (color)
            text = ChatColor.translateAlternateColorCodes('&', text);

        return text;
    }

    public static String revertColors(String s) {
        if (s == null)
            return null;
        return s.replace("§", "&");
    }

    public static List<String> revertColors(List<String> s) {
        ArrayList<String> list = new ArrayList<>();
        for (String text : s)
            list.add(revertColors(text));
        return list;
    }

    public static boolean checkPermission(CommandSender target, String permission) {
        if (permission == null || target.hasPermission(permission))
            return true;
        target.sendMessage(fixString("&cYou lack of permission '" + permission + "'", null, true));
        return false;
    }

    public static String clearColors(String msg) {
        return ChatColor.stripColor(msg);
    }


}