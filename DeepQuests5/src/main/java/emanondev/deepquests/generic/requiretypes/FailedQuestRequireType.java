package emanondev.deepquests.generic.requiretypes;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.data.AmountData;
import emanondev.deepquests.data.TargetQuestData;
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

public class FailedQuestRequireType<T extends User<T>> extends ARequireType<T> {

    public static final String ID = "failed_quest";

    public FailedQuestRequireType(@NotNull QuestManager<T> manager) {
        super(ID, manager);
    }

    @Override
    public Material getGuiMaterial() {
        return Material.RED_BANNER;
    }

    @Override
    public ItemStack getGuiItem() {
        return new ItemBuilder(getGuiMaterial()).addPattern(DyeColor.WHITE, PatternType.CURLY_BORDER).setGuiProperty()
                .build();
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7Player require to fail quest n times");
    }

    @Override
    public @NotNull Require<T> getInstance(int id, @NotNull QuestManager<T> manager, @NotNull YMLSection section) {
        return new FailedQuestRequire(id, manager, section);
    }

    public class FailedQuestRequire extends ARequire<T> {

        private final TargetQuestData<T, FailedQuestRequire> targetQuest;
        private final AmountData<T, FailedQuestRequire> amountData;

        public FailedQuestRequire(int id, QuestManager<T> manager, YMLSection section) {
            super(id, manager, FailedQuestRequireType.this, section);
            targetQuest = new TargetQuestData<>(this,
                    getConfig().loadSection(Paths.REQUIRE_INFO_TARGETMISSION));
            amountData = new AmountData<>(this,
                    getConfig().loadSection(Paths.REQUIRE_INFO_AMOUNT), 1, Integer.MAX_VALUE, 1);
        }

        @Override
        public boolean isAllowed(@NotNull T user) {
            Quest<T> quest = targetQuest.getQuest();
            if (quest == null)
                return false;
            QuestData<T> questData = user.getQuestData(quest);
            return questData.failedTimes() >= amountData.getAmount();
        }

        public TargetQuestData<T, FailedQuestRequire> getTargetQuestData() {
            return targetQuest;
        }

        public AmountData<T, FailedQuestRequire> getAmountData() {
            return amountData;
        }

        public @NotNull List<String> getInfo() {
            List<String> info = super.getInfo();
            info.addAll(targetQuest.getInfo());
            if (amountData.getAmount() != 1)
                info.add("&9Must be Failed: &e" + amountData.getAmount() + " &9times");
            return info;
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
                        getAmountData().getAmountEditorButton("&9Failed Times Selector",
                                Arrays.asList("&6Failed Times Selector", "&9Amount: &e%amount%"),
                                new ItemBuilder(Material.REPEATER).setGuiProperty().build(), this));
            }
        }
    }

}