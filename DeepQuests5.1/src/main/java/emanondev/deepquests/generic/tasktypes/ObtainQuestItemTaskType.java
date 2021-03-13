package emanondev.deepquests.generic.tasktypes;

import java.util.*;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import emanondev.deepquests.Holders;
import emanondev.deepquests.Translations;
import emanondev.deepquests.interfaces.Task.Phase;
import emanondev.core.YMLSection;
import emanondev.deepquests.data.QuestItemData;
import emanondev.deepquests.events.QuestItemObtainEvent;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.*;
import emanondev.deepquests.interfaces.*;

public class ObtainQuestItemTaskType<T extends User<T>> extends ATaskType<T> {

	public ObtainQuestItemTaskType(QuestManager<T> manager) {
		super(ID, manager);
	}

	private final static String ID = "obtain_quest_item";

	@Override
	public Material getGuiMaterial() {
		return Material.GOLDEN_AXE;
	}

	@Override
	public List<String> getDescription() {
		List<String> list = new ArrayList<>();
		list.add("&7Player has to obtain quest items");
		return list;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	private void onRightClick(QuestItemObtainEvent<T> event) {
		T user = event.getUser();
		if (user == null)
			return;
		QuestBag<T> bag = user.getQuestBag();
		if (bag == null)
			return;
		List<Task<T>> tasks = user.getActiveTasks(this);
		if (tasks == null || tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			ObtainQuestItemTask task = (ObtainQuestItemTask) tasks.get(i);
			if (!event.getID().equals(task.getQuestItemData().getQuestItemID()))
				continue;
			int progress = Math.min(task.getMaxProgress() - user.getTaskProgress(task), event.getAmount());
			if (progress > 0) {
				task.onProgress(user, progress, null, false);
			}
		}
	}

	public class ObtainQuestItemTask extends ATask<T> {

		private QuestItemData<T, ObtainQuestItemTask> itemData = null;

		public ObtainQuestItemTask(int id, Mission<T> mission, YMLSection section) {
			super(id, mission, ObtainQuestItemTaskType.this, section);
			itemData = new QuestItemData<T, ObtainQuestItemTask>(this, getConfig().loadSection(Paths.TASK_INFO_ITEM));
		}

		public QuestItemData<T, ObtainQuestItemTask> getQuestItemData() {
			return itemData;
		}

		public ObtainQuestItemTaskType<T> getType() {
			return ObtainQuestItemTaskType.this;
		}

		public List<String> getInfo() {
			List<String> info = super.getInfo();
			info.addAll(itemData.getInfo());
			return info;
		}

		@Override
		public Gui getEditorGui(Player target, Gui parent) {
			return new GuiEditor(target, parent);
		}

		private class GuiEditor extends ATaskGuiEditor {

			public GuiEditor(Player player, Gui previusHolder) {
				super(player, previusHolder);
				itemData.setupButtons(this, 27);
			}
		}
	}

	@Override
	public Task<T> getInstance(int id, Mission<T> mission, YMLSection section) {
		return new ObtainQuestItemTask(id, mission, section);
	}

	@Override
	public String getDefaultUnstartedDescription(Task<T> task) {
		if (!(task instanceof ObtainQuestItemTaskType.ObtainQuestItemTask))
			return null;
		ObtainQuestItemTask t = (ObtainQuestItemTask) task;
		YMLSection config = getProvider().getTypeConfig(this);
		String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), null);
		if (txt == null) {
			txt = "&9{action:deliver} &e" + Holders.TASK_MAX_PROGRESS + " {items}";
			config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), txt);

		}
		return Translations.replaceAll(txt).replace("{items}", t.getQuestItemData().getQuestItemNick());
	}

	@Override
	public String getDefaultCompleteDescription(Task<T> task) {
		if (!(task instanceof ObtainQuestItemTaskType.ObtainQuestItemTask))
			return null;
		ObtainQuestItemTask t = (ObtainQuestItemTask) task;
		YMLSection config = getProvider().getTypeConfig(this);
		String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), null);
		if (txt == null) {
			txt = "&a{action:deliver} &e" + Holders.TASK_MAX_PROGRESS + " {items} "
					+ Translations.translateAction("completed");
			config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), txt);

		}
		return Translations.replaceAll(txt).replace("{items}", t.getQuestItemData().getQuestItemNick());
	}

	@Override
	public String getDefaultProgressDescription(Task<T> task) {
		if (!(task instanceof ObtainQuestItemTaskType.ObtainQuestItemTask))
			return null;
		ObtainQuestItemTask t = (ObtainQuestItemTask) task;
		YMLSection config = getProvider().getTypeConfig(this);
		String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), null);
		if (txt == null) {
			txt = "&9{action:obtain} &e" + Holders.TASK_CURRENT_PROGRESS + " &9{conjun:of} &e"
					+ Holders.TASK_MAX_PROGRESS + " {items}";
			config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), txt);

		}
		return Translations.replaceAll(txt).replace("{items}", t.getQuestItemData().getQuestItemNick());
	}

}
