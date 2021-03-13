package emanondev.deepquests.interfaces;

import org.bukkit.Material;

import emanondev.deepquests.Holders;
import emanondev.deepquests.gui.button.GuiElementButton;
import emanondev.deepquests.gui.button.SortableButton;
import emanondev.deepquests.gui.inventory.Gui;

public interface Require<T extends User<T>> extends QuestComponent<T> {
	/**
	 * 
	 * @return the Type
	 */
	public RequireType<T> getType();
	/**
	 * 
	 * @param user
	 * @return true if user satisfy this require
	 */
	public boolean isAllowed(T user);

	public default String[] getHolders(T user){
		String[] list = new String[4];
		list[0] = Holders.REQUIRE_DESCRIPTION;
		list[1] = this.getDisplayName();
		list[2] = Holders.REQUIRE_IS_COMPLETED;
		list[3] = isAllowed(user) ? "&a" : "&c";
		return list;
	}

	@Override
	public default Material getGuiMaterial() {
		return Material.TRIPWIRE_HOOK;
	}

	@Override
	public default SortableButton getEditorButton(Gui parent) {
		return new GuiElementButton<Require<T>>(parent,this);
	}

	public QuestManager<T> getManager();
	
	public default String getTypeName() {
		return getType().getKeyID();
	}
	public boolean isHidden();
	public void setHidden(Boolean value);

}
