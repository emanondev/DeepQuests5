package emanondev.deepquests.implementations;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.gui.button.ElementSelectorButton;
import emanondev.deepquests.gui.button.GuiElementSelectorButton;
import emanondev.deepquests.gui.button.StringListEditorButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.interfaces.*;
import emanondev.deepquests.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AMission<T extends User<T>> extends AQuestComponentWithCooldown<T> implements Mission<T> {

    private final Quest<T> parent;

    private final Map<Integer, Task<T>> tasks = new HashMap<>();
    private final Map<Integer, Require<T>> requires = new HashMap<>();
    private final Map<Integer, Reward<T>> startRewards = new HashMap<>();
    private final Map<Integer, Reward<T>> completeRewards = new HashMap<>();
    private final Map<Integer, Reward<T>> failRewards = new HashMap<>();

    private final MissionDisplayInfo<T> displayInfo;

    private final EnumMap<PhaseChange, List<String>> rawMessages = new EnumMap<>(PhaseChange.class);
    private final EnumMap<PhaseChange, Boolean> messageIsDefault = new EnumMap<>(PhaseChange.class);
    private final EnumMap<PhaseChange, Boolean> showMessage = new EnumMap<>(PhaseChange.class);

    private String getPhaseShowMessagePath(PhaseChange phase) {
        return "show-" + phase.toString() + "-message";
    }

    private String getPhaseMessagePath(PhaseChange phase) {
        return phase.toString() + "-message";
    }

    public AMission(int id, Quest<T> quest, YMLSection section) {
        super(id, section, quest.getManager());
        this.parent = quest;
        displayInfo = new MissionDisplayInfo<>(getConfig().loadSection(Paths.QUESTCOMPONENT_DISPLAY_INFO), this);

        for (PhaseChange phase : PhaseChange.values()) {
            this.showMessage.put(phase, getConfig().loadBoolean(getPhaseShowMessagePath(phase), true));
            this.messageIsDefault.put(phase, getConfig().contains(getPhaseMessagePath(phase)));
            if (getConfig().contains(getPhaseMessagePath(phase)))
                this.rawMessages.put(phase, getConfig().getStringList(getPhaseMessagePath(phase)));
            else
                this.rawMessages.put(phase, phase.def);
        }
    }

    /*
     * @Override public Navigator getNavigator() { super.getNavigator();
     * nav.setNavigator(Paths.QUESTCOMPONENT_DISPLAY_INFO,
     * displayInfo.getNavigator()); for (PhaseChange phase:PhaseChange.values()) {
     * nav.setBoolean(this.getPhaseShowMessagePath(phase),
     * this.showMessage.get(phase));
     * nav.setBoolean(this.getPhaseMessageIsDefaultPath(phase),
     * this.messageIsDefault.get(phase)); if (!this.messageIsDefault.get(phase))
     * nav.setStringList(this.getPhaseMessagePath(phase),
     * this.rawMessages.get(phase)); } return nav; }
     */

    @Override
    public long getCooldownLeft(@NotNull T user) {
        MissionData<T> data = user.getMissionData(this);
        return Math.max(0, data.getCooldownTimeLeft());
    }

    @Override
    public List<String> getInfo() {
        List<String> info = new ArrayList<>();
        info.add("&9&lMission: &6" + this.getDisplayName());
        info.add("&8ID: " + this.getID());
        info.add("");
        info.add("&9Priority: &e" + getPriority());
        info.add("&9Quest: &e" + getQuest().getDisplayName());

        if (!this.isRepeatable())
            info.add("&9Repeatable: &cFalse");
        else {
            info.add("&9Repeatable: &aTrue");
            info.add("&9Cooldown: &e" + StringUtils.getStringCooldown(getCooldownTime()));
        }
        if (tasks.size() > 0) {
            info.add("&9Tasks:");
            for (Task<T> task : tasks.values()) {
                info.add("&9 - &e" + task.getDisplayName());
                info.add("   &7Type: " + (task.getType() != null ? task.getType().getKeyID() : "&cError"));
            }
        }
        List<String> blackList = new ArrayList<>();
        List<String> whiteList = new ArrayList<>();
        for (World world : Bukkit.getServer().getWorlds()) {
            if (isWorldAllowed(world) && getQuest().isWorldAllowed(world))
                whiteList.add("&9 - &a" + world.getName());
            else
                blackList.add("&9 - &c" + world.getName());
        }
        if (blackList.size() == 0)
            info.add("&9All Worlds are allowed");
        else if (whiteList.size() <= blackList.size()) {
            info.add("&9Whitelisted Worlds:");
            info.addAll(whiteList);
        } else {
            info.add("&9Blacklisted Worlds:");
            info.addAll(blackList);
        }
        if (requires.size() > 0) {
            info.add("&9Requires:");
            for (Require<T> require : requires.values()) {
                info.add("&9 - &e" + require.getDisplayName());
                info.add("   &7" + (require.getType() != null ? require.getType().getKeyID() : "&cError"));
            }
        }
        if (startRewards.size() > 0) {
            info.add("&9Start Rewards:");
            for (Reward<T> reward : startRewards.values()) {
                info.add("&9 - &e" + reward.getDisplayName());
                info.add("   &7" + (reward.getType() == null ? "&cError" : reward.getType().getKeyID()));
            }
        }
        if (completeRewards.size() > 0) {
            info.add("&9Complete Rewards:");
            for (Reward<T> reward : completeRewards.values()) {
                info.add("&9 - &e" + reward.getDisplayName());
                info.add("   &7" + (reward.getType() == null ? "&cError" : reward.getType().getKeyID()));
            }
        }
        if (failRewards.size() > 0) {
            info.add("&9Fail Rewards:");
            for (Reward<T> reward : failRewards.values()) {
                info.add("&9 - &e" + reward.getDisplayName());
                info.add("   &7" + (reward.getType() == null ? "&cError" : reward.getType().getKeyID()));
            }
        }
        return info;
    }

    @Override
    public final Task<T> getTask(int id) {
        return tasks.get(id);
    }

    @Override
    public final Collection<Task<T>> getTasks() {
        return Collections.unmodifiableCollection(tasks.values());
    }

    @Override
    public final boolean addTask(Task<T> task) {
        if (tasks.containsKey(task.getID()))
            throw new IllegalArgumentException();
        tasks.put(task.getID(), task);
        return true;
    }

    @Override
    public final boolean removeTask(Task<T> task) {
        return tasks.remove(task.getID()) != null;
    }

    @Override
    public final Quest<T> getQuest() {
        return parent;
    }

    @Override
    public final Collection<Require<T>> getRequires() {
        return Collections.unmodifiableCollection(requires.values());
    }

    @Override
    public final Require<T> getRequire(int id) {
        return requires.get(id);
    }

    @Override
    public final boolean addRequire(Require<T> require) {
        if (requires.containsKey(require.getID()))
            throw new IllegalArgumentException();
        requires.put(require.getID(), require);
        return true;
    }

    @Override
    public final boolean removeRequire(Require<T> require) {
        return requires.remove(require.getID()) != null;
    }

    @Override
    public final Collection<Reward<T>> getStartRewards() {
        return Collections.unmodifiableCollection(startRewards.values());
    }

    @Override
    public final Reward<T> getStartReward(int id) {
        return startRewards.get(id);
    }

    @Override
    public final boolean addStartReward(Reward<T> reward) {
        if (startRewards.containsKey(reward.getID()))
            throw new IllegalArgumentException();
        startRewards.put(reward.getID(), reward);
        return true;
    }

    @Override
    public final boolean removeStartReward(Reward<T> reward) {
        return startRewards.remove(reward.getID()) != null;
    }

    @Override
    public final Collection<Reward<T>> getCompleteRewards() {
        return Collections.unmodifiableCollection(completeRewards.values());
    }

    @Override
    public final Reward<T> getCompleteReward(int id) {
        return completeRewards.get(id);
    }

    @Override
    public final boolean addCompleteReward(Reward<T> reward) {
        if (completeRewards.containsKey(reward.getID()))
            throw new IllegalArgumentException();
        completeRewards.put(reward.getID(), reward);
        return true;
    }

    @Override
    public final boolean removeCompleteReward(Reward<T> reward) {
        return completeRewards.remove(reward.getID()) != null;
    }

    @Override
    public final Collection<Reward<T>> getFailRewards() {
        return Collections.unmodifiableCollection(failRewards.values());
    }

    @Override
    public final Reward<T> getFailReward(int id) {
        return failRewards.get(id);
    }

    @Override
    public final boolean addFailReward(Reward<T> reward) {
        if (failRewards.containsKey(reward.getID()))
            throw new IllegalArgumentException();
        failRewards.put(reward.getID(), reward);
        return true;
    }

    @Override
    public final boolean removeFailReward(Reward<T> reward) {
        return failRewards.remove(reward.getID()) != null;
    }

    @Override
    public Gui getEditorGui(Player target, Gui parent) {
        return new AMissionGuiEditor(target, parent);
    }

    @Override
    protected Set<String> getDefaultWorldsList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean getDefaultWorldsAreWhitelist() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public final MissionDisplayInfo<T> getDisplayInfo() {
        return displayInfo;
    }

    @Override
    public final ArrayList<String> getRawPhaseMessage(PhaseChange phase) {
        return new ArrayList<>(rawMessages.get(phase));
    }

    @Override
    public final void setPhaseMessage(PhaseChange phase, List<String> message) {
        if (message == null) {
            message = getDefaultPhaseMessage(phase);
            this.messageIsDefault.put(phase, true);
        } else
            this.messageIsDefault.put(phase, false);
        this.rawMessages.put(phase, message);
        //getConfig().set(this.getPhaseMessageIsDefaultPath(phase), this.messageIsDefault.get(phase));
        if (!this.messageIsDefault.get(phase))
            getConfig().set(this.getPhaseMessagePath(phase), this.rawMessages.get(phase));
        else
            getConfig().set(this.getPhaseMessagePath(phase), null);
    }

    @Override
    public final boolean showPhaseMessage(PhaseChange phase) {
        return showMessage.get(phase);
    }

    @Override
    public final void toggleShowPhaseMessage(PhaseChange phase) {
        this.showMessage.put(phase, !this.showMessage.get(phase));
        getConfig().set(this.getPhaseShowMessagePath(phase), this.showMessage.get(phase));
    }

    private ArrayList<String> getDefaultPhaseMessage(PhaseChange phase) {
        return new ArrayList<>(
                getManager().getConfig().loadStringList("default-mission-" + phase.toString() + "-message", phase.def));
    }

    protected class AMissionGuiEditor extends AAAGuiEditor {

        public AMissionGuiEditor(Player player, Gui parent) {
            super("&9Mission: &r" + getDisplayName() + " &9ID: &e" + getID(), player, parent);
            this.putButton(10, displayInfo.getDisplayEditorButton(this));
            this.putButton(27, new TaskCreate());
            this.putButton(28, new TaskSelector());
            this.putButton(29, new TaskDelete());
            this.putButton(36, new RequireCreate());
            this.putButton(37, new RequireSelector());
            this.putButton(38, new RequireDelete());
            this.putButton(24, new StartRewardCreate());
            this.putButton(25, new StartRewardSelector());
            this.putButton(26, new StartRewardDelete());
            this.putButton(33, new CompleteRewardCreate());
            this.putButton(34, new CompleteRewardSelector());
            this.putButton(35, new CompleteRewardDelete());
            this.putButton(42, new FailRewardCreate());
            this.putButton(43, new FailRewardSelector());
            this.putButton(44, new FailRewardDelete());
            // TODO
            this.putButton(45, new PhaseMessage(PhaseChange.START));
            this.putButton(46, new PhaseMessage(PhaseChange.COMPLETE));
            this.putButton(47, new PhaseMessage(PhaseChange.FAIL));
            this.putButton(48, new PhaseMessage(PhaseChange.PAUSE));
            this.putButton(49, new PhaseMessage(PhaseChange.UNPAUSE));
        }

        private class PhaseMessage extends StringListEditorButton {
            private final PhaseChange phase;

            private PhaseMessage(PhaseChange phase) {
                super("&9" + phase.toString() + " Message",
                        new ItemBuilder(guiFullMaterial(phase)).setGuiProperty().build(), AMissionGuiEditor.this);
                this.phase = phase;
            }

            @Override
            public List<String> getButtonDescription() {
                if (phase == null)
                    return new ArrayList<>();
                if (showPhaseMessage(phase)) {
                    ArrayList<String> list = new ArrayList<>(Arrays.asList("&6" + phase.name().toLowerCase() + " Message Editor",
                            "&7Left Click to edit", "&7Middle Click to reset default", "&7Right Click to disable", "",
                            "&9Current Value:"));
                    list.addAll(getRawPhaseMessage(phase));
                    return list;
                }
                return Arrays.asList("&6" + phase.name().toLowerCase() + " Message Editor", "&cDisabled", "", "&7Click to enable");
            }

            @Override
            public ItemStack getItem() {
                ItemStack item = super.getItem();
                if (phase == null)
                    return item;
                if (showPhaseMessage(phase))
                    item.setType(guiFullMaterial(phase));
                else
                    item.setType(guiGlassMaterial(phase));
                return item;
            }

            @Override
            public List<String> getCurrentList() {
                if (phase == null)
                    return new ArrayList<>();
                return getRawPhaseMessage(phase);
            }

            @Override
            public boolean onStringListChange(List<String> list) {
                AMission.this.setPhaseMessage(phase, list);
                return true;
            }

            @Override
            public void onClick(Player clicker, ClickType click) {
                if (showPhaseMessage(phase)) {
                    switch (click) {
                        case LEFT:
                            super.onClick(clicker, click);
                            return;
                        case MIDDLE:
                            setPhaseMessage(phase, null);
                            getGui().updateInventory();
                            return;
                        case RIGHT:
                            toggleShowPhaseMessage(phase);
                            getGui().updateInventory();
                            return;
                        default:
                            break;
                    }
                    return;
                }
                toggleShowPhaseMessage(phase);
                getGui().updateInventory();
            }
        }

        private class TaskSelector extends GuiElementSelectorButton<Task<T>> {

            public TaskSelector() {
                super("&9Select a Task", new ItemBuilder(Material.PAPER).setGuiProperty().build(),
                        AMissionGuiEditor.this, false, true, false);
            }

            @Override
            public List<String> getButtonDescription() {
                return List.of("&6Click to select a Task");
            }

            @Override
            public Collection<Task<T>> getValues() {
                return getTasks();
            }

            @Override
            public void onElementSelectRequest(Task<T> element, Player p) {
                p.openInventory(element.getEditorGui(p, AMissionGuiEditor.this).getInventory());
            }
        }

        private class TaskDelete extends GuiElementSelectorButton<Task<T>> {

            public TaskDelete() {
                super("&cDelete a Task", new ItemBuilder(Material.RED_DYE).setGuiProperty().build(),
                        AMissionGuiEditor.this, false, true, true);
            }

            @Override
            public List<String> getButtonDescription() {
                return Arrays.asList("&cClick to delete a Task", "", "&cDelete can't be undone");
            }

            @Override
            public Collection<Task<T>> getValues() {
                return getTasks();
            }

            @Override
            public void onElementSelectRequest(Task<T> element, Player p) {
                getManager().delete(element);
                AMissionGuiEditor.this.updateInventory();
                p.openInventory(AMissionGuiEditor.this.getInventory());
            }
        }

        private class TaskCreate extends ElementSelectorButton<TaskType<T>> {

            public TaskCreate() {
                super("&9Select a TaskType for the new Task",
                        new ItemBuilder(Material.YELLOW_DYE).setGuiProperty().build(), AMissionGuiEditor.this, false,
                        true, false);
            }

            @Override
            public List<String> getButtonDescription() {
                return List.of("&6Click to create a new Task");
            }

            @Override
            public List<String> getElementDescription(TaskType<T> element) {
                return element.getInfo();
            }

            @Override
            public ItemStack getElementItem(TaskType<T> element) {
                return element.getGuiItem();
            }

            @Override
            public void onElementSelectRequest(TaskType<T> element) {
                Task<T> task = getManager().createTask(AMission.this, element, this.getTargetPlayer());
                this.getTargetPlayer().openInventory(
                        task.getEditorGui(this.getTargetPlayer(), AMissionGuiEditor.this).getInventory());
            }

            @Override
            public Collection<TaskType<T>> getPossibleValues() {
                return getManager().getTaskProvider().getTypes();
            }
        }

        private class RequireSelector extends GuiElementSelectorButton<Require<T>> {

            public RequireSelector() {
                super("&9Select a Require", new ItemBuilder(Material.IRON_BARS).setGuiProperty().build(),
                        AMissionGuiEditor.this, false, true, false);
            }

            @Override
            public List<String> getButtonDescription() {
                return Arrays.asList("&6Click to select a Require", "", "&6each quest may contain tasks");
            }

            @Override
            public Collection<Require<T>> getValues() {
                return getRequires();
            }

            @Override
            public void onElementSelectRequest(Require<T> element, Player p) {
                p.openInventory(element.getEditorGui(p, AMissionGuiEditor.this).getInventory());
            }
        }

        private class RequireDelete extends GuiElementSelectorButton<Require<T>> {

            public RequireDelete() {
                super("&cDelete a Require", new ItemBuilder(Material.RED_DYE).setGuiProperty().build(),
                        AMissionGuiEditor.this, false, true, true);
            }

            @Override
            public List<String> getButtonDescription() {
                return Arrays.asList("&cClick to delete a Require", "", "&cDelete can't be undone");
            }

            @Override
            public Collection<Require<T>> getValues() {
                return getRequires();
            }

            @Override
            public void onElementSelectRequest(Require<T> element, Player p) {
                getManager().unlinkRequire(element, AMission.this);
                AMissionGuiEditor.this.updateInventory();
                p.openInventory(AMissionGuiEditor.this.getInventory());
            }
        }

        private class RequireCreate extends ElementSelectorButton<RequireType<T>> {

            public RequireCreate() {
                super("&9Select a RequireType for the new Require",
                        new ItemBuilder(Material.YELLOW_DYE).setGuiProperty().build(), AMissionGuiEditor.this, false,
                        true, false);
            }

            @Override
            public List<String> getButtonDescription() {
                return List.of("&6Click to create a new Require");
            }

            @Override
            public List<String> getElementDescription(RequireType<T> element) {
                return element.getInfo();
            }

            @Override
            public ItemStack getElementItem(RequireType<T> element) {
                return element.getGuiItem();
            }

            @Override
            public void onElementSelectRequest(RequireType<T> element) {
                Require<T> require = getManager().createRequire(element, this.getTargetPlayer());
                getManager().linkRequire(require, AMission.this);
                this.getTargetPlayer().openInventory(
                        require.getEditorGui(this.getTargetPlayer(), AMissionGuiEditor.this).getInventory());
            }

            @Override
            public Collection<RequireType<T>> getPossibleValues() {
                return getManager().getRequireProvider().getMissionTypes();
            }
        }

        private class StartRewardSelector extends GuiElementSelectorButton<Reward<T>> {

            public StartRewardSelector() {
                super("&9Select a StartReward", new ItemBuilder(Material.IRON_BARS).setGuiProperty().build(),
                        AMissionGuiEditor.this, false, true, false);
            }

            @Override
            public List<String> getButtonDescription() {
                return Arrays.asList("&6Click to select a StartReward", "", "&6each quest may contain tasks");
            }

            @Override
            public Collection<Reward<T>> getValues() {
                return getStartRewards();
            }

            @Override
            public void onElementSelectRequest(Reward<T> element, Player p) {
                p.openInventory(element.getEditorGui(p, AMissionGuiEditor.this).getInventory());
            }
        }

        private class StartRewardDelete extends GuiElementSelectorButton<Reward<T>> {

            public StartRewardDelete() {
                super("&cDelete a StartReward", new ItemBuilder(Material.RED_DYE).setGuiProperty().build(),
                        AMissionGuiEditor.this, false, true, true);
            }

            @Override
            public List<String> getButtonDescription() {
                return Arrays.asList("&cClick to delete a StartReward", "", "&cDelete can't be undone");
            }

            @Override
            public Collection<Reward<T>> getValues() {
                return getStartRewards();
            }

            @Override
            public void onElementSelectRequest(Reward<T> element, Player p) {
                getManager().unlinkStartReward(element, AMission.this);
                AMissionGuiEditor.this.updateInventory();
                p.openInventory(AMissionGuiEditor.this.getInventory());
            }
        }

        private class StartRewardCreate extends ElementSelectorButton<RewardType<T>> {

            public StartRewardCreate() {
                super("&9Select a RewardType for the new StartReward",
                        new ItemBuilder(Material.YELLOW_DYE).setGuiProperty().build(), AMissionGuiEditor.this, false,
                        true, false);
            }

            @Override
            public List<String> getButtonDescription() {
                return List.of("&6Click to create a new StartReward");
            }

            @Override
            public List<String> getElementDescription(RewardType<T> element) {
                return element.getInfo();
            }

            @Override
            public ItemStack getElementItem(RewardType<T> element) {
                return element.getGuiItem();
            }

            @Override
            public void onElementSelectRequest(RewardType<T> element) {
                Reward<T> reward = getManager().createReward(element, this.getTargetPlayer());
                getManager().linkStartReward(reward, AMission.this);
                this.getTargetPlayer().openInventory(
                        reward.getEditorGui(this.getTargetPlayer(), AMissionGuiEditor.this).getInventory());
            }

            @Override
            public Collection<RewardType<T>> getPossibleValues() {
                return getManager().getRewardProvider().getMissionTypes();
            }
        }

        private class CompleteRewardSelector extends GuiElementSelectorButton<Reward<T>> {

            public CompleteRewardSelector() {
                super("&9Select a CompleteReward", new ItemBuilder(Material.IRON_BARS).setGuiProperty().build(),
                        AMissionGuiEditor.this, false, true, false);
            }

            @Override
            public List<String> getButtonDescription() {
                return Arrays.asList("&6Click to select a CompleteReward", "", "&6each quest may contain tasks");
            }

            @Override
            public Collection<Reward<T>> getValues() {
                return getCompleteRewards();
            }

            @Override
            public void onElementSelectRequest(Reward<T> element, Player p) {
                p.openInventory(element.getEditorGui(p, AMissionGuiEditor.this).getInventory());
            }
        }

        private class CompleteRewardDelete extends GuiElementSelectorButton<Reward<T>> {

            public CompleteRewardDelete() {
                super("&cDelete a CompleteReward", new ItemBuilder(Material.RED_DYE).setGuiProperty().build(),
                        AMissionGuiEditor.this, false, true, true);
            }

            @Override
            public List<String> getButtonDescription() {
                return Arrays.asList("&cClick to delete a CompleteReward", "", "&cDelete can't be undone");
            }

            @Override
            public Collection<Reward<T>> getValues() {
                return getCompleteRewards();
            }

            @Override
            public void onElementSelectRequest(Reward<T> element, Player p) {
                getManager().unlinkCompleteReward(element, AMission.this);
                AMissionGuiEditor.this.updateInventory();
                p.openInventory(AMissionGuiEditor.this.getInventory());
            }
        }

        private class CompleteRewardCreate extends ElementSelectorButton<RewardType<T>> {

            public CompleteRewardCreate() {
                super("&9Select a RewardType for the new CompleteReward",
                        new ItemBuilder(Material.YELLOW_DYE).setGuiProperty().build(), AMissionGuiEditor.this, false,
                        true, false);
            }

            @Override
            public List<String> getButtonDescription() {
                return List.of("&6Click to create a new CompleteReward");
            }

            @Override
            public List<String> getElementDescription(RewardType<T> element) {
                return element.getInfo();
            }

            @Override
            public ItemStack getElementItem(RewardType<T> element) {
                return element.getGuiItem();
            }

            @Override
            public void onElementSelectRequest(RewardType<T> element) {
                Reward<T> reward = getManager().createReward(element, this.getTargetPlayer());
                getManager().linkCompleteReward(reward, AMission.this);
                this.getTargetPlayer().openInventory(
                        reward.getEditorGui(this.getTargetPlayer(), AMissionGuiEditor.this).getInventory());
            }

            @Override
            public Collection<RewardType<T>> getPossibleValues() {
                return getManager().getRewardProvider().getMissionTypes();
            }
        }

        private class FailRewardSelector extends GuiElementSelectorButton<Reward<T>> {

            public FailRewardSelector() {
                super("&9Select a FailReward", new ItemBuilder(Material.IRON_BARS).setGuiProperty().build(),
                        AMissionGuiEditor.this, false, true, false);
            }

            @Override
            public List<String> getButtonDescription() {
                return Arrays.asList("&6Click to select a FailReward", "", "&6each quest may contain tasks");
            }

            @Override
            public Collection<Reward<T>> getValues() {
                return getFailRewards();
            }

            @Override
            public void onElementSelectRequest(Reward<T> element, Player p) {
                p.openInventory(element.getEditorGui(p, AMissionGuiEditor.this).getInventory());
            }
        }

        private class FailRewardDelete extends GuiElementSelectorButton<Reward<T>> {

            public FailRewardDelete() {
                super("&cDelete a FailReward", new ItemBuilder(Material.RED_DYE).setGuiProperty().build(),
                        AMissionGuiEditor.this, false, true, true);
            }

            @Override
            public List<String> getButtonDescription() {
                return Arrays.asList("&cClick to delete a FailReward", "", "&cDelete can't be undone");
            }

            @Override
            public Collection<Reward<T>> getValues() {
                return getFailRewards();
            }

            @Override
            public void onElementSelectRequest(Reward<T> element, Player p) {
                getManager().unlinkFailReward(element, AMission.this);
                AMissionGuiEditor.this.updateInventory();
                p.openInventory(AMissionGuiEditor.this.getInventory());
            }
        }

        private class FailRewardCreate extends ElementSelectorButton<RewardType<T>> {

            public FailRewardCreate() {
                super("&9Select a RewardType for the new FailReward",
                        new ItemBuilder(Material.YELLOW_DYE).setGuiProperty().build(), AMissionGuiEditor.this, false,
                        true, false);
            }

            @Override
            public List<String> getButtonDescription() {
                return List.of("&6Click to create a new FailReward");
            }

            @Override
            public List<String> getElementDescription(RewardType<T> element) {
                return element.getInfo();
            }

            @Override
            public ItemStack getElementItem(RewardType<T> element) {
                return element.getGuiItem();
            }

            @Override
            public void onElementSelectRequest(RewardType<T> element) {
                Reward<T> reward = getManager().createReward(element, this.getTargetPlayer());
                getManager().linkFailReward(reward, AMission.this);
                this.getTargetPlayer().openInventory(
                        reward.getEditorGui(this.getTargetPlayer(), AMissionGuiEditor.this).getInventory());
            }

            @Override
            public Collection<RewardType<T>> getPossibleValues() {
                return getManager().getRewardProvider().getMissionTypes();
            }
        }
    }

    private static Material guiFullMaterial(PhaseChange phase) {
        return switch (phase) {
            case COMPLETE -> Material.GREEN_TERRACOTTA;
            case FAIL -> Material.RED_TERRACOTTA;
            case PAUSE -> Material.LIME_TERRACOTTA;
            case START -> Material.BLUE_TERRACOTTA;
            case UNPAUSE -> Material.BLUE_TERRACOTTA;
            default -> throw new IllegalStateException();
        };
    }

    private static Material guiGlassMaterial(PhaseChange phase) {
        return switch (phase) {
            case COMPLETE -> Material.GREEN_STAINED_GLASS;
            case FAIL -> Material.RED_STAINED_GLASS;
            case PAUSE -> Material.LIME_STAINED_GLASS;
            case START -> Material.BLUE_STAINED_GLASS;
            case UNPAUSE -> Material.BLUE_STAINED_GLASS;
            default -> throw new IllegalStateException();
        };
    }

    /*
     * @Override public boolean isWorldAllowed(World world) { return
     * getQuest().isWorldAllowed(world); }
     *
     * @Override public boolean isWorldListWhitelist() { return
     * getQuest().isWorldListWhitelist(); }
     */

}
