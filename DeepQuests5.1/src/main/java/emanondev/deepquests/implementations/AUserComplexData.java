package emanondev.deepquests.implementations;

import java.util.*;

import emanondev.core.YMLSection;
import emanondev.deepquests.interfaces.ComplexData;
import emanondev.deepquests.interfaces.User;

public abstract class AUserComplexData<T extends User<T>> extends AUserData<T> implements ComplexData<T> {

	public AUserComplexData(T user, YMLSection section) {
		super(user, section);
		lastStarted = Math.max(0, getConfig().getLong(Paths.USERDATA_LAST_STARTED, 0L));
		lastCompleted = Math.max(0, getConfig().getLong(Paths.USERDATA_LAST_COMPLETED, 0L));
		completedTimes = Math.max(0, getConfig().getInteger(Paths.USERDATA_COMPLETED_TIMES, 0));
		lastFailed = Math.max(0, getConfig().getLong(Paths.USERDATA_FAILED_TIMES, 0L));
		failedTimes = Math.max(0, getConfig().getInteger(Paths.USERDATA_LAST_FAILED, 0));
	}

	private long lastStarted;

	private long lastCompleted;
	private int completedTimes;

	private long lastFailed;
	private int failedTimes;

	@Override
	public boolean isFailed() {
		if (lastFailed > lastCompleted && lastFailed > lastStarted)
			return true;
		return false;
	}

	@Override
	public long getLastStarted() {
		return lastStarted;
	}

	@Override
	public long getLastCompleted() {
		return lastCompleted;
	}

	@Override
	public long getLastFailed() {
		return lastFailed;
	}

	@Override
	public boolean hasStartedAtLeastOnce() {
		return lastStarted > 0;
	}

	@Override
	public boolean hasCompletedAtLeastOnce() {
		return lastCompleted > 0;
	}

	@Override
	public boolean hasFailedAtLeastOnce() {
		return lastFailed > 0;
	}

	@Override
	public int successfullyCompletedTimes() {
		return completedTimes;
	}

	@Override
	public int failedTimes() {
		return failedTimes;
	}

	@Override
	public boolean isStarted() {
		if (lastStarted >= lastCompleted && lastStarted >= lastFailed && lastStarted > 0)
			return true;
		return false;
	}

	@Override
	public boolean isCompleted() {
		if (lastCompleted > lastStarted && lastCompleted > lastFailed)
			return true;
		return false;
	}

	@Override
	public void complete() {
		completedTimes++;
		lastCompleted = new Date().getTime();
		getConfig().set(Paths.USERDATA_LAST_COMPLETED, lastCompleted);
		getConfig().set(Paths.USERDATA_COMPLETED_TIMES, completedTimes);
	}

	@Override
	public void fail() {
		failedTimes++;
		lastFailed = new Date().getTime();
		getConfig().set(Paths.USERDATA_FAILED_TIMES, failedTimes);
		getConfig().set(Paths.USERDATA_LAST_FAILED, lastFailed);
	}

	@Override
	public void start() {
		lastStarted = new Date().getTime();
		getConfig().set(Paths.USERDATA_LAST_STARTED, lastStarted);
	}

	public void reset() {
		lastCompleted = 0;
		lastFailed = 0;
		lastStarted = 0;
		getConfig().set(Paths.USERDATA_LAST_STARTED, null);
		getConfig().set(Paths.USERDATA_LAST_COMPLETED, null);
		getConfig().set(Paths.USERDATA_LAST_FAILED, null);
	}

	public abstract long getCooldownTimeLeft();

	public abstract boolean isOnCooldown();

	public void setLastCompleted(long time) {
		this.lastCompleted = time;
		if (time == 0)
			getConfig().set(Paths.USERDATA_LAST_COMPLETED, null);
		else
			getConfig().set(Paths.USERDATA_LAST_COMPLETED, lastCompleted);
	}

	public void setLastStarted(long time) {
		this.lastStarted = time;
		if (time == 0)
			getConfig().set(Paths.USERDATA_LAST_STARTED, null);
		else
			getConfig().set(Paths.USERDATA_LAST_STARTED, lastStarted);
	}

	public void setCompletedTimes(int times) {
		this.completedTimes = times;
		if (times == 0)
			getConfig().set(Paths.USERDATA_COMPLETED_TIMES, null);
		else
			getConfig().set(Paths.USERDATA_COMPLETED_TIMES, completedTimes);
	}

	public void setLastFailed(long time) {
		this.lastFailed = time;
		if (lastFailed == 0)
			getConfig().set(Paths.USERDATA_LAST_FAILED, null);
		else
			getConfig().set(Paths.USERDATA_LAST_FAILED, lastFailed);
	}

	public void setFailedTimes(int times) {
		this.failedTimes = times;
		if (failedTimes == 0)
			getConfig().set(Paths.USERDATA_FAILED_TIMES, null);
		else
			getConfig().set(Paths.USERDATA_FAILED_TIMES, failedTimes);
	}
}
