package emanondev.deepquests.interfaces;

import emanondev.core.YMLSection;
import org.jetbrains.annotations.NotNull;

public interface RewardType<T extends User<T>> extends QuestComponentType<T, Reward<T>> {

    @NotNull Reward<T> getInstance(int id, @NotNull QuestManager<T> manager, @NotNull YMLSection nav);

    default @NotNull RewardProvider<T> getProvider() {
        return getManager().getRewardProvider();
    }

    String getDefaultFeedback(Reward<T> reward);
}
