package emanondev.deepquests.gui.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.deepquests.gui.GuiConfig;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.utils.Utils;

public class BackButton extends AButton {
	private ItemStack item;
	
	public BackButton(Gui parent) {
		super(parent);
		if (parent.getPreviusGui()!=null) {
			item = new ItemStack(GuiConfig.Generic.BACK_INVENTORY_ITEM);
			Utils.updateDescription(item, GuiConfig.Generic.BACK_INVENTORY, getTargetPlayer(), true);
		}
		else {
			item = new ItemStack(GuiConfig.Generic.CLOSE_INVENTORY_ITEM);
			Utils.updateDescription(item, GuiConfig.Generic.CLOSE_INVENTORY, getTargetPlayer(), true);
		}
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
		if (getGui().getPreviusGui()!=null) {
			getGui().getPreviusGui().updateInventory();
			clicker.openInventory(getGui().getPreviusGui().getInventory());
			return;
		}
		clicker.closeInventory();
	}
}