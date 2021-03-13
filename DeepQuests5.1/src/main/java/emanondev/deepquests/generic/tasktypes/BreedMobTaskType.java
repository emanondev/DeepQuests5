package emanondev.deepquests.generic.tasktypes;

import java.util.*;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityBreedEvent;

import emanondev.core.YMLSection;
import emanondev.deepquests.Holders;
import emanondev.deepquests.Translations;
import emanondev.deepquests.data.DropData;
import emanondev.deepquests.data.EntityData;
import emanondev.deepquests.data.ToolData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.ATask;
import emanondev.deepquests.implementations.ATaskType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Task;
import emanondev.deepquests.interfaces.Task.Phase;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.utils.DataUtils;

public class BreedMobTaskType<T extends User<T>> extends ATaskType<T> {
	public BreedMobTaskType(QuestManager<T> manager) {
		super(ID, manager);
	}

	private final static String ID = "breed_mob";

	@Override
	public List<String> getDescription() {
		List<String> list = new ArrayList<>();
		list.add("&7Player has to breed mobs");
		return list;
	}

	@Override
	public Task<T> getInstance(int id, Mission<T> mission, YMLSection section) {
		return new BreedMobTask(id, mission, section);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	private void onBreedMob(EntityBreedEvent event) {
		if (!(event.getBreeder() instanceof Player))
			return;
		Player p = (Player) event.getBreeder();
		T user = getManager().getUserManager().getUser(p);
		if (user == null)
			return;
		List<Task<T>> tasks = user.getActiveTasks(this);
		if (tasks == null || tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			BreedMobTask task = (BreedMobTask) tasks.get(i);
			if (task.isWorldAllowed(p.getWorld()) && task.entityData.isValidEntity(event.getEntity())
					&& task.toolData.isValidTool(event.getBredWith(), p))
				if (task.onProgress(user, 1, p, false) > 0) {
					if (task.dropsData.removeExpDrops())
						event.setExperience(0);
				}
		}
	}

	public class BreedMobTask extends ATask<T> {

		private EntityData<T, BreedMobTask> entityData = null;
		private DropData<T, BreedMobTask> dropsData = null;
		private ToolData<T, BreedMobTask> toolData = null;

		public BreedMobTask(int id, Mission<T> mission, YMLSection section) {
			super(id, mission, BreedMobTaskType.this, section);
			entityData = new EntityData<T, BreedMobTask>(this, getConfig().loadSection(Paths.TASK_INFO_ENTITYDATA));
			dropsData = new DropData<T, BreedMobTask>(this, getConfig().loadSection(Paths.TASK_INFO_DROPDATA));
			toolData = new ToolData<T, BreedMobTask>(this, getConfig().loadSection(Paths.TASK_INFO_TOOLDATA));
		}

		public EntityData<T, BreedMobTask> getEntityData() {
			return entityData;
		}

		public DropData<T, BreedMobTask> getDropData() {
			return dropsData;
		}

		public ToolData<T, BreedMobTask> getBreedItemData() {
			return toolData;
		}

		@Override
		public BreedMobTaskType<T> getType() {
			return BreedMobTaskType.this;
		}

		public List<String> getInfo() {
			List<String> info = super.getInfo();
			info.addAll(entityData.getInfo());

			if (dropsData.removeExpDrops())
				info.add("&9Exp Drops: &cDisabled");
			if (toolData.isEnabled()) {
				info.add("&9Breeding Item Check:");
				info.addAll(toolData.getInfo());
			}
			return info;
		}

		@Override
		public Gui getEditorGui(Player target, Gui parent) {
			return new GuiEditor(target, parent);
		}

		private class GuiEditor extends ATaskGuiEditor {

			public GuiEditor(Player player, Gui previusHolder) {
				super(player, previusHolder);
				this.putButton(27, entityData.getEntityTypeButton(this));
				this.putButton(28, entityData.getSpawnReasonButton(this));
				this.putButton(36, entityData.getIgnoreNPCFlagButton(this));
				this.putButton(37, dropsData.getExpDropsFlagButton(this));
				toolData.setupButtons("&6Breeding Item Button", this, 45);
			}
		}
	}

	@Override
	public Material getGuiMaterial() {
		return Material.CARROT;
	}

	@Override
	public String getDefaultUnstartedDescription(Task<T> task) {
		if (!(task instanceof BreedMobTaskType.BreedMobTask))
			return null;
		BreedMobTask t = (BreedMobTask) task;
		YMLSection config = getProvider().getTypeConfig(this);
		String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), null);
		if (txt == null) {
			txt = "&9{action:breed} &e" + Holders.TASK_MAX_PROGRESS + " &9{entities}";
			config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), txt);

		}
		return Translations.replaceAll(txt).replace("{entities}", DataUtils.getEntityHolder(t.getEntityData()));
	}

	@Override
	public String getDefaultProgressDescription(Task<T> task) {
		if (!(task instanceof BreedMobTaskType.BreedMobTask))
			return null;
		BreedMobTask t = (BreedMobTask) task;
		YMLSection config = getProvider().getTypeConfig(this);
		String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), null);
		if (txt == null) {
			txt = "&9{action:breeded} &e" + Holders.TASK_CURRENT_PROGRESS + " &9{conjun:of} &e"
					+ Holders.TASK_MAX_PROGRESS + " &9{entities}";
			config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), txt);

		}
		return Translations.replaceAll(txt).replace("{entities}", DataUtils.getEntityHolder(t.getEntityData()));
	}

	@Override
	public String getDefaultCompleteDescription(Task<T> task) {
		if (!(task instanceof BreedMobTaskType.BreedMobTask))
			return null;
		BreedMobTask t = (BreedMobTask) task;
		YMLSection config = getProvider().getTypeConfig(this);
		String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), null);
		if (txt == null) {
			txt = "&a{action:breeded} &e" + Holders.TASK_MAX_PROGRESS + " &a{entities} "
					+ Translations.translateAction("completed");
			config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), txt);

		}
		return Translations.replaceAll(txt).replace("{entities}", DataUtils.getEntityHolder(t.getEntityData()));
	}

}