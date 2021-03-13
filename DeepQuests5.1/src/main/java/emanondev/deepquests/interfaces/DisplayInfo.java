package emanondev.deepquests.interfaces;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import emanondev.deepquests.utils.DisplayState;

public interface DisplayInfo<T extends User<T>> extends Navigable {
	/**
	 * forceShow = false
	 * @param state status
	 * @param user target user
	 * @param player who
	 * @return display item for this or null if user/player should not see this
	 */
	public default ItemStack getGuiItem(DisplayState state, T user, Player player) {
		return getGuiItem(state, user, player, false);
	}
	/**
	 * @param state status
	 * @param user target user
	 * @param player who
	 * @param forceShow skips limit and always returns an item
	 * @return display item for this
	 */
	public ItemStack getGuiItem(DisplayState state, T user, Player player, boolean forceShow);
	/**
	 * 
	 * @param state
	 * @return raw itemstack no description applyed
	 */
	public ItemStack getRawItem(DisplayState state);
	/**
	 * 
	 * @param state
	 * @return raw description no holders applyed
	 */
	public List<String> getRawDescription(DisplayState state);
	/**
	 * 
	 * @param state
	 * @return true if should be hidden
	 */
	public boolean isHidden(DisplayState state);
	/**
	 * sets raw item
	 * @param state
	 * @param item
	 */
	public void setItem(DisplayState state,ItemStack item);
	/**
	 * sets if should be hidden when displaystate equals state
	 * @param state
	 * @param value
	 */
	public void setHide(DisplayState state, Boolean value);
	/**
	 * sets raw description
	 * @param state
	 * @param description
	 */
	public void setDescription(DisplayState state, List<String> description);
}