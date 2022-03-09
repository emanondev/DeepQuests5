package emanondev.deepquests.player.requiretypes;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.data.AmountData;
import emanondev.deepquests.data.SkillAPIClassData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.ARequire;
import emanondev.deepquests.implementations.ARequireType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Require;
import emanondev.deepquests.player.QuestPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class SkillAPILevelRequireType extends ARequireType<QuestPlayer> {

    public SkillAPILevelRequireType(QuestManager<QuestPlayer> manager) {
        super(ID, manager);
    }

    private static final String ID = "skillapi_level";

    @Override
    public Material getGuiMaterial() {
        return Material.IRON_PICKAXE;
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList("&7Player require a certain lv on a skillAPI class");
    }

    @Override
    public Require<QuestPlayer> getInstance(int id, QuestManager<QuestPlayer> manager, YMLSection section) {
        return new SkillAPILevelRequire(id, manager, section);
    }

    public class SkillAPILevelRequire extends ARequire<QuestPlayer> {

        private SkillAPIClassData<QuestPlayer, SkillAPILevelRequire> skillData = null;
        private AmountData<QuestPlayer, SkillAPILevelRequire> amountData = null;

        public SkillAPILevelRequire(int id, QuestManager<QuestPlayer> manager, YMLSection section) {
            super(id, manager, SkillAPILevelRequireType.this, section);
            skillData = new SkillAPIClassData<QuestPlayer, SkillAPILevelRequire>(this,
                    getConfig().loadSection(Paths.REQUIRE_INFO_JOBDATA));
            amountData = new AmountData<QuestPlayer, SkillAPILevelRequire>(this,
                    getConfig().loadSection(Paths.REQUIRE_INFO_AMOUNT), 1, Integer.MAX_VALUE, 1);
        }

        public SkillAPIClassData<QuestPlayer, SkillAPILevelRequire> getSkillAPITypeData() {
            return skillData;
        }

        public AmountData<QuestPlayer, SkillAPILevelRequire> getAmountData() {
            return amountData;
        }

        @Override
        public boolean isAllowed(QuestPlayer user) {
            if (user.getPlayer() == null)
                return false;
            PlayerData data = SkillAPI.getPlayerData(user.getOfflinePlayer());
            if (data == null)
                return false;
            try {
                PlayerClass[] array = data.getClasses().toArray(new PlayerClass[0]);
                for (int i = 0; i < array.length; i++) {
                    if (array[i] != null && skillData.isValidRPGClass(array[i].getData())
                            && array[i].getLevel() >= amountData.getAmount())
                        return true;

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.addAll(skillData.getInfo());
            info.add("&9Required Level: &e" + amountData.getAmount());
            return info;
        }

        @Override
        public Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ARequireGuiEditor {

            public GuiEditor(Player player, Gui previusHolder) {
                super(player, previusHolder);
                this.putButton(27, skillData.getGroupButton(this));
                this.putButton(28, skillData.getRPGClassButton(this));
                this.putButton(29,
                        amountData.getAmountEditorButton("&9Select required Level",
                                Arrays.asList("&6Required Level Selector", "&9Level: &e%amount%"),
                                new ItemBuilder(Material.REPEATER).setGuiProperty().build(), this));
            }
        }
    }
}
