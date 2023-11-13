package emanondev.deepquests.generic.tasktypes;

import emanondev.core.YMLSection;
import emanondev.deepquests.Holders;
import emanondev.deepquests.Translations;
import emanondev.deepquests.data.QuestItemData;
import emanondev.deepquests.events.QuestItemObtainEvent;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.ATask;
import emanondev.deepquests.implementations.ATaskType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.*;
import emanondev.deepquests.interfaces.Task.Phase;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ObtainQuestItemTaskType<T extends User<T>> extends ATaskType<T> {

    private final static String ID = "obtain_quest_item";

    public ObtainQuestItemTaskType(QuestManager<T> manager) {
        super(ID, manager);
    }

    @Override
    public Material getGuiMaterial() {
        return Material.GOLDEN_AXE;
    }

    @Override
    public List<String> getDescription() {
        List<String> list = new ArrayList<>();
        list.add("&7Player has to obtain quest items");
        return list;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onRightClick(QuestItemObtainEvent<T> event) {
        T user = event.getUser();
        if (user == null)
            return;
        QuestBag<T> bag = user.getQuestBag();
        if (bag == null)
            return;
        for (Task<T> tTask : new ArrayList<>(user.getActiveTasks(this))) {
            ObtainQuestItemTask task = (ObtainQuestItemTask) tTask;
            if (!event.getID().equals(task.getQuestItemData().getQuestItemID()))
                continue;
            int progress = Math.min(task.getMaxProgress() - user.getTaskProgress(task), event.getAmount());
            if (progress > 0) {
                task.onProgress(user, progress, null, false);
            }
        }
    }

    @Override
    public @NotNull Task<T> getInstance(int id, @NotNull Mission<T> mission, YMLSection section) {
        return new ObtainQuestItemTask(id, mission, section);
    }

    @Override
    public String getDefaultUnstartedDescription(@NotNull Task<T> task) {
        if (!(task instanceof ObtainQuestItemTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), null);
        if (txt == null) {
            txt = "&9{action:deliver} &e" + Holders.TASK_MAX_PROGRESS + " {items}";
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.UNSTARTED), txt);

        }
        return Translations.replaceAll(txt).replace("{items}", t.getQuestItemData().getQuestItemNick());
    }

    @Override
    public String getDefaultCompleteDescription(@NotNull Task<T> task) {
        if (!(task instanceof ObtainQuestItemTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), null);
        if (txt == null) {
            txt = "&a{action:deliver} &e" + Holders.TASK_MAX_PROGRESS + " {items} "
                    + Translations.translateAction("completed");
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.COMPLETE), txt);

        }
        return Translations.replaceAll(txt).replace("{items}", t.getQuestItemData().getQuestItemNick());
    }

    @Override
    public String getDefaultProgressDescription(@NotNull Task<T> task) {
        if (!(task instanceof ObtainQuestItemTask t))
            return null;
        YMLSection config = getProvider().getTypeConfig(this);
        String txt = config.getString(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), null);
        if (txt == null) {
            txt = "&9{action:obtain} &e" + Holders.TASK_CURRENT_PROGRESS + " &9{conjun:of} &e"
                    + Holders.TASK_MAX_PROGRESS + " {items}";
            config.set(Paths.TASK_PHASE_DESCRIPTION(Phase.PROGRESS), txt);

        }
        return Translations.replaceAll(txt).replace("{items}", t.getQuestItemData().getQuestItemNick());
    }

    public class ObtainQuestItemTask extends ATask<T> {

        private final QuestItemData<T, ObtainQuestItemTask> itemData;

        public ObtainQuestItemTask(int id, Mission<T> mission, YMLSection section) {
            super(id, mission, ObtainQuestItemTaskType.this, section);
            itemData = new QuestItemData<>(this, getConfig().loadSection(Paths.TASK_INFO_ITEM));
        }

        public QuestItemData<T, ObtainQuestItemTask> getQuestItemData() {
            return itemData;
        }

        public @NotNull ObtainQuestItemTaskType<T> getType() {
            return ObtainQuestItemTaskType.this;
        }

        public @NotNull List<String> getInfo() {
            List<String> info = super.getInfo();
            info.addAll(itemData.getInfo());
            return info;
        }

        @Override
        public @NotNull Gui getEditorGui(Player target, Gui parent) {
            return new GuiEditor(target, parent);
        }

        private class GuiEditor extends ATaskGuiEditor {

            public GuiEditor(Player player, Gui previousHolder) {
                super(player, previousHolder);
                getQuestItemData().setupButtons(this, 27);
            }
        }
    }

}
