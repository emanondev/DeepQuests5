package emanondev.deepquests.gui.inventory;

import emanondev.deepquests.Quests;
import emanondev.deepquests.gui.GuiConfig;
import emanondev.deepquests.gui.button.BackButton;
import emanondev.deepquests.gui.button.Button;
import emanondev.deepquests.gui.button.NextPageButton;
import emanondev.deepquests.gui.button.PreviusPageButton;
import emanondev.deepquests.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

public abstract class PagedChestGui extends ChestGui implements PagedGui {
    private final String rawTitle;
    private final Button[] controlButtons = new Button[9];
    private int page;

    public PagedChestGui(String title, int rows, Player player, Gui previusHolder, int page) {
        super(Utils.fixString(title, player, true, GuiConfig.PAGE_HOLDER, String.valueOf(Math.max(page, 1))), rows, player, previusHolder);
        if (rows == 1)
            throw new IllegalArgumentException("at least 2 rows");
        this.page = Math.max(page, 1);

        this.rawTitle = title;
        for (int i = 0; i < 9; i++)
            getInventory().setItem(getInventorySize() - 9 + i, GuiConfig.Generic.EMPTY_BUTTON_ITEM);

        if (this.loadPreviousPageButtonPosition() >= 0 && this.loadPreviousPageButtonPosition() < 9)
            setControlButton(loadPreviousPageButtonPosition(), new PreviusPageButton(this));

        if (this.loadNextPageButtonPosition() >= 0 && this.loadNextPageButtonPosition() < 9)
            Bukkit.getScheduler().runTaskLater(Quests.get(), () -> setControlButton(loadNextPageButtonPosition(), new NextPageButton(PagedChestGui.this)), 1);

        if (this.loadBackButtonPosition() >= 0 && this.loadBackButtonPosition() < 9)
            setControlButton(loadBackButtonPosition(), new BackButton(this));
    }

    private Inventory getInventory(int page) {
        return Bukkit.createInventory(this, getInventorySize(),
                Utils.fixString(rawTitle, getTargetPlayer(), true, GuiConfig.PAGE_HOLDER, String.valueOf(page)));
    }

    @Override
    public int getPage() {
        return page;
    }

    /**
     * returns true if the page changed
     */
    public boolean setPage(int pag) {
        pag = Math.max(1, pag);
        if (pag == page)
            return false;
        if ((pag - 1) * (getInventorySize() - 9) >= getMaxSlot())
            return false;
        page = pag;
        setInventory(getInventory(page));
        reloadInventory();
        return true;
    }

    protected void reloadInventory() {
        updateInventory();
        for (int i = 0; i < 9; i++)
            if (controlButtons[i] != null)
                getInventory().setItem(getInventorySize() - 9 + i, controlButtons[i].getItem());
    }

    /**
     * control buttons are the last line buttons
     *
     * @param slot   - from 0 to 8
     * @param button - what button? might be null
     */
    public void setControlButton(int slot, Button button) {
        if (slot < 0 || slot >= 9)
            return;
        controlButtons[slot] = button;
        getInventory().setItem(getInventorySize() - 9 + slot, button.getItem());
    }

    public int getMaxPage() {
        return (getMaxSlot() - 1) / (getInventorySize() - 9) + 1;
    }


    /**
     * override to change position (default 2)
     * <p>
     * warning this method is called in the constructor
     */
    protected int loadPreviousPageButtonPosition() {
        return 2;
    }

    /**
     * override to change position (default 6)
     * <p>
     * warning this method is called in the constructor
     */
    protected int loadNextPageButtonPosition() {
        return 6;
    }

    /**
     * override to change position (default 8)
     * <p>
     * warning this method is called in the constructor
     */
    protected int loadBackButtonPosition() {
        return 8;
    }

    public void onSlotClick(Player clicker, int slot, ClickType click) {
        if (slot >= getInventorySize() - 9 && slot < getInventorySize())
            if (controlButtons[slot - getInventorySize() + 9] != null)
                controlButtons[slot - getInventorySize() + 9].onClick(clicker, click);
    }

    /**
     * utility
     *
     * @return
     */
    protected int getPageOffset() {
        return (getPage() - 1) * (getInventorySize() - 9);
    }

    public boolean updateInventory() {
        boolean result = false;
        for (int i = 0; i < 9; i++) {
            if (controlButtons[i] == null) {
                if (getInventory().getItem(getInventorySize() - 9 + i) != GuiConfig.Generic.EMPTY_BUTTON_ITEM)
                    getInventory().setItem(getInventorySize() - 9 + i, GuiConfig.Generic.EMPTY_BUTTON_ITEM);
            } else {
                if (controlButtons[i].update())
                    getInventory().setItem(getInventorySize() - 9 + i, controlButtons[i].getItem());
            }
        }
        return result;
    }
}