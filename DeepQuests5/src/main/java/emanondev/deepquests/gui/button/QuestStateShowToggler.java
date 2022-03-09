package emanondev.deepquests.gui.button;

import emanondev.deepquests.Quests;
import emanondev.deepquests.gui.GuiConfig;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.utils.DisplayState;

public class QuestStateShowToggler extends StaticFlagButton {
    private final DisplayState state;

    public QuestStateShowToggler(Gui gui, DisplayState state) {
        super(GuiConfig.PlayerQuests.getQuestDisplayFlagItem(state, false),
                GuiConfig.PlayerQuests.getQuestDisplayFlagItem(state, true),
                gui);
        if (state == null)
            throw new NullPointerException();
        this.state = state;
    }

    @Override
    public boolean getCurrentValue() {
        return Quests.get().getPlayerInfo(getGui().getTargetPlayer()).canSeeQuestState(state);
    }

    @Override
    public boolean onValueChangeRequest(boolean value) {
        if (Quests.get().getPlayerInfo(getGui().getTargetPlayer()).canSeeQuestState(state) == value)
            return false;
        Quests.get().getPlayerInfo(getGui().getTargetPlayer()).toggleCanSeeQuestState(state);
        getGui().updateInventory();
        return true;
    }

}
