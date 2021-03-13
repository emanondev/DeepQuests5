package emanondev.deepquests.interfaces;

import java.util.*;

import emanondev.core.YMLSection;

public interface RewardProvider<T extends User<T>> extends QuestComponentTypeProvider<T, Reward<T>, RewardType<T>> {

	public void registerQuestType(RewardType<T> rewardType);

	public void registerMissionType(RewardType<T> rewardType);

	public void registerTaskType(RewardType<T> rewardType);

	public Collection<RewardType<T>> getQuestTypes();

	public Collection<RewardType<T>> getMissionTypes();

	public Collection<RewardType<T>> getTaskTypes();

	public Reward<T> getInstance(int id, QuestManager<T> questManager, YMLSection section);

}
