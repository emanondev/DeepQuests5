package emanondev.deepquests.player.requiretypes;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobsPlayer;
import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.data.AmountData;
import emanondev.deepquests.data.JobTypeData;
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

public class JobLevelRequireType extends ARequireType<QuestPlayer> {

    private static final String ID = "job_level";

    public JobLevelRequireType(QuestManager<QuestPlayer> manager) {
        super(ID, manager);
    }

    @Override
    public Material getGuiMaterial() {
        return Material.IRON_PICKAXE;
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7Player require a certain lv on a job");
    }

    @Override
    public @NotNull Require<QuestPlayer> getInstance(int id, @NotNull QuestManager<QuestPlayer> manager, @NotNull YMLSection section) {
        return new JobLevelRequire(id, manager, section);
    }

    public class JobLevelRequire extends ARequire<QuestPlayer> {

        private final JobTypeData<QuestPlayer, JobLevelRequire> jobData;
        private final AmountData<QuestPlayer, JobLevelRequire> amountData;

        public JobLevelRequire(int id, QuestManager<QuestPlayer> manager, YMLSection section) {
            super(id, manager, JobLevelRequireType.this, section);
            jobData = new JobTypeData<>(this,
                    getConfig().loadSection(Paths.REQUIRE_INFO_JOBDATA));
            amountData = new AmountData<>(this,
                    getConfig().loadSection(Paths.REQUIRE_INFO_AMOUNT), 1, Integer.MAX_VALUE, 1);
        }

        public JobTypeData<QuestPlayer, JobLevelRequire> getJobTypeData() {
            return jobData;
        }

        public AmountData<QuestPlayer, JobLevelRequire> getAmountData() {
            return amountData;
        }

        @Override
        public boolean isAllowed(@NotNull QuestPlayer user) {
            if (user.getPlayer() == null)
                return false;
            if (jobData.getJob() == null)
                return false;
            JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(user.getPlayer());
            return jobsPlayer != null && jobsPlayer.isInJob(jobData.getJob())
                    && jobsPlayer.getJobProgression(jobData.getJob()).getLevel() >= amountData.getAmount();
        }

        @Override
        public @NotNull List<String> getInfo() {
            List<String> info = super.getInfo();
            info.addAll(jobData.getInfo());
            info.add("&9Required Level: &e" + amountData.getAmount());
            return info;
        }

        @Override
        public @NotNull Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ARequireGuiEditor {

            public GuiEditor(Player player, Gui previousHolder) {
                super(player, previousHolder);
                this.putButton(27, getJobTypeData().getJobSelector(this));
                this.putButton(28,
                        getAmountData().getAmountEditorButton("&9Select required Level",
                                Arrays.asList("&6Required Level Selector", "&9Level: &e%amount%"),
                                new ItemBuilder(Material.REPEATER).setGuiProperty().build(), this));
            }
        }
    }
}
