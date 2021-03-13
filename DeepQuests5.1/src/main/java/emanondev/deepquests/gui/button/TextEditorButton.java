package emanondev.deepquests.gui.button;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import emanondev.deepquests.command.DeepQuestText;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.utils.Utils;
import net.md_5.bungee.api.chat.BaseComponent;

public abstract class TextEditorButton extends AButton {
	private ItemStack item;

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
		DeepQuestText.requestText(p, textBase, description, this);
	}

	public abstract void onReicevedText(String text);

	protected void requestText(Player p, BaseComponent[] message) {
		DeepQuestText.requestText(p, message, this);
	}

}