package emanondev.deepquests.utils;

public enum DisplayState {
    LOCKED("When Requires aren't satisfied"),
    UNSTARTED("When not started yet"),
    ONPROGRESS("When ongoing"),
    COOLDOWN("When waiting cooldown"),
    COMPLETED("When completed"),
    FAILED("When failed");
    private final String desc;

    private DisplayState(String desc) {
        this.desc = desc;
    }

    public String getDescription() {
        return desc;
    }
}
