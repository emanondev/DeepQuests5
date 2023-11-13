package emanondev.deepquests.player.requiretypes;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.player.UserManager;
import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.data.AmountData;
import emanondev.deepquests.data.McMMOSkillTypeData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.ARequire;
import emanondev.deepquests.implementations.ARequireType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Require;
import emanondev.deepquests.player.QuestPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class McmmoLevelRequireType extends ARequireType<QuestPlayer> {

    private static final String ID = "mcmmo_level";

    public McmmoLevelRequireType(QuestManager<QuestPlayer> manager) {
        super(ID, manager);
    }

    @Override
    public Material getGuiMaterial() {
        return Material.EXPERIENCE_BOTTLE;
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7Player require a certain lv on a mcmmo skill");
    }

    @Override
    public Require<QuestPlayer> getInstance(int id, QuestManager<QuestPlayer> manager, YMLSection section) {
        return new McmmoLevelRequire(id, manager, section);
    }

    public class McmmoLevelRequire extends ARequire<QuestPlayer> {

        private final McMMOSkillTypeData<QuestPlayer, McmmoLevelRequire> skillData;
        private final AmountData<QuestPlayer, McmmoLevelRequire> amountData;

        public McmmoLevelRequire(int id, QuestManager<QuestPlayer> manager, YMLSection section) {
            super(id, manager, McmmoLevelRequireType.this, section);
            skillData = new McMMOSkillTypeData<>(this,
                    getConfig().loadSection(Paths.REQUIRE_INFO_MCMMODATA));
            amountData = new AmountData<>(this,
                    getConfig().loadSection(Paths.REQUIRE_INFO_AMOUNT), 1, Integer.MAX_VALUE, 1);
        }

        public McMMOSkillTypeData<QuestPlayer, McmmoLevelRequire> getMcMMOSkillTypeData() {
            return skillData;
        }

        public AmountData<QuestPlayer, McmmoLevelRequire> getAmountData() {
            return amountData;
        }

        public boolean isAllowed(QuestPlayer p) {
            if (skillData.getSkillType() == null)
                return false;
            try {
                McMMOPlayer mcmmoPlayer = UserManager.getPlayer(p.getPlayer());
                return mcmmoPlayer.getSkillLevel(skillData.getSkillType()) >= amountData.getAmount();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        public @NotNull List<String> getInfo() {
            List<String> info = super.getInfo();
            info.addAll(skillData.getInfo());
            info.add("&9Required Level: &e" + amountData.getAmount());
            return info;
        }

        @Override
        public @NotNull Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ARequireGuiEditor {

            public GuiEditor(Player player, Gui previusHolder) {
                super(player, previusHolder);
                this.putButton(27, getMcMMOSkillTypeData().getSkillTypeSelector(this));
                this.putButton(28,
                        getAmountData().getAmountEditorButton("&9Select required Level",
                                Arrays.asList("&6Required Level Selector", "&9Level: &e%amount%"),
                                new ItemBuilder(Material.REPEATER).setGuiProperty().build(), this));
            }
        }
    }
}
