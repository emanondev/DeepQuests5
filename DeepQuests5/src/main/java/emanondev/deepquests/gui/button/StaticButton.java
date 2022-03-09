package emanondev.deepquests.gui.button;

import emanondev.deepquests.gui.inventory.Gui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class StaticButton extends AButton {
    ItemStack item;

    public StaticButton(ItemStack item, Gui parent) {
        super(parent);
        this.item = item;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    @Override
    public boolean update() {
        return false;
    }

    @Override
    public void onClick(Player clicker, ClickType click) {
    }

}