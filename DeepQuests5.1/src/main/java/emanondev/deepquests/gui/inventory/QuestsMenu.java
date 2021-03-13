package emanondev.deepquests.gui.inventory;

import java.util.*;

import org.bukkit.entity.Player;

import emanondev.deepquests.Quests;
import emanondev.deepquests.gui.GuiConfig;
import emanondev.deepquests.gui.button.QuestButton;
import emanondev.deepquests.gui.button.QuestStateShowToggler;
import emanondev.deepquests.interfaces.Quest;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.utils.DisplayState;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class QuestsMenu extends ListGui<QuestButton> {
	private ArrayList<QuestButton> rawQuestButtons = new ArrayList<>();

	public QuestsMenu(Player player, Gui previusHolder,Collection<QuestManager<?>> managers) {
		super(GuiConfig.Generic.getQuestsMenuTitle(player), 6, player, previusHolder, 1);
		if (managers==null)
			managers = Quests.get().getManagers();
		for (QuestManager<?> manager:managers) {
			if (manager==null)
				continue;
			User user = manager.getUserManager().getUser(getTargetPlayer());
			if (user==null)
				continue;
			for (Quest quest:manager.getQuests())
				rawQuestButtons.add(new QuestButton(this,quest,user));
		}
		Collections.sort(rawQuestButtons);
		this.setControlButton(0, new QuestStateShowToggler(this,DisplayState.LOCKED));
		this.setControlButton(1, new QuestStateShowToggler(this,DisplayState.UNSTARTED));
		this.setControlButton(2, new QuestStateShowToggler(this,DisplayState.ONPROGRESS));
		this.setControlButton(3, new QuestStateShowToggler(this,DisplayState.COMPLETED));
		this.setControlButton(4, new QuestStateShowToggler(this,DisplayState.COOLDOWN));
		this.setControlButton(5, new QuestStateShowToggler(this,DisplayState.FAILED));
		updateInventory();
	}
	
	public boolean updateInventory() {
		this.clearButtons();
		for (QuestButton button:rawQuestButtons)
			if (button.getUser().canSee(getTargetPlayer(), button.getQuestComponent())
					&&(!button.getQuestComponent().getDisplayInfo().isHidden(button.getUser().getDisplayState(button.getQuestComponent()))))
				this.addButton(button);
		return super.updateInventory();
	}
	protected int loadPreviusPageButtonPosition() {
		return 6;
	}
	protected int loadNextPageButtonPosition() {
		return 7;
	}
	

}
