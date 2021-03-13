package emanondev.deepquests.events;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import emanondev.deepquests.interfaces.Reward;
import emanondev.deepquests.interfaces.User;

public abstract class UserEventWithRewards<T extends User<T>> extends UserEvent<T> {
	
	private final TreeSet<Reward<T>> rewards = new TreeSet<Reward<T>>();
	public UserEventWithRewards(T user,Collection<Reward<T>> rewards) {
		super(user);
		this.rewards.addAll(rewards);
	}

	public SortedSet<Reward<T>> getRewards() {
		return rewards;
	}

}
