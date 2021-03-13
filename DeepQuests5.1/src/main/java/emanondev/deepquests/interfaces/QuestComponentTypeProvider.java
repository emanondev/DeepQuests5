package emanondev.deepquests.interfaces;

import java.util.Collection;

import org.bukkit.permissions.Permission;

import emanondev.core.YMLSection;

public interface QuestComponentTypeProvider<T extends User<T>, K extends QuestComponent<T>, E extends QuestComponentType<T, K>> {

	/**
	 * @param id
	 *            type id
	 * @return true if Type with id == type.getKeyID() is registered
	 */
	public default boolean existType(String id) {
		return getType(id) != null;
	}

	/**
	 * 
	 * @param id
	 *            type id
	 * @return type with type.getKeyID().equals(id) or null
	 */
	public E getType(String id);

	/**
	 * 
	 * @param type
	 *            - type to register
	 * @throws IllegalArgumentException
	 *             - if (existType(type.getKeyID()) == true)
	 */
	public void registerType(E type);

	/**
	 * @return types
	 */
	public Collection<E> getTypes();

	QuestManager<T> getManager();

	public Permission getEditorPermission();

	public YMLSection getTypeConfig(E t);

	@Deprecated
	public void saveConfig();

	public void reload();
}
