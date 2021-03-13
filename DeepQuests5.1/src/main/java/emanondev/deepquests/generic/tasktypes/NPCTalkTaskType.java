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
import emanondev.deepquests.data.NPCData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.ATask;
import emanondev.deepquests.implementations.ATaskType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Task;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.utils.DataUtils;
import net.citizensnpcs.api.event.NPCRightClickEvent;

public class NPCTalkTaskType<T extends User<T>> extends ATaskType<T> {
	public NPCTalkTaskType(QuestManager<T> manager) {
		super(ID, manager);
	}

	private final static String ID = "talk_to_npc";

	@Override
	public Material getGuiMaterial() {
		return Material.POPPY;
	}

	@Override
	public List<String> getDescription() {
		List<String> list = new ArrayList<>();
		list.add("&7Player has to interact with NPC");
		return list;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	private void onRightClick(NPCRightClickEvent event) {
		Player p = (Player) event.getClicker();
		T user = getManager().getUserManager().getUser(p);
		if (user == null)
			return;
		List<Task<T>> tasks = user.getActiveTasks(this);
		if (tasks == null || tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			NPCTalkTask task = (NPCTalkTask) tasks.get(i);
			if (task.isWorldAllowed(p.getWorld()) && task.npcData.isValidNPC(event.getNPC())) {
				// LoggerManager.logText("debug.log","task "+task.getID()+" tentativo di
				// progresso (A)");
				if (task.onProgress(user, 1, p, false) > 0) {

				}
			}
		}
	}

	public class NPCTalkTask extends ATask<T> {

		private NPCData<T, NPCTalkTask> npcData = null;

		public NPCTalkTask(int id, Mission<T> mission, YMLSection section) {
			super(id, mission, NPCTalkTaskType.this, section);
			npcData = new NPCData<T, NPCTalkTask>(this, getConfig().loadSection(Paths.TASK_INFO_NPCDATA));
		}

		public NPCData<T, NPCTalkTask> getNPCData() {
			return npcData;
		}

		public NPCTalkTaskType<T> getType() {
			return NPCTalkTaskType.this;
		}

		public List<String> getInfo() {
			List<String> info = super.getInfo();
			info.addAll(npcData.getInfo());
			return info;
		}

		@Override
		public Gui getEditorGui(Player target, Gui parent) {
			return new GuiEditor(target, parent);
		}

		private class GuiEditor extends ATaskGuiEditor {

			public GuiEditor(Player player, Gui previusHolder) {
				super(player, previusHolder);
				this.putButton(27, npcData.getNPCSelectorButton(this));
			}
		}
	}

	@Override
	public Task<T> getInstance(int id, Mission<T> mission, YMLSection section) {
		return new NPCTalkTask(id, mission, section);
	}

	@Override
	public String getDefaultUnstartedDescription(Task<T> task) {
		if (!(task instanceof NPCTalkTaskType.NPCTalkTask))
			return null;
		NPCTalkTask t = (NPCTalkTask) task;
		YMLSection config = getProvider().getTypeConfig(this);
		String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), null);
		if (txt == null) {
			txt = "&9{action:talk} &e" + Holders.TASK_MAX_PROGRESS + " &9{npcs}";
			config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), txt);

		}
		return Translations.replaceAll(txt).replace("{npcs}", DataUtils.getNPCHolder(t.getNPCData()));
	}

	@Override
	public String getDefaultCompleteDescription(Task<T> task) {
		if (!(task instanceof NPCTalkTaskType.NPCTalkTask))
			return null;
		NPCTalkTask t = (NPCTalkTask) task;
		YMLSection config = getProvider().getTypeConfig(this);
		String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), null);
		if (txt == null) {
			txt = "&a{action:talk} &e" + Holders.TASK_MAX_PROGRESS + " &a{npcs} "
					+ Translations.translateAction("completed");
			config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), txt);

		}
		return Translations.replaceAll(txt).replace("{npcs}", DataUtils.getNPCHolder(t.getNPCData()));
	}

	@Override
	public String getDefaultProgressDescription(Task<T> task) {
		if (!(task instanceof NPCTalkTaskType.NPCTalkTask))
			return null;
		NPCTalkTask t = (NPCTalkTask) task;
		YMLSection config = getProvider().getTypeConfig(this);
		String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), null);
		if (txt == null) {
			txt = "&9{action:talk} {conjun:to} &9{npcs}";
			config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), txt);

		}
		return Translations.replaceAll(txt).replace("{npcs}", DataUtils.getNPCHolder(t.getNPCData()));
	}
}
