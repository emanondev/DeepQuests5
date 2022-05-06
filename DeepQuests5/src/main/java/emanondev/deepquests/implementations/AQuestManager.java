package emanondev.deepquests.implementations;

import emanondev.core.*;
import emanondev.deepquests.Quests;
import emanondev.deepquests.command.QuestBagCommand;
import emanondev.deepquests.generic.requiretypes.*;
import emanondev.deepquests.generic.rewardtypes.*;
import emanondev.deepquests.generic.tasktypes.*;
import emanondev.deepquests.gui.button.*;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.gui.inventory.PagedMapGui;
import emanondev.deepquests.hooks.Hooks;
import emanondev.deepquests.interfaces.*;
import emanondev.deepquests.utils.Utils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;

import java.io.File;
import java.util.*;

public abstract class AQuestManager<T extends User<T>> implements QuestManager<T> {

    private final static String SUB_PATH_COMPLETE_REWARDS = "complete-rewards";
    private final static String SUB_PATH_START_REWARDS = "start-rewards";
    private final static String SUB_PATH_FAIL_REWARDS = "fail-rewards";
    private final static String SUB_PATH_REQUIRES = "requires";
    private final static String SUB_PATH_PROGRESS_REWARDS = "progress-rewards";
    private final static String PATH_TASKS = "tasks";
    private final static String PATH_MISSIONS = "missions";
    private final static String PATH_QUESTS = "quests";
    private final static String PATH_REWARDS = "rewards";
    private final static String PATH_REQUIRES = "requires";
    private static final String SUB_PATH_TASKS = "tasks";
    private static final String SUB_PATH_MISSIONS = "missions";

    private final String name;

    private final File folder;

    private final YMLConfig managerConfig;

    private final YMLConfig questsDB;

    private final YMLConfig missionsDB;

    private final YMLConfig tasksDB;

    private final YMLConfig requiresDB;

    private final YMLConfig rewardsDB;

    private final BossBarManager<T> bossBarManager;

    private final TaskProvider<T> taskProvider;

    private final RequireProvider<T> requireProvider;

    private final RewardProvider<T> rewardProvider;

    private final Map<Integer, Quest<T>> quests = Collections.synchronizedMap(new HashMap<>());

    private final Map<Integer, Mission<T>> missions = Collections.synchronizedMap(new HashMap<>());

    private final Map<Integer, Task<T>> tasks = Collections.synchronizedMap(new HashMap<>());

    private final Map<Integer, Reward<T>> rewards = Collections.synchronizedMap(new HashMap<>());

    private final Map<Integer, Require<T>> requires = Collections.synchronizedMap(new HashMap<>());

    private final CorePlugin plugin;

    private final int priority;

    private final Permission editorPermission;

    public final YMLConfig getConfig(String fileName) {
        return Quests.get().getConfig(this.getName() + File.separator + fileName);
    }

    public final YMLConfig getConfig() {
        return managerConfig;
    }

    public AQuestManager(String name, CorePlugin plugin) {
        if (name == null || plugin == null)
            throw new NullPointerException();
        if (name.isEmpty() || !Paths.ALPHANUMERIC.matcher(name).matches())
            throw new IllegalArgumentException();
        this.name = name;
        this.plugin = plugin;
        this.folder = new File(Quests.get().getDataFolder(), this.name);
        if (!folder.exists())
            folder.mkdirs();
        this.managerConfig = getConfig("config.yml");
        this.priority = managerConfig.loadInteger(Paths.PRIORITY, 0);

        this.bossBarManager = new ABossBarManager<>(this);
        this.taskProvider = new ATaskProvider<>(this);
        this.requireProvider = new ARequireProvider<>(this);
        this.rewardProvider = new ARewardProvider<>(this);

        this.questsDB = getConfig("quests.yml");
        this.missionsDB = getConfig("missions.yml");
        this.tasksDB = getConfig("tasks.yml");
        this.requiresDB = getConfig("requires.yml");
        this.rewardsDB = getConfig("rewards.yml");

        this.editorPermission = new PermissionBuilder("deepquests.editor." + this.getName())
                .setDescription("Allows to edit anything for manager " + this.getName())
                .addChild(getTaskProvider().getEditorPermission(), true)
                .addChild(getRewardProvider().getEditorPermission(), true)
                .addChild(getRequireProvider().getEditorPermission(), true).buildAndRegister(getPlugin(), true);

        getTaskProvider().registerType(new BreakBlockTaskType<>(this));
        getTaskProvider().registerType(new BreedMobTaskType<>(this));
        getTaskProvider().registerType(new FishingTaskType<>(this));
        getTaskProvider().registerType(new InteractAtTaskType<>(this));
        getTaskProvider().registerType(new KillMobTaskType<>(this));
        getTaskProvider().registerType(new PlaceBlockTaskType<>(this));
        getTaskProvider().registerType(new ShearSheepTaskType<>(this));
        getTaskProvider().registerType(new TameMobTaskType<>(this));
        getRequireProvider().registerType(new CompletedMissionRequireType<>(this));
        getRequireProvider().registerType(new CompletedQuestRequireType<>(this));
        getRequireProvider().registerType(new FailedMissionRequireType<>(this));
        getRequireProvider().registerType(new FailedQuestRequireType<>(this));
        getRequireProvider().registerType(new MissionsPointsRequireType<>(this));
        getRequireProvider().registerType(new QuestsPointsRequireType<>(this));
        getRequireProvider().registerType(new CurrentMissionStateRequireType<>(this));
        getRequireProvider().registerType(new CurrentQuestStateRequireType<>(this));
        getRequireProvider().registerType(new DayOfWeekRequireType<>(this));
        getRewardProvider().registerType(new CompleteMissionRewardType<>(this));
        getRewardProvider().registerType(new CompleteQuestRewardType<>(this));
        getRewardProvider().registerType(new FailMissionRewardType<>(this));
        getRewardProvider().registerType(new FailQuestRewardType<>(this));
        getRewardProvider().registerType(new MissionsPointsRewardType<>(this));
        getRewardProvider().registerType(new QuestsPointsRewardType<>(this));
        getRewardProvider().registerType(new StartMissionRewardType<>(this));
        getRewardProvider().registerType(new SoundRewardType<>(this));
        if (Hooks.isCitizenEnabled()) {
            getTaskProvider().registerType(new NPCDeliverTaskType<>(this));
            getTaskProvider().registerType(new NPCTalkTaskType<>(this));
            getTaskProvider().registerType(new NPCKillTaskType<>(this));
        }
        if (Hooks.isMythicMobsEnabled()) {
            getTaskProvider().registerType(new KillMythicMobTaskType<>(this));
        }
        if (Hooks.isRegionAPIEnabled()) {
            getTaskProvider().registerType(new EnterRegionTaskType<>(this));
            getTaskProvider().registerType(new EnterRegionWithItemTaskType<>(this));
            getTaskProvider().registerType(new LeaveRegionTaskType<>(this));
        }

        if (Hooks.isRegionAPIEnabled()) {
            getRequireProvider().registerType(new HaveQuestItemRequireType<>(this));
            getRewardProvider().registerType(new GetQuestItemRewardType<>(this));
        }
        if (Hooks.isItemEditEnable()) {
            if (Hooks.isCitizenEnabled())
                getTaskProvider().registerType(new DeliverQuestItemTaskType<>(this));
            getTaskProvider().registerType(new ObtainQuestItemTaskType<>(this));
            Quests.get().registerCommand(new QuestBagCommand<>(this));
        }
    }

    public void debugUnused() {
        SortedSet<Integer> rewardIds = new TreeSet<>(rewards.keySet());
        SortedSet<Integer> requireIds = new TreeSet<>(requires.keySet());
        for (Task<T> task : tasks.values()) {
            if (task.getCompleteRewards() != null)
                for (Reward<T> rew : task.getCompleteRewards())
                    rewardIds.remove(rew.getID());
            if (task.getProgressRewards() != null)
                for (Reward<T> rew : task.getProgressRewards())
                    rewardIds.remove(rew.getID());
        }
        for (Mission<T> m : missions.values()) {
            if (m.getCompleteRewards() != null)
                for (Reward<T> rew : m.getCompleteRewards())
                    rewardIds.remove(rew.getID());
            if (m.getFailRewards() != null)
                for (Reward<T> rew : m.getFailRewards())
                    rewardIds.remove(rew.getID());
            if (m.getStartRewards() != null)
                for (Reward<T> rew : m.getStartRewards())
                    rewardIds.remove(rew.getID());
            if (m.getRequires() != null)
                for (Require<T> rew : m.getRequires())
                    requireIds.remove(rew.getID());
        }
        for (Quest<T> q : quests.values()) {
            if (q.getRequires() != null)
                for (Require<T> rew : q.getRequires())
                    requireIds.remove(rew.getID());
        }

        for (int id : rewardIds)
            getPlugin().logDone("reward &e" + id + " &fis unused");
        for (int id : requireIds)
            getPlugin().logDone("require &e" + id + " &fis unused");
    }

    @Override
    public void reload() {
        try {

            quests.clear();
            missions.clear();
            tasks.clear();
            rewards.clear();
            requires.clear();

            HashSet<Integer> ids = new HashSet<>();
            for (String rw : rewardsDB.getKeys(PATH_REWARDS)) {
                try {
                    ids.add(Integer.valueOf(rw));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (int id : ids)
                try {
                    rewards.put(id, loadReward(id));
                } catch (Throwable t) {
                    t.printStackTrace();
                }

            ids.clear();
            for (String rw : requiresDB.getKeys(PATH_REQUIRES)) {
                try {
                    ids.add(Integer.valueOf(rw));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (int id : ids)
                try {
                    requires.put(id, loadRequire(id));
                } catch (Throwable t) {
                    t.printStackTrace();
                }

            ids.clear();
            for (String questId : questsDB.getKeys(PATH_QUESTS)) {
                try {
                    ids.add(Integer.valueOf(questId));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (int questId : ids)
                try {
                    Quest<T> quest = loadQuest(questId);
                    quests.put(questId, quest);

                    for (int missionId : questsDB.getIntegerList(PATH_QUESTS + "." + questId + "." + SUB_PATH_MISSIONS))
                        try {
                            if (missions.containsKey(missionId))
                                throw new IllegalStateException("More quests have the same mission (" + missionId
                                        + " on manager " + this.getName() + ")");
                            Mission<T> mission = loadMission(missionId, quest);
                            quest.addMission(mission);
                            missions.put(missionId, mission);

                            for (int taskId : missionsDB.getIntegerList(PATH_MISSIONS + "." + missionId + "." + SUB_PATH_TASKS))
                                try {
                                    if (tasks.containsKey(taskId))
                                        throw new IllegalStateException("More missions have the same task (" + taskId
                                                + " on manager " + this.getName() + ")");
                                    Task<T> task = loadTask(taskId, mission);
                                    mission.addTask(task);
                                    tasks.put(taskId, task);

                                    for (int rewId : tasksDB
                                            .getIntegerList(PATH_TASKS + "." + taskId + "." + SUB_PATH_PROGRESS_REWARDS))
                                        try {
                                            Reward<T> rew = rewards.get(rewId);
                                            if (rew == null)
                                                throw new NullPointerException();
                                            task.addProgressReward(rew);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    for (int rewId : tasksDB
                                            .getIntegerList(PATH_TASKS + "." + taskId + "." + SUB_PATH_COMPLETE_REWARDS))
                                        try {
                                            Reward<T> rew = rewards.get(rewId);
                                            if (rew == null)
                                                throw new NullPointerException();
                                            task.addCompleteReward(rew);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            for (int rewId : missionsDB
                                    .getIntegerList(PATH_MISSIONS + "." + missionId + "." + SUB_PATH_COMPLETE_REWARDS))
                                try {
                                    Reward<T> rew = rewards.get(rewId);
                                    if (rew == null)
                                        throw new NullPointerException();
                                    mission.addCompleteReward(rew);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            for (int rewId : missionsDB
                                    .getIntegerList(PATH_MISSIONS + "." + missionId + "." + SUB_PATH_FAIL_REWARDS))
                                try {
                                    Reward<T> rew = rewards.get(rewId);
                                    if (rew == null)
                                        throw new NullPointerException();
                                    mission.addFailReward(rew);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            for (int rewId : missionsDB
                                    .getIntegerList(PATH_MISSIONS + "." + missionId + "." + SUB_PATH_START_REWARDS))
                                try {
                                    Reward<T> rew = rewards.get(rewId);
                                    if (rew == null)
                                        throw new NullPointerException();
                                    mission.addStartReward(rew);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            for (int reqId : missionsDB
                                    .getIntegerList(PATH_MISSIONS + "." + missionId + "." + SUB_PATH_REQUIRES))
                                try {
                                    Require<T> req = requires.get(reqId);
                                    if (req == null)
                                        throw new NullPointerException();
                                    mission.addRequire(req);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    for (int reqId : questsDB.getIntegerList(PATH_QUESTS + "." + questId + "." + SUB_PATH_REQUIRES))
                        try {
                            Require<T> req = requires.get(reqId);
                            if (req == null)
                                throw new NullPointerException();
                            quest.addRequire(req);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                } catch (Throwable t) {
                    t.printStackTrace();
                }

            Quests.get().logDone("Loaded &e" + quests.size() + "&f quest(s) for manager &e" + this.getName());
            Quests.get().logDone("Loaded &e" + missions.size() + "&f mission(s) for manager &e" + this.getName());
            Quests.get().logDone("Loaded &e" + tasks.size() + "&f task(s) for manager &e" + this.getName());
            Quests.get().logDone("Loaded &e" + rewards.size() + "&f reward(s) for manager &e" + this.getName());
            Quests.get().logDone("Loaded &e" + requires.size() + "&f require(s) for manager &e" + this.getName());

            getUserManager().reload();
            bossBarManager.reload();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save() {
        try {
            getUserManager().saveAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disable() {
    }

    @Override
    public SortableButton getEditorButton(Gui parent) {
        return new GuiElementButton<>(parent, this);
    }

    protected Quest<T> generateQuest(int id, YMLSection section) {
        return new AQuest<>(id, this, section);
    }

    private Require<T> generateRequire(int id, YMLSection section) {
        return getRequireProvider().getInstance(id, this, section);
    }

    private Reward<T> generateReward(int id, YMLSection section) {
        return getRewardProvider().getInstance(id, this, section);
    }

    protected Mission<T> generateMission(int id, Quest<T> quest, YMLSection section) {
        return new AMission<>(id, quest, section);
    }

    private Task<T> generateTask(int id, Mission<T> mission, YMLSection section) {
        return getTaskProvider().getInstance(id, mission, section);
    }

    @Override
    public CorePlugin getPlugin() {
        return plugin;
    }

    @Override
    public final Collection<Quest<T>> getQuests() {
        return quests.values();
    }

    @Override
    public final Collection<Mission<T>> getMissions() {
        return missions.values();
    }

    @Override
    public final Collection<Task<T>> getTasks() {
        return tasks.values();
    }

    @Override
    public final Collection<Reward<T>> getRewards() {
        return rewards.values();
    }

    @Override
    public final Collection<Require<T>> getRequires() {
        return requires.values();
    }

    @Override
    public final Integer getDefaultMissionLimit(T user) {
        return getConfig().loadInteger(Paths.DEFAULT_MISSION_LIMIT, 3);
    }

    @Override
    public final int hashCode() {
        return name.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public final Quest<T> createQuest(OfflinePlayer author) {
        int max = 0;
        for (String key : questsDB.getKeys(PATH_QUESTS))
            try {
                max = Math.max(max, Integer.parseInt(key));
            } catch (Exception e) {
                e.printStackTrace();
            }
        int id = max + 1;
        YMLSection section = questsDB.loadSection(PATH_QUESTS + "." + id);
        if (author != null)
            section.set(Paths.AUTHOR, author.getName());
        quests.put(id, generateQuest(id, section));
        return quests.get(id);
    }

    @Override
    public final Mission<T> createMission(Quest<T> quest, OfflinePlayer author) {
        int max = 0;
        for (String key : missionsDB.getKeys(PATH_MISSIONS))
            try {
                max = Math.max(max, Integer.parseInt(key));
            } catch (Exception e) {
                e.printStackTrace();
            }
        int id = max + 1;
        YMLSection section = missionsDB.loadSection(PATH_MISSIONS + "." + id);
        if (author != null)
            section.set(Paths.AUTHOR, author.getName());

        TreeSet<Integer> missionsIds = new TreeSet<>(
                questsDB.getIntegerList(PATH_QUESTS + "." + quest.getID() + "." + SUB_PATH_MISSIONS, Collections.emptyList()));
        missionsIds.add(id);
        questsDB.set(PATH_QUESTS + "." + quest.getID() + "." + SUB_PATH_MISSIONS,
                missionsIds.isEmpty() ? null : new ArrayList<>(missionsIds));

        quest.addMission(generateMission(id, quest, section));
        missions.put(id, quest.getMission(id));
        return quest.getMission(id);
    }

    @Override
    public final Task<T> createTask(Mission<T> mission, TaskType<T> type, OfflinePlayer author) {

        int max = 0;
        for (String key : tasksDB.getKeys(PATH_TASKS))
            try {
                max = Math.max(max, Integer.parseInt(key));
            } catch (Exception e) {
                e.printStackTrace();
            }

        int id = max + 1;
        YMLSection section = tasksDB.loadSection(PATH_TASKS + "." + id);
        section.set(Paths.TYPE_NAME, type.getKeyID());
        if (author != null)
            section.set(Paths.AUTHOR, author.getName());

        TreeSet<Integer> tasksIds = new TreeSet<>(
                missionsDB.getIntegerList(PATH_MISSIONS + "." + mission.getID() + "." + SUB_PATH_TASKS, Collections.emptyList()));
        tasksIds.add(id);
        missionsDB.set(PATH_MISSIONS + "." + mission.getID() + "." + SUB_PATH_TASKS,
                tasksIds.isEmpty() ? null : new ArrayList<>(tasksIds));

        mission.addTask(generateTask(id, mission, section));
        tasks.put(id, mission.getTask(id));
        return mission.getTask(id);
    }

    @Override
    public final Reward<T> createReward(RewardType<T> type, OfflinePlayer author) {
        int max = 0;
        for (String key : rewardsDB.getKeys(PATH_REWARDS))
            try {
                max = Math.max(max, Integer.parseInt(key));
            } catch (Exception e) {
                e.printStackTrace();
            }

        int id = max + 1;
        YMLSection section = rewardsDB.loadSection(PATH_REWARDS + "." + id);
        if (author != null)
            section.set(Paths.AUTHOR, author.getName());
        section.set(Paths.TYPE_NAME, type.getKeyID());
        rewards.put(id, generateReward(id, section));
        return rewards.get(id);
    }

    @Override
    public final Require<T> createRequire(RequireType<T> type, OfflinePlayer author) {
        int max = 0;
        for (String key : requiresDB.getKeys(PATH_REQUIRES))
            try {
                max = Math.max(max, Integer.parseInt(key));
            } catch (Exception e) {
                e.printStackTrace();
            }

        int id = max + 1;
        YMLSection section = requiresDB.loadSection(PATH_REQUIRES + "." + id);
        if (author != null)
            section.set(Paths.AUTHOR, author.getName());
        section.set(Paths.TYPE_NAME, type.getKeyID());
        requires.put(id, generateRequire(id, section));
        return requires.get(id);
    }

    @Override
    public final void delete(Quest<T> quest) {
        quests.remove(quest.getID());
        questsDB.set(PATH_QUESTS + "." + quest.getID(), null);
        for (Mission<T> mission : quest.getMissions()) {
            missions.remove(mission.getID());
            missionsDB.set(PATH_MISSIONS + "." + mission.getID(), null);
            for (Task<T> task : mission.getTasks()) {
                tasks.remove(task.getID());
                tasksDB.set(PATH_TASKS + "." + task.getID(), null);
            }
        }
    }

    @Override
    public final void delete(Mission<T> mission) {
        Quest<T> quest = mission.getQuest();
        quest.removeMission(mission);
        TreeSet<Integer> missionsIds = new TreeSet<>(
                questsDB.getIntegerList(PATH_QUESTS + "." + quest.getID() + "." + SUB_PATH_MISSIONS, Collections.emptyList()));
        missionsIds.remove(mission.getID());
        questsDB.set(PATH_QUESTS + "." + quest.getID() + "." + SUB_PATH_MISSIONS,
                missionsIds.isEmpty() ? null : new ArrayList<>(missionsIds));

        missions.remove(mission.getID());
        missionsDB.set(PATH_MISSIONS + "." + mission.getID(), null);
        for (Task<T> task : mission.getTasks()) {
            tasks.remove(task.getID());
            tasksDB.set(PATH_TASKS + "." + task.getID(), null);
        }
    }

    @Override
    public final void delete(Task<T> task) {
        Mission<T> mission = task.getMission();
        mission.removeTask(task);
        TreeSet<Integer> tasksIds = new TreeSet<>(
                missionsDB.getIntegerList(PATH_MISSIONS + "." + mission.getID() + "." + SUB_PATH_TASKS, Collections.emptyList()));
        tasksIds.remove(task.getID());
        missionsDB.set(PATH_MISSIONS + "." + mission.getID() + "." + SUB_PATH_TASKS,
                tasksIds.isEmpty() ? null : new ArrayList<>(tasksIds));

        tasks.remove(task.getID());
        tasksDB.set(PATH_TASKS + "." + task.getID(), null);
    }

    @Override
    public final void delete(Reward<T> reward) {
        // TODO not bruteforce
        for (Quest<T> quest : quests.values()) {
            for (Mission<T> mission : quest.getMissions()) {
                if (mission.removeCompleteReward(reward)) {
                    TreeSet<Integer> rewardsIds = new TreeSet<>(missionsDB.getIntegerList(
                            PATH_MISSIONS + "." + mission.getID() + "." + SUB_PATH_COMPLETE_REWARDS,
                            Collections.emptyList()));
                    rewardsIds.remove(reward.getID());
                    missionsDB.set(PATH_MISSIONS + "." + mission.getID() + "." + SUB_PATH_COMPLETE_REWARDS,
                            rewardsIds.isEmpty() ? null : new ArrayList<>(rewardsIds));
                }
                if (mission.removeFailReward(reward)) {
                    TreeSet<Integer> rewardsIds = new TreeSet<>(missionsDB.getIntegerList(
                            PATH_MISSIONS + "." + mission.getID() + "." + SUB_PATH_FAIL_REWARDS,
                            Collections.emptyList()));
                    rewardsIds.remove(reward.getID());
                    missionsDB.set(PATH_MISSIONS + "." + mission.getID() + "." + SUB_PATH_FAIL_REWARDS,
                            rewardsIds.isEmpty() ? null : new ArrayList<>(rewardsIds));
                }
                if (mission.removeStartReward(reward)) {
                    TreeSet<Integer> rewardsIds = new TreeSet<>(missionsDB.getIntegerList(
                            PATH_MISSIONS + "." + mission.getID() + "." + SUB_PATH_START_REWARDS,
                            Collections.emptyList()));
                    rewardsIds.remove(reward.getID());
                    missionsDB.set(PATH_MISSIONS + "." + mission.getID() + "." + SUB_PATH_START_REWARDS,
                            rewardsIds.isEmpty() ? null : new ArrayList<>(rewardsIds));
                }
                for (Task<T> task : mission.getTasks()) {
                    if (task.removeCompleteReward(reward)) {
                        TreeSet<Integer> rewardsIds = new TreeSet<>(tasksDB.getIntegerList(
                                PATH_TASKS + "." + mission.getID() + "." + SUB_PATH_COMPLETE_REWARDS,
                                Collections.emptyList()));
                        rewardsIds.remove(reward.getID());
                        tasksDB.set(PATH_TASKS + "." + mission.getID() + "." + SUB_PATH_COMPLETE_REWARDS,
                                rewardsIds.isEmpty() ? null : new ArrayList<>(rewardsIds));
                    }
                    if (task.removeProgressReward(reward)) {
                        TreeSet<Integer> rewardsIds = new TreeSet<>(tasksDB.getIntegerList(
                                PATH_TASKS + "." + mission.getID() + "." + SUB_PATH_PROGRESS_REWARDS,
                                Collections.emptyList()));
                        rewardsIds.remove(reward.getID());
                        tasksDB.set(PATH_TASKS + "." + mission.getID() + "." + SUB_PATH_PROGRESS_REWARDS,
                                rewardsIds.isEmpty() ? null : new ArrayList<>(rewardsIds));
                    }
                }
            }
        }
        rewards.remove(reward.getID());
        rewardsDB.set(PATH_REWARDS + "." + reward.getID(), null);
    }

    @Override
    public final void delete(Require<T> require) {
        // TODO not bruteforce
        for (Quest<T> quest : quests.values()) {
            if (quest.removeRequire(require)) {
                TreeSet<Integer> requiresIds = new TreeSet<>(questsDB.getIntegerList(
                        PATH_QUESTS + "." + quest.getID() + "." + SUB_PATH_REQUIRES, Collections.emptyList()));
                requiresIds.remove(require.getID());
                questsDB.set(PATH_QUESTS + "." + quest.getID() + "." + SUB_PATH_REQUIRES,
                        requiresIds.isEmpty() ? null : new ArrayList<>(requiresIds));
            }
            for (Mission<T> mission : quest.getMissions()) {
                if (mission.removeRequire(require)) {
                    TreeSet<Integer> requiresIds = new TreeSet<>(missionsDB.getIntegerList(
                            PATH_MISSIONS + "." + mission.getID() + "." + SUB_PATH_REQUIRES, Collections.emptyList()));
                    requiresIds.remove(require.getID());
                    missionsDB.set(PATH_MISSIONS + "." + mission.getID() + "." + SUB_PATH_REQUIRES,
                            requiresIds.isEmpty() ? null : new ArrayList<>(requiresIds));
                }
            }
        }
        // database.deleteRequire(require);
        requires.remove(require.getID());
        requiresDB.set(PATH_REQUIRES + "." + require.getID(), null);
    }

    @Override
    public final void linkRequire(Require<T> require, Quest<T> quest) {
        quest.addRequire(require);
        TreeSet<Integer> requiresIds = new TreeSet<>(questsDB
                .getIntegerList(PATH_QUESTS + "." + quest.getID() + "." + SUB_PATH_REQUIRES, Collections.emptyList()));
        requiresIds.add(require.getID());
        questsDB.set(PATH_QUESTS + "." + quest.getID() + "." + SUB_PATH_REQUIRES,
                requiresIds.isEmpty() ? null : new ArrayList<>(requiresIds));

    }

    @Override
    public final void unlinkRequire(Require<T> require, Quest<T> quest) {
        quest.removeRequire(require);
        TreeSet<Integer> requiresIds = new TreeSet<>(questsDB
                .getIntegerList(PATH_QUESTS + "." + quest.getID() + "." + SUB_PATH_REQUIRES, Collections.emptyList()));
        requiresIds.remove(require.getID());
        questsDB.set(PATH_QUESTS + "." + quest.getID() + "." + SUB_PATH_REQUIRES,
                requiresIds.isEmpty() ? null : new ArrayList<>(requiresIds));

    }

    @Override
    public final void linkRequire(Require<T> require, Mission<T> mission) {
        mission.addRequire(require);
        TreeSet<Integer> requiresIds = new TreeSet<>(missionsDB.getIntegerList(
                PATH_MISSIONS + "." + mission.getID() + "." + SUB_PATH_REQUIRES, Collections.emptyList()));
        requiresIds.add(require.getID());
        missionsDB.set(PATH_MISSIONS + "." + mission.getID() + "." + SUB_PATH_REQUIRES,
                requiresIds.isEmpty() ? null : new ArrayList<>(requiresIds));
    }

    @Override
    public final void unlinkRequire(Require<T> require, Mission<T> mission) {
        mission.removeRequire(require);
        TreeSet<Integer> requiresIds = new TreeSet<>(missionsDB.getIntegerList(
                PATH_MISSIONS + "." + mission.getID() + "." + SUB_PATH_REQUIRES, Collections.emptyList()));
        requiresIds.remove(require.getID());
        missionsDB.set(PATH_MISSIONS + "." + mission.getID() + "." + SUB_PATH_REQUIRES,
                requiresIds.isEmpty() ? null : new ArrayList<>(requiresIds));
    }

    @Override
    public final void linkCompleteReward(Reward<T> reward, Mission<T> mission) {
        mission.addCompleteReward(reward);
        TreeSet<Integer> requiresIds = new TreeSet<>(missionsDB.getIntegerList(
                PATH_MISSIONS + "." + mission.getID() + "." + SUB_PATH_COMPLETE_REWARDS, Collections.emptyList()));
        requiresIds.add(reward.getID());
        missionsDB.set(PATH_MISSIONS + "." + mission.getID() + "." + SUB_PATH_COMPLETE_REWARDS,
                requiresIds.isEmpty() ? null : new ArrayList<>(requiresIds));
    }

    @Override
    public final void unlinkCompleteReward(Reward<T> reward, Mission<T> mission) {
        mission.removeCompleteReward(reward);
        TreeSet<Integer> requiresIds = new TreeSet<>(missionsDB.getIntegerList(
                PATH_MISSIONS + "." + mission.getID() + "." + SUB_PATH_COMPLETE_REWARDS, Collections.emptyList()));
        requiresIds.remove(reward.getID());
        missionsDB.set(PATH_MISSIONS + "." + mission.getID() + "." + SUB_PATH_COMPLETE_REWARDS,
                requiresIds.isEmpty() ? null : new ArrayList<>(requiresIds));
    }

    @Override
    public final void linkStartReward(Reward<T> reward, Mission<T> mission) {
        mission.addStartReward(reward);
        TreeSet<Integer> requiresIds = new TreeSet<>(missionsDB.getIntegerList(
                PATH_MISSIONS + "." + mission.getID() + "." + SUB_PATH_START_REWARDS, Collections.emptyList()));
        requiresIds.add(reward.getID());
        missionsDB.set(PATH_MISSIONS + "." + mission.getID() + "." + SUB_PATH_START_REWARDS,
                requiresIds.isEmpty() ? null : new ArrayList<>(requiresIds));
    }

    @Override
    public final void unlinkStartReward(Reward<T> reward, Mission<T> mission) {
        mission.removeStartReward(reward);
        TreeSet<Integer> requiresIds = new TreeSet<>(missionsDB.getIntegerList(
                PATH_MISSIONS + "." + mission.getID() + "." + SUB_PATH_START_REWARDS, Collections.emptyList()));
        requiresIds.remove(reward.getID());
        missionsDB.set(PATH_MISSIONS + "." + mission.getID() + "." + SUB_PATH_START_REWARDS,
                requiresIds.isEmpty() ? null : new ArrayList<>(requiresIds));
    }

    @Override
    public final void linkFailReward(Reward<T> reward, Mission<T> mission) {
        mission.addFailReward(reward);
        TreeSet<Integer> requiresIds = new TreeSet<>(missionsDB.getIntegerList(
                PATH_MISSIONS + "." + mission.getID() + "." + SUB_PATH_FAIL_REWARDS, Collections.emptyList()));
        requiresIds.add(reward.getID());
        missionsDB.set(PATH_MISSIONS + "." + mission.getID() + "." + SUB_PATH_FAIL_REWARDS,
                requiresIds.isEmpty() ? null : new ArrayList<>(requiresIds));
    }

    @Override
    public final void unlinkFailReward(Reward<T> reward, Mission<T> mission) {
        mission.removeFailReward(reward);
        TreeSet<Integer> requiresIds = new TreeSet<>(missionsDB.getIntegerList(
                PATH_MISSIONS + "." + mission.getID() + "." + SUB_PATH_FAIL_REWARDS, Collections.emptyList()));
        requiresIds.remove(reward.getID());
        missionsDB.set(PATH_MISSIONS + "." + mission.getID() + "." + SUB_PATH_FAIL_REWARDS,
                requiresIds.isEmpty() ? null : new ArrayList<>(requiresIds));
    }

    @Override
    public final void linkCompleteReward(Reward<T> reward, Task<T> task) {
        task.addCompleteReward(reward);
        TreeSet<Integer> requiresIds = new TreeSet<>(tasksDB.getIntegerList(
                PATH_TASKS + "." + task.getID() + "." + SUB_PATH_COMPLETE_REWARDS, Collections.emptyList()));
        requiresIds.add(reward.getID());
        tasksDB.set(PATH_TASKS + "." + task.getID() + "." + SUB_PATH_COMPLETE_REWARDS,
                requiresIds.isEmpty() ? null : new ArrayList<>(requiresIds));
    }

    @Override
    public final void unlinkCompleteReward(Reward<T> reward, Task<T> task) {
        task.removeCompleteReward(reward);
        TreeSet<Integer> requiresIds = new TreeSet<>(tasksDB.getIntegerList(
                PATH_TASKS + "." + task.getID() + "." + SUB_PATH_COMPLETE_REWARDS, Collections.emptyList()));
        requiresIds.remove(reward.getID());
        tasksDB.set(PATH_TASKS + "." + task.getID() + "." + SUB_PATH_COMPLETE_REWARDS,
                requiresIds.isEmpty() ? null : new ArrayList<>(requiresIds));
    }

    @Override
    public final void linkProgressReward(Reward<T> reward, Task<T> task) {
        task.addProgressReward(reward);
        TreeSet<Integer> requiresIds = new TreeSet<>(tasksDB.getIntegerList(
                PATH_TASKS + "." + task.getID() + "." + SUB_PATH_PROGRESS_REWARDS, Collections.emptyList()));
        requiresIds.add(reward.getID());
        tasksDB.set(PATH_TASKS + "." + task.getID() + "." + SUB_PATH_PROGRESS_REWARDS,
                requiresIds.isEmpty() ? null : new ArrayList<>(requiresIds));
    }

    @Override
    public final void unlinkProgressReward(Reward<T> reward, Task<T> task) {
        task.removeProgressReward(reward);
        TreeSet<Integer> requiresIds = new TreeSet<>(tasksDB.getIntegerList(
                PATH_TASKS + "." + task.getID() + "." + SUB_PATH_PROGRESS_REWARDS, Collections.emptyList()));
        requiresIds.remove(reward.getID());
        tasksDB.set(PATH_TASKS + "." + task.getID() + "." + SUB_PATH_PROGRESS_REWARDS,
                requiresIds.isEmpty() ? null : new ArrayList<>(requiresIds));
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final RequireProvider<T> getRequireProvider() {
        return requireProvider;
    }

    @Override
    public final RewardProvider<T> getRewardProvider() {
        return rewardProvider;
    }

    @Override
    public final TaskProvider<T> getTaskProvider() {
        return taskProvider;
    }

    @Override
    public final File getFolder() {
        return folder;
    }

    /*
     * @Override public final ConfigFile getConfig() { return managerConfig; }
     */

    @Override
    public final BossBarManager<T> getBossBarManager() {
        return bossBarManager;
    }

    @Override
    public final int getPriority() {
        return priority;
    }

    @Override
    public final Quest<T> getQuest(int id) {
        return quests.get(id);
    }

    @Override
    public final Mission<T> getMission(int id) {
        return missions.get(id);
    }

    @Override
    public final Task<T> getTask(int id) {
        return tasks.get(id);
    }

    @Override
    public final Reward<T> getReward(int id) {
        return rewards.get(id);
    }

    @Override
    public final Require<T> getRequire(int id) {
        return requires.get(id);
    }

    @Override
    public Permission getEditorPermission() {
        return this.editorPermission;
    }

    private Quest<T> loadQuest(int questId) {
        return generateQuest(questId, questsDB.loadSection(Paths.DATABASE_QUESTS + "." + questId));
    }

    private Mission<T> loadMission(int missionId, Quest<T> quest) {
        return generateMission(missionId, quest, missionsDB.loadSection(Paths.DATABASE_MISSIONS + "." + missionId));
    }

    private Task<T> loadTask(int taskId, Mission<T> mission) {
        return generateTask(taskId, mission, tasksDB.loadSection(Paths.DATABASE_TASKS + "." + taskId));
    }

    private Reward<T> loadReward(int rewardId) {
        return generateReward(rewardId, rewardsDB.loadSection(Paths.DATABASE_REWARDS + "." + rewardId));
    }

    private Require<T> loadRequire(int requireId) {
        return generateRequire(requireId, requiresDB.loadSection(Paths.DATABASE_REQUIRES + "." + requireId));
    }

    protected class EditorGui extends PagedMapGui {
        public EditorGui(Player player, Gui previousHolder) {
            super("&9Quest Manager: &6" + getName(), 6, player, previousHolder);
            this.putButton(18, new QuestCreate());
            this.putButton(19, new QuestSelector());
            this.putButton(20, new QuestDelete());
            this.putButton(4, new GuiElementDescriptionButton(this, AQuestManager.this));
        }

        private class QuestSelector extends GuiElementSelectorButton<Quest<T>> {

            public QuestSelector() {
                super("&9Select a Quest", new ItemBuilder(Material.KNOWLEDGE_BOOK).setGuiProperty().build(),
                        EditorGui.this, false, true, false);
            }

            @Override
            public List<String> getButtonDescription() {
                return Arrays.asList("&6Click to select a Quest", "", "&6each quest may contain missions");
            }

            @Override
            public Collection<Quest<T>> getValues() {
                return getQuests();
            }

            @Override
            public void onElementSelectRequest(Quest<T> element, Player p) {
                p.openInventory(element.getEditorGui(p, EditorGui.this).getInventory());
            }
        }

        private class QuestDelete extends GuiElementSelectorButton<Quest<T>> {

            public QuestDelete() {
                super("&cDelete a Quest", new ItemBuilder(Material.RED_DYE).setGuiProperty().build(), EditorGui.this,
                        false, true, true);
            }

            @Override
            public List<String> getButtonDescription() {
                return Arrays.asList("&cClick to delete a Quest", "", "&cAlso delete his Missions and Tasks",
                        "&cDelete can't be undone");
            }

            @Override
            public Collection<Quest<T>> getValues() {
                return getQuests();
            }

            @Override
            public void onElementSelectRequest(Quest<T> element, Player p) {
                delete(element);
                EditorGui.this.updateInventory();
                p.openInventory(EditorGui.this.getInventory());
            }
        }

        private class QuestCreate extends AButton {

            private final ItemStack item = new ItemBuilder(Material.YELLOW_DYE).setGuiProperty().build();

            public QuestCreate() {
                super(EditorGui.this);
            }

            @Override
            public ItemStack getItem() {
                Utils.updateDescription(item, List.of("&6Click to create a new Quest"),
                        getGui().getTargetPlayer(), true);
                return item;
            }

            @Override
            public boolean update() {
                return true;
            }

            @Override
            public void onClick(Player clicker, ClickType click) {
                Quest<T> quest = createQuest(clicker);
                clicker.openInventory(quest.getEditorGui(clicker, EditorGui.this).getInventory());
            }

        }
    }

}
