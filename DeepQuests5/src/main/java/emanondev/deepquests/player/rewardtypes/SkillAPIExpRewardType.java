package emanondev.deepquests.player.rewardtypes;

import com.sucy.skill.SkillAPI;
import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.Translations;
import emanondev.deepquests.data.AmountData;
import emanondev.deepquests.data.SkillAPIClassData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.AReward;
import emanondev.deepquests.implementations.ARewardType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Reward;
import emanondev.deepquests.player.QuestPlayer;
import emanondev.deepquests.utils.DataUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.enums.ExpSource;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.api.player.PlayerData;

import java.util.Arrays;
import java.util.List;

public class SkillAPIExpRewardType extends ARewardType<QuestPlayer> {
    private final static String ID = "skillapi_exp";

    public SkillAPIExpRewardType(QuestManager<QuestPlayer> manager) {
        super(ID, manager);
    }

    @Override
    protected boolean getStandardHiddenValue() {
        return false;
    }

    @Override
    public Material getGuiMaterial() {
        return Material.IRON_PICKAXE;
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList("&7Reward the player with an Experience", "&7on a SkillAPI class");
    }

    @Override
    public @NotNull SkillAPIExpReward getInstance(int id, @NotNull QuestManager<QuestPlayer> manager, @NotNull YMLSection section) {
        return new SkillAPIExpReward(id, manager, section);
    }

    @Override
    public String getDefaultFeedback(Reward<QuestPlayer> reward) {
        if (!(reward instanceof SkillAPIExpReward r))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.REWARD_FEEDBACK, null);
        if (txt == null) {
            txt = "&a{action:obtained} &e{amount} &a{action:experience} {conjun:on} {skillapiclass}";
            config.set(Paths.REWARD_FEEDBACK, txt);

        }
        return Translations.replaceAll(txt)
                .replace("{skillapiclass}", DataUtils.getSkillAPIClassHolder(r.getSkillAPIClassData()))
                .replace("{amount}", DataUtils.getAmountHolder(r.getAmountData()));
    }

    public class SkillAPIExpReward extends AReward<QuestPlayer> {
        private final SkillAPIClassData<QuestPlayer, SkillAPIExpReward> skillData;
        private final AmountData<QuestPlayer, SkillAPIExpReward> amountData;

        public SkillAPIExpReward(int id, QuestManager<QuestPlayer> manager, YMLSection section) {
            super(id, manager, SkillAPIExpRewardType.this, section);
            skillData = new SkillAPIClassData<>(this,
                    getConfig().loadSection(Paths.REWARD_INFO_JOB));
            amountData = new AmountData<>(this,
                    getConfig().loadSection(Paths.REWARD_INFO_AMOUNT), 1, Integer.MAX_VALUE, 1);
        }

        public @NotNull List<String> getInfo() {
            List<String> info = super.getInfo();
            info.addAll(skillData.getInfo());
            info.add("&9Exp Reward: &e" + amountData.getAmount());
            return info;
        }

        @Override
        public void apply(@NotNull QuestPlayer qPlayer, int amount) {
            if (amount <= 0 || amountData.getAmount() <= 0)
                return;
            PlayerData data = Fabled.getData(qPlayer.getOfflinePlayer());
            if (data == null)
                return;
            try {
                PlayerClass pClass = data.getMainClass();
                if (pClass == null || !skillData.isValidRPGClass(pClass.getData())) {
                    pClass = null;
                    PlayerClass[] array = data.getClasses().toArray(new PlayerClass[0]);
                    for (PlayerClass playerClass : array) {
                        if (playerClass != null && skillData.isValidRPGClass(playerClass.getData())) {
                            pClass = playerClass;
                            break;
                        }
                    }
                }
                if (pClass == null)
                    return;
                pClass.giveExp(amountData.getAmount(), ExpSource.QUEST);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public AmountData<QuestPlayer, SkillAPIExpReward> getAmountData() {
            return amountData;
        }

        public SkillAPIClassData<QuestPlayer, SkillAPIExpReward> getSkillAPIClassData() {
            return skillData;
        }

        @Override
        public @NotNull Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ARewardGuiEditor {

            public GuiEditor(Player player, Gui previusHolder) {
                super(player, previusHolder);
                this.putButton(27, getSkillAPIClassData().getGroupButton(this));
                this.putButton(28, getSkillAPIClassData().getRPGClassButton(this));
                this.putButton(29,
                        getAmountData().getAmountEditorButton("&9Select exp reiceved as reward",
                                Arrays.asList("&6Exp Reward Selector", "&9Experience: &e%amount%"),
                                new ItemBuilder(Material.REPEATER).setGuiProperty().build(), this));
            }
        }
    }
}