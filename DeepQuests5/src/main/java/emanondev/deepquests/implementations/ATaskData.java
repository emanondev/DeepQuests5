package emanondev.deepquests.implementations;

import emanondev.core.YMLSection;
import emanondev.deepquests.interfaces.Task;
import emanondev.deepquests.interfaces.TaskData;
import emanondev.deepquests.interfaces.User;
import org.jetbrains.annotations.NotNull;

public class ATaskData<T extends User<T>> extends AUserData<T> implements TaskData<T> {

    private final Task<T> task;

    private int progress;

    public ATaskData(@NotNull T user, @NotNull Task<T> task, @NotNull YMLSection section) {
        super(user, section);
        this.task = task;
        progress = Math.max(0, section.getInteger(Paths.USERDATA_TASK_PROGRESS, 0));
    }

    @Override
    public void reset() {
        setProgress(0);
    }

    @Override
    public void erase() {
        getUser().eraseTaskData(getTask());
    }

    public final boolean isCompleted() {
        return getTask().getMaxProgress() <= progress;
    }

    public final int getProgress() {
        return progress;
    }

    public final void setProgress(int amount) {
        progress = Math.min(Math.max(0, amount), getTask().getMaxProgress());
        if (progress == 0)
            getConfig().set(Paths.USERDATA_TASK_PROGRESS, null);
        else
            getConfig().set(Paths.USERDATA_TASK_PROGRESS, progress);
    }

    public final @NotNull Task<T> getTask() {
        return task;
    }
}
