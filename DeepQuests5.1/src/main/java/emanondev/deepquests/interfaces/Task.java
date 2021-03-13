package emanondev.deepquests.interfaces;

import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import emanondev.core.UtilsString;
import emanondev.deepquests.Holders;
import emanondev.deepquests.gui.button.GuiElementButton;
import emanondev.deepquests.gui.button.SortableButton;
import emanondev.deepquests.gui.inventory.Gui;

/**
 * 
 * @author emanon<br>
 * 
 *         remember to add <br>
 * 
 *         .@SerializableAs("PlayerTask")<br>
 *         .@DelegateDeserialization(PlayerTaskManager.class)<br>
 *         for playertasks
 *
 * @param <T>
 *            - User Type
 * 
 */
public interface Task<T extends User<T>> extends HasWorlds, QuestComponent<T> {

	public Mission<T> getMission();

	/**
	 * @return ordered list of rewards
	 */
	public Collection<Reward<T>> getCompleteRewards();

	/**
	 * 
	 * @param id
	 *            - key of the reward
	 * @return the reward with key or null
	 */
	public Reward<T> getCompleteReward(int id);

	/**
	 * @throws IllegalArgumentException
	 *             if reward.getParent() != null
	 * @throws IllegalArgumentException
	 *             if getReward(reward.getKey()) != null
	 * 
	 * @param reward
	 *            - the reward to add
	 * @return true if sucessfully added
	 */
	public boolean addCompleteReward(Reward<T> reward);

	/**
	 * 
	 * @param reward
	 *            - the reward to remove
	 * @return true if sucessfully removed
	 */
	public boolean removeCompleteReward(Reward<T> reward);

	/**
	 * @return ordered list of rewards
	 */
	public Collection<Reward<T>> getProgressRewards();

	/**
	 * 
	 * @param id
	 *            - key of the reward
	 * @return the reward with key or null
	 */
	public Reward<T> getProgressReward(int id);

	/**
	 * @throws IllegalArgumentException
	 *             if reward.getParent() != null
	 * @throws IllegalArgumentException
	 *             if getReward(reward.getKey()) != null
	 * 
	 * @param reward
	 *            - the reward to add
	 * @return true if sucessfully added
	 */
	public boolean addProgressReward(Reward<T> reward);

	/**
	 * 
	 * @param reward
	 *            - the reward to remove
	 * @return true if sucessfully removed
	 */
	public boolean removeProgressReward(Reward<T> reward);

	/**
	 * ProgressChance is the probability to Obtain a progress <br>
	 * on the task doing the action, by default 1 = 100% <br>
	 * rewards are given only when progress is sucessfull
	 * 
	 * @return progressChance of this
	 */
	public double getProgressChance();

	/**
	 * 
	 * 
	 * @param progressChance
	 *            Allowed values are ]0;1] , where 1 is 100% and 0 is 0% chances to
	 *            progress
	 * @return true if succesfully updated progressChance
	 */
	public void setProgressChance(double progressChance);

	/**
	 * When the max progress is reached the task is completed
	 * 
	 * @return max allowed progress for this task
	 */
	public int getMaxProgress();

	/**
	 * Allowed values are [1;Integer.MAX_VALUE]
	 * 
	 * @param maxProgress
	 *            - new value for the max progress
	 * @return if the maxProgress was succesfully set
	 */
	public void setMaxProgress(int maxProgress);

	/**
	 * 
	 * @return the TaskType of this
	 */
	public TaskType<T> getType();

	/**
	 * @return BossBar style of this
	 */
	public BarStyle getBossBarStyle();

	/**
	 * @return BossBar color of this
	 */
	public BarColor getBossBarColor();

	/**
	 * set BossBar style of this
	 * 
	 * @param barStyle
	 *            value, use null to inherith default value
	 * @return true if succesfully updated
	 */
	public void setBossBarStyle(BarStyle barStyle);

	/**
	 * set BossBar color of this
	 * 
	 * @param barColor
	 *            value, use null to inherith default value
	 * @return true if succesfully updated
	 */
	public void setBossBarColor(BarColor barColor);

	/**
	 * @return should BossBar be shown for this task progress?
	 */
	public boolean showBossBar();

	/**
	 * sets if BossBar should be used
	 * 
	 * @param value
	 *            should boss bar be shown to user for this task when progressing?
	 *            set null to inherith default value
	 * @return true if succesfully updated
	 */
	public void setShowBossBar(Boolean value);

	/**
	 * called when the task has a progress of amount for user
	 * 
	 * @param user
	 *            target user
	 * @param amount
	 *            progressed value
	 * @param player
	 *            who progressed the task
	 * @param forced
	 *            - avoid checking permission of player to progress
	 * @return the final progress amount
	 */
	public default int onProgress(T user, int amount, @Nullable Player player, boolean forced) {
		if (getProgressChance() >= 1)
			return user.progressTask(this, amount, player, forced);
		int counter = 0;
		for (int i = 0; i < amount; i++) {
			if (Math.random() <= getProgressChance())
				counter++;
		}
		if (counter > 0)
			return user.progressTask(this, counter, player, forced);
		return 0;
	}

	/**
	 * 
	 * @return taskType ID of this
	 */
	public default String getTypeName() {
		return getType().getKeyID();
	}

	public default String[] getHolders(T user) {
		String[] list = new String[14];
		TaskData<T> data = user.getTaskData(this);
		list[0] = Holders.TASK_STATUS;

		list[2] = Holders.TASK_COMPLETE_DESCRIPTION;
		list[3] = getRawPhaseDescription(Phase.COMPLETE);
		list[4] = Holders.TASK_UNSTARTED_DESCRIPTION;
		list[5] = getRawPhaseDescription(Phase.UNSTARTED);
		list[6] = Holders.TASK_PROGRESS_DESCRIPTION;
		list[7] = getRawPhaseDescription(Phase.PROGRESS);
		list[8] = Holders.DISPLAY_NAME;
		list[9] = this.getDisplayName();
		list[10] = Holders.TASK_MAX_PROGRESS;
		list[11] = String.valueOf(getMaxProgress());
		list[12] = Holders.TASK_CURRENT_PROGRESS;
		list[13] = String.valueOf(user.getTaskData(this).getProgress());

		if (data.isCompleted())
			list[1] = list[3];
		else if (user.getMissionData(this.getMission()).isStarted())
			list[1] = list[5];
		else
			list[1] = list[7];

		return list;
	}

	@Override
	public default Material getGuiMaterial() {
		return Material.PAPER;
	}

	@Override
	public default SortableButton getEditorButton(Gui parent) {
		return new GuiElementButton<Task<T>>(parent, this);
	}

	@Override
	public default QuestManager<T> getManager() {
		return getMission().getManager();
	}

	public boolean isHidden();

	public void setHidden(Boolean value);

	public default String getPhaseDescription(T user, Phase phase) {
		return UtilsString.fix(getRawPhaseDescription(phase), null, true,
				new String[] { Holders.DISPLAY_NAME, getDisplayName(), Holders.TASK_MAX_PROGRESS,
						String.valueOf(getMaxProgress()), Holders.TASK_CURRENT_PROGRESS,
						String.valueOf(user.getTaskData(this).getProgress()) });
	}

	public String getRawPhaseDescription(Phase phase);

	public void setPhaseDescription(String value, Phase phase);

	public enum Phase {
		PROGRESS, COMPLETE, UNSTARTED;

	}

}
