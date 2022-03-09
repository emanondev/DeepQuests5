package emanondev.deepquests.gui.button;

import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.interfaces.GuiElement;
import emanondev.deepquests.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class GuiElementButton<T extends GuiElement> extends AGuiElementButton<T> {

    public GuiElementButton(Gui parent, T element) {
        super(parent, element);
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = getElement().getGuiItem();
        Utils.updateDescription(item, this.getElement().getInfo(), this.getTargetPlayer(), true);
        return item;
    }

    @Override
    public boolean update() {
        return true;
    }

    @Override
    public void onClick(Player clicker, ClickType click) {
        clicker.openInventory(getElement().getEditorGui(clicker, this.getGui()).getInventory());
    }

}
