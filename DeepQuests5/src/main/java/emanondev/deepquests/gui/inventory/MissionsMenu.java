package emanondev.deepquests.gui.inventory;

import emanondev.deepquests.Quests;
import emanondev.deepquests.gui.GuiConfig;
import emanondev.deepquests.gui.button.MissionButton;
import emanondev.deepquests.gui.button.MissionStateShowToggler;
import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.Quest;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.utils.DisplayState;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MissionsMenu extends ListGui<MissionButton> {
    private final ArrayList<MissionButton> rawMissionButtons = new ArrayList<>();

    public MissionsMenu(@NotNull Player player, @Nullable Gui previusHolder) {
        this(player, previusHolder, Quests.get().getManagers());
    }

    public MissionsMenu(@NotNull Player player, @Nullable Gui previusHolder, @NotNull Collection<QuestManager<?>> managers) {
        super(GuiConfig.Generic.getQuestsMenuTitle(player), 6, player, previusHolder, 1);
        for (QuestManager<?> manager : managers) {
            if (manager == null)
                continue;
            User user = manager.getUserManager().getUser(getTargetPlayer());
            if (user == null)
                continue;

            for (Quest quest : manager.getQuests()) {
                Collection<Mission> missions = quest.getMissions();
                for (Mission mission : missions)
                    rawMissionButtons.add(new MissionButton(this, mission, user));
            }


        }
        rawMissionButtons.sort((b1, b2) -> {
            int result = b1.getQuestComponent().getQuest().getPriority() - b2.getQuestComponent().getQuest().getPriority();
            if (result == 0)
                return b1.getQuestComponent().getPriority() - b2.getQuestComponent().getPriority();
            return result;
        });
        this.setControlButton(0, new MissionStateShowToggler(this, DisplayState.LOCKED));
        this.setControlButton(1, new MissionStateShowToggler(this, DisplayState.UNSTARTED));
        this.setControlButton(2, new MissionStateShowToggler(this, DisplayState.ONPROGRESS));
        this.setControlButton(3, new MissionStateShowToggler(this, DisplayState.COMPLETED));
        this.setControlButton(4, new MissionStateShowToggler(this, DisplayState.COOLDOWN));
        this.setControlButton(5, new MissionStateShowToggler(this, DisplayState.FAILED));
        updateInventory();
    }

    public boolean updateInventory() {
        this.clearButtons();
        HashMap<Quest, Boolean> canseeCache = new HashMap<>();
        for (MissionButton button : rawMissionButtons) {
            Quest quest = button.getQuestComponent().getQuest();
            if (!canseeCache.containsKey(quest))
                canseeCache.put(quest, button.getUser().canSee(getTargetPlayer(), quest)
                        && (!quest.getDisplayInfo().isHidden(button.getUser().getDisplayState(quest))));
            if (!canseeCache.get(quest))
                continue;
            if (button.getUser().canSee(getTargetPlayer(), button.getQuestComponent())
                    && (!button.getQuestComponent().getDisplayInfo().isHidden(button.getUser().getDisplayState(button.getQuestComponent()))))
                this.addButton(button);
        }
        return super.updateInventory();
    }

    @Override
    protected int loadPreviousPageButtonPosition() {
        return 6;
    }

    @Override
    protected int loadNextPageButtonPosition() {
        return 7;
    }


}
