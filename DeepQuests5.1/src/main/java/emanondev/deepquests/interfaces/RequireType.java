package emanondev.deepquests.interfaces;

import emanondev.core.YMLSection;

public interface RequireType<T extends User<T>> extends QuestComponentType<T,Require<T>> {

	public Require<T> getInstance(int id, QuestManager<T> manager, YMLSection nav);
	public default RequireProvider<T> getProvider() {
		return getManager().getRequireProvider();
	}
}
