package emanondev.deepquests.interfaces;

public interface ComplexData<T extends User<T>> extends UserData<T> {

	/**
	 * 
	 * @return true if this has been started by User
	 */
	public boolean isStarted();

	/**
	 * 
	 * @return true if this is on cooldown for User
	 */
	public boolean isOnCooldown();

	/**
	 * 
	 * @return true if this is completed for user
	 */
	public boolean isCompleted();

	/**
	 * 
	 * @return true if this is failed for User
	 */
	public boolean isFailed();

	/**
	 * start this for User
	 */
	public void start();

	/**
	 * fail this for User
	 */
	public void fail();

	/**
	 * complete this for User
	 */
	public void complete();

	/**
	 * 
	 * @return how many millisec before user can start this again (might be 0)
	 */
	public long getCooldownTimeLeft();

	/**
	 * 
	 * @return timestamp of last time user started this, 0 if never
	 */
	public long getLastStarted();

	/**
	 * 
	 * @return timestamp of last time user completed this, 0 if never
	 */
	public long getLastCompleted();

	/**
	 * 
	 * @return timestamp of last time user failed this, 0 if never
	 */
	public long getLastFailed();

	/**
	 * 
	 * @return true if user started this at least once
	 */
	public boolean hasStartedAtLeastOnce();

	/**
	 * 
	 * @return true if user failed this at least once
	 */
	public boolean hasFailedAtLeastOnce();

	/**
	 * 
	 * @return how many times user completed this
	 */
	public int successfullyCompletedTimes();

	/**
	 * 
	 * @return how many times user failed this
	 */
	public int failedTimes();

	/**
	 * 
	 * @return true if user completed this at least once
	 */
	public boolean hasCompletedAtLeastOnce();
	
	public void setLastCompleted(long time);
	public void setLastStarted(long time);
	public void setCompletedTimes(int times);
	public void setLastFailed(long time);
	public void setFailedTimes(int times);
}
