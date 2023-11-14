package emanondev.deepquests.data;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.gui.button.AButton;
import emanondev.deepquests.gui.button.ElementSelectorButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.gui.inventory.PagedMapGui;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestComponent;
import emanondev.deepquests.interfaces.User;
import emanondev.itemedit.ItemEdit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class QuestItemData<T extends User<T>, E extends QuestComponent<T>> extends QuestComponentData<T, E> {
    private String id;

    public QuestItemData(@NotNull E parent, @NotNull YMLSection section) {
        super(parent, section);
        id = getConfig().getString(Paths.QUESTITEM_ID, null);
    }

    public void setQuestItemID(String id) {
        if (Objects.equals(this.id, id))
            return;
        this.id = id;
        getConfig().set(Paths.QUESTITEM_ID, this.id);
    }

    public String getQuestItemID() {
        return id;
    }

    public String getQuestItemNick() {
        if (id == null)
            return "?";
        String nick = ItemEdit.get().getServerStorage().getNick(id);
        return nick == null ? "?" : nick;
    }

    public void setupButtons(PagedMapGui gui, int slot) {
        gui.putButton(slot, new ItemEditor(gui));
        gui.putButton(slot + 9, new ItemDisplayButton(gui));
    }

    public ArrayList<String> getInfo() {
        ArrayList<String> info = new ArrayList<>();
        if (id == null)
            info.add("&9Item: &cnot setted");
        else {
            info.add("&9Item:");
            info.add("  &9ID: &e" + id);
            info.add("  &9Nick: &f" + ItemEdit.get().getServerStorage().getNick(id));
        }
        return info;
    }

    private class ItemDisplayButton extends AButton {

        public ItemDisplayButton(Gui parent) {
            super(parent);
        }

        @Override
        public ItemStack getItem() {
            if (id == null)
                return null;
            return ItemEdit.get().getServerStorage().getItem(id);
        }

        @Override
        public boolean update() {
            return true;
        }

        @Override
        public void onClick(Player clicker, ClickType click) {
        }

    }

    private class ItemEditor extends ElementSelectorButton<String> {

        public ItemEditor(Gui parent) {
            super("&9Select a QuestItem", new ItemBuilder(Material.PAPER).setGuiProperty().build(), parent, true, true,
                    false);
        }

        @Override
        public List<String> getButtonDescription() {
            ArrayList<String> list = getInfo();
            list.add("");
            list.add("&9Click to change");
            return list;
        }

        @Override
        public List<String> getElementDescription(String element) {
            List<String> list = new ArrayList<>();
            ItemMeta meta = ItemEdit.get().getServerStorage().getItem(element).getItemMeta();
            if (meta.hasDisplayName())
                list.add(meta.getDisplayName());
            if (meta.hasLore())
                list.addAll(meta.getLore());
            return list;
        }

        @Override
        public ItemStack getElementItem(String element) {
            return ItemEdit.get().getServerStorage().getItem(element);
        }

        @Override
        public void onElementSelectRequest(String element) {
            setQuestItemID(element);
        }

        @Override
        public Collection<String> getPossibleValues() {
            return ItemEdit.get().getServerStorage().getIds();
        }

    }

}