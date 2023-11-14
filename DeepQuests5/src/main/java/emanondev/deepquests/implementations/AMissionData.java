package emanondev.deepquests.implementations;

import emanondev.core.YMLSection;
import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.MissionData;
import emanondev.deepquests.interfaces.Task;
import emanondev.deepquests.interfaces.User;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class AMissionData<T extends User<T>> extends AUserComplexData<T> implements MissionData<T> {

    private final Mission<T> mission;

    public AMissionData(@NotNull T user, @NotNull Mission<T> mission, @NotNull YMLSection section) {
        super(user, section);
        this.mission = mission;
    }

    public @NotNull Mission<T> getMission() {
        return mission;
    }

    public long getCooldownTimeLeft() {
        return Math.max(0,
                Math.max(getLastCompleted(), getLastFailed()) + mission.getCooldownTime() - new Date().getTime());
    }

    public boolean isOnCooldown() {
        return mission.isRepeatable() && getCooldownTimeLeft() > 0;
    }

    @Override
    public void start() {
        reset();
        super.start();
    }

    public void reset() {
        super.reset();
        for (Task<T> task : getMission().getTasks())
            getUser().getTaskData(task).reset();
    }

    public void complete() {
        super.complete();
        for (Task<T> task : getMission().getTasks())
            getUser().getTaskData(task).reset();
    }

    @Override
    public void erase() {
        for (Task<T> task : getMission().getTasks())
            getUser().getTaskData(task).erase();
        getUser().eraseMissionData(getMission());
    }

}
