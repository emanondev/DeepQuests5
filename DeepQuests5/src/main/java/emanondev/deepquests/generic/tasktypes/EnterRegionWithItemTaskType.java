package emanondev.deepquests.generic.tasktypes;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import emanondev.core.UtilsInventory;
import emanondev.core.UtilsInventory.LackManage;
import emanondev.core.YMLSection;
import emanondev.deepquests.Translations;
import emanondev.deepquests.data.ItemStackData;
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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class EnterRegionWithItemTaskType<T extends User<T>> extends ATaskType<T> {
    public EnterRegionWithItemTaskType(QuestManager<T> manager) {
        super(ID, manager);
    }

    private final static String ID = "enter_region_with_item";

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onRegionEnter(RegionEnteredEvent event) {
        Player p = event.getPlayer();
        T user = getManager().getUserManager().getUser(p);
        if (user == null)
            return;
        List<Task<T>> tasks = user.getActiveTasks(this);
        if (tasks == null || tasks.isEmpty())
            return;
        for (int i = 0; i < tasks.size(); i++) {
            EnterRegionWithItemTask task = (EnterRegionWithItemTask) tasks.get(i);
            if (task.isWorldAllowed(p.getWorld()) && task.regionInfo.isValidRegion(event.getRegion())) {
                ItemStack targetItem = task.itemData.getItem();
                if (targetItem == null)
                    continue;
                int removedAmount = UtilsInventory.removeAmount(p, targetItem,
                        task.getMaxProgress() - user.getTaskProgress(task), LackManage.REMOVE_MAX_POSSIBLE);
                if (removedAmount > 0) {
                    task.onProgress(user, removedAmount, p, false);
                }
            }
        }
    }

    public class EnterRegionWithItemTask extends ATask<T> {
        private final RegionsData<T, EnterRegionWithItemTask> regionInfo;
        private final ItemStackData<T, EnterRegionWithItemTask> itemData;

        public EnterRegionWithItemTask(int id, Mission<T> mission, YMLSection section) {
            super(id, mission, EnterRegionWithItemTaskType.this, section);
            regionInfo = new RegionsData<>(this,
                    getConfig().loadSection(Paths.TASK_INFO_REGIONSDATA));
            itemData = new ItemStackData<>(this,
                    getConfig().loadSection(Paths.TASK_INFO_ITEM));
        }

        public RegionsData<T, EnterRegionWithItemTask> getRegionsData() {
            return regionInfo;
        }

        public ItemStackData<T, EnterRegionWithItemTask> getItemStackData() {
            return itemData;
        }

        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.addAll(regionInfo.getInfo());

            info.add("&9Item:");
            ItemStack item = itemData.getItem();
            if (item == null)
                info.add("&cNot setted");
            else {
                info.add("  &9Material: &e" + item.getType());
                if (item.hasItemMeta()) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta.hasDisplayName())
                        info.add("  &9DisplayName: &e" + meta.getDisplayName());
                    if (meta.hasLore()) {
                        info.add("  &9Lore:");
                        for (String line : meta.getLore())
                            info.add("  &9- &e" + line);
                    }
                    if (meta.hasEnchants()) {
                        info.add("  &9Enchants:");
                        for (Enchantment ench : meta.getEnchants().keySet())
                            info.add("  &9- &e" + ench.getKey().getKey() + " " + meta.getEnchantLevel(ench));
                    }
                    if (meta.getItemFlags().size() > 0) {
                        info.add("  &9Flags:");
                        for (ItemFlag flag : meta.getItemFlags())
                            info.add("  &9- &e" + flag);
                    }
                    if (meta.isUnbreakable())
                        info.add("  &9Unbreakable: &etrue");
                }
            }
            return info;
        }

        @Override
        public EnterRegionWithItemTaskType<T> getType() {
            return EnterRegionWithItemTaskType.this;
        }

        @Override
        public Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ATaskGuiEditor {

            public GuiEditor(Player player, Gui previusHolder) {
                super(player, previusHolder);
                itemData.setupButtons(this, 27);
                this.putButton(28, regionInfo.getRegionSelectorButton(this));
            }
        }
    }

    @Override
    public Task<T> getInstance(int id, Mission<T> mission, YMLSection section) {
        return new EnterRegionWithItemTask(id, mission, section);
    }

    @Override
    public Material getGuiMaterial() {
        return Material.END_CRYSTAL;
    }

    @Override
    public List<String> getDescription() {
        List<String> list = new ArrayList<>();
        list.add("&7Player has to enter inside region");
        return list;
    }

    @Override
    public String getDefaultUnstartedDescription(Task<T> task) {
        if (!(task instanceof EnterRegionWithItemTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), null);
        if (txt == null) {
            txt = "&9{action:enter} {regions}";
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), txt);

        }
        return Translations.replaceAll(txt).replace("{regions}", DataUtils.getRegionHolder(t.getRegionsData()));
    }

    @Override
    public String getDefaultProgressDescription(Task<T> task) {
        if (!(task instanceof EnterRegionWithItemTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), null);
        if (txt == null) {
            txt = "&9{action:enter} {regions}";
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), txt);

        }
        return Translations.replaceAll(txt).replace("{regions}", DataUtils.getRegionHolder(t.getRegionsData()));
    }

    @Override
    public String getDefaultCompleteDescription(Task<T> task) {
        if (!(task instanceof EnterRegionWithItemTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), null);
        if (txt == null) {
            txt = "&a{action:enter} {regions} " + Translations.translateAction("completed");
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), txt);

        }
        return Translations.replaceAll(txt).replace("{regions}", DataUtils.getRegionHolder(t.getRegionsData()));
    }

}
