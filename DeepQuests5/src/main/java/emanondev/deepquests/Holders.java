package emanondev.deepquests;

public class Holders {

    public static final String DISPLAY_NAME = holder("name");
    public static final String QUEST_DISPLAY_NAME = holder("quest-name");
    public static final String TASK_MAX_PROGRESS = holder("max-progress");
    public static final String TASK_STATUS = holder("task-status");
    public static final String TASK_CURRENT_PROGRESS = holder("current-progress");
    public static final String TASK_UNSTARTED_DESCRIPTION = holder("unstarted-description");
    public static final String TASK_PROGRESS_DESCRIPTION = holder("progress-description");
    public static final String TASK_COMPLETE_DESCRIPTION = holder("complete-description");
    /**
     * used for colors, completed = &a uncompleted = &c
     */
    public static final String REQUIRE_IS_COMPLETED = holder("is-completed");
    public static final String REQUIRE_DESCRIPTION = holder("description");
    public static final String COOLDOWN_LEFT = holder("cooldown-left");
    public static final String QUEST_MISSIONS_AMOUNT = holder("missions-amount");
    public static final String QUEST_COMPLETED_MISSIONS = holder("completed-missions-amount");
    public static final String QUEST_FAILED_MISSIONS = holder("failed-missions-amount");
    public static final String QUEST_UNSTARTED_MISSIONS = holder("unstarted-missions-amount");
    public static final String QUEST_ONPROGRESS_MISSIONS = holder("onprogress-missions-amount");
    public static final String QUEST_COOLDOWN_MISSIONS = holder("cooldown-missions-amount");
    public static final String QUEST_LOCKED_MISSIONS = holder("locked-missions-amount");


    public static final String PERMISSION = holder("permission");


    private static String holder(String name) {
        return "{" + name + "}";
    }
}
