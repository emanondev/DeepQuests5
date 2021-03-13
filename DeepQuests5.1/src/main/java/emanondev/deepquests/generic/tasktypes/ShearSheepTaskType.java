package emanondev.deepquests.generic.tasktypes;

import java.util.*;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerShearEntityEvent;

import emanondev.deepquests.Holders;
import emanondev.deepquests.Translations;
import emanondev.deepquests.interfaces.Task.Phase;
import emanondev.core.YMLSection;
import emanondev.deepquests.data.DropData;
import emanondev.deepquests.data.EntityData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.ATask;
import emanondev.deepquests.implementations.ATaskType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Task;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.utils.DataUtils;

public class ShearSheepTaskType<T extends User<T>> extends ATaskType<T> {
	public ShearSheepTaskType(QuestManager<T> manager) {
		super(ID, manager);
	}

	private final static String ID = "shear_sheep";

	@Override
	public Material getGuiMaterial() {
		return Material.SHEARS;
	}

	@Override
	public List<String> getDescription() {
		List<String> list = new ArrayList<>();
		list.add("&7Player has to shear sheeps");
		return list;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	private void onShear(PlayerShearEntityEvent event) {
		T user = getManager().getUserManager().getUser(event.getPlayer());
		if (user == null)
			return;
		List<Task<T>> tasks = user.getActiveTasks(this);
		if (tasks == null || tasks.isEmpty())
			return;
		if (tasks == null || tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			ShearSheepTask task = (ShearSheepTask) tasks.get(i);
			if (task.isWorldAllowed(event.getPlayer().getWorld()) && task.entityData.isValidEntity(event.getEntity())) {
				if (task.onProgress(user, 1, event.getPlayer(), false) > 0 && task.dropsData.removeItemDrops()
						&& event.getEntity() instanceof Sheep) {
					((Sheep) event.getEntity()).setSheared(true);
					event.setCancelled(true);
					// TODO break/damage shears
				}
			}
		}
	}

	public class ShearSheepTask extends ATask<T> {

		private EntityData<T, ShearSheepTask> entityData = null;
		private DropData<T, ShearSheepTask> dropsData = null;

		public ShearSheepTask(int id, Mission<T> mission, YMLSection section) {
			super(id, mission, ShearSheepTaskType.this, section);
			entityData = new EntityData<T, ShearSheepTask>(this, getConfig().loadSection(Paths.TASK_INFO_ENTITYDATA));
			dropsData = new DropData<T, ShearSheepTask>(this, getConfig().loadSection(Paths.TASK_INFO_DROPDATA));
		}

		public EntityData<T, ShearSheepTask> getEntityData() {
			return entityData;
		}

		public DropData<T, ShearSheepTask> getDropData() {
			return dropsData;
		}

		public ShearSheepTaskType<T> getType() {
			return ShearSheepTaskType.this;
		}

		public List<String> getInfo() {
			List<String> info = super.getInfo();
			info.addAll(entityData.getInfo());

			if (dropsData.removeItemDrops())
				info.add("&9Item Drops: &cDisabled");
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
				this.putButton(37, dropsData.getItemDropsFlagButton(this));
			}
		}
	}

	@Override
	public Task<T> getInstance(int id, Mission<T> mission, YMLSection section) {
		return new ShearSheepTask(id, mission, section);
	}

	@Override
	public String getDefaultUnstartedDescription(Task<T> task) {
		if (!(task instanceof ShearSheepTaskType.ShearSheepTask))
			return null;
		ShearSheepTask t = (ShearSheepTask) task;
		YMLSection config = getProvider().getTypeConfig(this);
		String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), null);
		if (txt == null) {
			txt = "&9{action:shear} &e" + Holders.TASK_MAX_PROGRESS + " &9" + Translations.translate(EntityType.SHEEP);
			config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), txt);

		}
		return Translations.replaceAll(txt).replace("{entities}", DataUtils.getEntityHolder(t.getEntityData()));
	}

	@Override
	public String getDefaultCompleteDescription(Task<T> task) {
		if (!(task instanceof ShearSheepTaskType.ShearSheepTask))
			return null;
		ShearSheepTask t = (ShearSheepTask) task;
		YMLSection config = getProvider().getTypeConfig(this);
		String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), null);
		if (txt == null) {
			txt = "&a{action:shear} &e" + Holders.TASK_MAX_PROGRESS + " &a" + Translations.translate(EntityType.SHEEP)
					+ " " + Translations.translateAction("completed");
			config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), txt);

		}
		return Translations.replaceAll(txt).replace("{entities}", DataUtils.getEntityHolder(t.getEntityData()));
	}

	@Override
	public String getDefaultProgressDescription(Task<T> task) {
		if (!(task instanceof ShearSheepTaskType.ShearSheepTask))
			return null;
		ShearSheepTask t = (ShearSheepTask) task;
		YMLSection config = getProvider().getTypeConfig(this);
		String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), null);
		if (txt == null) {
			txt = "&9{action:sheared} &e" + Holders.TASK_CURRENT_PROGRESS + " &9{conjun:of} &e"
					+ Holders.TASK_MAX_PROGRESS + " &9" + Translations.translate(EntityType.SHEEP);
			config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), txt);

		}
		return Translations.replaceAll(txt).replace("{entities}", DataUtils.getEntityHolder(t.getEntityData()));
	}
}
