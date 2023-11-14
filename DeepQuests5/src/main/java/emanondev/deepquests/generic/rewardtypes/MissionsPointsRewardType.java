package emanondev.deepquests.generic.rewardtypes;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.Translations;
import emanondev.deepquests.data.AmountData;
import emanondev.deepquests.data.TargetQuestData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.AReward;
import emanondev.deepquests.implementations.ARewardType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.*;
import emanondev.deepquests.utils.DataUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class MissionsPointsRewardType<T extends User<T>> extends ARewardType<T> {
    private final static String ID = "missions_points";

    public MissionsPointsRewardType(@NotNull QuestManager<T> manager) {
        super(ID, manager);
    }

    @Override
    protected boolean getStandardHiddenValue() {
        return false;
    }

    @Override
    public Material getGuiMaterial() {
        return Material.IRON_NUGGET;
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7give missions points");
    }

    @Override
    public @NotNull MissionsPointsReward getInstance(int id, @NotNull QuestManager<T> manager, @NotNull YMLSection section) {
        return new MissionsPointsReward(id, manager, section);
    }

    @Override
    public String getDefaultFeedback(Reward<T> reward) {
        if (!(reward instanceof MissionsPointsReward r))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.REWARD_FEEDBACK, null);
        if (txt == null) {
            txt = "&a{action:obtained} &e{amount} &a{action:points} on &e{target}";
            config.set(Paths.REWARD_FEEDBACK, txt);

        }
        return Translations.replaceAll(txt).replace("{target}", DataUtils.getTargetHolder(r.getTargetQuestData()))
                .replace("{amount}", DataUtils.getAmountHolder(r.getAmountData()));
    }

    public class MissionsPointsReward extends AReward<T> {
        private final TargetQuestData<T, MissionsPointsReward> questData;
        private final AmountData<T, MissionsPointsReward> amountData;

        public MissionsPointsReward(int id, QuestManager<T> manager, YMLSection section) {
            super(id, manager, MissionsPointsRewardType.this, section);
            amountData = new AmountData<>(this,
                    getConfig().loadSection(Paths.REWARD_INFO_AMOUNT));
            questData = new TargetQuestData<>(this,
                    getConfig().loadSection(Paths.REWARD_INFO_TARGET_QUEST));
        }

        public @NotNull List<String> getInfo() {
            List<String> info = super.getInfo();
            info.addAll(questData.getInfo());
            info.add("&9Points: &e%amount%");
            return info;
        }

        public TargetQuestData<T, MissionsPointsReward> getTargetQuestData() {
            return questData;
        }

        @Override
        public void apply(@NotNull T qPlayer, int amount) {
            if (amount <= 0)
                return;
            try {
                Quest<T> quest = questData.getQuest();
                if (quest == null)
                    new NullPointerException("Data missing or not setted still on reward " + this.getID())
                            .printStackTrace();
                QuestData<T> questData = qPlayer.getQuestData(quest);
                questData.setPoints(questData.getPoints() + amount * amountData.getAmount());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public AmountData<T, MissionsPointsReward> getAmountData() {
            return amountData;
        }

        @Override
        public @NotNull Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ARewardGuiEditor {

            public GuiEditor(Player player, Gui previousHolder) {
                super(player, previousHolder);
                this.putButton(27, questData.getQuestSelectorButton(this));
                this.putButton(28,
                        amountData.getAmountEditorButton("&9Points Selector",
                                Arrays.asList("&6Points Selector", "&9Points: &e%amount%"),
                                new ItemBuilder(Material.REPEATER).setGuiProperty().build(), this));
            }
        }
    }

}