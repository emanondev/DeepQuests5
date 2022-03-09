package emanondev.deepquests.utils;

import emanondev.core.UtilsInventory;
import emanondev.core.UtilsInventory.ExcessManage;
import emanondev.core.UtilsInventory.LackManage;
import emanondev.deepquests.Quests;
import emanondev.deepquests.gui.button.Button;
import emanondev.deepquests.gui.button.TextEditorPlusButton;
import emanondev.deepquests.gui.button.TextListEditorPlusButton;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BookTextEditor implements Listener {
    public static HashMap<Player, Request> editors = new HashMap<>();

    public static void requestText(TextEditorPlusButton button, Player p, String def) {
        UtilsInventory.giveAmount(p, newBook(def), 1, ExcessManage.DELETE_EXCESS);
        editors.put(p, new Request(button));
        p.closeInventory();
    }

    public static void requestTextList(TextListEditorPlusButton button, Player p, List<String> def) {
        UtilsInventory.giveAmount(p, newBook(def), 1, ExcessManage.DELETE_EXCESS);
        editors.put(p, new Request(button));
        p.closeInventory();
    }

    private static ItemStack newBook(String oldText) {
        if (oldText == null)
            return newBook((List<String>) null);
        return newBook(Arrays.asList(oldText.split("\n")));
    }

    private static ItemStack newBook(List<String> oldText) {
        ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        if (oldText != null && !oldText.isEmpty())
            meta.setPages(Utils.revertColors(oldText));
        meta.setDisplayName("DeepQuests4 List Editor");
        book.setItemMeta(meta);
        return book;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private static void onBookEdit(PlayerEditBookEvent event) {
        if (!editors.containsKey(event.getPlayer()) || !event.isSigning())
            return;
        List<String> list = event.getNewBookMeta().getPages();
        StringBuilder longText = new StringBuilder("");
        for (String text : list)
            if (text != null)
                longText.append(text + "\n");
            else
                longText.append("\n");
        String txt;
        if (longText.length() >= 2)
            txt = Utils.fixString(longText.substring(0, longText.length() - 2), null, true);
        else
            txt = Utils.fixString(longText.toString(), null, true);

        if (editors.get(event.getPlayer()).button instanceof TextListEditorPlusButton) {
            list = Arrays.asList(txt.split("\n"));
            ((TextListEditorPlusButton) editors.get(event.getPlayer()).button).onReicevedTextList(list);
        } else if (editors.get(event.getPlayer()).button instanceof TextEditorPlusButton) {
            ((TextEditorPlusButton) editors.get(event.getPlayer()).button).onReicevedText(txt);
        } else
            throw new IllegalStateException("that shouldn't happen");

        Bukkit.getScheduler().runTaskLater(Quests.get(), new Runnable() {
            public void run() {
                ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
                item.setItemMeta(event.getNewBookMeta());
                UtilsInventory.removeAmount(event.getPlayer(), item, 1, LackManage.REMOVE_MAX_POSSIBLE);
            }
        }, 1);
    }

}

class Request {
    protected Button button;

    Request(TextEditorPlusButton button) {
        this.button = button;
    }

    Request(TextListEditorPlusButton button) {
        this.button = button;
    }

}
