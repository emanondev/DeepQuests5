package emanondev.deepquests.player.rewardtypes;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobsPlayer;
import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.Translations;
import emanondev.deepquests.data.AmountData;
import emanondev.deepquests.data.JobTypeData;
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

public class JobsExpRewardType extends ARewardType<QuestPlayer> {
    private final static String ID = "jobs_exp";

    public JobsExpRewardType(QuestManager<QuestPlayer> manager) {
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
    public @NotNull JobsExpReward getInstance(int id, @NotNull QuestManager<QuestPlayer> manager, @NotNull YMLSection section) {
        return new JobsExpReward(id, manager, section);
    }

    @Override
    public String getDefaultFeedback(Reward<QuestPlayer> reward) {
        if (!(reward instanceof JobsExpRewardType.JobsExpReward))
            return null;
        JobsExpReward r = (JobsExpReward) reward;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.REWARD_FEEDBACK, null);
        if (txt == null) {
            txt = "&a{action:obtained} &e{amount} &a{action:experience} {conjun:on} {jobs}";
            config.set(Paths.REWARD_FEEDBACK, txt);

        }
        return Translations.replaceAll(txt).replace("{jobs}", DataUtils.getJobsHolder(r.getJobsData()))
                .replace("{amount}", DataUtils.getAmountHolder(r.getAmountData()));
    }

    public class JobsExpReward extends AReward<QuestPlayer> {
        private final JobTypeData<QuestPlayer, JobsExpReward> jobData;
        private final AmountData<QuestPlayer, JobsExpReward> amountData;

        public JobsExpReward(int id, QuestManager<QuestPlayer> manager, YMLSection section) {
            super(id, manager, JobsExpRewardType.this, section);
            jobData = new JobTypeData<QuestPlayer, JobsExpReward>(this,
                    getConfig().loadSection(Paths.REWARD_INFO_JOB));
            amountData = new AmountData<QuestPlayer, JobsExpReward>(this,
                    getConfig().loadSection(Paths.REWARD_INFO_AMOUNT), 1, Integer.MAX_VALUE, 1);
        }

        public @NotNull List<String> getInfo() {
            List<String> info = super.getInfo();
            info.addAll(jobData.getInfo());
            info.add("&9Exp reward: &e" + amountData.getAmount());
            return info;
        }

        @Override
        public void apply(@NotNull QuestPlayer qPlayer, int amount) {
            if (qPlayer.getPlayer() == null || jobData.getJob() == null || amount <= 0 || amountData.getAmount() <= 0)
                return;

            JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(qPlayer.getPlayer());
            if (jobsPlayer != null && jobsPlayer.isInJob(jobData.getJob()))
                jobsPlayer.getJobProgression(jobData.getJob()).addExperience(amountData.getAmount() * amount);
        }

        public AmountData<QuestPlayer, JobsExpReward> getAmountData() {
            return amountData;
        }

        public JobTypeData<QuestPlayer, JobsExpReward> getJobsData() {
            return jobData;
        }

        @Override
        public @NotNull Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ARewardGuiEditor {

            public GuiEditor(Player player, Gui previusHolder) {
                super(player, previusHolder);
                this.putButton(27, getJobsData().getJobSelector(this));
                this.putButton(28,
                        getAmountData().getAmountEditorButton("&9Select exp reiceved as reward",
                                Arrays.asList("&6Exp Reward Selector", "&9Level: &e%amount%"),
                                new ItemBuilder(Material.REPEATER).setGuiProperty().build(), this));
            }
        }
    }
}