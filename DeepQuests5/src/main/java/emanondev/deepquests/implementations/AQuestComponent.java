package emanondev.deepquests.implementations;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.gui.button.AmountSelectorButton;
import emanondev.deepquests.gui.button.GuiElementDescriptionButton;
import emanondev.deepquests.gui.button.TextEditorButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.gui.inventory.PagedMapGui;
import emanondev.deepquests.interfaces.QuestComponent;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

abstract class AQuestComponent<T extends User<T>> implements QuestComponent<T> {

    private int priority;
    private final int id;
    private final YMLSection section;
    private final QuestManager<T> manager;

    private String displayName;

    public AQuestComponent(int id, YMLSection section, QuestManager<T> manager) {
        if (manager == null || section == null)
            throw new NullPointerException();
        this.id = id;
        this.section = section;
        this.manager = manager;
        this.priority = section.getInteger(Paths.PRIORITY, 0);
        this.displayName = section.loadMessage(Paths.DISPLAY_NAME, String.valueOf(this.id));
    }

    @Override
    public final YMLSection getConfig() {
        return section;
    }

    @Override
    public final int getPriority() {
        return priority;
    }

    @Override
    public final void setPriority(int priority) {
        if (this.priority == priority)
            return;
        this.priority = priority;
        if (this.priority == 0)
            section.set(Paths.PRIORITY, null);
        else
            section.set(Paths.PRIORITY, this.priority);
    }

    public final String getDisplayName() {
        return displayName;
    }

    public final void setDisplayName(String name) {
        if (name == null || name.isEmpty())
            return;
        if (displayName.equals(name))
            return;
        this.displayName = name;
        section.set(Paths.DISPLAY_NAME, displayName);
        section.saveAsync();
    }

    @Override
    public final int hashCode() {
        return ((manager.hashCode() % 509) << 12) + id;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AQuestComponent other = (AQuestComponent) obj;
        if (id != other.id)
            return false;
        return manager.equals(other.manager);
    }

    @Override
    public final @NotNull QuestManager<T> getManager() {
        return manager;
    }

    @Override
    public final int getID() {
        return id;
    }

    protected class AGuiEditor extends PagedMapGui {

        public AGuiEditor(String title, Player player, Gui previousHolder) {
            super(title, 6, player, previousHolder);
            this.putButton(4, new GuiElementDescriptionButton(this, AQuestComponent.this));
            this.putButton(0, new DisplayNameButton());
            this.putButton(1, new PriorityButton());
        }

        private class PriorityButton extends AmountSelectorButton {

            public PriorityButton() {
                super("&9Priority Editor", new ItemBuilder(Material.GOLD_NUGGET).setGuiProperty().build(),
                        AGuiEditor.this);
            }

            @Override
            public List<String> getButtonDescription() {
                List<String> desc = new ArrayList<>();
                desc.add("&6Priority Button");
                desc.add("&9Current priority: &e" + priority);
                desc.add("");
                desc.add("&7Click to edit");
                return desc;
            }

            @Override
            public long getCurrentAmount() {
                return priority;
            }

            @Override
            public boolean onAmountChangeRequest(long value) {
                setPriority((int) value);
                return true;
            }

        }

        private class DisplayNameButton extends TextEditorButton {

            public DisplayNameButton() {
                super(new ItemBuilder(Material.NAME_TAG).setGuiProperty().build(), AGuiEditor.this);
            }

            @Override
            public void onClick(Player clicker, ClickType click) {
                this.requestText(clicker, displayName, "&6Set the DisplayName");
            }

            @Override
            public List<String> getButtonDescription() {
                List<String> desc = new ArrayList<>();
                desc.add("&6Display Name Button");
                desc.add("&9Current name: '&r" + displayName + "&9'");
                desc.add("");
                desc.add("&7Click to edit");
                return desc;
            }

            @Override
            public void onReicevedText(String text) {
                setDisplayName(text);
                getGui().updateInventory();
            }

        }

    }

}
