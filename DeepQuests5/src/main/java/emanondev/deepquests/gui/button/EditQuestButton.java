package emanondev.deepquests.gui.button;

import emanondev.core.ItemBuilder;
import emanondev.deepquests.gui.inventory.EditMissionsMenu;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.interfaces.Quest;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.utils.DisplayState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class EditQuestButton<T extends User<T>> extends QuestComponentButton<Quest<T>> {
    private final T user;
    private DisplayState state;

    public EditQuestButton(Gui parent, Quest<T> questComponent, T user) {
        super(parent, questComponent);
        this.user = user;
        state = user.getDisplayState(getQuestComponent());
    }

    @Override
    public ItemStack getItem() {
        ArrayList<String> text = new ArrayList<>();
        ItemStack item = getQuestComponent().getDisplayInfo().getGuiItem(state, user, getGui().getTargetPlayer(), true);
        ItemMeta meta = item.getItemMeta();
        if (meta.hasLore())
            text.addAll(meta.getLore());
        text.add("");
        text.add(ChatColor.RED + "Left click to open");
        text.add(ChatColor.RED + "Right Click to edit");
        meta.setLore(text);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public boolean update() {
        state = user.getDisplayState(getQuestComponent());
        return true;
    }

    @Override
    public void onClick(Player clicker, ClickType click) {
        if (click == ClickType.RIGHT) {
            this.clicker = clicker;
            new ClickEditorButton().onClick(clicker, click);
            return;
        }
        clicker.openInventory(
                new EditMissionsMenu<>(getTargetPlayer(), getGui(), getQuestComponent(), user).getInventory());
    }

    private Player clicker = null;

    public T getUser() {
        return user;
    }

    private enum ActionType {
        RESET(new ItemBuilder(Material.ORANGE_TERRACOTTA).setGuiProperty().build(), Arrays.asList(
                ChatColor.GOLD + "Reset this Quest for user",
                ChatColor.GRAY + "Reset progress of all the missions in this",
                ChatColor.GRAY + "quest but keep statistic of missions completed",
                ChatColor.GRAY + "and failed previously and related timings")), COMPLETE(
                new ItemBuilder(Material.GREEN_TERRACOTTA).setGuiProperty().build(),
                Arrays.asList(ChatColor.GREEN + "Complete this Quest for user",
                        ChatColor.GRAY + "Mark the quest as completed")), FAIL(
                new ItemBuilder(Material.RED_TERRACOTTA).setGuiProperty().build(),
                Arrays.asList(ChatColor.RED + "Fail this Quest for user",
                        ChatColor.GRAY + "Mark the quest as failed")), ERASE(
                new ItemBuilder(Material.BLACK_TERRACOTTA).setGuiProperty()
                        .build(),
                Arrays.asList(ChatColor.RED + "Erase this Quest for user",
                        ChatColor.GRAY
                                + "All data about this quest and related",
                        ChatColor.GRAY
                                + "missions is completely erased for user"));
        private final ItemStack item;
        private final List<String> desc;

        ActionType(ItemStack item, List<String> desc) {
            this.item = item;
            this.desc = desc;
        }

        public ItemStack getItem() {
            return new ItemStack(item);
        }

        public List<String> getDescription() {
            return desc;
        }
    }

    private class ClickEditorButton extends ElementSelectorButton<ActionType> {

        public ClickEditorButton() {
            super(ChatColor.BLUE + "Click the action for " + EditQuestButton.this.getQuestComponent().getDisplayName(),
                    new ItemBuilder(Material.BARRIER).build(), EditQuestButton.this.getGui(), false, true, true);
        }

        @Override
        public List<String> getButtonDescription() {
            return null;
        }

        @Override
        public List<String> getElementDescription(ActionType element) {
            ArrayList<String> text = new ArrayList<>();
            ItemStack item = getQuestComponent().getDisplayInfo().getGuiItem(state, user, getGui().getTargetPlayer());
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName())
                text.add(meta.getDisplayName());
            if (meta.hasLore())
                text.addAll(meta.getLore());
            text.add("");
            text.add("&7Quest ID: " + getQuestComponent().getID());
            text.add("&7Display Name: &r" + getQuestComponent().getDisplayName());
            text.add("");
            text.addAll(element.getDescription());
            return text;
        }

        @Override
        public ItemStack getElementItem(ActionType element) {
            return element.getItem();
        }

        @Override
        public void onElementSelectRequest(ActionType element) {
            EditQuestButton.this.handle(element);

        }

        @Override
        public Collection<ActionType> getPossibleValues() {
            EnumSet<ActionType> set = EnumSet.noneOf(ActionType.class);
            switch (state) {
                case COMPLETED:
                    set.add(ActionType.RESET);
                    set.add(ActionType.FAIL);
                    set.add(ActionType.ERASE);
                    break;
                case FAILED:
                    set.add(ActionType.RESET);
                    set.add(ActionType.COMPLETE);
                    set.add(ActionType.ERASE);
                    break;
                case LOCKED:
                case COOLDOWN:
                case ONPROGRESS:
                    set.add(ActionType.RESET);
                    set.add(ActionType.COMPLETE);
                    set.add(ActionType.FAIL);
                    set.add(ActionType.ERASE);
                    break;
                case UNSTARTED:
                    set.add(ActionType.COMPLETE);
                    set.add(ActionType.FAIL);
                    set.add(ActionType.ERASE);
                    break;
            }
            return set;
        }

    }

    public void handle(ActionType element) {
        switch (element) {
            case COMPLETE:
                getUser().completeQuest(getQuestComponent());
                break;
            case ERASE:
                getUser().eraseQuestData(getQuestComponent());
                break;
            case FAIL:
                getUser().failQuest(getQuestComponent());
                break;
            case RESET:
                getUser().resetQuest(getQuestComponent());
                break;
        }
        this.getGui().updateInventory();
        if (clicker != null)
            clicker.openInventory(getGui().getInventory());
    }
}
