package emanondev.deepquests.gui.button;

import emanondev.deepquests.command.DeepQuestText;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.utils.Utils;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class TextEditorButton extends AButton {
    private final ItemStack item;

    public TextEditorButton(ItemStack item, Gui parent) {
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

    protected void requestText(Player p, String textBase, String description) {
        DeepQuestText.requestText(p, textBase.replace("§","&"), description, this);
    }

    public abstract void onReicevedText(String text);

    protected void requestText(Player p, BaseComponent[] message) {
        DeepQuestText.requestText(p, message, this);
    }

}