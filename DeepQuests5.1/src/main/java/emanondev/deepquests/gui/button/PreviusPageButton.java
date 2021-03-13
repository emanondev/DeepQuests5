package emanondev.deepquests.gui.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.deepquests.gui.GuiConfig;
import emanondev.deepquests.gui.inventory.PagedGui;
import emanondev.deepquests.utils.Utils;

public class PreviusPageButton extends APagedButton {
	private ItemStack item;
	
	public PreviusPageButton(PagedGui parent) {
		super(parent);
		this.item = new ItemStack(GuiConfig.Generic.PREVIUS_PAGE_ITEM);
		Utils.updateDescription(item, GuiConfig.Generic.PREVIUS_PAGE, getTargetPlayer(), true, 
				GuiConfig.TARGET_PAGE_HOLDER,String.valueOf(getGui().getPage()-1));
	}

	@Override
	public ItemStack getItem() {
		if (getPage()==1)
			return GuiConfig.Generic.EMPTY_BUTTON_ITEM;
		return item;
	}

	@Override
	public boolean update() {
		Utils.updateDescription(item, GuiConfig.Generic.PREVIUS_PAGE, getTargetPlayer(), true, 
				GuiConfig.TARGET_PAGE_HOLDER,String.valueOf(getGui().getPage()-1));
		return true;
	}

	@Override
	public void onClick(Player clicker, ClickType click) {
		if (getGui().decPage())
			clicker.openInventory(getGui().getInventory());
	}
}