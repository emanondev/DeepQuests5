package emanondev.deepquests.gui.button;

import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.utils.DisplayState;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MissionButton extends QuestComponentButton<Mission> {
    private final User user;
    private DisplayState state;

    public MissionButton(Gui parent, Mission questComponent, User user) {
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
        if (user.getDisplayState(getQuestComponent()) == DisplayState.UNSTARTED)
            if (!user.startMission(getQuestComponent(), getTargetPlayer(), false)) {
                //TODO
                getTargetPlayer().sendMessage("TODO DEBUG can't start mission");
            } else
                this.getGui().updateInventory();
    }

    public User getUser() {
        return user;
    }

}