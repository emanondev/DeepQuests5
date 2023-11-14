package emanondev.deepquests.generic.requiretypes;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.data.AmountData;
import emanondev.deepquests.data.QuestItemData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.ARequire;
import emanondev.deepquests.implementations.ARequireType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class HaveQuestItemRequireType<T extends User<T>> extends ARequireType<T> {

    private static final String ID = "have_quest_items";

    public HaveQuestItemRequireType(@NotNull QuestManager<T> manager) {
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
    public @NotNull HaveQuestItemRequire getInstance(int id, @NotNull QuestManager<T> manager, @NotNull YMLSection section) {
        return new HaveQuestItemRequire(id, manager, section);
    }

    public class HaveQuestItemRequire extends ARequire<T> {

        private final AmountData<T, HaveQuestItemRequire> amountData;
        private final QuestItemData<T, HaveQuestItemRequire> questItemData;

        public HaveQuestItemRequire(int id, QuestManager<T> manager, YMLSection section) {
            super(id, manager, HaveQuestItemRequireType.this, section);
            amountData = new AmountData<>(this,
                    getConfig().loadSection(Paths.REQUIRE_INFO_AMOUNT), 1, Integer.MAX_VALUE, 1);
            questItemData = new QuestItemData<>(this,
                    getConfig().loadSection(Paths.REQUIRE_INFO_TARGETQUEST));
        }

        public AmountData<T, HaveQuestItemRequire> getAmountData() {
            return amountData;
        }

        public boolean isAllowed(@NotNull T p) {
            if (this.questItemData.getQuestItemID() == null)
                return false;
            return p.getQuestBag().hasQuestItem(this.questItemData.getQuestItemID(), amountData.getAmount());
        }

        public @NotNull List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add("&9Required Points: &e" + amountData.getAmount());
            info.addAll(questItemData.getInfo());
            return info;
        }

        public QuestItemData<T, HaveQuestItemRequire> getQuestItemData() {
            return questItemData;
        }

        @Override
        public @NotNull Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ARequireGuiEditor {

            public GuiEditor(Player player, Gui previousHolder) {
                super(player, previousHolder);
                getQuestItemData().setupButtons(this, 27);
                this.putButton(28,
                        getAmountData().getAmountEditorButton("&9Required Points Selector",
                                Arrays.asList("&6Required Points Selector", "&9Amount: &e%amount%"),
                                new ItemBuilder(Material.REPEATER).setGuiProperty().build(), this));
            }
        }
    }

}
