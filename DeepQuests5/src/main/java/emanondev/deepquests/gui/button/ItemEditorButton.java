package emanondev.deepquests.gui.button;

import emanondev.deepquests.command.DeepQuestItem;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class ItemEditorButton extends AButton {
    protected ItemStack item = new ItemStack(Material.BARRIER);

    public ItemEditorButton(Gui parent) {
        super(parent);

    }

    public abstract ItemStack getCurrentItem();

    public abstract void onReicevedItem(ItemStack item);

    @Override
    public ItemStack getItem() {
        ItemStack current = getCurrentItem();
        if (current == null || current.getType() == Material.AIR)
            item.setType(Material.BARRIER);
        else
            item.setType(current.getType());
        Utils.updateDescription(item, getButtonDescription(), getTargetPlayer(), true);
        return item;
    }

    public abstract List<String> getButtonDescription();

    @Override
    public boolean update() {
        return true;
    }

    protected void requestItem(Player p, String description) {
        DeepQuestItem.requestItem(p, description, this);
    }
}
