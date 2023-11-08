package emanondev.deepquests.generic.tasktypes;

import emanondev.core.YMLSection;
import emanondev.deepquests.Holders;
import emanondev.deepquests.Translations;
import emanondev.deepquests.data.DropData;
import emanondev.deepquests.data.NPCData;
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
import net.citizensnpcs.api.event.NPCDeathEvent;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NPCKillTaskType<T extends User<T>> extends ATaskType<T> {
    public NPCKillTaskType(QuestManager<T> manager) {
        super(ID, manager);
    }

    private final static String ID = "kill_npc";

    @Override
    public Material getGuiMaterial() {
        return Material.GOLDEN_SWORD;
    }

    @Override
    public List<String> getDescription() {
        List<String> list = new ArrayList<>();
        list.add("&7Player has to kill NPC");
        return list;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onNpcDeath(NPCDeathEvent event) {
        if (event.getNPC().getEntity() == null || !(event.getNPC().getEntity() instanceof LivingEntity)
                || ((LivingEntity) event.getNPC().getEntity()).getKiller() == null
                || ((LivingEntity) event.getNPC().getEntity()).getKiller().hasMetadata("NPC"))
            return;
        Player p = ((LivingEntity) event.getNPC().getEntity()).getKiller();
        if (p == null)
            return;
        T user = getManager().getUserManager().getUser(p);
        if (user == null)
            return;
        List<Task<T>> tasks = user.getActiveTasks(this);
        if (tasks == null || tasks.isEmpty())
            return;
        for (Task<T> tTask : tasks) {
            NPCKillTask task = (NPCKillTask) tTask;
            if (task.isWorldAllowed(p.getWorld()) && task.npcData.isValidNPC(event.getNPC())
                    && task.toolData.isValidTool(p.getInventory().getItemInMainHand(), p)) {
                if (task.onProgress(user, 1, p, false) > 0) {
                    if (task.dropsData.removeExpDrops())
                        event.setDroppedExp(0);
                    if (task.dropsData.removeItemDrops())
                        event.getDrops().clear();
                }
            }
        }
    }

    public class NPCKillTask extends ATask<T> {

        private final NPCData<T, NPCKillTask> npcData;
        private final ToolData<T, NPCKillTask> toolData;
        private final DropData<T, NPCKillTask> dropsData;

        public NPCKillTask(int id, Mission<T> mission, YMLSection section) {
            super(id, mission, NPCKillTaskType.this, section);
            npcData = new NPCData<>(this, getConfig().loadSection(Paths.TASK_INFO_NPCDATA));
            toolData = new ToolData<>(this, getConfig().loadSection(Paths.TASK_INFO_TOOLDATA));
            dropsData = new DropData<>(this, getConfig().loadSection(Paths.TASK_INFO_DROPDATA));
        }

        public NPCData<T, NPCKillTask> getNPCData() {
            return npcData;
        }

        public DropData<T, NPCKillTask> getDropData() {
            return dropsData;
        }

        public ToolData<T, NPCKillTask> getWeaponData() {
            return toolData;
        }

        public @NotNull NPCKillTaskType<T> getType() {
            return NPCKillTaskType.this;
        }

        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.addAll(npcData.getInfo());

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
        public Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ATaskGuiEditor {

            public GuiEditor(Player player, Gui previousHolder) {
                super(player, previousHolder);
                this.putButton(27, npcData.getNPCSelectorButton(this));
                this.putButton(36, dropsData.getItemDropsFlagButton(this));
                this.putButton(37, dropsData.getExpDropsFlagButton(this));
                toolData.setupButtons("&6Weapon editor", this, 45);
            }
        }
    }

    @Override
    public @NotNull Task<T> getInstance(int id, @NotNull Mission<T> mission, YMLSection section) {
        return new NPCKillTask(id, mission, section);
    }

    @Override
    public String getDefaultUnstartedDescription(@NotNull Task<T> task) {
        if (!(task instanceof NPCKillTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), null);
        if (txt == null) {
            txt = "&9{action:kill} &e" + Holders.TASK_MAX_PROGRESS + " &9{npcs}";
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), txt);

        }
        return Translations.replaceAll(txt).replace("{npcs}", DataUtils.getNPCHolder(t.getNPCData()));
    }

    @Override
    public String getDefaultCompleteDescription(@NotNull Task<T> task) {
        if (!(task instanceof NPCKillTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), null);
        if (txt == null) {
            txt = "&a{action:kill} &e" + Holders.TASK_MAX_PROGRESS + " &a{npcs} "
                    + Translations.translateAction("completed");
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), txt);

        }
        return Translations.replaceAll(txt).replace("{npcs}", DataUtils.getNPCHolder(t.getNPCData()));
    }

    @Override
    public String getDefaultProgressDescription(@NotNull Task<T> task) {
        if (!(task instanceof NPCKillTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), null);
        if (txt == null) {
            txt = "&9{action:killed} &e" + Holders.TASK_CURRENT_PROGRESS + " &9{conjun:of} &e"
                    + Holders.TASK_MAX_PROGRESS + " &9{npcs}";
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), txt);

        }
        return Translations.replaceAll(txt).replace("{npcs}", DataUtils.getNPCHolder(t.getNPCData()));
    }
}
