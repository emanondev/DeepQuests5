package emanondev.deepquests.gui.button;

import emanondev.deepquests.gui.GuiConfig;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.gui.inventory.MapGui;
import emanondev.deepquests.gui.inventory.SortedListGui;
import emanondev.deepquests.interfaces.GuiElement;
import emanondev.deepquests.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

public abstract class GuiElementSelectorButton<T extends GuiElement> extends AButton {

    private final ItemStack item;
    private final String subGuiTitle;
    private final boolean allowNull;
    private final boolean allowBack;
    private final boolean requireConfirm;

    public GuiElementSelectorButton(String subGuiTitle, ItemStack item, Gui parent,
                                    boolean allowNull, boolean allowBack, boolean requireConfirm) {
        super(parent);
        this.allowBack = allowBack;
        this.allowNull = allowNull;
        this.item = item;
        this.subGuiTitle = subGuiTitle;
        this.requireConfirm = requireConfirm;
        update();
    }

    public abstract List<String> getButtonDescription();

    public List<String> getElementDescription(T element) {
        return element.getInfo();
    }

    public ItemStack getElementItem(T element) {
        return element.getGuiItem();
    }

    public abstract Collection<T> getValues();

    @Override
    public ItemStack getItem() {
        Collection<T> values = getValues();
        if (values == null || values.isEmpty())
            return null;
        Utils.updateDescription(item, getButtonDescription(), getGui().getTargetPlayer(), true);
        item.setAmount(getValues().size());
        return item;
    }

    public boolean update() {
        return true;
    }

    @Override
    public void onClick(Player clicker, ClickType click) {
        Collection<T> values = getValues();
        if (values == null || values.isEmpty())
            return;
        clicker.openInventory(new ListEditorGui(clicker).getInventory());
    }

    private class ListEditorGui extends SortedListGui<ElementButton> {
        public ListEditorGui(Player clicker) {
            super(subGuiTitle, 6, clicker, GuiElementSelectorButton.this.getGui(), 1);
            for (T element : getValues()) {
                this.addButton(new ElementButton(this, element));
            }
            if (allowNull)
                this.setControlButton(0, new NullElementButton(this));
            if (allowBack)
                this.setControlButton(8, new BackButton(this));
            updateInventory();
        }

        @Override
        public int loadNextPageButtonPosition() {
            return 7;
        }

        @Override
        public int loadPreviusPageButtonPosition() {
            return 6;
        }

    }

    private class ElementButton extends AGuiElementButton<T> {
        private final ItemStack item;

        public ElementButton(Gui parent, T element) {
            super(parent, element);
            item = Utils.setDescription(getElementItem(element)
                    , getElementDescription(element), null, true);
        }

        @Override
        public void onClick(Player clicker, ClickType click) {
            if (requireConfirm)
                clicker.openInventory(new ConfirmGui(clicker, this).getInventory());
            else
                onElementSelectRequest(getElement(), clicker);
        }


        @Override
        public ItemStack getItem() {
            return Utils.setDescription(item, getElement().getInfo(), null, true);
        }

        @Override
        public boolean update() {
            return true;
        }
    }

    private class NullElementButton extends StaticButton {

        public NullElementButton(Gui parent) {
            super(Utils.setDescription(new ItemStack(Material.BARRIER), GuiConfig.Generic.NULL_ELEMENT, null, true), parent);
        }

        @Override
        public void onClick(Player clicker, ClickType click) {
            if (requireConfirm)
                clicker.openInventory(new NullConfirmGui(clicker, this).getInventory());
            else
                onElementSelectRequest(null, clicker);
        }


    }

    private class NullConfirmGui extends MapGui {

        public NullConfirmGui(Player p, Button button) {
            super(GuiConfig.Generic.CONFIRM_CLICK_GUI_TITLE, 6, p, button.getGui());
            this.putButton(53, new BackButton(this));
            this.putButton(4, new StaticButton(button.getItem(), NullConfirmGui.this) {
                public void onClick(Player clicker, ClickType click) {
                }
            });
            this.putButton(29, new StaticButton(
                    GuiConfig.Generic.getConfirmButtonItem(getTargetPlayer()), this) {
                public void onClick(Player clicker, ClickType click) {
                    onElementSelectRequest(null, clicker);
                }
            });
            this.putButton(33, new StaticButton(
                    GuiConfig.Generic.getUnconfirmButtonItem(getTargetPlayer()), this) {
                public void onClick(Player clicker, ClickType click) {
                    clicker.openInventory(GuiElementSelectorButton.this.getGui().getInventory());
                }
            });
        }
    }

    private class ConfirmGui extends MapGui {

        public ConfirmGui(Player p, ElementButton button) {
            super(GuiConfig.Generic.CONFIRM_CLICK_GUI_TITLE, 6, p, button.getGui());
            this.putButton(53, new BackButton(this));
            this.putButton(4, new StaticButton(button.getItem(), ConfirmGui.this) {
                public void onClick(Player clicker, ClickType click) {
                }
            });
            this.putButton(29, new StaticButton(
                    GuiConfig.Generic.getConfirmButtonItem(getTargetPlayer()), this) {
                public void onClick(Player clicker, ClickType click) {
                    onElementSelectRequest(button.getElement(), clicker);
                }
            });
            this.putButton(33, new StaticButton(
                    GuiConfig.Generic.getUnconfirmButtonItem(getTargetPlayer()), this) {
                public void onClick(Player clicker, ClickType click) {
                    clicker.openInventory(GuiElementSelectorButton.this.getGui().getInventory());
                }
            });
        }
    }

    public abstract void onElementSelectRequest(T element, Player p);
}