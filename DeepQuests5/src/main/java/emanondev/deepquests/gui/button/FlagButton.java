package emanondev.deepquests.gui.button;

import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class FlagButton extends AButton {
    private final ItemStack falseItem;
    private final ItemStack trueItem;

    public FlagButton(ItemStack falseItem, ItemStack trueItem, Gui parent) {
        super(parent);
        this.falseItem = falseItem;
        this.trueItem = trueItem;
        update();
    }

    /**
     * @return description of the item
     */
    public abstract List<String> getButtonDescription();

    public abstract boolean getCurrentValue();

    public abstract boolean onValueChangeRequest(boolean value);

    @Override
    public ItemStack getItem() {
        if (getCurrentValue()) {
            Utils.updateDescription(trueItem, getButtonDescription(), getTargetPlayer(), true);
            return trueItem;
        }
        Utils.updateDescription(falseItem, getButtonDescription(), getTargetPlayer(), true);
        return falseItem;
    }

    @Override
    public boolean update() {
        return true;
    }

    @Override
    public void onClick(Player clicker, ClickType click) {
        if (onValueChangeRequest(!getCurrentValue()))
            getGui().updateInventory();
    }

}