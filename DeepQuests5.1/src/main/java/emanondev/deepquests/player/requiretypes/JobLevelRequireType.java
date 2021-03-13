package emanondev.deepquests.player.requiretypes;

import java.util.*;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobsPlayer;

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
import emanondev.core.ItemBuilder;

public class JobLevelRequireType extends ARequireType<QuestPlayer> {

	public JobLevelRequireType(QuestManager<QuestPlayer> manager) {
		super(ID, manager);
	}

	private static final String ID = "job_level";

	@Override
	public Material getGuiMaterial() {
		return Material.IRON_PICKAXE;
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("&7Player require a certain lv on a job");
	}

	@Override
	public Require<QuestPlayer> getInstance(int id, QuestManager<QuestPlayer> manager, YMLSection section) {
		return new JobLevelRequire(id, manager, section);
	}

	public class JobLevelRequire extends ARequire<QuestPlayer> {

		private JobTypeData<QuestPlayer, JobLevelRequire> jobData = null;
		private AmountData<QuestPlayer, JobLevelRequire> amountData = null;

		public JobLevelRequire(int id, QuestManager<QuestPlayer> manager, YMLSection section) {
			super(id, manager, JobLevelRequireType.this, section);
			jobData = new JobTypeData<QuestPlayer, JobLevelRequire>(this,
					getConfig().loadSection(Paths.REQUIRE_INFO_JOBDATA));
			amountData = new AmountData<QuestPlayer, JobLevelRequire>(this,
					getConfig().loadSection(Paths.REQUIRE_INFO_AMOUNT), 1, Integer.MAX_VALUE, 1);
		}

		public JobTypeData<QuestPlayer, JobLevelRequire> getJobTypeData() {
			return jobData;
		}

		public AmountData<QuestPlayer, JobLevelRequire> getAmountData() {
			return amountData;
		}

		@Override
		public boolean isAllowed(QuestPlayer user) {
			if (user.getPlayer() == null)
				return false;
			if (jobData.getJob() == null)
				return false;
			JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(user.getPlayer());
			if (jobsPlayer != null && jobsPlayer.isInJob(jobData.getJob())
					&& jobsPlayer.getJobProgression(jobData.getJob()).getLevel() >= amountData.getAmount())
				return true;
			return false;
		}

		@Override
		public List<String> getInfo() {
			List<String> info = super.getInfo();
			info.addAll(jobData.getInfo());
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
				this.putButton(27, jobData.getJobSelector(this));
				this.putButton(28,
						amountData.getAmountEditorButton("&9Select required Level",
								Arrays.asList("&6Required Level Selector", "&9Level: &e%amount%"),
								new ItemBuilder(Material.REPEATER).setGuiProperty().build(), this));
			}
		}
	}
}
