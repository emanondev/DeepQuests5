package emanondev.deepquests.generic.tasktypes;

import emanondev.core.UtilsInventory;
import emanondev.core.UtilsInventory.LackManage;
import emanondev.core.YMLSection;
import emanondev.deepquests.Holders;
import emanondev.deepquests.Translations;
import emanondev.deepquests.data.ItemStackData;
import emanondev.deepquests.data.NPCData;
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
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NPCDeliverTaskType<T extends User<T>> extends ATaskType<T> {
    private final static String ID = "deliver_to_npc";

    public NPCDeliverTaskType(QuestManager<T> manager) {
        super(ID, manager);
    }

    @Override
    public Material getGuiMaterial() {
        return Material.CHEST;
    }

    @Override
    public List<String> getDescription() {
        List<String> list = new ArrayList<>();
        list.add("&7Player has to deliver items to NPC");
        return list;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onRightClick(NPCRightClickEvent event) {
        Player p = event.getClicker();
        T user = getManager().getUserManager().getUser(p);
        if (user == null)
            return;
        for (Task<T> tTask : new ArrayList<>(user.getActiveTasks(this))) {
            NPCDeliverTask task = (NPCDeliverTask) tTask;
            if (task.isWorldAllowed(p.getWorld()) && task.npcData.isValidNPC(event.getNPC())) {
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

    @Override
    public @NotNull Task<T> getInstance(int id, @NotNull Mission<T> mission, YMLSection section) {
        return new NPCDeliverTask(id, mission, section);
    }

    @Override
    public String getDefaultUnstartedDescription(@NotNull Task<T> task) {
        if (!(task instanceof NPCDeliverTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.loadString(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED),
                "&9{action:deliver} &e" + Holders.TASK_MAX_PROGRESS + " {items} &9{conjun:to} {npcs}");
        return Translations.replaceAll(txt).replace("{npcs}", DataUtils.getNPCHolder(t.getNPCData())).replace("{items}",
                DataUtils.getItemsHolder(t.getItemStackData()));
    }

    @Override
    public String getDefaultCompleteDescription(@NotNull Task<T> task) {
        if (!(task instanceof NPCDeliverTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.loadString(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), "&a{action:deliver} &e"
                + Holders.TASK_MAX_PROGRESS + " {items} &a{conjun:to} {npcs} {action:completed}");
        return Translations.replaceAll(txt).replace("{npcs}", DataUtils.getNPCHolder(t.getNPCData())).replace("{items}",
                DataUtils.getItemsHolder(t.getItemStackData()));
    }

    @Override
    public String getDefaultProgressDescription(@NotNull Task<T> task) {
        if (!(task instanceof NPCDeliverTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.loadString(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS),
                "&9{action:delivered} &e" + Holders.TASK_CURRENT_PROGRESS + " &9{conjun:of} &e"
                        + Holders.TASK_MAX_PROGRESS + " {items} &9{conjun:to} {npcs}");
        return Translations.replaceAll(txt).replace("{npcs}", DataUtils.getNPCHolder(t.getNPCData())).replace("{items}",
                DataUtils.getItemsHolder(t.getItemStackData()));
    }

    public class NPCDeliverTask extends ATask<T> {

        private final NPCData<T, NPCDeliverTask> npcData;
        private final ItemStackData<T, NPCDeliverTask> itemData;

        public NPCDeliverTask(int id, Mission<T> mission, YMLSection section) {
            super(id, mission, NPCDeliverTaskType.this, section);
            npcData = new NPCData<>(this, getConfig().loadSection(Paths.TASK_INFO_NPCDATA));
            itemData = new ItemStackData<>(this, getConfig().loadSection(Paths.TASK_INFO_ITEM));
        }

        public NPCData<T, NPCDeliverTask> getNPCData() {
            return npcData;
        }

        public ItemStackData<T, NPCDeliverTask> getItemStackData() {
            return itemData;
        }

        public @NotNull NPCDeliverTaskType<T> getType() {
            return NPCDeliverTaskType.this;
        }

        public @NotNull List<String> getInfo() {
            List<String> info = super.getInfo();
            info.addAll(npcData.getInfo());

            info.add("&9Item:");
            ItemStack item = itemData.getItem();
            if (item == null)
                info.add("&cNot setted");
            else {
                info.add("  &9Material: &e" + item.getType());
                if (item.hasItemMeta()) {
                    ItemMeta meta = item.getItemMeta();
                    assert meta != null;
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
        public @NotNull Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ATaskGuiEditor {

            public GuiEditor(Player player, Gui previousHolder) {
                super(player, previousHolder);
                getItemStackData().setupButtons(this, 27);
                this.putButton(28, getNPCData().getNPCSelectorButton(this));
            }
        }
    }

}
