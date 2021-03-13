package emanondev.deepquests.gui.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.interfaces.GuiElement;
import emanondev.deepquests.utils.Utils;

public class GuiElementDescriptionButton extends AGuiElementButton<GuiElement> {

	public GuiElementDescriptionButton(Gui parent, GuiElement element) {
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
	}

}
