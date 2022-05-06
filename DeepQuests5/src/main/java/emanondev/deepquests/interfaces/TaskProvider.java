package emanondev.deepquests.interfaces;

import emanondev.core.YMLSection;

public interface TaskProvider<T extends User<T>> extends QuestComponentTypeProvider<T, Task<T>, TaskType<T>> {

    default TaskType<T> getType(Task<T> task) {
        return getType(task.getTypeName());
    }

    Task<T> getInstance(int id, Mission<T> mission, YMLSection nav);
}