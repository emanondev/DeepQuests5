package emanondev.deepquests.generic.requiretypes;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.data.AmountData;
import emanondev.deepquests.data.TargetQuestData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.ARequire;
import emanondev.deepquests.implementations.ARequireType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.Quest;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class MissionsPointsRequireType<T extends User<T>> extends ARequireType<T> {

    private static final String ID = "missions_points";

    public MissionsPointsRequireType(@NotNull QuestManager<T> manager) {
        super(ID, manager);
    }

    @Override
    public Material getGuiMaterial() {
        return Material.IRON_NUGGET;
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7Player require missions points");
    }

    @Override
    public @NotNull MissionsPointsRequire getInstance(int id, @NotNull QuestManager<T> manager, @NotNull YMLSection section) {
        return new MissionsPointsRequire(id, manager, section);
    }

    public class MissionsPointsRequire extends ARequire<T> {

        private final AmountData<T, MissionsPointsRequire> amountData;
        private final TargetQuestData<T, MissionsPointsRequire> targetQuestData;

        public MissionsPointsRequire(int id, QuestManager<T> manager, YMLSection section) {
            super(id, manager, MissionsPointsRequireType.this, section);
            amountData = new AmountData<>(this,
                    getConfig().loadSection(Paths.REQUIRE_INFO_AMOUNT));
            targetQuestData = new TargetQuestData<>(this,
                    getConfig().loadSection(Paths.REQUIRE_INFO_TARGETQUEST));
        }

        public AmountData<T, MissionsPointsRequire> getAmountData() {
            return amountData;
        }

        public boolean isAllowed(@NotNull T p) {
            Quest<T> quest = targetQuestData.getQuest();
            if (quest == null)
                return false;

            return p.getQuestData(quest).getPoints() >= amountData.getAmount();
        }

        public @NotNull List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add("&9Required Points: &e" + amountData.getAmount());
            info.addAll(targetQuestData.getInfo());
            return info;
        }

        public TargetQuestData<T, MissionsPointsRequire> getTargetQuestData() {
            return targetQuestData;
        }

        @Override
        public @NotNull Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ARequireGuiEditor {

            public GuiEditor(Player player, Gui previousHolder) {
                super(player, previousHolder);
                this.putButton(27, getTargetQuestData().getQuestSelectorButton(this));
                this.putButton(28,
                        getAmountData().getAmountEditorButton("&9Required Points Selector",
                                Arrays.asList("&6Required Points Selector", "&9Amount: &e%amount%"),
                                new ItemBuilder(Material.REPEATER).setGuiProperty().build(), this));
            }
        }
    }

}