package emanondev.deepquests.interfaces;

import emanondev.deepquests.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface HasDisplay<T extends User<T>> {

    DisplayInfo<T> getDisplayInfo();

    QuestManager<T> getManager();

    default ItemStack getDisplayItem(ItemStack rawItem, ArrayList<String> rawDescription, T user, Player player) {
        return Utils.setDescription(rawItem, getDisplayDescription(rawDescription, user, player), null, true);
    }

    List<String> getDisplayDescription(ArrayList<String> rawDescription, T user, Player player);

}
