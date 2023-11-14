package emanondev.deepquests.interfaces;

import emanondev.core.UtilsString;
import emanondev.deepquests.Holders;
import emanondev.deepquests.gui.button.GuiElementButton;
import emanondev.deepquests.gui.button.SortableButton;
import emanondev.deepquests.gui.inventory.Gui;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * @param <T> - User Type
 * @author emanon<br>
 * <p>
 * remember to add <br>
 * <p>
 * for playertasks
 */
public interface Task<T extends User<T>> extends HasWorlds, QuestComponent<T> {

    @NotNull Mission<T> getMission();

    /**
     * @return ordered list of rewards
     */
    @NotNull Collection<Reward<T>> getCompleteRewards();

    /**
     * @param id - key of the reward
     * @return the reward with key or null
     */
    @Nullable Reward<T> getCompleteReward(int id);

    /**
     * @param reward - the reward to add
     * @return true if successfully added
     * @throws IllegalArgumentException if reward.getParent() != null
     * @throws IllegalArgumentException if getReward(reward.getKey()) != null
     */
    boolean addCompleteReward(@NotNull Reward<T> reward);

    /**
     * @param reward - the reward to remove
     * @return true if successfully removed
     */
    boolean removeCompleteReward(@NotNull Reward<T> reward);

    /**
     * @return ordered list of rewards
     */
    @NotNull Collection<Reward<T>> getProgressRewards();

    /**
     * @param id - key of the reward
     * @return the reward with key or null
     */
    @NotNull Reward<T> getProgressReward(int id);

    /**
     * @param reward - the reward to add
     * @return true if successfully added
     * @throws IllegalArgumentException if reward.getParent() != null
     * @throws IllegalArgumentException if getReward(reward.getKey()) != null
     */
    boolean addProgressReward(@NotNull Reward<T> reward);

    /**
     * @param reward - the reward to remove
     * @return true if successfully removed
     */
    boolean removeProgressReward(@NotNull Reward<T> reward);

    /**
     * ProgressChance is the probability to Obtain a progress <br>
     * on the task doing the action, by default 1 = 100% <br>
     * rewards are given only when progress is successful
     *
     * @return progressChance of this
     */
    double getProgressChance();

    /**
     * @param progressChance Allowed values are ]0;1] , where 1 is 100% and 0 is 0% chances to
     *                       progress
     */
    void setProgressChance(double progressChance);

    /**
     * When the max progress is reached the task is completed
     *
     * @return max allowed progress for this task
     */
    int getMaxProgress();

    /**
     * Allowed values are [1;Integer.MAX_VALUE]
     *
     * @param maxProgress - new value for the max progress
     */
    void setMaxProgress(int maxProgress);

    /**
     * @return the TaskType of this
     */
    TaskType<T> getType();

    /**
     * @return BossBar style of this
     */
    @NotNull
    BarStyle getBossBarStyle();

    /**
     * @return BossBar color of this
     */
    @NotNull
    BarColor getBossBarColor();

    /**
     * set BossBar style of this
     *
     * @param barStyle value, use null to inherit default value
     */
    void setBossBarStyle(@Nullable BarStyle barStyle);

    /**
     * set BossBar color of this
     *
     * @param barColor value, use null to inherit default value
     */
    void setBossBarColor(@Nullable BarColor barColor);

    /**
     * @return should BossBar be shown for this task progress?
     */
    boolean showBossBar();

    /**
     * sets if BossBar should be used
     *
     * @param value should boss bar be shown to user for this task when progressing?
     *              set null to inherit default value
     */
    void setShowBossBar(@Nullable Boolean value);

    /**
     * called when the task has progress of amount for user
     *
     * @param user   target user
     * @param amount progressed value
     * @param player who progressed the task
     * @param forced - avoid checking permission of player to progress
     * @return the final progress amount
     */
    default int onProgress(@NotNull T user, int amount, @Nullable Player player, boolean forced) {
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
     * @return taskType ID of this
     */
    default @NotNull String getTypeName() {
        return getType().getKeyID();
    }

    default @NotNull String[] getHolders(T user) {
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
            list[1] = list[7];
        else
            list[1] = list[5];

        return list;
    }

    @Override
    default @NotNull Material getGuiMaterial() {
        return Material.PAPER;
    }

    @Override
    default @NotNull SortableButton getEditorButton(@NotNull Gui parent) {
        return new GuiElementButton<>(parent, this);
    }

    @Override
    default @NotNull QuestManager<T> getManager() {
        return getMission().getManager();
    }

    boolean isHidden();

    void setHidden(Boolean value);

    default String getPhaseDescription(T user, Phase phase) {
        return UtilsString.fix(getRawPhaseDescription(phase), null, true,
                Holders.DISPLAY_NAME, getDisplayName(), Holders.TASK_MAX_PROGRESS,
                String.valueOf(getMaxProgress()), Holders.TASK_CURRENT_PROGRESS,
                String.valueOf(user.getTaskData(this).getProgress()));
    }

    String getRawPhaseDescription(Phase phase);

    void setPhaseDescription(String value, Phase phase);

    enum Phase {
        PROGRESS, COMPLETE, UNSTARTED

    }

}
