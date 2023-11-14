package emanondev.deepquests.interfaces;

import org.jetbrains.annotations.NotNull;

public interface TaskData<T extends User<T>> extends UserData<T> {

    @NotNull Task<T> getTask();

    boolean isCompleted();

    int getProgress();

    default int addProgress(int amount) {
        int old = getProgress();
        setProgress(old + amount);
        return getProgress() - old;
    }

    void setProgress(int amount);

}
