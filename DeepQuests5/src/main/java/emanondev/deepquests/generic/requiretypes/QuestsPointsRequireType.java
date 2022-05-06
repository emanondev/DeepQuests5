package emanondev.deepquests.generic.requiretypes;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.data.AmountData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.ARequire;
import emanondev.deepquests.implementations.ARequireType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class QuestsPointsRequireType<T extends User<T>> extends ARequireType<T> {

    public QuestsPointsRequireType(QuestManager<T> manager) {
        super(ID, manager);
    }

    private static final String ID = "quests_points";

    @Override
    public Material getGuiMaterial() {
        return Material.GOLD_NUGGET;
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7Player require quest points");
    }

    @Override
    public QuestsPointsRequire getInstance(int id, QuestManager<T> manager, YMLSection section) {
        return new QuestsPointsRequire(id, manager, section);
    }

    public class QuestsPointsRequire extends ARequire<T> {

        private final AmountData<T, QuestsPointsRequire> amountData;

        public QuestsPointsRequire(int id, QuestManager<T> manager, YMLSection section) {
            super(id, manager, QuestsPointsRequireType.this, section);
            amountData = new AmountData<>(this,
                    getConfig().loadSection(Paths.REQUIRE_INFO_AMOUNT));
        }

        public AmountData<T, QuestsPointsRequire> getAmountData() {
            return amountData;
        }

        public boolean isAllowed(T p) {
            return p.getPoints() >= amountData.getAmount();
        }

        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add("&9Required Quests Points: &e" + amountData.getAmount());
            return info;
        }

        @Override
        public Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ARequireGuiEditor {

            public GuiEditor(Player player, Gui previousHolder) {
                super(player, previousHolder);
                this.putButton(27,
                        amountData.getAmountEditorButton("&9Required Points Selector",
                                Arrays.asList("&6Required Points Selector", "&9Amount: &e%amount%"),
                                new ItemBuilder(Material.REPEATER).setGuiProperty().build(), this));
            }
        }
    }

}