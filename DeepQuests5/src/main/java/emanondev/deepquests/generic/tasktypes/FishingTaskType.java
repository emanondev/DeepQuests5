package emanondev.deepquests.generic.tasktypes;

import emanondev.core.YMLSection;
import emanondev.deepquests.Holders;
import emanondev.deepquests.Translations;
import emanondev.deepquests.data.DropData;
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
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FishingTaskType<T extends User<T>> extends ATaskType<T> {
    private static final String ID = "fishing";

    public FishingTaskType(@NotNull QuestManager<T> manager) {
        super(ID, manager);
    }

    private static ItemStack getFishingRod(Player p) {
        ItemStack hand = p.getInventory().getItemInMainHand();
        if (hand.getType() == Material.FISHING_ROD)
            return hand;
        ItemStack offHand = p.getInventory().getItemInOffHand();
        if (offHand.getType() == Material.FISHING_ROD)
            return offHand;
        return hand;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    private void onFishing(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH)
            return;
        T user = getManager().getUserManager().getUser(event.getPlayer());
        if (user == null)
            return;
        for (Task<T> tTask : new ArrayList<>(user.getActiveTasks(this))) {
            FishingTask task = (FishingTask) tTask;
            if (task.isWorldAllowed(event.getPlayer().getWorld())
                    && task.fishingRod.isValidTool(getFishingRod(event.getPlayer()), event.getPlayer())) {
                if (!task.fishedItem.isEnabled() || (event.getCaught() instanceof Item
                        && task.fishedItem.isValidTool(((Item) event.getCaught()).getItemStack(), event.getPlayer()))) {
                    if (task.onProgress(user, 1, event.getPlayer(), false) > 0) {
                        if (task.dropsData.removeExpDrops())
                            event.setExpToDrop(0);
                        if (task.dropsData.removeItemDrops()) {
                            //event.getHook();
                            event.getHook().remove();
                        }
                    }
                }
            }
        }
    }

    @Override
    public Material getGuiMaterial() {
        return Material.FISHING_ROD;
    }

    @Override
    public List<String> getDescription() {
        List<String> list = new ArrayList<>();
        list.add("&7Player has to fish items");
        return list;
    }

    @Override
    public @NotNull Task<T> getInstance(int id, @NotNull Mission<T> mission, YMLSection section) {
        return new FishingTask(id, mission, section);
    }

    @Override
    public String getDefaultUnstartedDescription(@NotNull Task<T> task) {
        if (!(task instanceof FishingTaskType.FishingTask))
            return null;
        // FishingTask t = (FishingTask) task;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), null);
        if (txt == null) {
            txt = "&9{action:fish} &e" + Holders.TASK_MAX_PROGRESS + " &9{action:times}";
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), txt);

        }
        return Translations.replaceAll(txt);
    }

    @Override
    public String getDefaultProgressDescription(@NotNull Task<T> task) {
        if (!(task instanceof FishingTaskType.FishingTask))
            return null;
        // FishingTask t = (FishingTask) task;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), null);
        if (txt == null) {
            txt = "&9{action:fished} &e" + Holders.TASK_CURRENT_PROGRESS + " &9{conjun:of} &e"
                    + Holders.TASK_MAX_PROGRESS + " &9{action:times}";
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), txt);

        }
        return Translations.replaceAll(txt);
    }

    @Override
    public String getDefaultCompleteDescription(@NotNull Task<T> task) {
        if (!(task instanceof FishingTaskType.FishingTask))
            return null;
        // FishingTask t = (FishingTask) task;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), null);
        if (txt == null) {
            txt = "&a{action:fish} &e" + Holders.TASK_MAX_PROGRESS + " &a{action:times} " + Translations.translateAction("completed");
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), txt);

        }
        return Translations.replaceAll(txt);
    }

    public class FishingTask extends ATask<T> {
        private final DropData<T, FishingTask> dropsData;
        private final ToolData<T, FishingTask> fishingRod;
        private final ToolData<T, FishingTask> fishedItem;

        public FishingTask(int id, Mission<T> mission, YMLSection section) {
            super(id, mission, FishingTaskType.this, section);
            dropsData = new DropData<>(this, getConfig().loadSection(Paths.TASK_INFO_DROPDATA));
            fishingRod = new ToolData<>(this, getConfig().loadSection(Paths.TASK_INFO_TOOLDATA));
            fishedItem = new ToolData<>(this, getConfig().loadSection(Paths.TASK_INFO_TOOLDATA_TARGET));
        }

        public DropData<T, FishingTask> getDropData() {
            return dropsData;
        }

        public ToolData<T, FishingTask> getFishingRodData() {
            return fishingRod;
        }

        public ToolData<T, FishingTask> getFishedItemData() {
            return fishedItem;
        }

        @Override
        public @NotNull FishingTaskType<T> getType() {
            return FishingTaskType.this;
        }

        public @NotNull List<String> getInfo() {
            List<String> info = super.getInfo();
            if (dropsData.removeExpDrops())
                info.add("&9Exp Drops: &cDisabled");
            if (dropsData.removeItemDrops())
                info.add("&9Item Drops: &cDisabled");
            if (fishingRod.isEnabled()) {
                info.add("&9Fishing Rod:");
                info.addAll(fishingRod.getInfo());
            }

            if (fishedItem.isEnabled()) {
                info.add("&9Fished Item:");
                info.addAll(fishedItem.getInfo());
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
                this.putButton(36, dropsData.getItemDropsFlagButton(this));
                this.putButton(37, dropsData.getExpDropsFlagButton(this));
                fishingRod.setupButtons("&6Fishing Rod Button", this, 45);
                fishedItem.setupButtons("&6Fished Item Button", this, 72);
            }
        }
    }

}
