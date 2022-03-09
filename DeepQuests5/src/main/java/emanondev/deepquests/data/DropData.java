package emanondev.deepquests.data;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.gui.button.StaticFlagButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestComponent;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.utils.Utils;
import org.bukkit.Material;

import java.util.Arrays;

public class DropData<T extends User<T>, E extends QuestComponent<T>> extends QuestComponentData<T, E> {

    private boolean removeDrops;
    private boolean removeExp;

    public DropData(E parent, YMLSection section) {
        super(parent, section);
        removeDrops = getConfig().getBoolean(Paths.DATA_DENY_ITEM_DROPS, false);
        removeExp = getConfig().getBoolean(Paths.DATA_DENY_EXP_DROPS, false);
    }

    public boolean removeItemDrops() {
        return removeDrops;
    }

    public boolean removeExpDrops() {
        return removeExp;
    }

    public void setRemoveItemDrops(boolean value) {
        if (removeDrops == value)
            return;
        removeDrops = value;
        getConfig().set(Paths.DATA_DENY_ITEM_DROPS, removeDrops);
    }

    public void setRemoveExpDrops(boolean value) {
        if (removeExp == value)
            return;
        removeExp = value;
        getConfig().set(Paths.DATA_DENY_EXP_DROPS, removeExp);
    }

    public ItemDropsFlag getItemDropsFlagButton(Gui gui) {
        return new ItemDropsFlag(gui);
    }

    public ExpDropsFlag getExpDropsFlagButton(Gui gui) {
        return new ExpDropsFlag(gui);
    }

    private class ItemDropsFlag extends StaticFlagButton {

        public ItemDropsFlag(Gui parent) {
            super(Utils.setDescription(new ItemBuilder(Material.GOLD_INGOT).setGuiProperty().build(),
                            Arrays.asList("&6&lDrop Flag", "&6Click to Toggle", "&7Drops are not removed",
                                    "&7(Vanilla behavior)"),
                            null, true),
                    Utils.setDescription(new ItemBuilder(Material.GOLD_INGOT).setGuiProperty().build(),
                            Arrays.asList("&6&lDrop Flag", "&6Click to Toggle", "&cDrops are removed"), null, true),
                    parent);
        }

        @Override
        public boolean getCurrentValue() {
            return removeItemDrops();
        }

        @Override
        public boolean onValueChangeRequest(boolean value) {
            setRemoveItemDrops(value);
            return true;
        }

    }

    private class ExpDropsFlag extends StaticFlagButton {

        public ExpDropsFlag(Gui parent) {
            super(Utils.setDescription(new ItemBuilder(Material.EXPERIENCE_BOTTLE).setGuiProperty().build(),
                            Arrays.asList("&6&lExp Drops Flag", "&6Click to Toggle", "&7Exp Drops are not removed",
                                    "&7(Vanilla behavior)"),
                            null, true),
                    Utils.setDescription(new ItemBuilder(Material.EXPERIENCE_BOTTLE).setGuiProperty().build(),
                            Arrays.asList("&6&lExp Drops Flag", "&6Click to Toggle", "&cExp Drops are removed"), null,
                            true),
                    parent);
        }

        @Override
        public boolean getCurrentValue() {
            return removeExpDrops();
        }

        @Override
        public boolean onValueChangeRequest(boolean value) {
            setRemoveExpDrops(value);
            return true;
        }

    }

}