package emanondev.deepquests.data;

import emanondev.core.YMLSection;
import emanondev.deepquests.interfaces.Navigable;
import emanondev.deepquests.interfaces.QuestComponent;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.User;

public abstract class QuestComponentData<T extends User<T>,E extends QuestComponent<T>> implements Navigable {
	private final E parent;
	private final YMLSection section;
	public QuestComponentData(E parent,YMLSection section) {
		if (parent == null || section == null)
			throw new NullPointerException();
		this.parent = parent;
		this.section = section;
	}
	
	public YMLSection getConfig() {
		return section;
	}

	public E getParent() {
		return parent;
	}

	public QuestManager<T> getQuestManager() {
		return parent.getManager();
	}

}