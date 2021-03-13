package emanondev.deepquests.implementations;

import java.util.Date;

import emanondev.core.YMLSection;
import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.MissionData;
import emanondev.deepquests.interfaces.Task;
import emanondev.deepquests.interfaces.User;

public class AMissionData<T extends User<T>> extends AUserComplexData<T> implements MissionData<T> {

	private Mission<T> mission;

	public AMissionData(T user, Mission<T> mission, YMLSection section) {
		super(user, section);
		if (mission == null)
			throw new NullPointerException();
		this.mission = mission;
	}

	public Mission<T> getMission() {
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
