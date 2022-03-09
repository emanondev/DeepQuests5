package emanondev.deepquests.generic.tasktypes;

import emanondev.core.YMLSection;
import emanondev.deepquests.Holders;
import emanondev.deepquests.Translations;
import emanondev.deepquests.data.NPCData;
import emanondev.deepquests.data.QuestItemData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.ATask;
import emanondev.deepquests.implementations.ATaskType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.*;
import emanondev.deepquests.interfaces.Task.Phase;
import emanondev.deepquests.utils.DataUtils;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.List;

public class DeliverQuestItemTaskType<T extends User<T>> extends ATaskType<T> {
    public DeliverQuestItemTaskType(QuestManager<T> manager) {
        super(ID, manager);
    }

    private final static String ID = "deliver_quest_item_to_npc";

    @Override
    public Material getGuiMaterial() {
        return Material.CHEST;
    }

    @Override
    public List<String> getDescription() {
        List<String> list = new ArrayList<>();
        list.add("&7Player has to deliver quest items to NPC");
        return list;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onRightClick(NPCRightClickEvent event) {
        Player p = (Player) event.getClicker();
        T user = getManager().getUserManager().getUser(p);
        if (user == null)
            return;
        QuestBag<T> bag = user.getQuestBag();
        if (bag == null)
            return;
        List<Task<T>> tasks = user.getActiveTasks(this);
        if (tasks == null || tasks.isEmpty())
            return;
        for (int i = 0; i < tasks.size(); i++) {
            DeliverQuestItemTask task = (DeliverQuestItemTask) tasks.get(i);
            if (task.isWorldAllowed(p.getWorld()) && task.npcData.isValidNPC(event.getNPC())) {
                if (task.getQuestItemData().getQuestItemID() == null)
                    continue;
                String id = task.getQuestItemData().getQuestItemID();
                if (!bag.hasQuestItem(id))
                    return;
                int removedAmount = bag.removeQuestItem(id, task.getMaxProgress() - user.getTaskProgress(task));
                if (removedAmount > 0) {
                    task.onProgress(user, removedAmount, p, false);
                }
            }
        }
    }

    public class DeliverQuestItemTask extends ATask<T> {

        private final NPCData<T, DeliverQuestItemTask> npcData;
        private final QuestItemData<T, DeliverQuestItemTask> itemData;

        public DeliverQuestItemTask(int id, Mission<T> mission, YMLSection section) {
            super(id, mission, DeliverQuestItemTaskType.this, section);
            npcData = new NPCData<>(this, getConfig().loadSection(Paths.TASK_INFO_NPCDATA));
            itemData = new QuestItemData<>(this,
                    getConfig().loadSection(Paths.TASK_INFO_ITEM));
        }

        public NPCData<T, DeliverQuestItemTask> getNPCData() {
            return npcData;
        }

        public QuestItemData<T, DeliverQuestItemTask> getQuestItemData() {
            return itemData;
        }

        public DeliverQuestItemTaskType<T> getType() {
            return DeliverQuestItemTaskType.this;
        }

        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.addAll(npcData.getInfo());
            info.addAll(itemData.getInfo());
            return info;
        }

        @Override
        public Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ATaskGuiEditor {

            public GuiEditor(Player player, Gui previousHolder) {
                super(player, previousHolder);
                itemData.setupButtons(this, 27);
                this.putButton(28, npcData.getNPCSelectorButton(this));
            }
        }
    }

    @Override
    public Task<T> getInstance(int id, Mission<T> mission, YMLSection section) {
        return new DeliverQuestItemTask(id, mission, section);
    }

    @Override
    public String getDefaultUnstartedDescription(Task<T> task) {
        if (!(task instanceof DeliverQuestItemTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), null);
        if (txt == null) {
            txt = "&9{action:deliver} &e" + Holders.TASK_MAX_PROGRESS + " {items} &9{conjun:to} {npcs}";
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), txt);

        }
        return Translations.replaceAll(txt).replace("{npcs}", DataUtils.getNPCHolder(t.getNPCData())).replace("{items}",
                t.getQuestItemData().getQuestItemNick());
    }

    @Override
    public String getDefaultCompleteDescription(Task<T> task) {
        if (!(task instanceof DeliverQuestItemTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), null);
        if (txt == null) {
            txt = "&a{action:deliver} &e" + Holders.TASK_MAX_PROGRESS + " {items} &a{conjun:to} {npcs} "
                    + Translations.translateAction("completed");
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), txt);

        }
        return Translations.replaceAll(txt).replace("{npcs}", DataUtils.getNPCHolder(t.getNPCData())).replace("{items}",
                t.getQuestItemData().getQuestItemNick());
    }

    @Override
    public String getDefaultProgressDescription(Task<T> task) {
        if (!(task instanceof DeliverQuestItemTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), null);
        if (txt == null) {
            txt = "&9{action:delivered} &e" + Holders.TASK_CURRENT_PROGRESS + " &9{conjun:of} &e"
                    + Holders.TASK_MAX_PROGRESS + " {items} &9{conjun:to} {npcs}";
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), txt);

        }
        return Translations.replaceAll(txt).replace("{npcs}", DataUtils.getNPCHolder(t.getNPCData())).replace("{items}",
                t.getQuestItemData().getQuestItemNick());
    }

}
