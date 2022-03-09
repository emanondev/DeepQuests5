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

import java.util.Arrays;
import java.util.List;

public class FailedMissionRequireType<T extends User<T>> extends ARequireType<T> {

    public FailedMissionRequireType(QuestManager<T> manager) {
        super(ID, manager);
    }

    public static final String ID = "failed_mission";

    @Override
    public Material getGuiMaterial() {
        return Material.RED_BANNER;
    }

    @Override
    public ItemStack getGuiItem() {
        return new ItemBuilder(getGuiMaterial()).addPattern(DyeColor.WHITE, PatternType.RHOMBUS_MIDDLE).setGuiProperty()
                .build();
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7Player require to fail mission n times");
    }

    @Override
    public Require<T> getInstance(int id, QuestManager<T> manager, YMLSection section) {
        return new FailedMissionRequire(id, manager, section);
    }

    public class FailedMissionRequire extends ARequire<T> {

        private final TargetMissionData<T, FailedMissionRequire> targetMission;
        private final AmountData<T, FailedMissionRequire> amountData;

        public FailedMissionRequire(int id, QuestManager<T> manager, YMLSection section) {
            super(id, manager, FailedMissionRequireType.this, section);
            targetMission = new TargetMissionData<>(this,
                    getConfig().loadSection(Paths.REQUIRE_INFO_TARGETMISSION));
            amountData = new AmountData<>(this,
                    getConfig().loadSection(Paths.REQUIRE_INFO_AMOUNT), 1, Integer.MAX_VALUE, 1);
        }

        @Override
        public boolean isAllowed(T user) {
            Mission<T> mission = targetMission.getMission();
            if (mission == null)
                return false;
            MissionData<T> missionData = user.getMissionData(mission);
            return missionData.failedTimes() >= amountData.getAmount();
        }

        public TargetMissionData<T, FailedMissionRequire> getTargetMissionData() {
            return targetMission;
        }

        public AmountData<T, FailedMissionRequire> getAmountData() {
            return amountData;
        }

        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.addAll(targetMission.getInfo());
            if (amountData.getAmount() != 1)
                info.add("&9Must be Failed: &e" + amountData.getAmount() + " &9times");
            return info;
        }

        @Override
        public Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ARequireGuiEditor {

            public GuiEditor(Player player, Gui previousHolder) {
                super(player, previousHolder);
                this.putButton(27, targetMission.getMissionSelectorButton(this));
                this.putButton(28,
                        amountData.getAmountEditorButton("&9Failed Times Selector",
                                Arrays.asList("&6Failed Times Selector", "&9Amount: &e%amount%"),
                                new ItemBuilder(Material.REPEATER).setGuiProperty().build(), this));
            }
        }
    }

}