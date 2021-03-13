package emanondev.deepquests.gui.inventory;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.entity.Player;

import emanondev.deepquests.gui.button.EditTaskButton;
import emanondev.deepquests.interfaces.Task;
import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.User;

public class EditTasksMenu<T extends User<T>> extends ListGui<EditTaskButton<T>> {
	public EditTasksMenu(Player player, Gui previusHolder,Mission<T> mission,T user) {
		super("tasks", 6, player, previusHolder, 1);
		if (user==null || mission == null)
			throw new NullPointerException();
		for (Task<T> task:mission.getTasks())
			rawTaskButtons.add(new EditTaskButton<T>(this,task,user));
		Collections.sort(rawTaskButtons);
		updateInventory();
	}

	private ArrayList<EditTaskButton<T>> rawTaskButtons = new ArrayList<>();
	
	public boolean updateInventory() {
		this.clearButtons();
		for (EditTaskButton<T> button:rawTaskButtons)
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