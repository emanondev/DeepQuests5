package emanondev.deepquests.interfaces;

import org.jetbrains.annotations.NotNull;

public interface QuestData<T extends User<T>> extends ComplexData<T> {

    @NotNull Quest<T> getQuest();

    int getPoints();

    void setPoints(int amount);

    boolean isOnSelfCooldown();

    boolean isOnMissionCooldown();

    long getMissionsCooldown();

}
