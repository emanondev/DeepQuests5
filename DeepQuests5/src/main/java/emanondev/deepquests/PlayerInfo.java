package emanondev.deepquests;

import emanondev.deepquests.utils.DisplayState;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface PlayerInfo {


    ProgressBarType getProgressBarType();

    void setProgressBarType(ProgressBarType type);

    enum ProgressBarType {
        BOSSBAR,
        NONE,
        ACTIONBAR
    }

    enum ProgressBarStyle {
        NUMERIC,
        BAR_10,
        BAR_20
    }


    Player getPlayer();

    OfflinePlayer getOfflinePlayer();

    boolean canSeeQuestState(DisplayState state);

    boolean canSeeMissionState(DisplayState state);

    void toggleCanSeeQuestState(DisplayState state);

    void toggleCanSeeMissionState(DisplayState state);

    ProgressBarStyle getProgressBarStyle();

    void setProgressBarStyle(ProgressBarStyle style);

}
