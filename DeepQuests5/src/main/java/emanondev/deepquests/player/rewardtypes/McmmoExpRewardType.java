package emanondev.deepquests.player.rewardtypes;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.player.UserManager;
import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.Translations;
import emanondev.deepquests.data.AmountData;
import emanondev.deepquests.data.McMMOSkillTypeData;
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

import java.util.Arrays;
import java.util.List;

public class McmmoExpRewardType extends ARewardType<QuestPlayer> {
    private final static String ID = "mcmmo_exp";

    public McmmoExpRewardType(QuestManager<QuestPlayer> manager) {
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
        return List.of("&7Reward the player with an Item");
    }

    @Override
    public @NotNull McmmoExpReward getInstance(int id, @NotNull QuestManager<QuestPlayer> manager, @NotNull YMLSection section) {
        return new McmmoExpReward(id, manager, section);
    }

    @Override
    public String getDefaultFeedback(Reward<QuestPlayer> reward) {
        if (!(reward instanceof McmmoExpReward r))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.REWARD_FEEDBACK, null);
        if (txt == null) {
            txt = "&a{action:obtained} &e{amount} &a{action:experience} {conjun:on} {mcmmoskilltype}";
            config.set(Paths.REWARD_FEEDBACK, txt);
        }
        return Translations.replaceAll(txt)
                .replace("{mcmmoskilltype}", DataUtils.getMcmmoSkillTypeHolder(r.getMcmmoSkillTypeData()))
                .replace("{amount}", DataUtils.getAmountHolder(r.getAmountData()));
    }

    public class McmmoExpReward extends AReward<QuestPlayer> {
        private final McMMOSkillTypeData<QuestPlayer, McmmoExpReward> skillData;
        private final AmountData<QuestPlayer, McmmoExpReward> amountData;

        public McmmoExpReward(int id, QuestManager<QuestPlayer> manager, YMLSection section) {
            super(id, manager, McmmoExpRewardType.this, section);
            skillData = new McMMOSkillTypeData<>(this,
                    getConfig().loadSection(Paths.REWARD_INFO_MCMMO));
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
            if (amount <= 0 || amountData.getAmount() <= 0 || skillData.getSkillType() == null)
                return;
            try {
                McMMOPlayer mcmmoPlayer = UserManager.getPlayer(qPlayer.getPlayer());
                mcmmoPlayer.addXp(skillData.getSkillType(), amountData.getAmount() * amount);
                mcmmoPlayer.getProfile().registerXpGain(skillData.getSkillType(), amountData.getAmount() * amount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public AmountData<QuestPlayer, McmmoExpReward> getAmountData() {
            return amountData;
        }

        public McMMOSkillTypeData<QuestPlayer, McmmoExpReward> getMcmmoSkillTypeData() {
            return skillData;
        }

        @Override
        public @NotNull Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ARewardGuiEditor {

            public GuiEditor(Player player, Gui previusHolder) {
                super(player, previusHolder);
                this.putButton(27, getMcmmoSkillTypeData().getSkillTypeSelector(this));
                this.putButton(28,
                        getAmountData().getAmountEditorButton("&9Select exp reiceved as reward",
                                Arrays.asList("&6Exp Reward Selector", "&9Experience: &e%amount%"),
                                new ItemBuilder(Material.REPEATER).setGuiProperty().build(), this));
            }
        }
    }
}