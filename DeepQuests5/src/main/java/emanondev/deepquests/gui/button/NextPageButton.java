package emanondev.deepquests.gui.button;

import emanondev.deepquests.gui.GuiConfig;
import emanondev.deepquests.gui.inventory.PagedGui;
import emanondev.deepquests.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class NextPageButton extends APagedButton {
    private final ItemStack item;

    public NextPageButton(PagedGui parent) {
        super(parent);
        this.item = new ItemStack(GuiConfig.Generic.NEXT_PAGE_ITEM);
        Utils.updateDescription(item, GuiConfig.Generic.NEXT_PAGE, getTargetPlayer(), true,
                GuiConfig.TARGET_PAGE_HOLDER, String.valueOf(getGui().getPage() + 1));
    }

    @Override
    public ItemStack getItem() {
        if (getGui().getMaxPage() <= getPage())
            return GuiConfig.Generic.EMPTY_BUTTON_ITEM;
        return item;
    }

    @Override
    public boolean update() {
        Utils.updateDescription(item, GuiConfig.Generic.NEXT_PAGE, getTargetPlayer(), true,
                GuiConfig.TARGET_PAGE_HOLDER, String.valueOf(getGui().getPage() + 1));
        return true;
    }

    @Override
    public void onClick(Player clicker, ClickType click) {
        if (getGui().incPage())
            clicker.openInventory(getGui().getInventory());
    }

}