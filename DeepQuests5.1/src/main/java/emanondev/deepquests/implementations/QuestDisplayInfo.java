package emanondev.deepquests.implementations;

import emanondev.core.YMLSection;
import emanondev.deepquests.interfaces.Quest;
import emanondev.deepquests.interfaces.User;

public class QuestDisplayInfo<T extends User<T>> extends ADisplayInfo<T,Quest<T>> {
	public QuestDisplayInfo(YMLSection section,Quest<T> quest) {
		super(section,quest);
	}
	@Override
	protected String getBasePath() {
		return "quest-display-defaults";
	}
}
