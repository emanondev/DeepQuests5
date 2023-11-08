package emanondev.deepquests.interfaces;

import emanondev.core.YMLSection;
import org.jetbrains.annotations.NotNull;

public interface TaskProvider<T extends User<T>> extends QuestComponentTypeProvider<T, Task<T>, TaskType<T>> {

    @NotNull default TaskType<T> getType(Task<T> task) {
        return getType(task.getTypeName());
    }

    @NotNull Task<T> getInstance(int id, Mission<T> mission, YMLSection nav);
}
