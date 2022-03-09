package emanondev.deepquests.generic.tasktypes;

import emanondev.core.YMLSection;
import emanondev.deepquests.Holders;
import emanondev.deepquests.Translations;
import emanondev.deepquests.data.BlockTypeData;
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
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.List;

public class PlaceBlockTaskType<T extends User<T>> extends ATaskType<T> {
    public PlaceBlockTaskType(QuestManager<T> manager) {
        super(ID, manager);
    }

    private final static String ID = "place_block";

    @Override
    public Material getGuiMaterial() {
        return Material.BRICKS;
    }

    @Override
    public List<String> getDescription() {
        List<String> list = new ArrayList<>();
        list.add("&7Player has to place blocks");
        return list;
    }

    @Override
    public Task<T> getInstance(int id, Mission<T> mission, YMLSection section) {
        return new PlaceBlockTask(id, mission, section);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onBlockPlace(BlockPlaceEvent event) {
        event.getPlayer();
        T user = getManager().getUserManager().getUser(event.getPlayer());
        if (user == null)
            return;
        List<Task<T>> tasks = user.getActiveTasks(this);
        if (tasks == null || tasks.isEmpty())
            return;
        for (int i = 0; i < tasks.size(); i++) {
            PlaceBlockTask task = (PlaceBlockTask) tasks.get(i);
            if (task.isWorldAllowed(event.getPlayer().getWorld())
                    && task.blockData.isValidMaterial(event.getBlock().getType()))
                task.onProgress(user, 1, event.getPlayer(), false);
        }
    }

    public class PlaceBlockTask extends ATask<T> {

        private final BlockTypeData<T, PlaceBlockTask> blockData;

        public PlaceBlockTask(int id, Mission<T> mission, YMLSection section) {
            super(id, mission, PlaceBlockTaskType.this, section);
            blockData = new BlockTypeData<>(this,
                    getConfig().loadSection(Paths.TASK_INFO_BLOCKDATA));
        }

        public BlockTypeData<T, PlaceBlockTask> getBlockTypeData() {
            return blockData;
        }

        public PlaceBlockTaskType<T> getType() {
            return PlaceBlockTaskType.this;
        }

        public List<String> getInfo() {
            List<String> info = super.getInfo();
            if (blockData.areMaterialsWhitelist()) {
                info.add("&9Materials &aAllowed&9:");
            } else {
                info.add("&9Materials &cUnallowed&9:");
            }
            for (Material mat : blockData.getMaterials())
                info.add("&9  - &e" + mat.toString());
            return info;
        }

        @Override
        public Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ATaskGuiEditor {

            public GuiEditor(Player player, Gui previousHolder) {
                super(player, previousHolder);
                this.putButton(27, blockData.getBlockEditorButton(this));
            }
        }
    }

    @Override
    public String getDefaultUnstartedDescription(Task<T> task) {
        if (!(task instanceof PlaceBlockTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), null);
        if (txt == null) {
            txt = "&9{action:place} &e" + Holders.TASK_MAX_PROGRESS + " &9{blocks}";
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), txt);

        }
        return Translations.replaceAll(txt).replace("{blocks}", DataUtils.getBlockHolder(t.getBlockTypeData()));
    }

    @Override
    public String getDefaultCompleteDescription(Task<T> task) {
        if (!(task instanceof PlaceBlockTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), null);
        if (txt == null) {
            txt = "&a{action:place} &e" + Holders.TASK_MAX_PROGRESS + " &a{blocks} "
                    + Translations.translateAction("completed");
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), txt);

        }
        return Translations.replaceAll(txt).replace("{blocks}", DataUtils.getBlockHolder(t.getBlockTypeData()));
    }

    @Override
    public String getDefaultProgressDescription(Task<T> task) {
        if (!(task instanceof PlaceBlockTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), null);
        if (txt == null) {
            txt = "&9{action:placed} &e" + Holders.TASK_CURRENT_PROGRESS + " &9{conjun:of} &e"
                    + Holders.TASK_MAX_PROGRESS + " &9{blocks}";
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), txt);

        }
        return Translations.replaceAll(txt).replace("{blocks}", DataUtils.getBlockHolder(t.getBlockTypeData()));
    }
}
