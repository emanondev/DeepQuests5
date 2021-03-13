package emanondev.deepquests.interfaces;

public interface QuestData<T extends User<T>> extends ComplexData<T> {

	public Quest<T> getQuest();

	public int getPoints();

	public void setPoints(int amount);

	public boolean isOnSelfCooldown();

	public boolean isOnMissionCooldown();

	public long getMissionsCooldown();

}
