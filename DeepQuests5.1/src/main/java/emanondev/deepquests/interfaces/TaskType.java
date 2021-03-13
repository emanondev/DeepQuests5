package emanondev.deepquests.interfaces;

import org.bukkit.event.Listener;

import emanondev.core.YMLSection;

public interface TaskType<T extends User<T>> extends QuestComponentType<T,Task<T>>,Listener {


	public default String getDefaultPhaseDescription(Task.Phase phase,Task<T> task) {
		switch (phase) {
		case COMPLETE:
			return getDefaultCompleteDescription(task);
		case PROGRESS:
			return getDefaultProgressDescription(task);
		case UNSTARTED:
			return getDefaultUnstartedDescription(task);
		default:
			throw new IllegalStateException();
		}
	}
	@Deprecated
	public String getDefaultUnstartedDescription(Task<T> task);

	@Deprecated
	public String getDefaultProgressDescription(Task<T> task);

	@Deprecated
	public String getDefaultCompleteDescription(Task<T> task);
	
	public Task<T> getInstance(int id, Mission<T> mission, YMLSection nav);
	
	public default TaskProvider<T> getProvider() {
		return getManager().getTaskProvider();
	}
}
