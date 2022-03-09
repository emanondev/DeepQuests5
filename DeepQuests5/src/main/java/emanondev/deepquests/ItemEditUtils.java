package emanondev.deepquests;

import emanondev.core.ItemBuilder;
import emanondev.core.UtilsString;
import emanondev.deepquests.gui.button.Button;
import emanondev.deepquests.gui.button.StaticButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.gui.inventory.ListGui;
import emanondev.deepquests.interfaces.User;
import emanondev.itemedit.ItemEdit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public class ItemEditUtils {

    public static Gui craftGui(@SuppressWarnings("rawtypes") @NotNull User target, @NotNull Map<String, Integer> map, @NotNull Player s,
                               @NotNull String title) {
        LinkedHashMap<String, ItemStack> itemMap = new LinkedHashMap<>();
        for (String id : map.keySet()) {
            try {
                ItemStack stack = ItemEdit.get().getServerStorage().getItem(id);
                if (stack == null)
                    stack = new ItemBuilder(Material.STONE).setDisplayName("???").setGuiProperty().build();
                itemMap.put(id,
                        new ItemBuilder(stack).setAmount(map.get(id) > 100 ? 101 : map.get(id))
                                .setDisplayName(stack.getItemMeta().getDisplayName()
                                        + UtilsString.fix(" &7[x &f" + map.get(id) + "&7]", null, true))
                                .build());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ListGui<Button> gui = new ListGui<>(title, 6, s, null);
        for (ItemStack item : itemMap.values())
            gui.addButton(new StaticButton(item, gui));
        return gui;
    }

}
