package emanondev.deepquests.interfaces;

import emanondev.core.YMLSection;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface RewardProvider<T extends User<T>> extends QuestComponentTypeProvider<T, Reward<T>, RewardType<T>> {

    void registerQuestType(@NotNull RewardType<T> rewardType);

    void registerMissionType(@NotNull RewardType<T> rewardType);

    void registerTaskType(@NotNull RewardType<T> rewardType);

    @NotNull Collection<RewardType<T>> getQuestTypes();

    @NotNull Collection<RewardType<T>> getMissionTypes();

    @NotNull Collection<RewardType<T>> getTaskTypes();

    @NotNull Reward<T> getInstance(int id, @NotNull YMLSection section);

}
