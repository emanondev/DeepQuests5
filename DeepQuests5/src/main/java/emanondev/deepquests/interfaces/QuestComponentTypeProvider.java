package emanondev.deepquests.interfaces;

import emanondev.core.YMLSection;
import org.bukkit.permissions.Permission;

import java.util.Collection;

public interface QuestComponentTypeProvider<T extends User<T>, K extends QuestComponent<T>, E extends QuestComponentType<T, K>> {

    /**
     * @param id type id
     * @return true if Type with id == type.getKeyID() is registered
     */
    default boolean existType(String id) {
        return getType(id) != null;
    }

    /**
     * @param id type id
     * @return type with type.getKeyID().equals(id) or null
     */
    E getType(String id);

    /**
     * @param type - type to register
     * @throws IllegalArgumentException - if (existType(type.getKeyID()) == true)
     */
    void registerType(E type);

    /**
     * @return types
     */
    Collection<E> getTypes();

    QuestManager<T> getManager();

    Permission getEditorPermission();

    YMLSection getTypeConfig(E t);

    @Deprecated
    void saveConfig();

    void reload();
}
