package emanondev.deepquests.utils;

import emanondev.deepquests.Translations;

public enum Time {
    WEEK(604800),
    DAY(86400),
    HOUR(3600),
    MINUTE(60),
    SECOND(1);
    public final long seconds;

    private Time(long seconds) {
        this.seconds = seconds;
    }

    public String getSingleName() {
        return Translations.translateSingle(this);
    }

    public String getMultipleName() {
        return Translations.translateSingle(this);
    }
}