package emanondev.deepquests.data;

import emanondev.core.YMLSection;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestComponent;
import emanondev.deepquests.interfaces.User;

@Deprecated
public class LevelData<T extends User<T>, E extends QuestComponent<T>> extends QuestComponentData<T, E> {
	private int level = 1;

	public LevelData(E parent, YMLSection section) {
		super(parent, section);
		level = Math.max(1, getConfig().getInteger(Paths.DATA_LEVEL, 1));
	}

	public long getLevel() {
		return level;
	}

	public void setLevel(int level) {
		level = Math.max(1, level);
		if (level == this.level)
			return;
		this.level = level;
		getConfig().set(Paths.DATA_LEVEL, level);
	}
}