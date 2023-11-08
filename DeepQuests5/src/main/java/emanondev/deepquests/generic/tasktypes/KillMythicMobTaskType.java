package emanondev.deepquests.generic.tasktypes;

import emanondev.core.YMLSection;
import emanondev.deepquests.Holders;
import emanondev.deepquests.Translations;
import emanondev.deepquests.data.DropData;
import emanondev.deepquests.data.MythicMobsData;
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
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class KillMythicMobTaskType<T extends User<T>> extends ATaskType<T> {

    public KillMythicMobTaskType(QuestManager<T> manager) {
        super(ID, manager);
    }

    private final static String ID = "kill_mythic_mob";

    @Override
    public Material getGuiMaterial() {
        return Material.IRON_SWORD;
    }

    @Override
    public List<String> getDescription() {
        List<String> list = new ArrayList<>();
        list.add("&7Player has to kill mythic mobs");
        return list;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onEntityDie(MythicMobDeathEvent event) {
        if (event.getKiller() == null || !(event.getKiller() instanceof Player p))
            return;
        T qPlayer = getManager().getUserManager().getUser(p);
        if (qPlayer == null)
            return;
        List<Task<T>> tasks = qPlayer.getActiveTasks(this);
        if (tasks == null || tasks.isEmpty())
            return;
        for (Task<T> tTask : tasks) {
            KillMythicMobTask task = (KillMythicMobTask) tTask;
            if (task.isWorldAllowed(p.getWorld()) && task.entityData.isValidMythicMob(event.getMob())) {
                if (task.onProgress(qPlayer, 1, p, false) > 0) {
                    if (task.dropsData.removeItemDrops())
                        event.setDrops(new ArrayList<>());
                    // if (task.dropsData.removeExpDrops())
                    // event..setExp(0);
                }
            }
        }
    }

    public class KillMythicMobTask extends ATask<T> {

        private final MythicMobsData<T, KillMythicMobTask> entityData;
        private final DropData<T, KillMythicMobTask> dropsData;
        private final ToolData<T, KillMythicMobTask> toolData;

        public KillMythicMobTask(int id, Mission<T> mission, YMLSection section) {
            super(id, mission, KillMythicMobTaskType.this, section);
            dropsData = new DropData<>(this, getConfig().loadSection(Paths.TASK_INFO_DROPDATA));
            entityData = new MythicMobsData<>(this,
                    getConfig().loadSection(Paths.TASK_INFO_MYTHICMOBSDATA));
            toolData = new ToolData<>(this, getConfig().loadSection(Paths.TASK_INFO_TOOLDATA));
        }

        public DropData<T, KillMythicMobTask> getDropData() {
            return dropsData;
        }

        public ToolData<T, KillMythicMobTask> getWeaponData() {
            return toolData;
        }

        public MythicMobsData<T, KillMythicMobTask> getMythicMobsData() {
            return entityData;
        }

        @Override
        public @NotNull KillMythicMobTaskType<T> getType() {
            return KillMythicMobTaskType.this;
        }

        public List<String> getInfo() {
            List<String> info = super.getInfo();
            if (entityData.areInternalNamesWhitelist()) {
                info.add("&9Valid Internal Names:");
                for (String type : entityData.getInternalNames())
                    info.add("  &9- &a" + type);
            } else {
                info.add("&9Invalid Internal Names:");
                for (String type : entityData.getInternalNames())
                    info.add("  &9- &c" + type);
            }
            if (entityData.checkLevel()) {
                info.add("&9Min level: &e" + entityData.getMinLevel());
                info.add("&9Max level: &e" + entityData.getMaxLevel());
            }
            // if (dropsData.removeExpDrops())
            // info.add("&9Exp Drops: &cDisabled");
            if (dropsData.removeItemDrops())
                info.add("&9Item Drops: &cDisabled");
            if (toolData.isEnabled()) {
                info.add("&9Weapon Check:");
                info.addAll(toolData.getInfo());
            }
            return info;
        }

        public Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ATaskGuiEditor {

            public GuiEditor(Player player, Gui previusHolder) {
                super(player, previusHolder);
                this.putButton(27, entityData.getMythicMobsSelectorButton(this));
                this.putButton(28, entityData.getMinLevelButton(this));
                this.putButton(29, entityData.getMaxLevelButton(this));
                this.putButton(36, entityData.getCheckLevelFlag(this));
                // this.putButton(37, dropsData.getExpDropsFlagButton(this));
                this.putButton(38, dropsData.getItemDropsFlagButton(this));
                toolData.setupButtons("&6Weapon Item Button", this, 45);
            }
        }
    }

    @Override
    public @NotNull Task<T> getInstance(int id, @NotNull Mission<T> mission, YMLSection section) {
        return new KillMythicMobTask(id, mission, section);
    }

    @Override
    public String getDefaultUnstartedDescription(@NotNull Task<T> task) {
        if (!(task instanceof KillMythicMobTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), null);
        if (txt == null) {
            txt = "&9{action:kill} &e" + Holders.TASK_MAX_PROGRESS + " &9{mythicmobs}";
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), txt);

        }
        return Translations.replaceAll(txt).replace("{mythicmobs}",
                DataUtils.getMythicMobsHolder(t.getMythicMobsData()));
    }

    @Override
    public String getDefaultProgressDescription(@NotNull Task<T> task) {
        if (!(task instanceof KillMythicMobTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), null);
        if (txt == null) {
            txt = "&9{action:killed} &e" + Holders.TASK_CURRENT_PROGRESS + " &9{conjun:of} &e"
                    + Holders.TASK_MAX_PROGRESS + " &9{mythicmobs}";
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), txt);

        }
        return Translations.replaceAll(txt).replace("{mythicmobs}",
                DataUtils.getMythicMobsHolder(t.getMythicMobsData()));
    }

    @Override
    public String getDefaultCompleteDescription(@NotNull Task<T> task) {
        if (!(task instanceof KillMythicMobTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), null);
        if (txt == null) {
            txt = "&a{action:kill} &e" + Holders.TASK_MAX_PROGRESS + " &a{mythicmobs} "
                    + Translations.translateAction("completed");
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), txt);

        }
        return Translations.replaceAll(txt).replace("{mythicmobs}",
                DataUtils.getMythicMobsHolder(t.getMythicMobsData()));
    }
}