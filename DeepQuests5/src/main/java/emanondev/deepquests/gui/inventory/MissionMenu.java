package emanondev.deepquests.gui.inventory;

import emanondev.deepquests.gui.GuiConfig;
import emanondev.deepquests.gui.button.MissionButton;
import emanondev.deepquests.gui.button.MissionStateShowToggler;
import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.Quest;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.utils.DisplayState;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;


@SuppressWarnings({"rawtypes", "unchecked"})
public class MissionMenu extends ListGui<MissionButton> {
    public MissionMenu(Player player, Gui previousHolder, Quest quest, User user) {
        super(GuiConfig.Generic.getMissionsMenuTitle(player), 6, player, previousHolder, 1);
        if (user == null || quest == null)
            throw new NullPointerException();
        for (Object mission : quest.getMissions())
            rawMissionButtons.add(new MissionButton(this, (Mission) mission, user));
        Collections.sort(rawMissionButtons);
        this.setControlButton(0, new MissionStateShowToggler(this, DisplayState.LOCKED));
        this.setControlButton(1, new MissionStateShowToggler(this, DisplayState.UNSTARTED));
        this.setControlButton(2, new MissionStateShowToggler(this, DisplayState.ONPROGRESS));
        this.setControlButton(3, new MissionStateShowToggler(this, DisplayState.COMPLETED));
        this.setControlButton(4, new MissionStateShowToggler(this, DisplayState.COOLDOWN));
        this.setControlButton(5, new MissionStateShowToggler(this, DisplayState.FAILED));

        updateInventory();
    }

    private final ArrayList<MissionButton> rawMissionButtons = new ArrayList<>();

    public boolean updateInventory() {
        this.clearButtons();
        for (MissionButton button : rawMissionButtons)
            if (button.getUser().canSee(getTargetPlayer(), button.getQuestComponent())
                    && (!button.getQuestComponent().getDisplayInfo().isHidden(button.getUser().getDisplayState(button.getQuestComponent()))))
                this.addButton(button);
        return super.updateInventory();
    }

    protected int loadPreviousPageButtonPosition() {
        return 6;
    }

    protected int loadNextPageButtonPosition() {
        return 7;
    }
}
