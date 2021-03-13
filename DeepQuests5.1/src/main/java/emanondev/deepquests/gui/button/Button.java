package emanondev.deepquests.gui.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.deepquests.gui.inventory.Gui;

public interface Button {
	
	/**
	 * retrieve the item for this button, might be null or AIR
	 * @return the item
	 */
	public ItemStack getItem();
	/**
	 * suggest the update of internal info
	 * @return true if something changed
	 */
	public boolean update();
	/**
	 * this method is called when the button is clicked
	 * @param clicker who clicked
	 * @param click click type
	 */
	public void onClick(Player clicker,ClickType click);
	/**
	 * parent Gui might have a referenced Player
	 * @return player target of the gui
	 */
	public default Player getTargetPlayer() {
		if (getGui()!=null)
			return getGui().getTargetPlayer();
		return null;
	}
	/**
	 * get the parent gui or null
	 * @return gui that holds this button
	 */
	public Gui getGui();
	/**
	 * @return true if the button should be seen, by default true
	 */
	public default boolean isVisible() {
		return true;
	}
}