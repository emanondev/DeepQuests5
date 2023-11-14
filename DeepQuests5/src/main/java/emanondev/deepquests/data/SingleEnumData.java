package emanondev.deepquests.data;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.gui.button.Button;
import emanondev.deepquests.gui.button.ElementSelectorButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestComponent;
import emanondev.deepquests.interfaces.User;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SingleEnumData<T extends User<T>, E extends QuestComponent<T>, Z extends Enum<Z>>
        extends QuestComponentData<T, E> {

    private final Class<Z> typeClass;
    private Z type;

    public SingleEnumData(@NotNull E parent, @NotNull YMLSection section, @NotNull Class<Z> type) {
        super(parent, section);
        this.typeClass = type;
    }

    public @Nullable Z getType() {
        return type;
    }

    public void setType(@Nullable Z type) {
        if (this.type == type)
            return;
        this.type = type;
        if (type != null)
            getConfig().setEnumAsString(Paths.DATA_ENUM, type);
        else
            getConfig().set(Paths.DATA_ENUM, null);
    }

    public ArrayList<String> getInfo() {
        ArrayList<String> list = new ArrayList<>();
        if (type != null)
            list.add("&9Type: &e" + type.name());
        else
            list.add("&9Type: &cnot set");
        return list;
    }


    public @NotNull Button getEditorButton(@NotNull Gui parent) {
        return new SingleEnumSelector(parent);
    }

    private class SingleEnumSelector extends ElementSelectorButton<Z> {

        public SingleEnumSelector(Gui parent) {
            super("&9Select a Value", new ItemBuilder(Material.EMERALD_BLOCK).setGuiProperty().build(), parent, true,
                    true, false);
        }

        @Override
        public List<String> getButtonDescription() {
            ArrayList<String> list = getInfo();
            list.add(0, "&6Type Selector");
            return list;
        }

        @Override
        public List<String> getElementDescription(Z element) {
            ArrayList<String> list = new ArrayList<>();
            list.add("&9SkillType: &e" + element.name());
            return list;
        }

        @Override
        public ItemStack getElementItem(Z element) {
            return new ItemBuilder(Material.EMERALD_BLOCK).setGuiProperty().build();
        }

        @Override
        public void onElementSelectRequest(Z element) {
            setType(element);
            getGui().updateInventory();
            getGui().getTargetPlayer().openInventory(getGui().getInventory());
        }

        @Override
        public Collection<Z> getPossibleValues() {
            return Arrays.asList(typeClass.getEnumConstants());
        }

    }

}
