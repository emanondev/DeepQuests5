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
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class KillMobTaskType<T extends User<T>> extends ATaskType<T> {

    private final static String ID = "kill_mob";

    public KillMobTaskType(QuestManager<T> manager) {
        super(ID, manager);
    }

    @Override
    public Material getGuiMaterial() {
        return Material.IRON_SWORD;
    }

    @Override
    public List<String> getDescription() {
        List<String> list = new ArrayList<>();
        list.add("&7Player has to kill mobs");
        return list;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onEntityDie(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null)
            return;
        Player p = event.getEntity().getKiller();
        if (p == null)
            return;
        T user = getManager().getUserManager().getUser(p);
        if (user == null)
            return;
        for (Task<T> tTask : new ArrayList<>(user.getActiveTasks(this))) {
            KillMobTask task = (KillMobTask) tTask;
            if (task.isWorldAllowed(p.getWorld()) && task.entityData.isValidEntity(event.getEntity())
                    && task.toolData.isValidTool(p.getInventory().getItemInMainHand(), p)) {
                if (task.onProgress(user, 1, p, false) > 0) {
                    if (task.dropsData.removeItemDrops())
                        event.getDrops().clear();
                    if (task.dropsData.removeExpDrops())
                        event.setDroppedExp(0);
                }
            }
        }

    }

    @Override
    public @NotNull Task<T> getInstance(int id, @NotNull Mission<T> mission, YMLSection section) {
        return new KillMobTask(id, mission, section);
    }

    @Override
    public String getDefaultUnstartedDescription(@NotNull Task<T> task) {
        if (!(task instanceof KillMobTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), null);
        if (txt == null) {
            txt = "&9{action:kill} &e" + Holders.TASK_MAX_PROGRESS + " &9{entities}";
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), txt);

        }
        return Translations.replaceAll(txt).replace("{entities}", DataUtils.getEntityHolder(t.getEntityData()));
    }

    @Override
    public String getDefaultProgressDescription(@NotNull Task<T> task) {
        if (!(task instanceof KillMobTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), null);
        if (txt == null) {
            txt = "&9{action:killed} &e" + Holders.TASK_CURRENT_PROGRESS + " &9{conjun:of} &e"
                    + Holders.TASK_MAX_PROGRESS + " &9{entities}";
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), txt);

        }
        return Translations.replaceAll(txt).replace("{entities}", DataUtils.getEntityHolder(t.getEntityData()));
    }

    @Override
    public String getDefaultCompleteDescription(@NotNull Task<T> task) {
        if (!(task instanceof KillMobTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), null);
        if (txt == null) {
            txt = "&a{action:killed} &e" + Holders.TASK_MAX_PROGRESS + " &a{entities} "
                    + Translations.translateAction("completed");
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), txt);

        }
        return Translations.replaceAll(txt).replace("{entities}", DataUtils.getEntityHolder(t.getEntityData()));
    }

    public class KillMobTask extends ATask<T> {

        private final EntityData<T, KillMobTask> entityData;
        private final DropData<T, KillMobTask> dropsData;
        private final ToolData<T, KillMobTask> toolData;

        public KillMobTask(int id, Mission<T> mission, YMLSection section) {
            super(id, mission, KillMobTaskType.this, section);
            dropsData = new DropData<>(this, getConfig().loadSection(Paths.TASK_INFO_DROPDATA));
            entityData = new EntityData<>(this, getConfig().loadSection(Paths.TASK_INFO_ENTITYDATA));
            toolData = new ToolData<>(this, getConfig().loadSection(Paths.TASK_INFO_TOOLDATA));
        }

        public DropData<T, KillMobTask> getDropData() {
            return dropsData;
        }

        public ToolData<T, KillMobTask> getWeaponData() {
            return toolData;
        }

        public EntityData<T, KillMobTask> getEntityData() {
            return entityData;
        }

        @Override
        public @NotNull KillMobTaskType<T> getType() {
            return KillMobTaskType.this;
        }

        public @NotNull List<String> getInfo() {
            List<String> info = super.getInfo();
            info.addAll(entityData.getInfo());
            if (dropsData.removeExpDrops())
                info.add("&9Exp Drops: &cDisabled");
            if (dropsData.removeItemDrops())
                info.add("&9Item Drops: &cDisabled");
            if (toolData.isEnabled()) {
                info.add("&9Weapon Check:");
                info.addAll(toolData.getInfo());
            }
            return info;
        }

        @Override
        public @NotNull Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ATaskGuiEditor {

            public GuiEditor(Player player, Gui previousHolder) {
                super(player, previousHolder);
                this.putButton(27, getEntityData().getEntityTypeButton(this));
                this.putButton(28, getEntityData().getSpawnReasonButton(this));
                this.putButton(36, getEntityData().getIgnoreNPCFlagButton(this));
                this.putButton(37, getDropData().getExpDropsFlagButton(this));
                this.putButton(38, getDropData().getItemDropsFlagButton(this));
                getWeaponData().setupButtons("&6Killing Item Button", this, 45);
            }
        }

    }
}
