package emanondev.deepquests.gui.button;

import emanondev.deepquests.gui.inventory.Gui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public interface Button {

    /**
     * retrieve the item for this button, might be null or AIR
     *
     * @return the item
     */
    ItemStack getItem();

    /**
     * suggest the update of internal info
     *
     * @return true if something changed
     */
    boolean update();

    /**
     * this method is called when the button is clicked
     *
     * @param clicker who clicked
     * @param click   click type
     */
    void onClick(Player clicker, ClickType click);

    /**
     * parent Gui might have a referenced Player
     *
     * @return player target of the gui
     */
    default Player getTargetPlayer() {
        if (getGui() != null)
            return getGui().getTargetPlayer();
        return null;
    }

    /**
     * get the parent gui or null
     *
     * @return gui that holds this button
     */
    Gui getGui();

    /**
     * @return true if the button should be seen, by default true
     */
    default boolean isVisible() {
        return true;
    }
}