package emanondev.deepquests.interfaces;

public interface QuestData<T extends User<T>> extends ComplexData<T> {

    Quest<T> getQuest();

    int getPoints();

    void setPoints(int amount);

    boolean isOnSelfCooldown();

    boolean isOnMissionCooldown();

    long getMissionsCooldown();

}
