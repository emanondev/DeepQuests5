package emanondev.deepquests.gui.button;

import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.gui.inventory.MissionMenu;
import emanondev.deepquests.interfaces.Quest;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.utils.DisplayState;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings({"rawtypes", "unchecked"})
public class QuestButton extends QuestComponentButton<Quest> {
    private final User user;
    private DisplayState state;

    public QuestButton(Gui parent, Quest questComponent, User user) {
        super(parent, questComponent);
        this.user = user;
        state = user.getDisplayState(getQuestComponent());
    }

    @Override
    public ItemStack getItem() {
        return getQuestComponent().getDisplayInfo().getGuiItem(state, user, getGui().getTargetPlayer());
    }

    @Override
    public boolean update() {
        state = user.getDisplayState(getQuestComponent());
        return true;
    }

    @Override
    public void onClick(Player clicker, ClickType click) {
        if (state != DisplayState.LOCKED && state != DisplayState.FAILED)
            clicker.openInventory(new MissionMenu(getTargetPlayer(), getGui(), getQuestComponent(), user).getInventory());
    }

    public User getUser() {
        return user;
    }

}
