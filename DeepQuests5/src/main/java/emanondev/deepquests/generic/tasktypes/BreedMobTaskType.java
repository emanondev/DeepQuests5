package emanondev.deepquests.generic.tasktypes;

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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityBreedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
    public @NotNull Task<T> getInstance(int id, @NotNull Mission<T> mission, YMLSection section) {
        return new BreedMobTask(id, mission, section);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onBreedMob(EntityBreedEvent event) {
        if (!(event.getBreeder() instanceof Player p))
            return;
        T user = getManager().getUserManager().getUser(p);
        if (user == null)
            return;
        List<Task<T>> tasks = user.getActiveTasks(this);
        if (tasks == null || tasks.isEmpty())
            return;
        for (Task<T> tTask : tasks) {
            BreedMobTask task = (BreedMobTask) tTask;
            if (task.isWorldAllowed(p.getWorld()) && task.entityData.isValidEntity(event.getEntity())
                    && task.toolData.isValidTool(event.getBredWith(), p))
                if (task.onProgress(user, 1, p, false) > 0) {
                    if (task.dropsData.removeExpDrops())
                        event.setExperience(0);
                }
        }
    }

    public class BreedMobTask extends ATask<T> {

        private final EntityData<T, BreedMobTask> entityData;
        private final DropData<T, BreedMobTask> dropsData;
        private final ToolData<T, BreedMobTask> toolData;

        public BreedMobTask(int id, Mission<T> mission, YMLSection section) {
            super(id, mission, BreedMobTaskType.this, section);
            entityData = new EntityData<>(this, getConfig().loadSection(Paths.TASK_INFO_ENTITYDATA));
            dropsData = new DropData<>(this, getConfig().loadSection(Paths.TASK_INFO_DROPDATA));
            toolData = new ToolData<>(this, getConfig().loadSection(Paths.TASK_INFO_TOOLDATA));
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
        public @NotNull BreedMobTaskType<T> getType() {
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

            public GuiEditor(Player player, Gui previousHolder) {
                super(player, previousHolder);
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
    public String getDefaultUnstartedDescription(@NotNull Task<T> task) {
        if (!(task instanceof BreedMobTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), null);
        if (txt == null) {
            txt = "&9{action:breed} &e" + Holders.TASK_MAX_PROGRESS + " &9{entities}";
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), txt);

        }
        return Translations.replaceAll(txt).replace("{entities}", DataUtils.getEntityHolder(t.getEntityData()));
    }

    @Override
    public String getDefaultProgressDescription(@NotNull Task<T> task) {
        if (!(task instanceof BreedMobTask t))
            return null;
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
    public String getDefaultCompleteDescription(@NotNull Task<T> task) {
        if (!(task instanceof BreedMobTask t))
            return null;
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