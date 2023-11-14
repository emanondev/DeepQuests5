package emanondev.deepquests.generic.tasktypes;

import emanondev.core.YMLSection;
import emanondev.deepquests.Holders;
import emanondev.deepquests.Translations;
import emanondev.deepquests.data.LocationData;
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
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InteractAtTaskType<T extends User<T>> extends ATaskType<T> {

    private final static String ID = "interact_at";

    public InteractAtTaskType(@NotNull QuestManager<T> manager) {
        super(ID, manager);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null)
            return;
        T user = getManager().getUserManager().getUser(event.getPlayer());
        if (user == null)
            return;
        for (Task<T> tTask : new ArrayList<>(user.getActiveTasks(this))) {
            InteractAtTask task = (InteractAtTask) tTask;
            if (task.isWorldAllowed(event.getPlayer().getWorld())
                    && task.locData.isValidLocation(event.getClickedBlock().getLocation())
                    && task.toolData.isValidTool(event.getItem(), event.getPlayer()))
                task.onProgress(user, 1, event.getPlayer(), false);
        }
    }

    @Override
    public @NotNull Task<T> getInstance(int id, @NotNull Mission<T> mission, YMLSection section) {
        return new InteractAtTask(id, mission, section);
    }

    @Override
    public Material getGuiMaterial() {
        return Material.STICK;
    }

    @Override
    public List<String> getDescription() {
        List<String> list = new ArrayList<>();
        list.add("&7Player has to interact at location");
        return list;
    }

    @Override
    public String getDefaultUnstartedDescription(@NotNull Task<T> task) {
        if (!(task instanceof InteractAtTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), null);
        if (txt == null) {
            txt = "&9{action:touch} &e" + Holders.TASK_MAX_PROGRESS + " &9{action:times} {conjun:at} &e{location}";
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), txt);

        }
        return Translations.replaceAll(txt).replace("{location}", DataUtils.getLocationHolder(t.getLocationData()));
    }

    @Override
    public String getDefaultProgressDescription(@NotNull Task<T> task) {
        if (!(task instanceof InteractAtTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), null);
        if (txt == null) {
            txt = "&9{action:touched} &e" + Holders.TASK_CURRENT_PROGRESS + " &9{conjun:of} &e"
                    + Holders.TASK_MAX_PROGRESS + " &9{action:times} {conjun:at} &e{location}";
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), txt);

        }
        return Translations.replaceAll(txt).replace("{location}", DataUtils.getLocationHolder(t.getLocationData()));
    }

    @Override
    public String getDefaultCompleteDescription(@NotNull Task<T> task) {
        if (!(task instanceof InteractAtTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), null);
        if (txt == null) {
            txt = "&a{action:touch} &e" + Holders.TASK_MAX_PROGRESS + " &a{action:times} {conjun:at} &e{location} &a"
                    + Translations.translateAction("completed");
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), txt);

        }
        return Translations.replaceAll(txt).replace("{location}", DataUtils.getLocationHolder(t.getLocationData()));
    }

    public class InteractAtTask extends ATask<T> {
        private final LocationData<T, InteractAtTask> locData;
        private final ToolData<T, InteractAtTask> toolData;

        public InteractAtTask(int id, Mission<T> mission, YMLSection section) {
            super(id, mission, InteractAtTaskType.this, section);
            locData = new LocationData<>(this,
                    getConfig().loadSection(Paths.TASK_INFO_LOCATIONDATA));
            toolData = new ToolData<>(this, getConfig().loadSection(Paths.TASK_INFO_TOOLDATA));
        }

        public LocationData<T, InteractAtTask> getLocationData() {
            return locData;
        }

        public ToolData<T, InteractAtTask> getToolData() {
            return toolData;
        }

        @Override
        public @NotNull InteractAtTaskType<T> getType() {
            return InteractAtTaskType.this;
        }

        public @NotNull List<String> getInfo() {
            List<String> info = super.getInfo();
            info.addAll(locData.getInfo());

            if (toolData.isEnabled()) {
                info.add("&9Tool Info:");
                info.addAll(toolData.getInfo());
            }
            return info;
        }

        @Override
        public @NotNull Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ATaskGuiEditor {

            public GuiEditor(Player player, Gui previusHolder) {
                super(player, previusHolder);
                this.putButton(27, locData.getWorldButton(this));
                this.putButton(28, locData.getXButton(this));
                this.putButton(29, locData.getYButton(this));
                this.putButton(30, locData.getZButton(this));
                toolData.setupButtons("&6Tool Info Button", this, 45);
            }
        }

    }
}