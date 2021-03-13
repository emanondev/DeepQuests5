package emanondev.deepquests.gui.button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.deepquests.gui.inventory.EditTasksMenu;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.utils.DisplayState;
import emanondev.core.ItemBuilder;
import net.md_5.bungee.api.ChatColor;

public class EditMissionButton<T extends User<T>> extends QuestComponentButton<Mission<T>> {
	private final T user;
	private DisplayState state;

	public EditMissionButton(Gui parent, Mission<T> questComponent, T user) {
		super(parent, questComponent);
		this.user = user;
		state = user.getDisplayState(getQuestComponent());
	}

	@Override
	public ItemStack getItem() {
		return getQuestComponent().getDisplayInfo().getGuiItem(state, user, getGui().getTargetPlayer(),true);
	}

	@Override
	public boolean update() {
		state = user.getDisplayState(getQuestComponent());
		return true;
	}

	@Override
	public void onClick(Player clicker, ClickType click) {
		if (click == ClickType.RIGHT) {
			this.clicker = clicker;
			new ClickEditorButton().onClick(clicker, click);
			return;
		}
		clicker.openInventory(new EditTasksMenu<T>(getTargetPlayer(), getGui(), getQuestComponent(), user).getInventory());
	}

	private Player clicker = null;

	public T getUser() {
		return user;
	}

	private enum ActionType {
		RESET(new ItemBuilder(Material.ORANGE_TERRACOTTA).setGuiProperty().build(), Arrays.asList(
				ChatColor.GOLD + "Reset this Mission for user", 
				ChatColor.GRAY + "Reset progress of this mission",
				ChatColor.GRAY + "but keep statistic of completed and",
				ChatColor.GRAY + "failed times and related timings")), COMPLETE(
						new ItemBuilder(Material.GREEN_TERRACOTTA).setGuiProperty().build(),
						Arrays.asList(ChatColor.GREEN + "Complete this Mission for user",
								ChatColor.GRAY + "complete the mission and assign rewards")), START(
										new ItemBuilder(Material.LIGHT_BLUE_TERRACOTTA).setGuiProperty().build(),
										Arrays.asList(ChatColor.BLUE + "Start this Mission for user",
												ChatColor.GRAY + "Reset progress and Start this mission",
												ChatColor.GRAY + "for user and bypass requires",
												ChatColor.GRAY + "also assign start rewards")), FAIL(
														new ItemBuilder(Material.RED_TERRACOTTA).setGuiProperty()
																.build(),
														Arrays.asList(ChatColor.RED + "Fail this Mission for user",
																ChatColor.GRAY + "Mark the mission as failed",
																ChatColor.GRAY + "also assign failing rewards")), ERASE(
																		new ItemBuilder(Material.BLACK_TERRACOTTA)
																				.setGuiProperty().build(),
																		Arrays.asList(
																				ChatColor.RED
																						+ "Erase this Mission for user",
																				ChatColor.GRAY
																						+ "All data abouth this mission and related",
																				ChatColor.GRAY
																						+ "tasks is completely erased for user"));
		private ItemStack item;
		private List<String> desc;

		private ActionType(ItemStack item, List<String> desc) {
			this.item = item;
			this.desc = desc;
		}

		public ItemStack getItem() {
			return new ItemStack(item);
		}

		public List<String> getDescription() {
			return desc;
		}
	}

	private class ClickEditorButton extends ElementSelectorButton<ActionType> {

		public ClickEditorButton() {
			super(ChatColor.BLUE + "Click the action for "
					+ EditMissionButton.this.getQuestComponent().getDisplayName(),
					new ItemBuilder(Material.BARRIER).build(), EditMissionButton.this.getGui(), false, true, true);
		}

		@Override
		public List<String> getButtonDescription() {
			return null;
		}

		@Override
		public List<String> getElementDescription(ActionType element) {
			ArrayList<String> text = new ArrayList<>();
			ItemStack item = getQuestComponent().getDisplayInfo().getGuiItem(state, user, getGui().getTargetPlayer(),true);
			ItemMeta meta = item.getItemMeta();
			if (meta.hasDisplayName())
				text.add(meta.getDisplayName());
			if (meta.hasLore())
				text.addAll(meta.getLore());
			text.add("");
			text.add("&7Mission ID: "+getQuestComponent().getID());
			text.add("&7Display Name: &r"+getQuestComponent().getDisplayName());
			text.add("");
			text.addAll(element.getDescription());
			return text;
		}

		@Override
		public ItemStack getElementItem(ActionType element) {
			return element.getItem();
		}

		@Override
		public void onElementSelectRequest(ActionType element) {
			EditMissionButton.this.handle(element);

		}

		@Override
		public Collection<ActionType> getPossibleValues() {
			EnumSet<ActionType> set = EnumSet.noneOf(ActionType.class);
			switch (state) {
			case COMPLETED:
				set.add(ActionType.RESET);
				set.add(ActionType.START);
				set.add(ActionType.FAIL);
				set.add(ActionType.ERASE);
				break;
			case FAILED:
				set.add(ActionType.RESET);
				set.add(ActionType.START);
				set.add(ActionType.COMPLETE);
				set.add(ActionType.ERASE);
				break;
			case LOCKED:
			case COOLDOWN:
			case ONPROGRESS:
				set.add(ActionType.RESET);
				set.add(ActionType.START);
				set.add(ActionType.COMPLETE);
				set.add(ActionType.FAIL);
				set.add(ActionType.ERASE);
				break;
			case UNSTARTED:
				set.add(ActionType.START);
				set.add(ActionType.COMPLETE);
				set.add(ActionType.FAIL);
				set.add(ActionType.ERASE);
				break;
			}
			return set;
		}

	}

	public void handle(ActionType element) {
		switch (element) {
		case COMPLETE:
			getUser().completeMission(getQuestComponent());
			break;
		case ERASE:
			getUser().eraseMissionData(getQuestComponent());
			break;
		case FAIL:
			getUser().failMission(getQuestComponent());
			break;
		case RESET:
			getUser().resetMission(getQuestComponent());
			break;
		case START:
			getUser().resetMission(getQuestComponent());
			getUser().startMission(getQuestComponent(), null, true);
			break;
		}
		this.getGui().updateInventory();
		if (clicker != null)
			clicker.openInventory(getGui().getInventory());
	}
}