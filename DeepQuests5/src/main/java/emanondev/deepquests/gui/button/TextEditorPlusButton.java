package emanondev.deepquests.gui.button;

import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.utils.BookTextEditor;
import emanondev.deepquests.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class TextEditorPlusButton extends AButton {
    private final ItemStack item;

    public TextEditorPlusButton(ItemStack item, Gui parent) {
        super(parent);
        this.item = item;
        update();
    }

    public boolean update() {
        Utils.updateDescription(item, getButtonDescription(), getTargetPlayer(), true);
        return true;
    }

    public abstract List<String> getButtonDescription();

    @Override
    public ItemStack getItem() {
        return item;
    }

    protected void requestText(Player p, String def) {
        BookTextEditor.requestText(this, p, def);
    }

    /**
     * handle updates of the string
     *
     * @param text is colored and may contains \n
     */
    public abstract void onReicevedText(String text);
}