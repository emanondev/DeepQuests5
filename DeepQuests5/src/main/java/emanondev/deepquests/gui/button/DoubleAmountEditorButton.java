package emanondev.deepquests.gui.button;

import emanondev.core.ItemBuilder;
import emanondev.deepquests.gui.GuiConfig;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.gui.inventory.MapGui;
import emanondev.deepquests.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class DoubleAmountEditorButton extends AButton {
    private final ItemStack item;
    private final String subGuiTitle;
    private final ArrayList<Double> values = new ArrayList<>();

    public DoubleAmountEditorButton(String subGuiTitle, ItemStack item, Gui parent) {
        this(item, subGuiTitle, parent, 0.01D, 0.1D, 1D, 10D, 100D, 1000D, 10000D);
    }


    public DoubleAmountEditorButton(String subGuiTitle, ItemStack item, Gui parent, double one, double two, double tree) {
        this(item, subGuiTitle, parent, one, two, tree);
    }

    public DoubleAmountEditorButton(String subGuiTitle, ItemStack item, Gui parent, double one, double two, double tree, double four, double five, double six, double seven) {
        this(item, subGuiTitle, parent, one, two, tree, four, five, six, seven);
    }

    public DoubleAmountEditorButton(ItemStack item, String subGuiTitle, Gui parent, double... values) {
        super(parent);
        this.item = item;
        this.subGuiTitle = subGuiTitle;
        for (double val : values)
            this.values.add(val);
        update();
    }

    private static ItemStack craftEditorAmountButtonItem(double amount) {
        ItemStack item;
        if (amount > 0) {
            item = new ItemBuilder(Material.LIME_WOOL).build();
            Utils.updateDescription(item, GuiConfig.Generic.AMOUNT_SELECTOR_ADD, null, true,
                    GuiConfig.AMOUNT_HOLDER, amount + "");
        } else {
            item = new ItemBuilder(Material.RED_WOOL).build();
            Utils.updateDescription(item, GuiConfig.Generic.AMOUNT_SELECTOR_REMOVE, null, true,
                    GuiConfig.AMOUNT_HOLDER, -amount + "");
        }
        return item;
    }

    /**
     * @return description of the item
     */
    public abstract List<String> getButtonDescription();

    public abstract double getCurrentAmount();

    public abstract boolean onAmountChangeRequest(double i);

    @Override
    public ItemStack getItem() {
        return item;
    }

    public boolean update() {
        Utils.updateDescription(item, getButtonDescription(), getGui().getTargetPlayer(), true, GuiConfig.AMOUNT_HOLDER,
                getCurrentAmount() + "");
        return true;
    }

    @Override
    public void onClick(Player clicker, ClickType click) {
        clicker.openInventory(new AmountEditorGui().getInventory());
    }

    private class AmountEditorGui extends MapGui {
        public AmountEditorGui() {
            super(Utils.fixString(subGuiTitle, DoubleAmountEditorButton.this.getTargetPlayer(), true), 6,
                    DoubleAmountEditorButton.this.getTargetPlayer(), DoubleAmountEditorButton.this.getGui());
            this.putButton(4, new ShowAmountButton());
            this.putButton(53, new BackButton(this));
            if (values.size() == 3) {
                for (int i = 0; i < values.size(); i++) {
                    this.putButton(19 + i * 3, new EditAmountButton(values.get(i)));
                    this.putButton(28 + i * 3, new EditAmountButton(-values.get(i)));
                }
            } else if (values.size() == 7)
                for (int i = 0; i < values.size(); i++) {
                    this.putButton(19 + i, new EditAmountButton(values.get(i)));
                    this.putButton(28 + i, new EditAmountButton(-values.get(i)));
                }
        }

        private class ShowAmountButton extends AButton {
            private final ItemStack item = new ItemStack(Material.REPEATER);

            public ShowAmountButton() {
                super(AmountEditorGui.this);
                update();
            }

            @Override
            public ItemStack getItem() {
                return item;
            }

            @Override
            public boolean update() {
                Utils.updateDescription(item, GuiConfig.Generic.AMOUNT_SELECTOR_SHOW, getTargetPlayer(), true, GuiConfig.AMOUNT_HOLDER, "" + getCurrentAmount());
                return true;
            }

            @Override
            public void onClick(Player clicker, ClickType click) {
            }

        }

        private class EditAmountButton extends StaticButton {
            private final double amount;

            public EditAmountButton(double amount) {
                super(craftEditorAmountButtonItem(amount), AmountEditorGui.this);
                this.amount = amount;
            }

            @Override
            public void onClick(Player clicker, ClickType click) {
                if (click == ClickType.LEFT)
                    if (onAmountChangeRequest(getCurrentAmount() + amount))
                        getGui().updateInventory();
            }
        }
    }
}