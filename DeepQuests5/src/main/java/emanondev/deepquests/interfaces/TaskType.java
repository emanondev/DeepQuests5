package emanondev.deepquests.interfaces;

import emanondev.core.YMLSection;
import org.bukkit.event.Listener;

public interface TaskType<T extends User<T>> extends QuestComponentType<T, Task<T>>, Listener {


    default String getDefaultPhaseDescription(Task.Phase phase, Task<T> task) {
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
    String getDefaultUnstartedDescription(Task<T> task);

    @Deprecated
    String getDefaultProgressDescription(Task<T> task);

    @Deprecated
    String getDefaultCompleteDescription(Task<T> task);

    Task<T> getInstance(int id, Mission<T> mission, YMLSection nav);

    default TaskProvider<T> getProvider() {
        return getManager().getTaskProvider();
    }
}
