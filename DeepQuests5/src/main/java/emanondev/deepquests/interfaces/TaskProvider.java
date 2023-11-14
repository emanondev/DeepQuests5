package emanondev.deepquests.interfaces;

import emanondev.core.YMLSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TaskProvider<T extends User<T>> extends QuestComponentTypeProvider<T, Task<T>, TaskType<T>> {

    @Nullable default TaskType<T> getType(Task<T> task) {
        return getType(task.getTypeName());
    }

    @NotNull Task<T> getInstance(int id, @NotNull Mission<T> mission, @NotNull YMLSection nav);
}
