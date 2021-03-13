package emanondev.deepquests.interfaces;

import java.io.File;
import java.util.*;

import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.Permission;

import emanondev.core.CorePlugin;
import emanondev.core.YMLConfig;

public interface QuestManager<T extends User<T>> extends GuiElement {

	/**
	 * name of this, must be unique and immutable, used for folder name
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * 
	 * @return require manager of this
	 */
	public RequireProvider<T> getRequireProvider();

	/**
	 * 
	 * @return reward manager of this
	 */
	public RewardProvider<T> getRewardProvider();

	/**
	 * 
	 * @return task manager of this
	 */
	public TaskProvider<T> getTaskProvider();

	/**
	 * 
	 * @return user manager of this
	 */
	public UserManager<T> getUserManager();

	public void save();

	public YMLConfig getConfig();
	
	public YMLConfig getConfig(String fileName);

	public File getFolder();

	public BossBarManager<T> getBossBarManager();

	public int getPriority();

	public Collection<Quest<T>> getQuests();

	public Quest<T> getQuest(int id);

	public Collection<Reward<T>> getRewards();

	public Collection<Require<T>> getRequires();

	public Quest<T> createQuest(OfflinePlayer author);
	
	public Mission<T> createMission(Quest<T> quest,OfflinePlayer author);
	
	public Task<T> createTask(Mission<T> mission, TaskType<T> type,OfflinePlayer author);

	public Reward<T> createReward(RewardType<T> type,OfflinePlayer author);

	public Require<T> createRequire(RequireType<T> type,OfflinePlayer author);

	public void linkRequire(Require<T> require, Quest<T> quest);

	public void unlinkRequire(Require<T> require, Quest<T> quest);

	public void linkRequire(Require<T> require, Mission<T> mission);

	public void unlinkRequire(Require<T> require, Mission<T> mission);

	public void linkCompleteReward(Reward<T> reward, Mission<T> mission);

	public void unlinkCompleteReward(Reward<T> reward, Mission<T> mission);

	public void linkStartReward(Reward<T> reward, Mission<T> mission);

	public void unlinkStartReward(Reward<T> reward, Mission<T> mission);

	public void linkFailReward(Reward<T> reward, Mission<T> mission);

	public void unlinkFailReward(Reward<T> reward, Mission<T> mission);

	public void linkProgressReward(Reward<T> reward, Task<T> task);

	public void unlinkProgressReward(Reward<T> reward, Task<T> task);

	public void linkCompleteReward(Reward<T> reward, Task<T> task);

	public void unlinkCompleteReward(Reward<T> reward, Task<T> task);

	public void delete(Reward<T> reward);

	public void delete(Require<T> require);
	public void delete(Mission<T> mission);

	public void delete(Quest<T> quest);
	public void delete(Task<T> task);

	public void reload();

	/**
	 * 
	 * @param User
	 * @return
	 */
	public Integer getDefaultMissionLimit(T user);

	public Mission<T> getMission(int id);

	public Task<T> getTask(int id);
	public Reward<T> getReward(int id);
	public Require<T> getRequire(int id);

	public Collection<Mission<T>> getMissions();

	public Collection<Task<T>> getTasks();
	
	public void disable();
	
	/**
	 * Utility for command to manipolate user, user must be returned from string
	 * the string may not represent the user id, usually it's something human readable
	 * like player name for players instead of their uuid
	 * @param argoment
	 * @return the related user, may be null
	 */
	public T getArgomentUser(String argoment);

	/**
	 * Utility for command to autocomplete user, must return a collection of users arguments taken by command
	 * @param argoment
	 * @return collection of users arguments taken by command
	 */
	public Collection<String> getUsersArguments();

	public CorePlugin getPlugin();

	Permission getEditorPermission();

	
}