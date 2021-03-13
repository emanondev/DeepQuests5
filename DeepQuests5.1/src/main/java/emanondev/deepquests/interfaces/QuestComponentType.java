package emanondev.deepquests.interfaces;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import emanondev.core.ItemBuilder;

public interface QuestComponentType<T extends User<T>, E extends QuestComponent<T>> {

	/**
	 * 
	 * @return an item for display utility
	 */
	public Material getGuiMaterial();

	public default ItemStack getGuiItem() {
		return new ItemBuilder(getGuiMaterial()).setGuiProperty().build();
	}

	/**
	 * 
	 * @return description of how this works
	 */
	public List<String> getDescription();

	/**
	 * 
	 * @return Description Utility which shows a recap of the object
	 */
	public default List<String> getInfo() {
		List<String> info = new ArrayList<>();
		info.add("&6Type: &e" + getKeyID());
		info.add("");
		info.addAll(getDescription());
		return info;
	}

	/**
	 * 
	 * @return an unique key for this
	 */
	public @NotNull String getKeyID();

	public QuestManager<T> getManager();

	public boolean getDefaultIsHidden();

	public Permission getEditorPermission();
}
