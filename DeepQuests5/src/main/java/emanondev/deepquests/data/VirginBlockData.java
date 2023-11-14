package emanondev.deepquests.data;

import emanondev.core.Hooks;
import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.gui.button.StaticFlagButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestComponent;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.utils.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class VirginBlockData<T extends User<T>, E extends QuestComponent<T>> extends QuestComponentData<T, E> {
    private boolean checkVirgin;

    public VirginBlockData(@NotNull E parent, @NotNull YMLSection section) {
        super(parent, section);
        checkVirgin = getConfig().getBoolean(Paths.DATA_CHECK_VIRGIN, true);
    }

    public boolean isValidBlock(@NotNull Block block) {
        if (!checkVirgin)
            return true;
        return Hooks.isBlockVirgin(block);
    }

    public void setVirginCheck(boolean value) {
        if (checkVirgin == value)
            return;
        checkVirgin = value;
        getConfig().set(Paths.DATA_CHECK_VIRGIN, checkVirgin);
    }

    public boolean isVirginCheckEnabled() {
        return checkVirgin;
    }

    public @NotNull VirginStatus getVirginStatusEditor(@NotNull Gui gui) {
        return new VirginStatus(gui);
    }

    private class VirginStatus extends StaticFlagButton {

        public VirginStatus(Gui parent) {
            super(Utils.setDescription(new ItemBuilder(Material.STONE_BRICKS).setGuiProperty().build(),
                            Arrays.asList("&6Virgin Check", "&9Status: &cDisabled", "",
                                    "&7Check that block hasn't been placed by a player", "&7Click to toggle"),
                            null, true),
                    Utils.setDescription(new ItemBuilder(Material.CRACKED_STONE_BRICKS).setGuiProperty().build(),
                            Arrays.asList("&6Virgin Check", "&9Status: &aEnabled", "",
                                    "&7Check that block hasn't been placed by a player", "&7Click to toggle"),
                            null, true),
                    parent);
        }

        public ItemStack getItem() {
            if (!Hooks.isVirginBlockPluginEnabled())
                return null;
            return super.getItem();
        }

        @Override
        public boolean getCurrentValue() {
            return isVirginCheckEnabled();
        }

        @Override
        public boolean onValueChangeRequest(boolean value) {
            setVirginCheck(value);
            return true;
        }

    }
}
