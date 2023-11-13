package emanondev.deepquests.gui.button;

import emanondev.core.ItemBuilder;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.interfaces.Task;
import emanondev.deepquests.interfaces.Task.Phase;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class EditTaskButton<T extends User<T>> extends QuestComponentButton<Task<T>> {
    private final T user;
    private Player clicker = null;

    public EditTaskButton(Gui parent, Task<T> questComponent, T user) {
        super(parent, questComponent);
        this.user = user;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = getQuestComponent().getType().getGuiItem();
        Utils.updateDescription(item, Arrays.asList(getQuestComponent().getDisplayName(),
                        getQuestComponent().getPhaseDescription(user, Phase.UNSTARTED),
                        getQuestComponent().getPhaseDescription(user, Phase.PROGRESS), "", "&cClick to manipulate the task"),
                getGui().getTargetPlayer(), true, getQuestComponent().getHolders(user));
        return item;
    }

    @Override
    public boolean update() {
        return true;
    }

    @Override
    public void onClick(Player clicker, ClickType click) {
        this.clicker = clicker;
        new ClickEditorButton().onClick(clicker, click);
    }

    public T getUser() {
        return user;
    }

    public void handle(ActionType element) {
        switch (element) {
            case PROGRESS_1:
                getUser().progressTask(getQuestComponent(), 1, null, true);
                break;
            case PROGRESS_5:
                getUser().progressTask(getQuestComponent(), 5, null, true);
                break;
            case PROGRESS_10:
                getUser().progressTask(getQuestComponent(), 10, null, true);
                break;
            case PROGRESS_50:
                getUser().progressTask(getQuestComponent(), 50, null, true);
                break;
            case ERASE:
                getUser().eraseTaskData(getQuestComponent());
                break;
            case RESET:
                getUser().resetTask(getQuestComponent());
                break;
        }
        this.getGui().updateInventory();
        if (clicker != null)
            clicker.openInventory(getGui().getInventory());
    }

    private enum ActionType {
        RESET(new ItemBuilder(Material.ORANGE_TERRACOTTA).setGuiProperty().build(), Arrays.asList(
                ChatColor.GOLD + "Reset this task for user",
                ChatColor.GRAY + "Reset progress of this task")), PROGRESS_1(
                new ItemBuilder(Material.LIGHT_BLUE_TERRACOTTA).setGuiProperty().build(),
                Arrays.asList(ChatColor.BLUE + "Progress by 1 for user",
                        ChatColor.GRAY + "Progress on the task for user",
                        ChatColor.GRAY + "by 1 also assign progress rewards")), PROGRESS_5(
                new ItemBuilder(Material.LIGHT_BLUE_TERRACOTTA).setGuiProperty().build(),
                Arrays.asList(ChatColor.BLUE + "Progress by up to 5 for user",
                        ChatColor.GRAY + "Progress on the task for user",
                        ChatColor.GRAY + "by 5 also assign progress rewards")), PROGRESS_10(
                new ItemBuilder(Material.LIGHT_BLUE_TERRACOTTA).setGuiProperty()
                        .build(),
                Arrays.asList(ChatColor.BLUE + "Progress by up to 10 for user",
                        ChatColor.GRAY + "Progress on the task for user",
                        ChatColor.GRAY
                                + "by 10 also assign progress rewards")), PROGRESS_50(
                new ItemBuilder(
                        Material.LIGHT_BLUE_TERRACOTTA)
                        .setGuiProperty()
                        .build(),
                Arrays.asList(ChatColor.BLUE
                                + "Progress by up to 50 for user",
                        ChatColor.GRAY
                                + "Progress on the task for user",
                        ChatColor.GRAY
                                + "by 50 also assign progress rewards")), ERASE(
                new ItemBuilder(
                        Material.BLACK_TERRACOTTA)
                        .setGuiProperty()
                        .build(),
                Arrays.asList(
                        ChatColor.RED
                                + "Erase this Task for user",
                        ChatColor.GRAY
                                + "All data about this task is completely",
                        ChatColor.GRAY
                                + "erased for user"));
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
            super(ChatColor.BLUE + "Click the action for " + EditTaskButton.this.getQuestComponent().getDisplayName(),
                    new ItemBuilder(Material.BARRIER).build(), EditTaskButton.this.getGui(), false, true, true);
        }

        @Override
        public List<String> getButtonDescription() {
            return null;
        }

        @Override
        public List<String> getElementDescription(ActionType element) {
            ArrayList<String> text = new ArrayList<>();
            switch (user.getDisplayState(getQuestComponent().getMission())) {
                case COMPLETED:
                case COOLDOWN:
                case FAILED:
                case ONPROGRESS:
                    text.add(getQuestComponent().getPhaseDescription(user, Phase.PROGRESS));
                    break;
                case LOCKED:
                case UNSTARTED:
                    text.add(getQuestComponent().getPhaseDescription(user, Phase.UNSTARTED));
                    break;
            }
            text.add("");
            text.add("&7Task ID: " + getQuestComponent().getID());
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
            EditTaskButton.this.handle(element);

        }

        @Override
        public Collection<ActionType> getPossibleValues() {
            EnumSet<ActionType> set = EnumSet.noneOf(ActionType.class);
            switch (user.getDisplayState(getQuestComponent().getMission())) {
                case COMPLETED:
                case LOCKED:
                case COOLDOWN:
                case FAILED:
                case UNSTARTED:
                    set.add(ActionType.RESET);
                    set.add(ActionType.ERASE);
                    break;
                case ONPROGRESS:
                    set.add(ActionType.RESET);
                    set.add(ActionType.PROGRESS_1);
                    set.add(ActionType.PROGRESS_5);
                    set.add(ActionType.PROGRESS_10);
                    set.add(ActionType.PROGRESS_50);
                    set.add(ActionType.ERASE);
                    break;
            }
            return set;
        }

    }
}