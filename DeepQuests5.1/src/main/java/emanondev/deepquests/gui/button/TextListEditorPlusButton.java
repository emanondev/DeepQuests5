package emanondev.deepquests.gui.button;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.utils.BookTextEditor;
import emanondev.deepquests.utils.Utils;

public abstract class TextListEditorPlusButton extends AButton {
	private ItemStack item;

	public TextListEditorPlusButton(ItemStack item,Gui parent) {
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

	protected void requestTextList(Player p, List<String> def) {
		BookTextEditor.requestTextList(this, p, def);
	}

	/**
	 * handle updates of the string list
	 * 
	 * @param list is colored
	 */
	public abstract void onReicevedTextList(List<String> list);
}