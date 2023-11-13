package emanondev.deepquests.gui.button;

import emanondev.core.ItemBuilder;
import emanondev.deepquests.gui.GuiConfig;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.gui.inventory.ListGui;
import emanondev.deepquests.utils.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

public abstract class CollectionSelectorButton<E> extends AButton {
    private final ItemStack item;
    private final String subGuiTitle;
    private final boolean canToggleWhitelist;

    public CollectionSelectorButton(String subGuiTitle, ItemStack item, Gui parent) {
        this(subGuiTitle, item, parent, true);
    }

    public CollectionSelectorButton(String subGuiTitle, ItemStack item, Gui parent, boolean canToggleWhitelist) {
        super(parent);
        this.canToggleWhitelist = canToggleWhitelist;
        this.item = item;
        this.subGuiTitle = subGuiTitle;
        update();
    }

    private static ItemStack createSelectedElementItem(ItemStack item) {
        return new ItemBuilder(item).setGuiProperty().addEnchantment(Enchantment.DURABILITY, 1).build();
    }

    private static ItemStack createBlacklistItem() {
        return new ItemBuilder(Material.BLACK_WOOL).setGuiProperty().build();
    }

    private static ItemStack createWhitelistItem() {
        return new ItemBuilder(Material.WHITE_WOOL).setGuiProperty().build();
    }

    public abstract Collection<E> getPossibleValues();

    /**
     * @return description of the item
     */
    public abstract List<String> getButtonDescription();

    public abstract List<String> getElementDescription(E element);

    public abstract ItemStack getElementItem(E element);

    public abstract boolean isValidContains(E element);

    public abstract boolean getIsWhitelist();

    public abstract boolean onToggleElementRequest(E element);

    public abstract boolean onWhitelistToggle();

    @Override
    public ItemStack getItem() {
        return item;
    }

    public boolean update() {
        Utils.updateDescription(item, getButtonDescription(), getGui().getTargetPlayer(), true);
        return true;
    }

    @Override
    public void onClick(Player clicker, ClickType click) {
        clicker.openInventory(new ListEditorGui(clicker).getInventory());
    }

    private class ListEditorGui extends ListGui<Button> {

        public ListEditorGui(Player clicker) {
            super(subGuiTitle, 6, clicker, CollectionSelectorButton.this.getGui(), 1);
            for (E element : getPossibleValues()) {
                this.addButton(new ElementButton(element));
            }
            if (canToggleWhitelist)
                this.setControlButton(1, new WhitelistFlagButton());
        }

        @Override
        public int loadNextPageButtonPosition() {
            return 7;
        }

        @Override
        public int loadPreviousPageButtonPosition() {
            return 6;
        }

        private class WhitelistFlagButton extends FlagButton {

            public WhitelistFlagButton() {
                super(createBlacklistItem(), createWhitelistItem(), ListEditorGui.this);
            }

            @Override
            public List<String> getButtonDescription() {
                if (getIsWhitelist())
                    return GuiConfig.Generic.WHITELIST_DESCRIPTION;
                return GuiConfig.Generic.BLACKLIST_DESCRIPTION;
            }

            @Override
            public boolean getCurrentValue() {
                return CollectionSelectorButton.this.getIsWhitelist();
            }

            @Override
            public boolean onValueChangeRequest(boolean value) {
                return CollectionSelectorButton.this.onWhitelistToggle();
            }
        }

        private class ElementButton extends FlagButton {
            private final E element;

            public ElementButton(E element) {
                super(getElementItem(element), createSelectedElementItem(getElementItem(element)), ListEditorGui.this);
                this.element = element;
            }

            @Override
            public List<String> getButtonDescription() {
                return getElementDescription(element);
            }

            @Override
            public boolean getCurrentValue() {
                return isValidContains(element);
            }

            @Override
            public boolean onValueChangeRequest(boolean value) {
                return onToggleElementRequest(element);
            }

        }


    }

}
