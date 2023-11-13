package emanondev.deepquests.generic.tasktypes;

import emanondev.core.Hooks;
import emanondev.core.YMLSection;
import emanondev.deepquests.Holders;
import emanondev.deepquests.Translations;
import emanondev.deepquests.data.BlockTypeData;
import emanondev.deepquests.data.DropData;
import emanondev.deepquests.data.ToolData;
import emanondev.deepquests.data.VirginBlockData;
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
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BreakBlockTaskType<T extends User<T>> extends ATaskType<T> {
    private final static String ID = "break_block";


    public BreakBlockTaskType(QuestManager<T> manager) {
        super(ID, manager);
    }

    @Override
    public List<String> getDescription() {
        List<String> list = new ArrayList<>();
        list.add("&7Player has to break blocks");
        return list;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onBlockBreak(BlockBreakEvent event) {
        T user = getManager().getUserManager().getUser(event.getPlayer());
        if (user == null)
            return;
        for (Task<T> tTask : new ArrayList<>(user.getActiveTasks(this))) {
            BreakBlockTask task = (BreakBlockTask) tTask;
            if (task.isWorldAllowed(event.getPlayer().getWorld())
                    && task.blockData.isValidMaterial(event.getBlock().getType())
                    && task.virginBlockData.isValidBlock(event.getBlock()) && task.toolData
                    .isValidTool(event.getPlayer().getInventory().getItemInMainHand(), event.getPlayer()))
                if (task.onProgress(user, 1, event.getPlayer(), false) > 0) {
                    if (task.dropsData.removeExpDrops())
                        event.setExpToDrop(0);
                    if (task.dropsData.removeItemDrops())
                        event.setDropItems(false);
                }
        }
    }

    @Override
    public @NotNull Task<T> getInstance(int id, @NotNull Mission<T> mission, YMLSection section) {
        return new BreakBlockTask(id, mission, section);
    }

    @Override
    public Material getGuiMaterial() {
        return Material.IRON_PICKAXE;
    }

    @Override
    public String getDefaultUnstartedDescription(@NotNull Task<T> task) {
        if (!(task instanceof BreakBlockTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), null);
        if (txt == null) {
            txt = "&9{action:break} &e" + Holders.TASK_MAX_PROGRESS + " &9{blocks}";
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), txt);

        }
        return Translations.replaceAll(txt.replace("{blocks}", DataUtils.getBlockHolder(t.getBlockTypeData())));
    }

    @Override
    public String getDefaultProgressDescription(@NotNull Task<T> task) {
        if (!(task instanceof BreakBlockTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), null);
        if (txt == null) {
            txt = "&9{action:broken} &e" + Holders.TASK_CURRENT_PROGRESS + " &9{conjun:of} &e"
                    + Holders.TASK_MAX_PROGRESS + " &9{blocks}";
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), txt);

        }
        return Translations.replaceAll(txt.replace("{blocks}", DataUtils.getBlockHolder(t.getBlockTypeData())));
    }

    @Override
    public String getDefaultCompleteDescription(@NotNull Task<T> task) {
        if (!(task instanceof BreakBlockTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), null);
        if (txt == null) {
            txt = "&a{action:broken} &e" + Holders.TASK_CURRENT_PROGRESS + " &a{blocks} "
                    + Translations.translateAction("completed");
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), txt);

        }
        return Translations.replaceAll(txt.replace("{blocks}", DataUtils.getBlockHolder(t.getBlockTypeData())));
    }

    public class BreakBlockTask extends ATask<T> {

        private final BlockTypeData<T, BreakBlockTask> blockData;
        private final VirginBlockData<T, BreakBlockTask> virginBlockData;
        private final DropData<T, BreakBlockTask> dropsData;
        private final ToolData<T, BreakBlockTask> toolData;

        public BreakBlockTask(int id, Mission<T> mission, YMLSection section) {
            super(id, mission, BreakBlockTaskType.this, section);
            blockData = new BlockTypeData<>(this,
                    getConfig().loadSection(Paths.TASK_INFO_BLOCKDATA));
            virginBlockData = new VirginBlockData<>(this,
                    getConfig().loadSection(Paths.TASK_INFO_VIRGINBLOCKDATA));
            dropsData = new DropData<>(this, getConfig().loadSection(Paths.TASK_INFO_DROPDATA));
            toolData = new ToolData<>(this, getConfig().loadSection(Paths.TASK_INFO_TOOLDATA));
        }

        public BlockTypeData<T, BreakBlockTask> getBlockTypeData() {
            return blockData;
        }

        public VirginBlockData<T, BreakBlockTask> getVirginBlockData() {
            return virginBlockData;
        }

        public DropData<T, BreakBlockTask> getDropData() {
            return dropsData;
        }

        public ToolData<T, BreakBlockTask> getToolData() {
            return toolData;
        }

        /*
         * public Navigator getNavigator() { super.getNavigator();
         * blockData.getNavigator(); virginBlockData.getNavigator();
         * dropsData.getNavigator(); toolData.getNavigator(); return nav; }
         */

        public @NotNull List<String> getInfo() {
            List<String> info = super.getInfo();
            if (blockData.areMaterialsWhitelist()) {
                info.add("&9Materials &aAllowed&9:");
            } else {
                info.add("&9Materials &cUnallowed&9:");
            }
            for (Material mat : blockData.getMaterials())
                info.add("&9  - &e" + mat.toString());
            if (Hooks.isVirginBlockPluginEnabled()) {
                if (virginBlockData.isVirginCheckEnabled())
                    info.add("&9Check Block is Virgin: &eEnabled");
                else
                    info.add("&9Check Block is Virgin: &cDisabled");
            }
            if (dropsData.removeExpDrops())
                info.add("&9Exp Drops: &cDisabled");
            if (dropsData.removeItemDrops())
                info.add("&9Item Drops: &cDisabled");
            if (toolData.isEnabled()) {
                info.add("&9Tool Check:");
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
                this.putButton(27, blockData.getBlockEditorButton(this));
                this.putButton(28, virginBlockData.getVirginStatusEditor(this));
                this.putButton(36, dropsData.getItemDropsFlagButton(this));
                this.putButton(37, dropsData.getExpDropsFlagButton(this));
                toolData.setupButtons("&6Tool Button", this, 45);
            }
        }
    }

}