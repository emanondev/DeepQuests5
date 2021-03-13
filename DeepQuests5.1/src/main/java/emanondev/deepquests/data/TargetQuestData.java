package emanondev.deepquests.data;

import java.util.*;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;

import emanondev.deepquests.gui.button.Button;
import emanondev.deepquests.gui.button.GuiElementSelectorButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.Quest;
import emanondev.deepquests.interfaces.QuestComponent;
import emanondev.deepquests.interfaces.User;

public class TargetQuestData<T extends User<T>, E extends QuestComponent<T>> extends QuestComponentData<T, E> {
	private Integer questKey = null;

	public TargetQuestData(E parent, YMLSection section) {
		super(parent, section);
		questKey = getConfig().getInteger(Paths.DATA_TARGET_QUEST_KEY, null);
	}

	public Quest<T> getQuest() {
		if (questKey == null)
			return null;
		return getQuestManager().getQuest(questKey);
	}

	public List<String> getInfo() {
		ArrayList<String> desc = new ArrayList<>();
		Quest<?> quest = getQuest();
		if (quest == null)
			desc.add("&9Quest: &cnot setted");
		else
			desc.add("&9Quest: &e" + quest.getDisplayName() + " &8(" + quest.getID() + ")");
		return desc;
	}

	public void setQuest(Quest<T> quest) {
		if (quest == null) {
			if (questKey == null)
				return;
			questKey = null;
			getConfig().set(Paths.DATA_TARGET_QUEST_KEY, questKey);
			return;
		}
		if (!this.getQuestManager().equals(quest.getManager()))
			return;
		if (questKey != null && questKey == quest.getID())
			return;
		questKey = quest.getID();
		getConfig().set(Paths.DATA_TARGET_QUEST_KEY, questKey);
	}

	public Button getQuestSelectorButton(Gui gui) {
		return new QuestSelectorButton(gui);
	}

	private class QuestSelectorButton extends GuiElementSelectorButton<Quest<T>> {

		public QuestSelectorButton(Gui parent) {
			super("&9Selector a quest", new ItemBuilder(Material.KNOWLEDGE_BOOK).setGuiProperty().build(), parent, true,
					true, false);
		}

		@Override
		public List<String> getButtonDescription() {
			List<String> list = getInfo();
			list.add(0, "&6Quest Selector Button");
			return list;
		}

		@Override
		public Collection<Quest<T>> getValues() {
			Collection<Quest<T>> res = getQuestManager().getQuests();
			return res;
		}

		@Override
		public void onElementSelectRequest(Quest<T> element, Player p) {
			setQuest(element);
			getGui().updateInventory();
			p.openInventory(getGui().getInventory());
		}

	}

}
