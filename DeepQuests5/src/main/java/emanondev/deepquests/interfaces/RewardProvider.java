package emanondev.deepquests.interfaces;

import emanondev.core.YMLSection;

import java.util.Collection;

public interface RewardProvider<T extends User<T>> extends QuestComponentTypeProvider<T, Reward<T>, RewardType<T>> {

    void registerQuestType(RewardType<T> rewardType);

    void registerMissionType(RewardType<T> rewardType);

    void registerTaskType(RewardType<T> rewardType);

    Collection<RewardType<T>> getQuestTypes();

    Collection<RewardType<T>> getMissionTypes();

    Collection<RewardType<T>> getTaskTypes();

    Reward<T> getInstance(int id, QuestManager<T> questManager, YMLSection section);

}
