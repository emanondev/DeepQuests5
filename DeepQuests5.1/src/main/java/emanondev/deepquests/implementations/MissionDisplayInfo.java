package emanondev.deepquests.implementations;

import emanondev.core.YMLSection;
import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.User;

public class MissionDisplayInfo<T extends User<T>> extends ADisplayInfo<T,Mission<T>> {

	public MissionDisplayInfo(YMLSection section,Mission<T> mission) {
		super(section, mission);
	}
	
	@Override
	protected String getBasePath() {
		return "mission-display-defaults";
	}
}
