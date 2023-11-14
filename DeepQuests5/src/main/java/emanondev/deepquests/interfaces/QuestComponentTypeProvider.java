package emanondev.deepquests.interfaces;

import emanondev.core.YMLSection;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface QuestComponentTypeProvider<T extends User<T>, K extends QuestComponent<T>, E extends QuestComponentType<T, K>> {

    /**
     * @param id type id
     * @return true if Type with id == type.getKeyID() is registered
     */
    default boolean existType(@NotNull String id) {
        return getType(id) != null;
    }

    /**
     * @param id type id
     * @return type with type.getKeyID().equals(id) or null
     */
    @Nullable E getType(@NotNull String id);

    /**
     * @param type - type to register
     * @throws IllegalArgumentException - if (existType(type.getKeyID()) == true)
     */
    void registerType(@NotNull E type);

    /**
     * @return types
     */
    @NotNull Collection<E> getTypes();

    @NotNull QuestManager<T> getManager();

    @NotNull Permission getEditorPermission();

    @NotNull YMLSection getTypeConfig(@NotNull E t);

    @Deprecated
    void saveConfig();

    void reload();
}
