package emanondev.deepquests.gui.button;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.utils.BookTextEditor;
import emanondev.deepquests.utils.Utils;

public abstract class TextEditorPlusButton extends AButton {
	private ItemStack item;

	public TextEditorPlusButton(ItemStack item,Gui parent) {
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