package emanondev.deepquests.interfaces;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import emanondev.deepquests.Perms;
import emanondev.deepquests.Quests;
import emanondev.deepquests.events.MissionCompleteEvent;
import emanondev.deepquests.events.MissionFailEvent;
import emanondev.deepquests.events.MissionStartEvent;
import emanondev.deepquests.events.QuestCompleteEvent;
import emanondev.deepquests.events.QuestFailEvent;
import emanondev.deepquests.events.TaskCompleteEvent;
import emanondev.deepquests.events.TaskProgressEvent;
import emanondev.deepquests.interfaces.Mission.PhaseChange;
import emanondev.deepquests.utils.DisplayState;

@SuppressWarnings("unchecked")
public interface User<T extends User<T>> extends Navigable {
	/**
	 * Reset all quests/missions/tasks progress
	 */
	public void reset();
	/**
	 * Completely erase data
	 */
	public void erase();

	/**
	 * Return missionData of selected mission for this.
	 * @param mission selected mission
	 * @return missionData of selected mission for this
	 * @throws NullPointerException if mission is null
	 * @throws IllegalException if mission manager is unequal to this manager
	 */
	public @Nonnull MissionData<T> getMissionData(@Nonnull Mission<T> mission);

	/**
	 * Return questData of selected quest for this.
	 * @param quest selected quest
	 * @return questData of selected quest for this
	 * @throws NullPointerException if quest is null
	 * @throws IllegalException if quest manager is unequal to this manager
	 */
	public @Nonnull QuestData<T> getQuestData(@Nonnull Quest<T> quest);

	/**
	 * Return taskData of selected task for this.
	 * @param task selected task
	 * @return taskData of selected task for this
	 * @throws NullPointerException if task is null
	 * @throws IllegalException if task manager is unequal to this manager
	 */
	public @Nonnull TaskData<T> getTaskData(@Nonnull Task<T> task);

	/**
	 * Completely erase data of selected mission
	 * @param mission selected mission
	 * @throws NullPointerException if mission is null
	 * @throws IllegalException if mission manager is unequal to this manager
	 */
	public void eraseMissionData(@Nonnull Mission<T> mission);

	/**
	 * Completely erase data of selected quest
	 * @param quest selected quest
	 * @throws NullPointerException if quest is null
	 * @throws IllegalException if quest manager is unequal to this manager
	 */
	public void eraseQuestData(@Nonnull Quest<T> quest);

	/**
	 * Completely erase data of selected task
	 * @param task selected task
	 * @throws NullPointerException if task is null
	 * @throws IllegalException if task manager is unequal to this manager
	 */
	public void eraseTaskData(@Nonnull Task<T> task);
	
	/**
	 * Returns questManager of this.
	 * @return questManager of this
	 */
	public @Nonnull QuestManager<T> getManager();

	/**
	 * Returns userManager of this.
	 * @return userManager of this
	 */
	public @Nonnull UserManager<T> getUserManager();
	

	/**
	 * Returns an unmutable unique identifier of this.
	 * @return an unmutable unique identifier of this
	 */
	public @Nonnull String getUID();
	

	/**
	 * Reset data of selected mission
	 * @param mission selected mission
	 * @throws NullPointerException if mission is null
	 * @throws IllegalException if mission manager is unequal to this manager
	 */
	public default void resetMission(@Nonnull Mission<T> mission) {
		getMissionData(mission).reset();
	}

	/**
	 * Reset data of selected quest
	 * @param quest selected quest
	 * @throws NullPointerException if quest is null
	 * @throws IllegalException if quest manager is unequal to this manager
	 */
	public default void resetQuest(Quest<T> quest) {
		getQuestData(quest).reset();
	}

	/**
	 * Reset data of selected task
	 * @param task selected task
	 * @throws NullPointerException if task is null
	 * @throws IllegalException if task manager is unequal to this manager
	 */
	public default void resetTask(Task<T> task) {
		getTaskData(task).reset();
	}
	
	/**
	 * Returns true if selected player may see selected quest.
	 * @param player - selected player
	 * @param quest - selected quest
	 * @return true if selected player may see selected quest
	 * @throws NullPointerException if player is null
	 * @throws NullPointerException if quest is null
	 */
	public default boolean canSee(Player player, Quest<T> quest) {
		return// quest.isWorldAllowed(player.getWorld()) && 
				Quests.get().getPlayerInfo(player).canSeeQuestState(getDisplayState(quest)) && (quest.isDeveloped() || player.hasPermission(Perms.ADMIN_EDITOR));
	}//TODO check displayinfo of quest?


	/**
	 * Returns true if selected player may see selected mission.
	 * @param player - selected player
	 * @param quest - selected quest
	 * @return true if selected player may see selected mission
	 * @throws NullPointerException if player is null
	 * @throws NullPointerException if mission is null
	 */
	public default boolean canSee(Player player, Mission<T> mission) {
		return (mission.getQuest().isDeveloped() || player.hasPermission(Perms.ADMIN_EDITOR)) && Quests.get().getPlayerInfo(player).canSeeMissionState(getDisplayState(mission));
	}

	/**
	 * Return progress of selected task for this.
	 * @param task selected task
	 * @return progress of selected task for this
	 * @throws NullPointerException if task is null
	 */
	public default int getTaskProgress(Task<T> task) {
		if (task == null)
			throw new NullPointerException();
		return getTaskData(task).getProgress();
	}

	/**
	 * Mark quest as completed for this.
	 * Also call {@link QuestCompleteEvent}
	 * @param quest
	 * @return true
	 */
	public default boolean completeQuest(Quest<T> quest) {
		QuestData<T> data = getQuestData(quest);
		Bukkit.getPluginManager().callEvent(new QuestCompleteEvent<T>((T) this,quest));
		data.complete();
		return true;
	}
	/**
	 * Mark quest as failed for this.
	 * Also call {@link QuestFailEvent}
	 * @param quest
	 * @return true
	 */
	public default boolean failQuest(Quest<T> quest) {
		QuestData<T> data = getQuestData(quest);
		Bukkit.getPluginManager().callEvent(new QuestFailEvent<T>((T) this,quest));
		data.fail();
		return true;
	}

	/**
	 * Returns validated progress amount for selected task.
	 * Also call {@link TaskProgressEvent}<br>
	 * Also call {@link TaskCompleteEvent} if task is completed
	 * @param task selected task
	 * @param amount amount to progress
	 * @param player who progressed
	 * @param skipPlayerCheck should skip restriction applyable on player?
	 * @return validated progress amount
	 */
	public default int progressTask(Task<T> task, int amount,Player player,boolean skipPlayerCheck) {
		if (!skipPlayerCheck && player!=null && !canProgress(task,player))
			return 0;
		TaskData<T> data = getTaskData(task);
		int limit = task.getMaxProgress()-data.getProgress();
		amount = Math.min(limit,amount);
		if (amount>0) {
			TaskProgressEvent<T> event = new TaskProgressEvent<T>((T) this,task,amount,limit);
			Bukkit.getPluginManager().callEvent(event);
			if (event.isCancelled() || event.getProgress()==0)
				return 0;
			amount = event.getProgress();
			for (Reward<T> reward:event.getRewards()) {
				if (amount>0) {
					reward.feedback((T) this,amount);
					reward.apply((T) this,amount);
				}
			}
			data.addProgress(amount);
			getManager().getBossBarManager().onProgress((T) this, task);
		}
		if (data.isCompleted()) {
			unregister(task);
			TaskCompleteEvent<T> taskCompleteEvent = new TaskCompleteEvent<T>((T) this,task);
			Bukkit.getPluginManager().callEvent(taskCompleteEvent);
			for (Reward<T> reward:taskCompleteEvent.getRewards()) {
				reward.feedback((T) this,1);
				reward.apply((T) this,1);
			}

						
			MissionData<T> missionData = getMissionData(task.getMission());
			if (missionData.isCompleted())
				return amount;
			if (missionData.isFailed() || !missionData.isStarted())
				return amount;
			boolean completedMission = true;
			for(Task<T> taskLoop:task.getMission().getTasks()) {
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
	 * @param task
	 */
	public void unregister(Task<?> task);
	/**
	 * 
	 * @param task
	 * @param player
	 * @return true if player can progress on task
	 */
	public boolean canProgress(Task<T> task, Player player);
	
	/**
	 * Also call {@link MissionCompleteEvent} if mission is started
	 * @param mission
	 * @return
	 */
	public default boolean completeMission(Mission<T> mission) {
		MissionData<T> missionData = getMissionData(mission);
		if (!missionData.isStarted())
			return false;
		MissionCompleteEvent<T> event = new MissionCompleteEvent<T>((T) this,mission);
		Bukkit.getPluginManager().callEvent(event);
		unregister(mission);
		missionData.complete();
		
		if (mission.showPhaseMessage(PhaseChange.COMPLETE))
			for (Player p:this.getPlayers())
				p.sendMessage(mission.getPhaseMessage((T) this, p,PhaseChange.COMPLETE).toArray(new String[0]));
		for (Reward<T> reward:event.getRewards()) {
			reward.feedback((T) this,1);
			reward.apply((T) this,1);
		}

		return true;
	}
	/**
	 * call {@link MissionFailEvent}
	 * @param mission
	 * @return
	 */
	public default boolean failMission(Mission<T> mission) {
		MissionData<T> missionData = getMissionData(mission);
		if (missionData.isFailed())
			return false;
		MissionFailEvent<T> event = new MissionFailEvent<T>((T) this,mission);
		Bukkit.getPluginManager().callEvent(event);
		unregister(mission);
		missionData.fail();
		for (Reward<T> reward:event.getRewards()) {
			reward.feedback((T) this,1);
			reward.apply((T) this,1);
		}
		if (mission.showPhaseMessage(PhaseChange.FAIL))
			for (Player p:this.getPlayers())
				p.sendMessage(mission.getPhaseMessage((T) this, p,PhaseChange.FAIL).toArray(new String[0]));
		return true;
	}
	/**
	 * call {@link MissionStartEvent}
	 * @param mission
	 * @return
	 */
	public default boolean startMission(Mission<T> mission,Player player,boolean forcedStart) {
		MissionData<T> missionData = getMissionData(mission);
		if (!forcedStart && !canStart(mission,player))
			return false;
		if (!forcedStart && !canRegister(mission))
			return false;
			
		MissionStartEvent<T> event = new MissionStartEvent<T>((T) this,mission);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled())
			return false;
		missionData.start();
		register(mission);
		for (Reward<T> reward:event.getRewards()) {
			reward.feedback((T) this,1);
			reward.apply((T) this,1);
		}
		if (mission.showPhaseMessage(PhaseChange.START))
			for (Player p:this.getPlayers())
				p.sendMessage(mission.getPhaseMessage((T) this, p,PhaseChange.START).toArray(new String[0]));
		return true;
	}

	/**
	 * if Mission isStarted return ONPROGRESS<br>
	 * if Mission isOnCooldown return COOLDOWN<br>
	 * if Mission isCompleted return COMPLETED<br>
	 * if Mission isFailed return FAILED<br>
	 * if user satisfy requires for Mission return UNSTARTED<br>
	 * else return LOCKED
	 * 
	 * @param mission
	 * @return
	 */
	public default DisplayState getDisplayState(Mission<T> mission) {
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
	 * 
	 * @param mission
	 * @return true if user satisfy all requires of mission
	 */
	public default boolean hasRequires(Mission<T> mission) {
		for (Require<T> require : mission.getRequires()) {
			if (!require.isAllowed((T) this))
				return false;
		}
		return true;
	}

	/**
	 * 
	 * @param quest
	 * @return in ordine di checks <br>
	 * 		se la Quest è dichiarata Completata ritorna COMPLETED <br>
	 * 		se la Quest è dichiarata Fallita ritorna FAILED <br>
	 * 		se la Quest è dichiarata Iniziata ritorna ONPROGRESS <br>
	 * 		se la Quest è in cooldown ritorna COOLDOWN <br>
	 * 		se user non soddisfa le require la quest è LOCKED <br>
	 * 		se la Quest non ha missioni la quest è UNSTARTED <br>
	 * 		se la Quest ha missioni in avanzamento (ONPROGRESS) è ONPROGRESS <br>
	 * 		se la Quest non ha missioni non iniziate (UNSTARTED) e ha missioni
	 *         bloccate (LOCKED) è ONPROGRESS <br>
	 * 		se la Quest non ha missioni non iniziate (UNSTARTED) e ha missioni
	 *         completate (COMPLETED) è COMPLETED <br>
	 * 		se la Quest non ha missioni non iniziate (UNSTARTED) è FAILED <br>
	 * 		se la Quest ha missioni in attesa o completate o fallite è UNSTARTED
	 *         <br>
	 * 		altrimenti è ONPROGRESS
	 */
	public default DisplayState getDisplayState(Quest<T> quest) {
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
			if (values.get(DisplayState.COMPLETED) > 0 || values.get(DisplayState.COOLDOWN)>0)
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
	 * 
	 * @param quest
	 * @return true if user satisfy all requires of quest
	 */
	public default boolean hasRequires(Quest<T> quest) {
		for (Require<T> require : quest.getRequires()) {
			if (!require.isAllowed((T) this))
				return false;
		}
		return true;
	}

	/**
	 * 
	 * @param quest - target quest
	 * @return a map with the amount of DisplayStatus of Mission of the quest
	 */
	public default EnumMap<DisplayState, Integer> getMissionsStates(Quest<T> quest) {
		EnumMap<DisplayState, Integer> values = new EnumMap<DisplayState, Integer>(DisplayState.class);
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
	 * @param quest
	 */
	public default void unregister(Quest<?> quest) {
		for (Mission<?> mission:quest.getMissions())
			unregister(mission);
	}
	/**
	 * set the mission active for this
	 * @param mission
	 */
	public void register(Mission<T> mission);
	/**
	 * set inactive the mission and it's tasks for this
	 * @param mission
	 */
	public void unregister(Mission<?> mission);
	/**
	 * 
	 * @param type
	 * @return active quest of selected type
	 */
	public List<Task<T>> getActiveTasks(TaskType<T> type);
	
	/**
	 * 
	 * @return players linked to user
	 */
	public Collection<Player> getPlayers();
	/**
	 * 
	 * @return questpoints of this
	 */
	public int getPoints();
	/**
	 * 
	 * @return set questpoints of this
	 */
	public void setPoints(int amount);
	/**
	 * 
	 * @param mission
	 * @param starter - might be null
	 * @return true if starter is allowed to start mission for this
	 */
	public boolean canStart(Mission<T> mission, Player starter);
	/**
	 * 
	 * @return the amount of missions currently active for this
	 */
	public int getActiveMissionAmount();
	/**
	 * 
	 * @return the amount of missions currently active for this
	 */
	public int getActiveMissionAmountLimit();
	/**
	 * 
	 * @return true if inherit limit form userManager
	 */
	public boolean isActiveMissionAmountLimitDefault();
	/**
	 * 
	 * @param limit - if null restore default value from userManager
	 */
	public void setActiveMissionAmountLimit(Integer limit);
	/**
	 * 
	 * @param mission
	 * @return true if curretly active missions are not beyond the limit
	 */
	public default boolean canRegister(Mission<T> mission) {
		return this.getActiveMissionAmountLimit()>this.getActiveMissionAmount();
	}
	public Collection<QuestData<T>> getQuestDatas();
	public Collection<MissionData<T>> getMissionDatas();
	public Collection<TaskData<T>> getTaskDatas();
	public default void saveOnDisk() {
		getUserManager().saveUser((T) this);
	}
	
	public @Nullable QuestBag<T> getQuestBag();
}
