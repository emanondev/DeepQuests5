package emanondev.deepquests.gui.inventory;

import emanondev.deepquests.gui.button.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.HashMap;

public class PagedMapGui extends PagedChestGui {

    protected HashMap<Integer, Button> buttons = new HashMap<Integer, Button>();

    public PagedMapGui(String title, int rows, Player player, Gui previusHolder) {
        this(title, rows, player, previusHolder, 1);
    }

    public PagedMapGui(String title, int rows, Player player, Gui previusHolder, int page) {
        super(title, rows, player, previusHolder, page);
    }

    @Override
    public int getMaxSlot() {
        int max = 0;
        for (int slot : buttons.keySet()) {
            if (buttons.get(slot).isVisible())
                max = Math.max(max, slot);
        }
        return max;
    }

    /**
     * Adds a new Button to the gui
     *
     * @param button
     */
    public void putButton(int slot, Button button) {
        if (button == null)
            return;
        buttons.put(slot, button);
        if (slot + 1 > getPageOffset() && slot + 1 <= getPageOffset() + getInventorySize() - 9)
            getInventory().setItem(slot - getPageOffset(), buttons.get(slot).getItem());
    }


    /**
     * Remove the specified Button from the gui
     *
     * @param button
     */
    public void removeButton(int slot) {
        if (slot < 0)
            return;
        buttons.remove(slot);
        if (slot <= getPageOffset() + getInventorySize() - 9)
            reloadInventory();
    }

    /**
     * remove all buttons
     */
    public void clearButtons() {
        buttons.clear();
        for (int i = 0; i < getInventorySize() - 9; i++)
            getInventory().setItem(i, null);
    }

    @Override
    public void onSlotClick(Player clicker, int slot, ClickType click) {
        super.onSlotClick(clicker, slot, click);
        if (slot >= 0 && slot < getInventorySize() - 9)
            if (buttons.get(slot + getPageOffset()) != null)
                buttons.get(slot + getPageOffset()).onClick(clicker, click);
    }

    @Override
    public boolean updateInventory() {
        boolean result = super.updateInventory();
        for (int i = 0; i < getInventorySize() - 9; i++)
            if (buttons.get(getPageOffset() + i) != null && buttons.get(getPageOffset() + i).update()) {
                getInventory().setItem(i, buttons.get(getPageOffset() + i).getItem());
                result = true;
            }
        return result;
    }

    @Override
    protected void reloadInventory() {
        super.reloadInventory();

        for (int i = 0; i < getInventorySize() - 9; i++)
            if ((getPageOffset() + i) < getMaxSlot()) {
                Button button = buttons.get(getPageOffset() + i);
                getInventory().setItem(i, button == null ? null : button.getItem());
            } else
                getInventory().setItem(i, null);
    }

}
