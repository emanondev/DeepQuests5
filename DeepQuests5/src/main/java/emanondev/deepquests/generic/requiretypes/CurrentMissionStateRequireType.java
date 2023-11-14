package emanondev.deepquests.generic.requiretypes;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.data.DisplayStateData;
import emanondev.deepquests.data.TargetMissionData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.ARequire;
import emanondev.deepquests.implementations.ARequireType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Require;
import emanondev.deepquests.interfaces.User;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CurrentMissionStateRequireType<T extends User<T>> extends ARequireType<T> {
    public static final String ID = "current_mission_state";

    public CurrentMissionStateRequireType(@NotNull QuestManager<T> manager) {
        super(ID, manager);
    }

    @Override
    public @NotNull Require<T> getInstance(int id, @NotNull QuestManager<T> manager, @NotNull YMLSection section) {
        return new CurrentMissionStateRequire(id, manager, section);
    }

    @Override
    public Material getGuiMaterial() {
        return Material.BLUE_BANNER;
    }

    @Override
    public ItemStack getGuiItem() {
        return new ItemBuilder(getGuiMaterial()).addPattern(DyeColor.WHITE, PatternType.RHOMBUS_MIDDLE).setGuiProperty()
                .build();
    }

    @Override
    public List<String> getDescription() {
        return List.of("Require selected quest to have one of selected status");
    }

    public class CurrentMissionStateRequire extends ARequire<T> {
        private final TargetMissionData<T, CurrentMissionStateRequire> targetMission;
        private final DisplayStateData<T, CurrentMissionStateRequire> stateData;

        public CurrentMissionStateRequire(int id, QuestManager<T> manager, YMLSection section) {
            super(id, manager, CurrentMissionStateRequireType.this, section);
            targetMission = new TargetMissionData<>(this,
                    getConfig().loadSection(Paths.REQUIRE_INFO_TARGETMISSION));
            stateData = new DisplayStateData<>(this,
                    getConfig().loadSection(Paths.REQUIRE_INFO_DISPLAYSTATE));
        }

        @Override
        public boolean isAllowed(@NotNull T user) {
            Mission<T> mission = targetMission.getMission();
            if (mission == null)
                return false;
            return stateData.isValidState(user.getDisplayState(mission));
        }

        public TargetMissionData<T, CurrentMissionStateRequire> getTargetMissionData() {
            return targetMission;
        }

        public DisplayStateData<T, CurrentMissionStateRequire> getDisplayStateData() {
            return stateData;
        }

        public @NotNull List<String> getInfo() {
            List<String> info = super.getInfo();
            info.addAll(targetMission.getInfo());
            info.addAll(stateData.getInfo());
            return info;
        }

        @Override
        public @NotNull Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ARequireGuiEditor {

            public GuiEditor(Player player, Gui previousHolder) {
                super(player, previousHolder);
                this.putButton(27, getTargetMissionData().getMissionSelectorButton(this));
                this.putButton(28, getDisplayStateData().getDisplaySelectorButton(this));
            }
        }

    }

}
