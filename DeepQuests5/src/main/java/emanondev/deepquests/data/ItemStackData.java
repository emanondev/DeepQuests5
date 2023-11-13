package emanondev.deepquests.data;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.gui.button.AButton;
import emanondev.deepquests.gui.button.ItemEditorButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.gui.inventory.PagedMapGui;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestComponent;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemStackData<T extends User<T>, E extends QuestComponent<T>> extends QuestComponentData<T, E> {
    private ItemStack item;

    public ItemStackData(E parent, YMLSection section) {
        super(parent, section);
        item = getConfig().getItemStack(Paths.ITEMSTACK_INFO, null);
    }

    public void setItem(ItemStack item) {
        if (this.item == item)
            return;
        if (item == null) {
            this.item = null;
            getConfig().set(Paths.ITEMSTACK_INFO, this.item);
            return;
        }
        if (this.item == null) {
            this.item = new ItemStack(item);
            this.item.setAmount(1);
            getConfig().set(Paths.ITEMSTACK_INFO, this.item);
            return;
        }
        if (this.item.isSimilar(item))
            return;
        this.item = new ItemStack(item);
        this.item.setAmount(1);
        getConfig().set(Paths.ITEMSTACK_INFO, this.item);
    }

    public ItemStack getItem() {
        if (item == null)
            return null;
        return new ItemStack(item);
    }

    public void setupButtons(PagedMapGui gui, int slot) {
        gui.putButton(slot, new ItemEditor(gui));
        gui.putButton(slot + 9, new ItemDisplayButton(gui));
    }

    public ArrayList<String> getInfo() {
        ArrayList<String> info = new ArrayList<>();
        if (item == null)
            info.add("&9Item: &cnot setted");
        else {
            info.add("&9Item:");
            info.add("  &9Type: &e" + item.getType());
            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta.hasDisplayName())
                    info.add("  &9Title: &f" + meta.getDisplayName());
                if (meta.hasLore()) {
                    info.add("  &9Lore:");
                    for (String str : meta.getLore()) {
                        info.add("    &5" + str);
                    }
                }
                if (meta.hasEnchants()) {
                    info.add("  &9Enchants:");
                    meta.getEnchants().forEach((ench, lv) -> info.add("    &e" + ench.getKey().getKey() + " &9lv &e" + lv));
                }
                if (meta.isUnbreakable())
                    info.add("  &9Unbreakable: &etrue");
                meta.getItemFlags();
                if (!meta.getItemFlags().isEmpty()) {
                    info.add("  &9Flags:");
                    meta.getItemFlags().forEach((flag) -> info.add("    &e" + flag));
                }
                if (meta instanceof SkullMeta && ((SkullMeta) meta).hasOwner()) {
                    info.add("  &9Skull Owner: &e" + ((SkullMeta) meta).getOwningPlayer().getName());
                }
                if (meta instanceof Damageable) {
                    int damage = ((Damageable) meta).getDamage();
                    if (damage > 0)
                        info.add("  &9Damage: &e" + damage);
                }
            }
        }
        return info;
    }

    private class ItemDisplayButton extends AButton {

        public ItemDisplayButton(Gui parent) {
            super(parent);
        }

        @Override
        public ItemStack getItem() {
            return ItemStackData.this.getItem();
        }

        @Override
        public boolean update() {
            return true;
        }

        @Override
        public void onClick(Player clicker, ClickType click) {
        }

    }

    private class ItemEditor extends ItemEditorButton {

        public ItemEditor(Gui parent) {
            super(parent);
        }

        @Override
        public void onClick(Player clicker, ClickType click) {
            this.requestItem(clicker, "&6Select the item");
        }

        @Override
        public ItemStack getCurrentItem() {
            return ItemStackData.this.getItem();
        }

        @Override
        public void onReicevedItem(ItemStack item) {
            setItem(item);
            getGui().updateInventory();
        }

        @Override
        public List<String> getButtonDescription() {
            if (ItemStackData.this.getItem() == null) {
                return Arrays.asList("&6ItemEditor Button", "&cNo item Setted", "&7Click to set");
            }
            return Arrays.asList("&6ItemEditor Button", "&9Item as below", "&7Click to set");
        }

        public ItemStack getItem() {
            if (ItemStackData.this.getItem() == null)
                return Utils.setDescription(new ItemBuilder(Material.BARRIER).setGuiProperty().build(),
                        getButtonDescription(), null, true);
            return Utils.setDescription(new ItemBuilder(ItemStackData.this.getItem()).setGuiProperty().build(),
                    getButtonDescription(), null, true);
        }

    }

}
