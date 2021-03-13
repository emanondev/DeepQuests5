package emanondev.deepquests.interfaces;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import emanondev.deepquests.gui.button.SortableButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.core.ItemBuilder;

public interface GuiElement {
	
	/**
	 * 
	 * @return a material rapresentation of the object
	 */
	public Material getGuiMaterial();
	
	/**
	 * 
	 * @return a item Stack rapresentation of the object, no DisplayName nor Lore should be set
	 */
	public default ItemStack getGuiItem() {
		return new ItemBuilder(getGuiMaterial()).setGuiProperty().build();
	}

	/**
	 * 
	 * @return an editor gui of the object
	 */
	public Gui getEditorGui(Player target,Gui parent);
	

	/**
	 * 
	 * @return the priority of the object
	 */
	public int getPriority();
	
	
	/**
	 * 
	 * @return Description Utility which shows a recap of the object
	 */
	public List<String> getInfo();
	
	/**
	 * 
	 * @param parent
	 * @return a button for Description Utility which shows a recap of the object and do nothing when clicked
	 */
	public SortableButton getEditorButton(Gui parent);

}
