package emanondev.deepquests.interfaces;

import emanondev.core.YMLSection;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public interface TaskType<T extends User<T>> extends QuestComponentType<T, Task<T>>, Listener {


    default String getDefaultPhaseDescription(@NotNull Task.Phase phase, @NotNull Task<T> task) {
        return switch (phase) {
            case COMPLETE -> getDefaultCompleteDescription(task);
            case PROGRESS -> getDefaultProgressDescription(task);
            case UNSTARTED -> getDefaultUnstartedDescription(task);
            default -> throw new IllegalStateException();
        };
    }

    /**
     * @see #getDefaultPhaseDescription(Task.Phase, Task)
     */
    @Deprecated
    String getDefaultUnstartedDescription(@NotNull Task<T> task);

    /**
     * @see #getDefaultPhaseDescription(Task.Phase, Task)
     */
    @Deprecated
    String getDefaultProgressDescription(@NotNull Task<T> task);

    /**
     * @see #getDefaultPhaseDescription(Task.Phase, Task)
     */
    @Deprecated
    String getDefaultCompleteDescription(@NotNull Task<T> task);

    @NotNull Task<T> getInstance(int id, @NotNull Mission<T> mission, YMLSection nav);

    default @NotNull TaskProvider<T> getProvider() {
        return getManager().getTaskProvider();
    }
}
