package emanondev.deepquests.gui.button;

import emanondev.deepquests.Quests;
import emanondev.deepquests.gui.GuiConfig;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.utils.DisplayState;

public class MissionStateShowToggler extends StaticFlagButton {
    private final DisplayState state;

    public MissionStateShowToggler(Gui gui, DisplayState state) {
        super(GuiConfig.PlayerQuests.getMissionDisplayFlagItem(state, false),
                GuiConfig.PlayerQuests.getMissionDisplayFlagItem(state, true),
                gui);
        if (state == null)
            throw new NullPointerException();
        this.state = state;
    }

    @Override
    public boolean getCurrentValue() {
        return Quests.get().getPlayerInfo(getGui().getTargetPlayer()).canSeeMissionState(state);
    }

    @Override
    public boolean onValueChangeRequest(boolean value) {
        if (Quests.get().getPlayerInfo(getGui().getTargetPlayer()).canSeeMissionState(state) == value)
            return false;
        Quests.get().getPlayerInfo(getGui().getTargetPlayer()).toggleCanSeeMissionState(state);
        getGui().updateInventory();
        return true;
    }

}