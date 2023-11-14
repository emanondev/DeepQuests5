package emanondev.deepquests.interfaces;

import emanondev.deepquests.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface HasDisplay<T extends User<T>> {

    @NotNull DisplayInfo<T> getDisplayInfo();

    @NotNull QuestManager<T> getManager();

    default ItemStack getDisplayItem(ItemStack rawItem, ArrayList<String> rawDescription, T user, Player player) {
        return Utils.setDescription(rawItem, getDisplayDescription(rawDescription, user, player), null, true);
    }

    @NotNull List<String> getDisplayDescription(ArrayList<String> rawDescription, T user, Player player);

}
