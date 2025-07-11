package emanondev.deepquests.data;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.gui.button.Button;
import emanondev.deepquests.gui.button.GuiElementSelectorButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.Quest;
import emanondev.deepquests.interfaces.QuestComponent;
import emanondev.deepquests.interfaces.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TargetMissionData<T extends User<T>, E extends QuestComponent<T>> extends QuestComponentData<T, E> {
    private Integer missionKey;

    public TargetMissionData(@NotNull E parent, @NotNull YMLSection section) {
        super(parent, section);
        missionKey = getConfig().getInteger(Paths.DATA_TARGET_MISSION_KEY, null);
    }

    public @Nullable Mission<T> getMission() {
        if (missionKey == null)
            return null;
        return getQuestManager().getMission(missionKey);
    }

    public List<String> getInfo() {
        ArrayList<String> desc = new ArrayList<>();
        Mission<T> mission = getMission();
        if (mission == null)
            desc.add("&9Mission: &cnot setted");
        else {
            desc.add("&9Mission: &e" + mission.getDisplayName() + " &8(" + mission.getID() + ")");
            desc.add("&9Quest: &e" + mission.getQuest().getDisplayName() + " &8(" + mission.getQuest().getID() + ")");
        }
        return desc;
    }

    public void setMission(@Nullable Mission<T> mission) {
        if (mission == null) {
            missionKey = null;
            getConfig().set(Paths.DATA_TARGET_MISSION_KEY, missionKey);
            return;
        }
        if (!this.getQuestManager().equals(mission.getManager()))
            return;
        this.missionKey = mission.getID();
        getConfig().set(Paths.DATA_TARGET_MISSION_KEY, missionKey);
    }

    public @NotNull Button getMissionSelectorButton(@NotNull Gui gui) {
        return new MissionSelectorButton(gui);
    }

    private class MissionSelectorButton extends GuiElementSelectorButton<Quest<T>> {

        public MissionSelectorButton(Gui parent) {
            super("&9Select a Quest", new ItemBuilder(Material.BOOK).setGuiProperty().build(), parent, false, true,
                    false);
        }

        @Override
        public List<String> getButtonDescription() {
            List<String> list = getInfo();
            list.add(0, "&6Mission Selector Button");
            return list;
        }

        @Override
        public Collection<Quest<T>> getValues() {
            return getQuestManager().getQuests();
        }

        @Override
        public void onElementSelectRequest(Quest<T> element, Player p) {
            new MissionSelector(getGui(), element).onClick(p, ClickType.LEFT);
        }

        private class MissionSelector extends GuiElementSelectorButton<Mission<T>> {
            private final Quest<T> quest;

            public MissionSelector(Gui parent, Quest<T> quest) {
                super("&9Select a Mission", new ItemBuilder(Material.BOOK).setGuiProperty().build(), parent, true, true,
                        false);
                this.quest = quest;
            }

            @Override
            public List<String> getButtonDescription() {
                return null;
            }

            @Override
            public Collection<Mission<T>> getValues() {
                return quest.getMissions();
            }

            @Override
            public void onElementSelectRequest(Mission<T> element, Player p) {
                setMission(element);
                getGui().updateInventory();
                p.openInventory(getGui().getInventory());
            }
        }
    }
}
