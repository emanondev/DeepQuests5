package emanondev.deepquests.interfaces;

import emanondev.core.YMLSection;

public interface RewardType<T extends User<T>> extends QuestComponentType<T, Reward<T>> {

    Reward<T> getInstance(int id, QuestManager<T> manager, YMLSection nav);

    default RewardProvider<T> getProvider() {
        return getManager().getRewardProvider();
    }

    String getDefaultFeedback(Reward<T> reward);
}
