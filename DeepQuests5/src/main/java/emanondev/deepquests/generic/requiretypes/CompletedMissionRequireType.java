package emanondev.deepquests.generic.requiretypes;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.data.AmountData;
import emanondev.deepquests.data.TargetMissionData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.ARequire;
import emanondev.deepquests.implementations.ARequireType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.*;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class CompletedMissionRequireType<T extends User<T>> extends ARequireType<T> {

    public static final String ID = "completed_mission";

    public CompletedMissionRequireType(@NotNull QuestManager<T> manager) {
        super(ID, manager);
    }

    @Override
    public Material getGuiMaterial() {
        return Material.LIME_BANNER;
    }

    @Override
    public ItemStack getGuiItem() {
        return new ItemBuilder(getGuiMaterial()).addPattern(DyeColor.WHITE, PatternType.RHOMBUS_MIDDLE).setGuiProperty()
                .build();
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7Player require to complete mission n times");
    }

    @Override
    public @NotNull Require<T> getInstance(int id, @NotNull QuestManager<T> manager, @NotNull YMLSection section) {
        return new CompletedMissionRequire(id, manager, section);
    }

    public class CompletedMissionRequire extends ARequire<T> {

        private final TargetMissionData<T, CompletedMissionRequire> targetMission;
        private final AmountData<T, CompletedMissionRequire> amountData;

        public CompletedMissionRequire(int id, QuestManager<T> manager, YMLSection section) {
            super(id, manager, CompletedMissionRequireType.this, section);
            targetMission = new TargetMissionData<>(this,
                    getConfig().loadSection(Paths.REQUIRE_INFO_TARGETMISSION));
            amountData = new AmountData<>(this,
                    getConfig().loadSection(Paths.REQUIRE_INFO_AMOUNT), 1, Integer.MAX_VALUE, 1);
        }

        @Override
        public boolean isAllowed(@NotNull T user) {
            Mission<T> mission = targetMission.getMission();
            if (mission == null)
                return false;
            MissionData<T> missionData = user.getMissionData(mission);
            return missionData.successfullyCompletedTimes() >= amountData.getAmount();
        }

        public TargetMissionData<T, CompletedMissionRequire> getTargetMissionData() {
            return targetMission;
        }

        public AmountData<T, CompletedMissionRequire> getAmountData() {
            return amountData;
        }

        public @NotNull List<String> getInfo() {
            List<String> info = super.getInfo();
            info.addAll(targetMission.getInfo());
            if (amountData.getAmount() != 1)
                info.add("&9Must be Completed: &e" + amountData.getAmount() + " &9times");
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
                this.putButton(28,
                        getAmountData().getAmountEditorButton("&9Completed Times Selector",
                                Arrays.asList("&6Completed Times Selector", "&9Amount: &e%amount%"),
                                new ItemBuilder(Material.REPEATER).setGuiProperty().build(), this));
            }
        }
    }
}
