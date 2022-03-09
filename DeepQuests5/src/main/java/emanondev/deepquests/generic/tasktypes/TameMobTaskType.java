package emanondev.deepquests.generic.tasktypes;

import emanondev.core.YMLSection;
import emanondev.deepquests.Holders;
import emanondev.deepquests.Translations;
import emanondev.deepquests.data.EntityData;
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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityTameEvent;

import java.util.ArrayList;
import java.util.List;

public class TameMobTaskType<T extends User<T>> extends ATaskType<T> {
    public TameMobTaskType(QuestManager<T> manager) {
        super(ID, manager);
    }

    private final static String ID = "tame_mob";

    @Override
    public Material getGuiMaterial() {
        return Material.CARROT;
    }

    @Override
    public List<String> getDescription() {
        List<String> list = new ArrayList<>();
        list.add("&7Player has to tame mobs");
        return list;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onTaming(EntityTameEvent event) {
        if (!(event.getOwner() instanceof Player p))
            return;
        T user = getManager().getUserManager().getUser(p);
        if (user == null)
            return;
        List<Task<T>> tasks = user.getActiveTasks(this);
        if (tasks == null || tasks.isEmpty())
            return;
        for (int i = 0; i < tasks.size(); i++) {
            TameMobTask task = (TameMobTask) tasks.get(i);
            if (task.isWorldAllowed(p.getWorld()) && task.entityData.isValidEntity(event.getEntity())) {
                task.onProgress(user, 1, p, false);
            }
        }
    }

    public class TameMobTask extends ATask<T> {

        private final EntityData<T, TameMobTask> entityData;

        public TameMobTask(int id, Mission<T> mission, YMLSection section) {
            super(id, mission, TameMobTaskType.this, section);
            entityData = new EntityData<>(this, getConfig().loadSection(Paths.TASK_INFO_ENTITYDATA));
        }

        public EntityData<T, TameMobTask> getEntityData() {
            return entityData;
        }

        public TameMobTaskType<T> getType() {
            return TameMobTaskType.this;
        }

        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.addAll(entityData.getInfo());
            return info;
        }

        @Override
        public Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ATaskGuiEditor {

            public GuiEditor(Player player, Gui previousHolder) {
                super(player, previousHolder);
                this.putButton(27, entityData.getEntityTypeButton(this));
                this.putButton(28, entityData.getSpawnReasonButton(this));
                this.putButton(36, entityData.getIgnoreNPCFlagButton(this));
            }
        }
    }

    @Override
    public Task<T> getInstance(int id, Mission<T> mission, YMLSection section) {
        return new TameMobTask(id, mission, section);
    }

    @Override
    public String getDefaultUnstartedDescription(Task<T> task) {
        if (!(task instanceof TameMobTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), null);
        if (txt == null) {
            txt = "&9{action:tame} &e" + Holders.TASK_MAX_PROGRESS + " &9{entities}";
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), txt);

        }
        return Translations.replaceAll(txt).replace("{entities}", DataUtils.getEntityHolder(t.getEntityData()));
    }

    @Override
    public String getDefaultCompleteDescription(Task<T> task) {
        if (!(task instanceof TameMobTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), null);
        if (txt == null) {
            txt = "&a{action:tame} &e" + Holders.TASK_MAX_PROGRESS + " &a{entities} "
                    + Translations.translateAction("completed");
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), txt);

        }
        return Translations.replaceAll(txt).replace("{entities}", DataUtils.getEntityHolder(t.getEntityData()));
    }

    @Override
    public String getDefaultProgressDescription(Task<T> task) {
        if (!(task instanceof TameMobTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), null);
        if (txt == null) {
            txt = "&9{action:tamed} &e" + Holders.TASK_CURRENT_PROGRESS + " &9{conjun:of} &e"
                    + Holders.TASK_MAX_PROGRESS + " &9{entities}";
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), txt);

        }
        return Translations.replaceAll(txt).replace("{entities}", DataUtils.getEntityHolder(t.getEntityData()));
    }

}
