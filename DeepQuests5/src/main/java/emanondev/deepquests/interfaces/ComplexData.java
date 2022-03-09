package emanondev.deepquests.interfaces;

public interface ComplexData<T extends User<T>> extends UserData<T> {

    /**
     * @return true if this has been started by User
     */
    boolean isStarted();

    /**
     * @return true if this is on cooldown for User
     */
    boolean isOnCooldown();

    /**
     * @return true if this is completed for user
     */
    boolean isCompleted();

    /**
     * @return true if this is failed for User
     */
    boolean isFailed();

    /**
     * start this for User
     */
    void start();

    /**
     * fail this for User
     */
    void fail();

    /**
     * complete this for User
     */
    void complete();

    /**
     * @return how many millisecond before user can start this again (might be 0)
     */
    long getCooldownTimeLeft();

    /**
     * @return timestamp of last time user started this, 0 if never
     */
    long getLastStarted();

    /**
     * @return timestamp of last time user completed this, 0 if never
     */
    long getLastCompleted();

    /**
     * @return timestamp of last time user failed this, 0 if never
     */
    long getLastFailed();

    /**
     * @return true if user started this at least once
     */
    boolean hasStartedAtLeastOnce();

    /**
     * @return true if user failed this at least once
     */
    boolean hasFailedAtLeastOnce();

    /**
     * @return how many times user completed this
     */
    int successfullyCompletedTimes();

    /**
     * @return how many times user failed this
     */
    int failedTimes();

    /**
     * @return true if user completed this at least once
     */
    boolean hasCompletedAtLeastOnce();

    void setLastCompleted(long time);

    void setLastStarted(long time);

    void setCompletedTimes(int times);

    void setLastFailed(long time);

    void setFailedTimes(int times);
}
