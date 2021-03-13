package emanondev.deepquests;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import emanondev.deepquests.utils.DisplayState;

public interface PlayerInfo {
	
	
	public ProgressBarType getProgressBarType();
	public void setProgressBarType(ProgressBarType type);

	public enum ProgressBarType {
		BOSSBAR,
		NONE,
		ACTIONBAR
	}
	public enum ProgressBarStyle {
		NUMERIC,
		BAR_10,
		BAR_20
	}
	
	
	public Player getPlayer();
	public OfflinePlayer getOfflinePlayer();

	public boolean canSeeQuestState(DisplayState state);

	public boolean canSeeMissionState(DisplayState state);

	public void toggleCanSeeQuestState(DisplayState state);

	public void toggleCanSeeMissionState(DisplayState state);
	public ProgressBarStyle getProgressBarStyle();
	public void setProgressBarStyle(ProgressBarStyle style);
	
}
