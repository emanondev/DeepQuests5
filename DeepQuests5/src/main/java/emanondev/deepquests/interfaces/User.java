package emanondev.deepquests.interfaces;

import emanondev.deepquests.Perms;
import emanondev.deepquests.Quests;
import emanondev.deepquests.events.*;
import emanondev.deepquests.interfaces.Mission.PhaseChange;
import emanondev.deepquests.utils.DisplayState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;

@SuppressWarnings("unchecked")
public interface User<T extends User<T>> extends Navigable {
    /**
     * Reset all quests/missions/tasks progress
     */
    void reset();

    /**
     * Completely erase data
     */
    void erase();

    /**
     * Return missionData of selected mission for this.
     *
     * @param mission selected mission
     * @return missionData of selected mission for this
     * @throws NullPointerException     if mission is null
     * @throws IllegalArgumentException if mission manager is unequal to this manager
     */
    @NotNull
    MissionData<T> getMissionData(@NotNull Mission<T> mission);

    /**
     * Return questData of selected quest for this.
     *
     * @param quest selected quest
     * @return questData of selected quest for this
     * @throws NullPointerException     if quest is null
     * @throws IllegalArgumentException if quest manager is unequal to this manager
     */
    @NotNull
    QuestData<T> getQuestData(@NotNull Quest<T> quest);

    /**
     * Return taskData of selected task for this.
     *
     * @param task selected task
     * @return taskData of selected task for this
     * @throws NullPointerException     if task is null
     * @throws IllegalArgumentException if task manager is unequal to this manager
     */
    @NotNull
    TaskData<T> getTaskData(@NotNull Task<T> task);

    /**
     * Completely erase data of selected mission
     *
     * @param mission selected mission
     * @throws NullPointerException     if mission is null
     * @throws IllegalArgumentException if mission manager is unequal to this manager
     */
    void eraseMissionData(@NotNull Mission<T> mission);

    /**
     * Completely erase data of selected quest
     *
     * @param quest selected quest
     * @throws NullPointerException     if quest is null
     * @throws IllegalArgumentException if quest manager is unequal to this manager
     */
    void eraseQuestData(@NotNull Quest<T> quest);

    /**
     * Completely erase data of selected task
     *
     * @param task selected task
     * @throws NullPointerException     if task is null
     * @throws IllegalArgumentException if task manager is unequal to this manager
     */
    void eraseTaskData(@NotNull Task<T> task);

    /**
     * Returns questManager of this.
     *
     * @return questManager of this
     */
    @NotNull
    QuestManager<T> getManager();

    /**
     * Returns userManager of this.
     *
     * @return userManager of this
     */
    @NotNull
    UserManager<T> getUserManager();


    /**
     * Returns an immutable unique identifier of this.
     *
     * @return an immutable unique identifier of this
     */
    @NotNull
    String getUID();


    /**
     * Reset data of selected mission
     *
     * @param mission selected mission
     * @throws NullPointerException     if mission is null
     * @throws IllegalArgumentException if mission manager is unequal to this manager
     */
    default void resetMission(@NotNull Mission<T> mission) {
        getMissionData(mission).reset();
    }

    /**
     * Reset data of selected quest
     *
     * @param quest selected quest
     * @throws NullPointerException     if quest is null
     * @throws IllegalArgumentException if quest manager is unequal to this manager
     */
    default void resetQuest(@NotNull Quest<T> quest) {
        getQuestData(quest).reset();
    }

    /**
     * Reset data of selected task
     *
     * @param task selected task
     * @throws NullPointerException     if task is null
     * @throws IllegalArgumentException if task manager is unequal to this manager
     */
    default void resetTask(@NotNull Task<T> task) {
        getTaskData(task).reset();
    }

    /**
     * Returns true if selected player may see selected quest.
     *
     * @param player - selected player
     * @param quest  - selected quest
     * @return true if selected player may see selected quest
     * @throws NullPointerException if player is null
     * @throws NullPointerException if quest is null
     */
    default boolean canSee(@NotNull Player player, @NotNull Quest<T> quest) {
        return// quest.isWorldAllowed(player.getWorld()) &&
                Quests.get().getPlayerInfo(player).canSeeQuestState(getDisplayState(quest)) && (quest.isDeveloped() || player.hasPermission(Perms.ADMIN_EDITOR));
    }//TODO check displayinfo of quest?


    /**
     * Returns true if selected player may see selected mission.
     *
     * @param player  - selected player
     * @param mission - selected quest
     * @return true if selected player may see selected mission
     * @throws NullPointerException if player is null
     * @throws NullPointerException if mission is null
     */
    default boolean canSee(@NotNull Player player, @NotNull Mission<T> mission) {
        return (mission.getQuest().isDeveloped() || player.hasPermission(Perms.ADMIN_EDITOR)) && Quests.get().getPlayerInfo(player).canSeeMissionState(getDisplayState(mission));
    }

    /**
     * Return progress of selected task for this.
     *
     * @param task selected task
     * @return progress of selected task for this
     * @throws NullPointerException if task is null
     */
    default int getTaskProgress(@NotNull Task<T> task) {
        return getTaskData(task).getProgress();
    }

    /**
     * Mark quest as completed for this.
     * Also call {@link QuestCompleteEvent}
     *
     * @return true
     */
    default boolean completeQuest(@NotNull Quest<T> quest) {
        QuestData<T> data = getQuestData(quest);
        Bukkit.getPluginManager().callEvent(new QuestCompleteEvent<>((T) this, quest));
        data.complete();
        return true;
    }

    /**
     * Mark quest as failed for this.
     * Also call {@link QuestFailEvent}
     *
     * @return true
     */
    default boolean failQuest(@NotNull Quest<T> quest) {
        QuestData<T> data = getQuestData(quest);
        Bukkit.getPluginManager().callEvent(new QuestFailEvent<>((T) this, quest));
        data.fail();
        return true;
    }

    /**
     * Returns validated progress amount for selected task.
     * Also call {@link TaskProgressEvent}<br>
     * Also call {@link TaskCompleteEvent} if task is completed
     *
     * @param task            selected task
     * @param amount          amount to progress
     * @param player          who progressed
     * @param skipPlayerCheck should skip restriction applicable on player?
     * @return validated progress amount
     */
    default int progressTask(@NotNull Task<T> task, int amount, @Nullable Player player, boolean skipPlayerCheck) {
        if (!skipPlayerCheck && player != null && !canProgress(task, player))
            return 0;
        TaskData<T> data = getTaskData(task);
        int limit = task.getMaxProgress() - data.getProgress();
        amount = Math.min(limit, amount);
        if (amount > 0) {
            TaskProgressEvent<T> event = new TaskProgressEvent<>((T) this, task, amount, limit);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled() || event.getProgress() == 0)
                return 0;
            amount = event.getProgress();
            for (Reward<T> reward : event.getRewards()) {
                if (amount > 0) {
                    reward.feedback((T) this, amount);
                    reward.apply((T) this, amount);
                }
            }
            data.addProgress(amount);
            getManager().getBossBarManager().onProgress((T) this, task);
        }
        if (data.isCompleted()) {
            unregister(task);
            TaskCompleteEvent<T> taskCompleteEvent = new TaskCompleteEvent<>((T) this, task);
            Bukkit.getPluginManager().callEvent(taskCompleteEvent);
            for (Reward<T> reward : taskCompleteEvent.getRewards()) {
                reward.feedback((T) this, 1);
                reward.apply((T) this, 1);
            }


            MissionData<T> missionData = getMissionData(task.getMission());
            if (missionData.isCompleted())
                return amount;
            if (missionData.isFailed() || !missionData.isStarted())
                return amount;
            boolean completedMission = true;
            for (Task<T> taskLoop : task.getMission().getTasks()) {
                if (!getTaskData(taskLoop).isCompleted()) {
                    completedMission = false;
                    break;
                }
            }

            if (completedMission)
                completeMission(task.getMission());

        }
        return amount;
    }

    /**
     * Sets selected task as inactive.
     */
    void unregister(@NotNull Task<?> task);

    /**
     * @return true if player can progress on task
     */
    boolean canProgress(@NotNull Task<T> task, @Nullable Player player);

    /**
     * Also call {@link MissionCompleteEvent} if mission is started
     */
    default boolean completeMission(@NotNull Mission<T> mission) {
        MissionData<T> missionData = getMissionData(mission);
        if (!missionData.isStarted())
            return false;
        MissionCompleteEvent<T> event = new MissionCompleteEvent<>((T) this, mission);
        Bukkit.getPluginManager().callEvent(event);
        unregister(mission);
        missionData.complete();

        if (mission.showPhaseMessage(PhaseChange.COMPLETE))
            for (Player p : this.getPlayers())
                p.sendMessage(mission.getPhaseMessage((T) this, p, PhaseChange.COMPLETE).toArray(new String[0]));
        for (Reward<T> reward : event.getRewards()) {
            reward.feedback((T) this, 1);
            reward.apply((T) this, 1);
        }

        return true;
    }

    /**
     * call {@link MissionFailEvent}
     */
    default boolean failMission(@NotNull Mission<T> mission) {
        MissionData<T> missionData = getMissionData(mission);
        if (missionData.isFailed())
            return false;
        MissionFailEvent<T> event = new MissionFailEvent<>((T) this, mission);
        Bukkit.getPluginManager().callEvent(event);
        unregister(mission);
        missionData.fail();
        for (Reward<T> reward : event.getRewards()) {
            reward.feedback((T) this, 1);
            reward.apply((T) this, 1);
        }
        if (mission.showPhaseMessage(PhaseChange.FAIL))
            for (Player p : this.getPlayers())
                p.sendMessage(mission.getPhaseMessage((T) this, p, PhaseChange.FAIL).toArray(new String[0]));
        return true;
    }

    /**
     * call {@link MissionStartEvent}
     */
    default boolean startMission(@NotNull Mission<T> mission, @Nullable Player player, boolean forcedStart) {
        MissionData<T> missionData = getMissionData(mission);
        if (!forcedStart && !canStart(mission, player))
            return false;
        if (!forcedStart && !canRegister(mission))
            return false;

        MissionStartEvent<T> event = new MissionStartEvent<>((T) this, mission);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return false;
        missionData.start();
        register(mission);
        for (Reward<T> reward : event.getRewards()) {
            reward.feedback((T) this, 1);
            reward.apply((T) this, 1);
        }
        if (mission.showPhaseMessage(PhaseChange.START))
            for (Player p : this.getPlayers())
                p.sendMessage(mission.getPhaseMessage((T) this, p, PhaseChange.START).toArray(new String[0]));
        return true;
    }

    /**
     * if Mission isStarted return ONPROGRESS<br>
     * if Mission isOnCooldown return COOLDOWN<br>
     * if Mission isCompleted return COMPLETED<br>
     * if Mission isFailed return FAILED<br>
     * if user satisfy requires for Mission return UNSTARTED<br>
     * else return LOCKED
     */
    default @NotNull DisplayState getDisplayState(@NotNull Mission<T> mission) {
        MissionData<T> data = getMissionData(mission);
        if (data.isStarted())
            return DisplayState.ONPROGRESS;
        if (data.isOnCooldown())
            return DisplayState.COOLDOWN;
        boolean hasRequires = hasRequires(mission);
        if (mission.isRepeatable() && hasRequires)
            return DisplayState.UNSTARTED;
        if (data.isCompleted())
            return DisplayState.COMPLETED;
        if (data.isFailed())
            return DisplayState.FAILED;
        if (hasRequires)
            return DisplayState.UNSTARTED;
        return DisplayState.LOCKED;
    }

    /**
     * @return true if user satisfy all requires of mission
     */
    default boolean hasRequires(@NotNull Mission<T> mission) {
        for (Require<T> require : mission.getRequires()) {
            if (!require.isAllowed((T) this))
                return false;
        }
        return true;
    }

    /**
     * @return in ordine di checks <br>
     * se la Quest è dichiarata Completata ritorna COMPLETED <br>
     * se la Quest è dichiarata Fallita ritorna FAILED <br>
     * se la Quest è dichiarata Iniziata ritorna ONPROGRESS <br>
     * se la Quest è in cooldown ritorna COOLDOWN <br>
     * se user non soddisfa le require la quest è LOCKED <br>
     * se la Quest non ha missioni la quest è UNSTARTED <br>
     * se la Quest ha missioni in avanzamento (ONPROGRESS) è ONPROGRESS <br>
     * se la Quest non ha missioni non iniziate (UNSTARTED) e ha missioni
     * bloccate (LOCKED) è ONPROGRESS <br>
     * se la Quest non ha missioni non iniziate (UNSTARTED) e ha missioni
     * completate (COMPLETED) è COMPLETED <br>
     * se la Quest non ha missioni non iniziate (UNSTARTED) è FAILED <br>
     * se la Quest ha missioni in attesa o completate o fallite è UNSTARTED
     * <br>
     * altrimenti è ONPROGRESS
     */
    default @NotNull DisplayState getDisplayState(@NotNull Quest<T> quest) {
        QuestData<T> data = getQuestData(quest);
        if (data.isCompleted())
            return DisplayState.COMPLETED;
        if (data.isFailed())
            return DisplayState.FAILED;
        if (data.isStarted())
            return DisplayState.ONPROGRESS;
        if (data.isOnSelfCooldown())
            return DisplayState.COOLDOWN;
        if (!hasRequires(quest))
            return DisplayState.LOCKED;
        if (quest.getMissions().size() == 0)
            return DisplayState.UNSTARTED;

        EnumMap<DisplayState, Integer> values = getMissionsStates(quest);

        if (values.get(DisplayState.ONPROGRESS) > 0)
            return DisplayState.ONPROGRESS;

        // ONPROGRESS == 0
        if (values.get(DisplayState.UNSTARTED) == 0) {
            //onprogress ==0 && unstarted == 0
            if (values.get(DisplayState.COOLDOWN) > 0)
                return DisplayState.COOLDOWN;
            // onprogress ==0 && unstarted == 0 && cooldown==0
            // completed,failed,locked
            if (values.get(DisplayState.LOCKED) > 0)
                return DisplayState.ONPROGRESS;
            if (values.get(DisplayState.COMPLETED) > 0 || values.get(DisplayState.COOLDOWN) > 0)
                return DisplayState.COMPLETED;
            return DisplayState.FAILED;
        }

        // ONPROGRESS ==0 && UNSTARTED >0
        if (values.get(DisplayState.COOLDOWN) == 0 && values.get(DisplayState.COMPLETED) == 0
                && values.get(DisplayState.FAILED) == 0)
            return DisplayState.UNSTARTED;

        return DisplayState.ONPROGRESS;
    }

    /**
     * @return true if user satisfy all requires of quest
     */
    default boolean hasRequires(@NotNull Quest<T> quest) {
        for (Require<T> require : quest.getRequires()) {
            if (!require.isAllowed((T) this))
                return false;
        }
        return true;
    }

    /**
     * @return a map with the amount of DisplayStatus of Mission of the quest
     */
    default @NotNull EnumMap<DisplayState, Integer> getMissionsStates(@NotNull Quest<T> quest) {
        EnumMap<DisplayState, Integer> values = new EnumMap<>(DisplayState.class);
        for (DisplayState state : DisplayState.values())
            values.put(state, 0);
        for (Mission<T> mission : quest.getMissions()) {
            DisplayState missionState = getDisplayState(mission);
            values.put(missionState, values.get(missionState) + 1);
        }
        return values;
    }

    /**
     * set inactive all missions and tasks of the quest for this
     */
    default void unregister(@NotNull Quest<?> quest) {
        for (Mission<?> mission : quest.getMissions())
            unregister(mission);
    }

    /**
     * set the mission active for this
     */
    void register(@NotNull Mission<T> mission);

    /**
     * set inactive the mission and it's tasks for this
     */
    void unregister(@NotNull Mission<?> mission);

    /**
     * @return active quest of selected type
     */
    @NotNull List<Task<T>> getActiveTasks(@NotNull TaskType<T> type);

    /**
     * @return players linked to user
     */
    @NotNull Collection<Player> getPlayers();

    /**
     * @return questpoints of this
     */
    int getPoints();

    void setPoints(int amount);

    /**
     * @param starter - might be null
     * @return true if starter is allowed to start mission for this
     */
    boolean canStart(@NotNull Mission<T> mission, @Nullable Player starter);

    /**
     * @return the amount of missions currently active for this
     */
    int getActiveMissionAmount();

    /**
     * @return the amount of missions currently active for this
     */
    int getActiveMissionAmountLimit();

    /**
     * @return true if inherit limit form userManager
     */
    boolean isActiveMissionAmountLimitDefault();

    /**
     * @param limit - if null restore default value from userManager
     */
    void setActiveMissionAmountLimit(@Nullable Integer limit);

    /**
     * @return true if currently active missions are not beyond the limit
     */
    default boolean canRegister(@NotNull Mission<T> mission) {
        return this.getActiveMissionAmountLimit() > this.getActiveMissionAmount();
    }

    @NotNull Collection<QuestData<T>> getQuestDatas();

    @NotNull Collection<MissionData<T>> getMissionDatas();

    @NotNull Collection<TaskData<T>> getTaskDatas();

    default void saveOnDisk() {
        getUserManager().saveUser((T) this);
    }

    @Nullable
    QuestBag<T> getQuestBag();
}
