package emanondev.deepquests.generic.tasktypes;

import com.mewin.worldguardregionapi.events.RegionLeftEvent;
import emanondev.core.YMLSection;
import emanondev.deepquests.Translations;
import emanondev.deepquests.data.RegionsData;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LeaveRegionTaskType<T extends User<T>> extends ATaskType<T> {
    private final static String ID = "leave_region";

    public LeaveRegionTaskType(QuestManager<T> manager) {
        super(ID, manager);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onRegionLeave(RegionLeftEvent event) {
        Player p = event.getPlayer();
        T user = getManager().getUserManager().getUser(p);
        if (user == null)
            return;
        for (Task<T> tTask : new ArrayList<>(user.getActiveTasks(this))) {
            LeaveRegionTask task = (LeaveRegionTask) tTask;
            if (task.isWorldAllowed(p.getWorld()) && task.regionInfo.isValidRegion(event.getRegion())) {
                task.onProgress(user, 1, p, false);
            }
        }
    }

    @Override
    public @NotNull Task<T> getInstance(int id, @NotNull Mission<T> mission, YMLSection section) {
        return new LeaveRegionTask(id, mission, section);
    }

    @Override
    public Material getGuiMaterial() {
        return Material.END_CRYSTAL;
    }

    @Override
    public List<String> getDescription() {
        List<String> list = new ArrayList<>();
        list.add("&7Player has to leave/exit region");
        return list;
    }

    @Override
    public String getDefaultUnstartedDescription(@NotNull Task<T> task) {
        if (!(task instanceof LeaveRegionTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), null);
        if (txt == null) {
            txt = "&9{action:leave} {regions}";
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), txt);

        }
        return Translations.replaceAll(txt).replace("{regions}", DataUtils.getRegionHolder(t.getRegionsData()));
    }

    @Override
    public String getDefaultCompleteDescription(@NotNull Task<T> task) {
        if (!(task instanceof LeaveRegionTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), null);
        if (txt == null) {
            txt = "&a{action:leave} {regions} " + Translations.translateAction("completed");
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), txt);

        }
        return Translations.replaceAll(txt).replace("{regions}", DataUtils.getRegionHolder(t.getRegionsData()));
    }

    @Override
    public String getDefaultProgressDescription(@NotNull Task<T> task) {
        if (!(task instanceof LeaveRegionTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), null);
        if (txt == null) {
            txt = "&9{action:leave} {regions}";
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), txt);

        }
        return Translations.replaceAll(txt).replace("{regions}", DataUtils.getRegionHolder(t.getRegionsData()));
    }

    public class LeaveRegionTask extends ATask<T> {
        private final RegionsData<T, LeaveRegionTask> regionInfo;

        public LeaveRegionTask(int id, Mission<T> mission, YMLSection section) {
            super(id, mission, LeaveRegionTaskType.this, section);
            regionInfo = new RegionsData<>(this,
                    getConfig().loadSection(Paths.TASK_INFO_REGIONSDATA));
        }

        public RegionsData<T, LeaveRegionTask> getRegionsData() {
            return regionInfo;
        }

        @Override
        public @NotNull LeaveRegionTaskType<T> getType() {
            return LeaveRegionTaskType.this;
        }

        public @NotNull List<String> getInfo() {
            List<String> info = super.getInfo();
            info.addAll(regionInfo.getInfo());
            return info;
        }

        @Override
        public @NotNull Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ATaskGuiEditor {

            public GuiEditor(Player player, Gui previusHolder) {
                super(player, previusHolder);
                this.putButton(27, getRegionsData().getRegionSelectorButton(this));
            }
        }
    }
}