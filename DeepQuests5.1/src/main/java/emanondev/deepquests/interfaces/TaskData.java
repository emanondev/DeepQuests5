package emanondev.deepquests.interfaces;

public interface TaskData<T extends User<T>> extends UserData<T> {

	Task<T> getTask();

	boolean isCompleted();
	
	public int getProgress();
	
	public default int addProgress(int amount) {
		int old = getProgress();
		setProgress(old+amount);
		return getProgress()-old;
	}
	public void setProgress(int amount);

}
