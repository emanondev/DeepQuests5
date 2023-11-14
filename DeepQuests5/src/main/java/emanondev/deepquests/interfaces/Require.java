package emanondev.deepquests.interfaces;

import emanondev.deepquests.Holders;
import emanondev.deepquests.gui.button.GuiElementButton;
import emanondev.deepquests.gui.button.SortableButton;
import emanondev.deepquests.gui.inventory.Gui;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public interface Require<T extends User<T>> extends QuestComponent<T> {
    /**
     * @return the Type
     */
    RequireType<T> getType();

    /**
     * @return true if user satisfy this require
     */
    boolean isAllowed(@NotNull T user);

    default @NotNull String[] getHolders(T user) {
        String[] list = new String[4];
        list[0] = Holders.REQUIRE_DESCRIPTION;
        list[1] = this.getDisplayName();
        list[2] = Holders.REQUIRE_IS_COMPLETED;
        list[3] = isAllowed(user) ? "&a" : "&c";
        return list;
    }

    @Override
    default @NotNull Material getGuiMaterial() {
        return Material.TRIPWIRE_HOOK;
    }

    @Override
    default @NotNull SortableButton getEditorButton(@NotNull Gui parent) {
        return new GuiElementButton<>(parent, this);
    }

    @NotNull QuestManager<T> getManager();

    default @NotNull String getTypeName() {
        return getType().getKeyID();
    }

    boolean isHidden();

    void setHidden(Boolean value);

}
