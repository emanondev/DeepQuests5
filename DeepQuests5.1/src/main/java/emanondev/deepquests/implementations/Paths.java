package emanondev.deepquests.implementations;

import java.util.regex.Pattern;

import emanondev.deepquests.interfaces.Task;

public class Paths {
	public static final Pattern ALPHANUMERIC = Pattern.compile("[a-zA-Z_0-9]*");
	//public static final String ID = "unique-key";
	public static final String USERDATA_LAST_STARTED = "last-started";
	public static final String USERDATA_LAST_COMPLETED = "last-completed";
	public static final String USERDATA_COMPLETED_TIMES = "completed-times";
	public static final String USERDATA_FAILED_TIMES = "failed-times";
	public static final String USERDATA_LAST_FAILED = "last-failed";
	public static final String USERDATA_POINTS = "points";
	public static final String USERDATA_MISSION_DATA_LIST = "missions-data";
	public static final String USERDATA_QUEST_DATA_LIST = "quests-data";
	public static final String USERDATA_TASK_DATA_LIST = "tasks-data";
	public static final String USERDATA_TASK_PROGRESS = "progress";
	
	public static final String PLAYERDATA_SEE_QUEST = "can-see-quest";
	public static final String PLAYERDATA_SEE_MISSION = "can-see-mission";
	
	
	public static final String DISPLAY_NAME = "display-name";
	public static final String PRIORITY = "priority";
	
	public static final String WORLDS_LIST = "worlds.list";
	public static final String WORLDS_IS_WHITELIST = "worlds.is-whitelist";
	
	public static final String REPEATABLE = "is-repeatable";
	public static final String COOLDOWN_MINUTES = "cooldown-minutes";
	
	public static final String TASK_PROGRESS_CHANCE = "progress-chance";
	public static final String TASK_MAX_PROGRESS = "max-progress";
	public static final String TASK_BAR_STYLE = "bossbar-style";
	public static final String TASK_BAR_COLOR = "bossbar-color";
	@Deprecated
	public static final String TASK_PROGRESS_DESCRIPTION = "progress-description";
	@Deprecated
	public static final String TASK_UNSTARTED_DESCRIPTION = "unstarted-description";
	public static final String TASK_SHOW_BOSSBAR = "show-bossbar";
	
	public static final String TYPE_NAME = "type-name";
	
	public static final String QUEST_IS_DEVELOPED = "is-developed";
	
	public static final String DATABASE_TASKS = "tasks";
	public static final String DATABASE_REWARDS = "rewards";
	public static final String DATABASE_REQUIRES = "requires";
	public static final String DATABASE_MISSIONS = "missions";
	public static final String DATABASE_QUESTS = "quests";
	
	public static final String USERDATA_BASE = "userdata";

	public static final String DATA_ENTITYTYPE_LIST = "entitytype-list";
	public static final String DATA_ENTITYTYPE_IS_WHITELIST = "entitytype-is-whitelist";
	public static final String DATA_ENTITY_NAME = "entity-name";
	public static final String DATA_IGNORE_NPC = "ignore-npc";
	public static final String DATA_ENTITY_SPAWNREASON_LIST = "spawn-reason-list";
	public static final String DATA_ENTITY_SPAWNREASON_IS_WHITELIST = "spawn-reason-is-whitelist";
	
	public static final String DATA_CHECK_VIRGIN = "check-virgin-block";
	
	public static final String DATA_TARGET_QUEST_KEY = "target-quest";
	public static final String DATA_TARGET_MISSION_KEY = "target-mission";
	
	//public static final String DATA_EXPERIENCE = "experience";
	
	public static final String DATA_REGION_LIST = "region-list";
	public static final String DATA_REGION_LIST_IS_WHITELIST = "region-is-whitelist";
	
	public static final String DATA_LEVEL = "level";
	
	public static final String DATA_DENY_ITEM_DROPS = "remove-item-drops";
	public static final String DATA_DENY_EXP_DROPS = "remove-exp-drops";
	
	public static final String DATA_JOB_TYPE = "job-type";
	
	public static final String DATA_MCMMO_SKILLTYPE = "mcmmo-skilltype";
	
	public static final String DATA_SOUND_VOLUME = "sound-volume";
	public static final String DATA_SOUND_PITCH = "sound-pitch";
	public static final String DATA_SOUND_NAME = "sound-name";
	
	public static final String DATA_PERMISSION = "permission";
	
	public static final String DATA_COMMAND = "command";
	
	public static final String DATA_LOCATION_X = "location-x";
	public static final String DATA_LOCATION_Y = "location-y";
	public static final String DATA_LOCATION_Z = "location-z";
	
	public static final String DATA_LOCATION_WORLD = "location-world-name";
	
	public static final String DATA_NPC_ID_LIST_IS_WHITELIST = "npc-id-list-is-whitelist";
	public static final String DATA_NPC_ID_LIST = "npc-id-list";
	

	public static final String DATA_DISPLAY_STATES = "display-states";
	
	public static final String DATA_BLOCK_TYPE_LIST = "blocktype-list";
	public static final String DATA_BLOCK_TYPE_IS_WHITELIST = "blocktype-list-is-whitelist";
	
	public static final String BOSSBAR_MANAGER_DURATION = "ticks-duration";
	public static final String BOSSBAR_MANAGER_DEFAULT_COLOR = "default-color";
	public static final String BOSSBAR_MANAGER_DEFAULT_STYLE = "default-style";
	public static final String BOSSBAR_MANAGER_DEFAULT_SHOWBOSSBAR = "default-show-bossbar";
	
	public static final String TASK_INFO_BLOCKDATA = "task-block-type-info";
	public static final String TASK_INFO_VIRGINBLOCKDATA = "task-block-virgin-info";
	public static final String TASK_INFO_DROPDATA = "task-drops-info";
	public static final String TASK_INFO_ENTITYDATA = "task-entity-info";
	public static final String TOOL_INFO_ITEM = "tool-base-item";
	public static final String TOOL_INFO_CHECK_MATERIAL = "tool-check-material";
	public static final String TOOL_INFO_CHECK_ENCHANT = "tool-check-enchant";
	public static final String TOOL_INFO_CHECK_AMOUNT = "tool-check-amount";
	public static final String TOOL_INFO_CHECK_LORE = "tool-check-lore";
	public static final String TOOL_INFO_CHECK_DISPLAY_NAME = "tool-check-display-name";
	public static final String TOOL_INFO_CHECK_DAMAGE = "tool-check-damage";
	public static final String TOOL_INFO_CHECK_FLAGS = "tool-check-flags";
	public static final String TOOL_INFO_CHECK_UNBREAKABLE = "tool-check-unbreakable";
	public static final String TOOL_INFO_CHECK_ALL = "tool-check-all";
	public static final String TOOL_INFO_CHECK_ATTRIBUTES = "tool-check-attributes";
	public static final String TOOL_INFO_DEFAULT_ENCHANT_CHECK = "tool-default-enchant-check";
	public static final String TOOL_INFO_ENCHANT_CHECK_TYPE = "tool-enchants-checks";
	public static final String TOOL_INFO_USE_PLACE_HOLDER = "tool-use-placeholder";
	public static final String TOOL_ENABLE_CHECK = "tool-checks-enabled";
	public static final String TASK_INFO_TOOLDATA = "task-tool-data-info";
	public static final String TASK_INFO_TOOLDATA_TARGET = "task-tool-target-data-info";
	public static final String TASK_INFO_REGIONSDATA = "task-regions-info";
	public static final String TASK_INFO_LOCATIONDATA = "task-location-info";
	
	public static final String MYTHICMOBDATA_MAX_LV = "mm-max-lv";
	public static final String MYTHICMOBDATA_INTERNAL_NAMES = "mm-internal-names";
	public static final String MYTHICMOBDATA_MIN_LV = "mm-min-lv";
	public static final String MYTHICMOBDATA_INTERNAL_NAMES_IS_WHITELIST = "mm-internal-names-is-whitelist";
	public static final String MYTHICMOBDATA_CHECK_LV = "mm-check-lv";
	
	public static final String TASK_INFO_MYTHICMOBSDATA = "task-mythicmobs-info";
	public static final String TASK_INFO_NPCDATA = "task-npc-info";
	public static final String TASK_INFO_ITEM = "task-itemstack-info";
	public static final String ITEMSTACK_INFO = "itemstack";
	
	public static final String REQUIRE_INFO_JOBDATA = "require-job-info";
	public static final String REQUIRE_INFO_LEVELDATA = "require-level-info";
	public static final String REQUIRE_INFO_MCMMODATA = "require-mcmmo-info";
	public static final String REQUIRE_INFO_PERMISSIONDATA = "require-permission-info";
	public static final String REQUIRE_INFO_TARGETMISSION = "require-target-mission";
	public static final String REQUIRE_INFO_TARGETQUEST = "require-target-quest";
	public static final String REQUIRE_INFO_DISPLAYSTATE = "require-display-state";
	public static final String DATA_ENUM = "enum_data";
	public static final String DATA_PLAYTIMERANGE = "playtimerange";
	
	public static final String DATA_AMOUNT = "data-amount";
	/*public static final String DATA_MAX_AMOUNT = "data-max-amount";
	public static final String DATA_MIN_AMOUNT = "data-min-amount";
	public static final String DATA_DEFAULT_AMOUNT = "data-default-amount";*/
	
	public static final String REQUIRE_INFO_AMOUNT = "require-amount-info";
	
	public static final String REWARD_INFO_COMMAND = "reward-command-info";
	public static final String REWARD_INFO_ITEMSTACK = "reward-itemstack-info";
	public static final String REWARD_INFO_AMOUNT = "reward-amount-info";
	//public static final String REWARD_INFO_EXPERIENCE = "reward-experience-info";
	public static final String REWARD_INFO_JOB = "reward-job-info";
	public static final String REWARD_INFO_TARGET_MISSION = "reward-target-mission";
	public static final String REWARD_INFO_TARGET_QUEST = "reward-target-quest";
	public static final String REWARD_INFO_SOUND = "reward-sound";
	public static final String QUESTCOMPONENT_DISPLAY_INFO = "display-info";
	public static final String IS_HIDDEN = "is-hidden";
	public static final String DATA_SKILLAPI_CLASSES = "data-rpgclasses";
	public static final String DATA_SKILLAPI_GROUPS = "data-rpggroups";
	public static final String DATA_SKILLAPI_CLASSES_IS_WHITELIST = "data-rpgclasses-is-whitelist";
	public static final String DATA_SKILLAPI_GROUPS_IS_WHITELIST = "data-rpggroups-is-whitelist";
	public static final String DEFAULT_MISSION_LIMIT = "default-active-mission-limit-at-once";
	//public static final String MISSION_PAUSABLE = "mission-is-pausable";
	@Deprecated
	public static final String MISSION_COMPLETE_MESSAGE = "complete-message";
	@Deprecated
	public static final String MISSION_START_MESSAGE = "start-message";
	@Deprecated
	public static final String MISSION_FAIL_MESSAGE = "fail-message";
	@Deprecated
	public static final String MISSION_COMPLETE_MESSAGE_IS_DEFAULT = "complete-message-is-default";
	@Deprecated
	public static final String MISSION_START_MESSAGE_IS_DEFAULT = "start-message-is-default";
	@Deprecated
	public static final String MISSION_FAIL_MESSAGE_IS_DEFAULT = "fail-message-is-default";
	public static final String MANAGER_DEFAULT_MISSION_START_MESSAGE = "default-mission-start-message";
	public static final String MANAGER_DEFAULT_MISSION_COMPLETE_MESSAGE = "default-mission-complete-message";
	public static final String MANAGER_DEFAULT_MISSION_FAIL_MESSAGE = "default-mission-fail-message";
	public static final String MANAGER_DEFAULT_MISSION_PAUSE_MESSAGE = "default-mission-pause-message";
	public static final String MANAGER_DEFAULT_MISSION_UNPAUSE_MESSAGE = "default-mission-unpause-message";
	public static final String REWARD_INFO_MCMMO = "reward-mcmmo-info";
	public static final String REQUIRE_INFO_SKILLAPI = "require-skillapi-info";
	public static final String REWARD_FEEDBACK = "reward-feedback";
	public static final String REWARD_IS_FEEDBACK_DEFAULT = "reward-feedback-is-default";
	@Deprecated public static final String TASK_COMPLETE_DESCRIPTION = "complete-description";
	public static final String QUESTITEM_ID = "item-id";
	public static final String REWARD_INFO_QUESTITEM = "reward-questitem-info";
	public static final String AUTHOR = "author";
	public static final class Command {
		public static final String PLAYERS_ONLY = "command.players-only";

	}

	public final static String TASK_PHASE_DESCRIPTION(Task.Phase phase) {
		return phase.name().toLowerCase()+"-description";
	}
}
