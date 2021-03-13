package emanondev.deepquests.interfaces;

import emanondev.core.YMLSection;

public interface RewardType<T extends User<T>> extends QuestComponentType<T,Reward<T>> {

	public Reward<T> getInstance(int id, QuestManager<T> manager, YMLSection nav);
	public default RewardProvider<T> getProvider() {
		return getManager().getRewardProvider();
	}
	public String getDefaultFeedback(Reward<T> reward);
}
