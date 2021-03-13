package emanondev.deepquests.interfaces;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import emanondev.deepquests.utils.Utils;

public interface HasDisplay<T extends User<T>> {

	public DisplayInfo<T> getDisplayInfo();
	
	public QuestManager<T> getManager();
	
	public default ItemStack getDisplayItem(ItemStack rawItem, ArrayList<String> rawDescription, T user, Player player) {
		return Utils.setDescription(rawItem, getDisplayDescription(rawDescription,user,player), null, true);
	}
	
	public List<String> getDisplayDescription(ArrayList<String> rawDescription, T user, Player player);

}
