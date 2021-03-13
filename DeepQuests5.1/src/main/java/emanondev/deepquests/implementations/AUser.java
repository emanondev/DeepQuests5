package emanondev.deepquests.implementations;

import java.util.*;

import emanondev.core.YMLConfig;
import emanondev.deepquests.interfaces.*;

public abstract class AUser<T extends AUser<T>> implements User<T> {
	private final UserManager<T> userManager;
	private final String uuid;
	// protected final Navigator nav;
	private final QuestManager<T> manager;
	private final YMLConfig config;

	private int questPoints;
	private final Map<Integer, QuestData<T>> questDatas = new HashMap<>();
	private final Map<Integer, MissionData<T>> missionDatas = new HashMap<>();
	private final Map<Integer, TaskData<T>> taskDatas = new HashMap<>();
	private final Set<Mission<T>> activeMissions = new HashSet<>();
	private final Map<TaskType<T>, List<Task<T>>> activeTasks = new HashMap<>();
	private Integer missionLimit = null;

	@SuppressWarnings("unchecked")
	public AUser(UserManager<T> userManager, String uuid) {
		if (userManager == null || uuid == null || uuid.isEmpty())
			throw new NullPointerException();
		this.userManager = userManager;
		this.manager = userManager.getManager();
		this.uuid = uuid;
		this.config = this.getUserManager().getUserConfig(getUID());
		for (QuestData<T> data : loadUserQuestDatas())
			questDatas.put(data.getQuest().getID(), data);
		for (MissionData<T> data : loadUserMissionDatas())
			missionDatas.put(data.getMission().getID(), data);
		for (TaskData<T> data : loadUserTaskDatas())
			taskDatas.put(data.getTask().getID(), data);

		questPoints = getConfig().getInteger(Paths.USERDATA_POINTS, 0);
		for (MissionData<T> missionData : missionDatas.values()) {
			if (missionData.isStarted())
				register(missionData.getMission());
		}
		bag = new AQuestBag<T>((T) this, getConfig().loadSection("quest_bag"));
	}

	@Override
	public final YMLConfig getConfig() {
		return config;
	}

	private final AQuestBag<T> bag;

	public QuestManager<T> getManager() {
		return manager;
	}

	public UserManager<T> getUserManager() {
		return userManager;
	}

	@Override
	public void reset() {
		for (Quest<T> quest : getManager().getQuests())
			getQuestData(quest).reset();
	}

	@Override
	public void erase() {
		for (String key : getConfig().getKeys(false))
			getConfig().set(key, null);
	}

	public void register(Mission<T> mission) {
		if (activeMissions.contains(mission))
			return;

		activeMissions.add(mission);
		for (Task<T> task : mission.getTasks())
			register(task);
	}

	public void unregister(Mission<?> mission) {
		if (!activeMissions.contains(mission))
			return;
		activeMissions.remove(mission);
		for (Task<?> task : mission.getTasks())
			unregister(task);
	}

	private boolean register(Task<T> task) {
		if (!getTaskData(task).isCompleted()) {
			List<Task<T>> list = activeTasks.get(task.getType());
			if (list == null) {
				list = new Vector<Task<T>>();
				activeTasks.put(task.getType(), list);
			}
			return list.add(task);
		}
		return false;
	}

	public final void unregister(Task<?> task) {
		if (activeTasks.get(task.getType()) != null)
			activeTasks.get(task.getType()).remove(task);
	}

	public final List<Task<T>> getActiveTasks(TaskType<T> type) {
		List<Task<T>> list = activeTasks.get(type);
		if (list == null) {
			list = new Vector<Task<T>>();
			activeTasks.put(type, list);
			return Collections.unmodifiableList(list);
		}
		return Collections.unmodifiableList(list);
	}

	@Override
	public final boolean isActiveMissionAmountLimitDefault() {
		return missionLimit == null;
	}

	@Override
	public final void setActiveMissionAmountLimit(Integer limit) {
		if (limit != null && limit < 1)
			throw new IllegalArgumentException();
		this.missionLimit = limit;
	}

	public final String getUID() {
		return uuid;
	}

	@Override
	public final int getPoints() {
		return questPoints;
	}

	@Override
	public final void setPoints(int amount) {
		questPoints = amount;
		if (questPoints == 0)
			getConfig().set(Paths.USERDATA_POINTS, null);
		else
			getConfig().set(Paths.USERDATA_POINTS, questPoints);
	}

	@Override
	public final Collection<QuestData<T>> getQuestDatas() {
		return questDatas.values();
	}

	@Override
	public final Collection<MissionData<T>> getMissionDatas() {
		return missionDatas.values();
	}

	@Override
	public final Collection<TaskData<T>> getTaskDatas() {
		return taskDatas.values();
	}

	@Override
	public final QuestData<T> getQuestData(Quest<T> quest) {
		if (questDatas.containsKey(quest.getID()))
			return questDatas.get(quest.getID());
		@SuppressWarnings("unchecked")
		QuestData<T> data = new AQuestData<T>((T) this, quest, config.loadSection("quests_data." + quest.getID()));
		questDatas.put(quest.getID(), data);
		return data;
	}

	@Override
	public final MissionData<T> getMissionData(Mission<T> mission) {
		if (missionDatas.containsKey(mission.getID()))
			return missionDatas.get(mission.getID());
		@SuppressWarnings("unchecked")
		MissionData<T> data = new AMissionData<T>((T) this, mission,
				config.loadSection("missions_data." + mission.getID()));
		missionDatas.put(mission.getID(), data);
		return data;
	}

	@Override
	public final TaskData<T> getTaskData(Task<T> task) {
		if (taskDatas.containsKey(task.getID()))
			return taskDatas.get(task.getID());
		@SuppressWarnings("unchecked")
		TaskData<T> data = new ATaskData<T>((T) this, task, config.loadSection("tasks_data." + task.getID()));
		taskDatas.put(task.getID(), data);
		return data;
	}

	@Override
	public final void eraseMissionData(Mission<T> mission) {
		missionDatas.remove(mission.getID());
		getConfig().set("missions_data." + String.valueOf(mission.getID()), null);
	}

	@Override
	public final void eraseQuestData(Quest<T> quest) {
		questDatas.remove(quest.getID());
		getConfig().set("quests_data." + String.valueOf(quest.getID()), null);
	}

	@Override
	public final void eraseTaskData(Task<T> task) {
		taskDatas.remove(task.getID());
		getConfig().set("tasks_data." + String.valueOf(task.getID()), null);
	}

	@Override
	public final int getActiveMissionAmount() {
		return activeMissions.size();
	}

	@SuppressWarnings("unchecked")
	@Override
	public final int getActiveMissionAmountLimit() {
		if (missionLimit != null)
			return missionLimit;
		Integer limit = getManager().getDefaultMissionLimit((T) this);
		return limit != null ? limit : Integer.MAX_VALUE;
	}

	@SuppressWarnings("unchecked")
	private final Collection<QuestData<T>> loadUserQuestDatas() {
		HashSet<QuestData<T>> set = new HashSet<>();
		for (String key : config.getKeys("quests_data")) {
			try {
				int id = Integer.valueOf(key);
				Quest<T> quest = getManager().getQuest(id);
				if (quest == null) {
					getManager().getPlugin().logIssue("Unable to load Quest &e" + id + " &ffor user &e" + uuid
							+ " &ffor manager &e" + getManager().getName() + " &ferasing it");
					config.set("quests_data." + key, null);
				} else
					set.add(new AQuestData<T>((T) this, quest, config.loadSection("quests_data." + key)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return set;
	}

	@SuppressWarnings("unchecked")
	private final Collection<MissionData<T>> loadUserMissionDatas() {
		HashSet<MissionData<T>> set = new HashSet<>();
		for (String key : config.getKeys("missions_data")) {
			try {
				int id = Integer.valueOf(key);
				Mission<T> mission = getManager().getMission(id);
				if (mission == null) {
					getManager().getPlugin().logIssue("Unable to load Mission &e" + id + " &ffor user &e" + uuid
							+ " &ffor manager &e" + getManager().getName() + " &ferasing it");
					config.set("missions_data." + key, null);
				} else
					set.add(new AMissionData<T>((T) this, mission, config.loadSection("missions_data." + key)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return set;
	}

	@SuppressWarnings("unchecked")
	private final Collection<TaskData<T>> loadUserTaskDatas() {
		HashSet<TaskData<T>> set = new HashSet<>();
		// Navigator subNav = nav.getNavigator("tasks_data");
		for (String key : config.getKeys("tasks_data")) {
			try {
				int id = Integer.valueOf(key);
				Task<T> task = getManager().getTask(id);
				if (task == null) {
					getManager().getPlugin().logIssue("Unable to load Task &e" + id + " &ffor user &e" + uuid
							+ " &ffor manager &e" + getManager().getName() + " &ferasing it");
					config.set("tasks_data." + key, null);
				} else
					set.add(new ATaskData<T>((T) this, task, config.loadSection("tasks_data." + key)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return set;
	}

	@Override
	public final QuestBag<T> getQuestBag() {
		return bag;
	}
}
