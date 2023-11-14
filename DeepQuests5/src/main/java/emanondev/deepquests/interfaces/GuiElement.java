package emanondev.deepquests.interfaces;

import emanondev.core.ItemBuilder;
import emanondev.deepquests.gui.button.SortableButton;
import emanondev.deepquests.gui.inventory.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface GuiElement {

    /**
     * @return a material representation of the object
     */
    @NotNull Material getGuiMaterial();

    /**
     * @return a item Stack representation of the object, no DisplayName nor Lore should be set
     */
    default @NotNull ItemStack getGuiItem() {
        return new ItemBuilder(getGuiMaterial()).setGuiProperty().build();
    }

    /**
     * @return an editor gui of the object
     */
    @NotNull Gui getEditorGui(Player target, Gui parent);


    /**
     * @return the priority of the object
     */
    int getPriority();


    /**
     * @return Description Utility which shows a recap of the object
     */
    List<String> getInfo();

    /**
     * @return a button for Description Utility which shows a recap of the object and do nothing when clicked
     */
    @NotNull SortableButton getEditorButton(@NotNull Gui parent);

}
