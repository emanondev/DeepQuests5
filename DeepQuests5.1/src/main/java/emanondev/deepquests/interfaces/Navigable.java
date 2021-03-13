package emanondev.deepquests.interfaces;

import emanondev.core.YMLSection;

public interface Navigable {
	
	@Deprecated
	public default YMLSection getNavigator() {
		return getConfig();
	}
	
	public YMLSection getConfig();

}
