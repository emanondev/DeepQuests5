package emanondev.deepquests.interfaces;

import emanondev.core.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface QuestComponentType<T extends User<T>, E extends QuestComponent<T>> {

    /**
     * @return an item for display utility
     */
    Material getGuiMaterial();

    default ItemStack getGuiItem() {
        return new ItemBuilder(getGuiMaterial()).setGuiProperty().build();
    }

    /**
     * @return description of how this works
     */
    List<String> getDescription();

    /**
     * @return Description Utility which shows a recap of the object
     */
    default List<String> getInfo() {
        List<String> info = new ArrayList<>();
        info.add("&6Type: &e" + getKeyID());
        info.add("");
        info.addAll(getDescription());
        return info;
    }

    /**
     * @return an unique key for this
     */
    @NotNull String getKeyID();

    QuestManager<T> getManager();

    boolean getDefaultIsHidden();

    Permission getEditorPermission();
}
