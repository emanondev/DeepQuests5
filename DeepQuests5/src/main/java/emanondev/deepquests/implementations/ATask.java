package emanondev.deepquests.implementations;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.Holders;
import emanondev.deepquests.Translations;
import emanondev.deepquests.gui.button.*;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.interfaces.*;
import emanondev.deepquests.utils.StringUtils;
import emanondev.deepquests.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class ATask<T extends User<T>> extends AQuestComponentWithWorlds<T> implements Task<T> {

    private final Mission<T> mission;
    private final TaskType<T> type;
    private final Map<Integer, Reward<T>> completeRewards = new HashMap<>();
    private final Map<Integer, Reward<T>> progressRewards = new HashMap<>();
    private final EnumMap<Phase, String> phaseDescriptions = new EnumMap<>(Phase.class);
    private final EnumMap<Phase, Boolean> phaseDescriptionIsDef = new EnumMap<>(Phase.class);
    private boolean isHidden;
    private double progressChance;
    private int maxProgress;
    private boolean isBarStyleDefault;
    private BarStyle barStyle;
    private boolean isBarColorDefault;
    private BarColor barColor;
    private boolean isShowBossBarDefault;
    private boolean showBossBar;

    public ATask(int id, Mission<T> mission, TaskType<T> type, YMLSection section) {
        super(id, section, mission.getManager());
        if (type == null)
            throw new NullPointerException();
        this.type = type;
        this.mission = mission;
        isHidden = getConfig().getBoolean(Paths.IS_HIDDEN, getType().getDefaultIsHidden());
        progressChance = getConfig().getDouble(Paths.TASK_PROGRESS_CHANCE, 1D);
        maxProgress = getConfig().getInteger(Paths.TASK_MAX_PROGRESS, 1);
        if (getConfig().getEnum(Paths.TASK_BAR_STYLE, null, BarStyle.class) != null) {
            isBarStyleDefault = false;
            barStyle = getConfig().getEnum(Paths.TASK_BAR_STYLE,
                    getManager().getBossBarManager().getBarStyle(this.getType()), BarStyle.class);
        } else {
            isBarStyleDefault = true;
            barStyle = getManager().getBossBarManager().getBarStyle(this.getType());
        }

        if (getConfig().getEnum(Paths.TASK_BAR_COLOR, null, BarColor.class) != null) {
            isBarColorDefault = false;
            barColor = getConfig().getEnum(Paths.TASK_BAR_COLOR,
                    getManager().getBossBarManager().getBarColor(this.getType()), BarColor.class);
        } else {
            isBarColorDefault = true;
            barColor = getManager().getBossBarManager().getBarColor(this.getType());
        }
        if (getConfig().loadBoolean(Paths.TASK_SHOW_BOSSBAR, null) != null) {
            isShowBossBarDefault = false;
            showBossBar = getConfig().getBoolean(Paths.TASK_SHOW_BOSSBAR,
                    getManager().getBossBarManager().getShowBossBar(this.getType()));
        } else {
            isShowBossBarDefault = true;
            showBossBar = getManager().getBossBarManager().getShowBossBar(this.getType());
        }
        for (Phase phase : Phase.values()) {
            // TODO fix path
            if (getConfig().getString(Paths.TASK_PHASE_DESCRIPTION(phase), null) != null) {
                phaseDescriptionIsDef.put(phase, false);
                phaseDescriptions.put(phase, getConfig().getString(Paths.TASK_PHASE_DESCRIPTION(phase), null));
            } else {
                phaseDescriptionIsDef.put(phase, true);
                phaseDescriptions.put(phase, null);
            }

        }
        getConfig().set(Paths.TYPE_NAME, type.getKeyID());
    }

    @Override
    public @NotNull List<String> getInfo() {
        List<String> info = new ArrayList<>();
        info.add("&9&lTask: &6" + this.getDisplayName());
        info.add("&8Type: &7" + (getType() != null ? getType().getKeyID() : "&cError"));

        info.add("&8ID: " + this.getID());
        info.add("");
        info.add("&9Priority: &e" + getPriority());
        info.add("&9Max Progress: &e" + getMaxProgress());
        info.add("&9Progress Chance: &e" + StringUtils.getDecimalFormat().format(getProgressChance() * 100) + "%");
        if (showBossBar())
            info.add("&9Show BossBar onProgress: &cfalse");
        else {
            info.add("&9Show BossBar onProgress: &atrue");
            info.add("&9  BarStyle: &e" + barStyle.toString());
            info.add("&9  BarColor: &e" + barColor.toString());
        }

        info.add("&9Quest: &e" + getMission().getQuest().getDisplayName());
        info.add("&9Mission: &e" + getMission().getDisplayName());
        List<String> blackList = new ArrayList<>();
        List<String> whiteList = new ArrayList<>();
        for (World world : Bukkit.getServer().getWorlds()) {
            if (isWorldAllowed(world))
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
        if (completeRewards.size() > 0) {
            info.add("&9Complete Rewards:");
            for (Reward<T> reward : completeRewards.values()) {
                info.add("&9 - &e" + reward.getDisplayName());
                info.add("   &7" + (reward.getType() != null ? reward.getType().getKeyID() : "&cError"));
            }
        }
        if (progressRewards.size() > 0) {
            info.add("&9Progress Rewards:");
            for (Reward<T> reward : progressRewards.values()) {
                info.add("&9 - &e" + reward.getDisplayName());
                info.add("   &7" + (reward.getType() != null ? reward.getType().getKeyID() : "&cError"));
            }
        }
        return info;
    }

    @Override
    public @NotNull TaskType<T> getType() {
        return type;
    }

    public boolean isWorldAllowed(World world) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    protected Set<String> getDefaultWorldsList() {
        // TODO
        return new HashSet<>();
    }

    @Override
    protected boolean getDefaultWorldsAreWhitelist() {
        // TODO
        return false;
    }

    @Override
    public final @NotNull Mission<T> getMission() {
        return mission;
    }

    @Override
    public final @NotNull Collection<Reward<T>> getCompleteRewards() {
        return Collections.unmodifiableCollection(completeRewards.values());
    }

    @Override
    public final Reward<T> getCompleteReward(int id) {
        return completeRewards.get(id);
    }

    @Override
    public final boolean addCompleteReward(@NotNull Reward<T> reward) {
        if (completeRewards.containsKey(reward.getID()))
            return false;
        completeRewards.put(reward.getID(), reward);
        return true;
    }

    @Override
    public final boolean removeCompleteReward(@NotNull Reward<T> reward) {
        if (!completeRewards.containsKey(reward.getID()))
            return false;
        completeRewards.remove(reward.getID());
        return true;
    }

    @Override
    public final @NotNull Collection<Reward<T>> getProgressRewards() {
        return Collections.unmodifiableCollection(progressRewards.values());
    }

    @Override
    public final @NotNull Reward<T> getProgressReward(int id) {
        return progressRewards.get(id);
    }

    @Override
    public final boolean addProgressReward(@NotNull Reward<T> reward) {
        if (progressRewards.containsKey(reward.getID()))
            return false;
        progressRewards.put(reward.getID(), reward);
        return true;
    }

    @Override
    public final boolean removeProgressReward(@NotNull Reward<T> reward) {
        if (!progressRewards.containsKey(reward.getID()))
            return false;
        progressRewards.remove(reward.getID());
        return true;
    }

    public final boolean isHidden() {
        return isHidden;
    }

    public final void setHidden(Boolean value) {
        if (value != null && isHidden == value)
            return;
        isHidden = Objects.requireNonNullElse(value, getType().getDefaultIsHidden());
        getConfig().set(Paths.IS_HIDDEN, value == null ? null : isHidden);
    }

    @Override
    public final double getProgressChance() {
        return progressChance;
    }

    @Override
    public final void setProgressChance(double progressChance) {
        if (progressChance < 0)
            return;
        this.progressChance = Math.min(1, progressChance);
        if (this.progressChance == 1)
            getConfig().set(Paths.TASK_PROGRESS_CHANCE, null);
        else
            getConfig().set(Paths.TASK_PROGRESS_CHANCE, progressChance);
    }

    @Override
    public final int getMaxProgress() {
        return maxProgress;
    }

    @Override
    public final void setMaxProgress(int maxProgress) {
        this.maxProgress = Math.max(1, maxProgress);
        getConfig().set(Paths.TASK_MAX_PROGRESS, maxProgress);
    }

    @Override
    public final @NotNull BarStyle getBossBarStyle() {
        if (isBarStyleDefault)
            barStyle = getManager().getBossBarManager().getBarStyle(this.getType());
        return barStyle;
    }

    @Override
    public final @NotNull BarColor getBossBarColor() {
        if (isBarColorDefault)
            barColor = getManager().getBossBarManager().getBarColor(this.getType());
        return barColor;
    }

    /*
     * @Override public boolean isWorldAllowed(World world) { return
     * getMission().getQuest().isWorldAllowed(world); }
     *
     * @Override public boolean isWorldListWhitelist() { return
     * getMission().getQuest().isWorldListWhitelist(); }
     */
    /*
     * @Override public boolean isWorldAllowed(World world) { return
     * super.isWorldAllowed(world) && getMission().isWorldAllowed(world) &&
     * getMission().getQuest().isWorldAllowed(world); }
     */

    @Override
    public final void setBossBarStyle(BarStyle barStyle) {
        if (barStyle == null) {
            this.isBarStyleDefault = true;
            getConfig().set(Paths.TASK_BAR_STYLE, null);
        } else {
            this.isBarStyleDefault = false;
            this.barStyle = barStyle;
            getConfig().set(Paths.TASK_BAR_STYLE, barStyle.name());
        }
    }

    @Override
    public final void setBossBarColor(BarColor barColor) {
        if (barColor == null) {
            this.isBarColorDefault = true;
            getConfig().set(Paths.TASK_BAR_COLOR, null);
        } else {
            this.isBarColorDefault = false;
            this.barColor = barColor;
            getConfig().set(Paths.TASK_BAR_COLOR, barColor.name());
        }
    }

    @Override
    public final boolean showBossBar() {
        if (isShowBossBarDefault)
            return getManager().getBossBarManager().getShowBossBar(getType());
        return showBossBar;
    }

    @Override
    public final void setShowBossBar(Boolean value) {
        if (value == null) {
            isShowBossBarDefault = true;
            getConfig().set(Paths.TASK_SHOW_BOSSBAR, null);
        } else {
            isShowBossBarDefault = false;
            showBossBar = value;
            getConfig().set(Paths.TASK_SHOW_BOSSBAR, showBossBar);
        }
    }

    public String getRawPhaseDescription(Phase phase) {
        if (phaseDescriptions.get(phase) == null)
            phaseDescriptions.put(phase, getDefaultPhaseDescription(phase));
        return phaseDescriptions.get(phase);
    }

    public void setPhaseDescription(String desc, Phase phase) {
        if (desc == null) {
            phaseDescriptionIsDef.put(phase, true);
            phaseDescriptions.put(phase, getDefaultPhaseDescription(phase));
            getConfig().set(Paths.TASK_PHASE_DESCRIPTION(phase), null);
        } else {
            phaseDescriptionIsDef.put(phase, false);
            phaseDescriptions.put(phase, desc);
            getConfig().set(Paths.TASK_PHASE_DESCRIPTION(phase), desc);
        }
    }

    private String getDefaultPhaseDescription(Phase phase) {
        String desc = getType().getDefaultPhaseDescription(phase, this);
        return switch (phase) {
            case COMPLETE -> desc != null ? desc : Holders.DISPLAY_NAME + " " + Translations.translateAction("completed");
            case PROGRESS -> desc != null ? desc
                    : Holders.DISPLAY_NAME + " " + Holders.TASK_CURRENT_PROGRESS + " "
                    + Translations.translateConjunction("of") + " " + Holders.TASK_MAX_PROGRESS;
            case UNSTARTED -> desc != null ? desc : Holders.DISPLAY_NAME + " " + Holders.TASK_MAX_PROGRESS;
            default -> throw new IllegalStateException();
        };
    }

    protected class ATaskGuiEditor extends AAGuiEditor {

        public ATaskGuiEditor(Player player, Gui previousHolder) {
            super("&9Task: &r" + getDisplayName() + " &9ID: &e" + getID() + " &9Type: &e" + getType().getKeyID(),
                    player, previousHolder);
            this.putButton(6, new BarShowButton());
            this.putButton(7, new BarStyleButton());
            this.putButton(8, new BarColorButton());

            this.putButton(9, new MaxProgressButton());
            this.putButton(10, new ProgressChanceButton());
            this.putButton(14, new HiddenButton());
            PhaseDescriptionButton b = new PhaseDescriptionButton(Phase.UNSTARTED);
            b.update();
            this.putButton(15, b);
            b = new PhaseDescriptionButton(Phase.PROGRESS);
            b.update();
            this.putButton(16, b);
            b = new PhaseDescriptionButton(Phase.COMPLETE);
            b.update();
            this.putButton(17, b);

            this.putButton(24, new ProgressRewardCreate());
            this.putButton(25, new ProgressRewardSelector());
            this.putButton(26, new ProgressRewardDelete());
            this.putButton(33, new CompleteRewardCreate());
            this.putButton(34, new CompleteRewardSelector());
            this.putButton(35, new CompleteRewardDelete());
        }

        private class HiddenButton extends StaticFlagButton {

            public HiddenButton() {
                super(Utils.setDescription(
                                new ItemBuilder(Material.HEAVY_WEIGHTED_PRESSURE_PLATE).setGuiProperty().build(),
                                Arrays.asList("&6Hidden Button", "&9Current value: '&r" + isHidden() + "&9'", "",
                                        "&7Hidden task do not show on mission task list", "&7Right Click to reset to default"),
                                null, true),
                        Utils.setDescription(
                                new ItemBuilder(Material.LIGHT_WEIGHTED_PRESSURE_PLATE).setGuiProperty().build(),
                                Arrays.asList("&6Hidden Button", "&9Current value: '&r" + isHidden() + "&9'", "",
                                        "&7Hidden task do not show on mission task list",
                                        "&7Right Click to reset to default"),
                                null, true),
                        ATaskGuiEditor.this);

            }

            @Override
            public boolean getCurrentValue() {
                return isHidden;
            }

            @Override
            public boolean onValueChangeRequest(boolean value) {
                setHidden(value);
                return true;
            }

            @Override
            public void onClick(Player clicker, ClickType click) {
                if (click == ClickType.RIGHT) {
                    setHidden(null);
                } else
                    super.onClick(clicker, click);
            }

        }

        private class MaxProgressButton extends AmountSelectorButton {

            public MaxProgressButton() {
                super("&9MaxProgress Editor", new ItemBuilder(Material.REPEATER).setGuiProperty().build(),
                        ATaskGuiEditor.this, 1, 5, 10, 50, 100, 500, 1000);
            }

            @Override
            public List<String> getButtonDescription() {
                List<String> desc = new ArrayList<>();
                desc.add("&6Max Progress Button");
                desc.add("&9Current value: &e" + maxProgress);
                desc.add("");
                desc.add("&7Click to edit");
                return desc;
            }

            @Override
            public long getCurrentAmount() {
                return maxProgress;
            }

            @Override
            public boolean onAmountChangeRequest(long value) {
                setMaxProgress((int) value);
                return true;
            }

        }

        private class ProgressChanceButton extends DoubleAmountEditorButton {

            public ProgressChanceButton() {
                super("&9ProgressChance Editor", new ItemBuilder(Material.IRON_NUGGET).setGuiProperty().build(),
                        ATaskGuiEditor.this, 0.0005D, 0.001D, 0.005D, 0.01D, 0.05D, 0.1D, 0.5D);
            }

            @Override
            public List<String> getButtonDescription() {
                List<String> desc = new ArrayList<>();
                desc.add("&6Progress Chance Button");
                desc.add("&9Current value: &e" + progressChance);
                desc.add("&9 = &e" + (progressChance * 100) + "&9%");
                desc.add("");
                desc.add("&7Click to edit");
                return desc;
            }

            @Override
            public double getCurrentAmount() {
                return progressChance;
            }

            @Override
            public boolean onAmountChangeRequest(double value) {
                setProgressChance(value);
                return true;
            }

        }

        private class BarStyleButton extends ElementSelectorButton<BarStyle> {

            public BarStyleButton() {
                super("&9BarStyle Editor", new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).setGuiProperty().build(),
                        ATaskGuiEditor.this, true, true, false);
            }

            public ItemStack getItem() {
                ItemStack item = super.getItem();
                item.setAmount(getAmount(getBossBarStyle()));
                return item;
            }

            private int getAmount(BarStyle style) {
                return switch (style) {
                    case SEGMENTED_10 -> 10;
                    case SEGMENTED_12 -> 12;
                    case SEGMENTED_20 -> 20;
                    case SEGMENTED_6 -> 6;
                    default -> 1;
                };
            }

            @Override
            public List<String> getButtonDescription() {
                List<String> desc = new ArrayList<>();
                desc.add("&6BarStyle Button");
                desc.add("&9Current Type: &e" + getBossBarStyle().toString());
                if (isBarStyleDefault)
                    desc.add("&9Inherit from TaskType");
                desc.add("");
                desc.add("&7Click to edit");
                return desc;
            }

            @Override
            public List<String> getElementDescription(BarStyle element) {
                List<String> desc = new ArrayList<>();
                desc.add("&9Type: &e" + element.toString());
                return desc;
            }

            @Override
            public ItemStack getElementItem(BarStyle element) {
                return new ItemBuilder(Material.OAK_SIGN).setGuiProperty().setAmount(getAmount(element)).build();
            }

            @Override
            public void onElementSelectRequest(BarStyle element) {
                setBossBarStyle(element);
                ATaskGuiEditor.this.updateInventory();
                getTargetPlayer().openInventory(ATaskGuiEditor.this.getInventory());
            }

            @Override
            public Collection<BarStyle> getPossibleValues() {
                return Arrays.asList(BarStyle.values());
            }
        }

        private class BarColorButton extends ElementSelectorButton<BarColor> {

            public BarColorButton() {
                super("&9BarColor Editor", new ItemBuilder(Material.ORANGE_DYE).setGuiProperty().build(),
                        ATaskGuiEditor.this, true, true, false);
            }

            public ItemStack getItem() {
                ItemStack item = super.getItem();
                switch (getBossBarColor()) {
                    case BLUE -> item.setType(Material.LAPIS_LAZULI);
                    case GREEN -> item.setType(Material.LIME_DYE);
                    case PINK -> item.setType(Material.PINK_DYE);
                    case PURPLE -> item.setType(Material.PURPLE_DYE);
                    case RED -> item.setType(Material.RED_DYE);
                    case WHITE -> item.setType(Material.BONE_MEAL);
                    case YELLOW -> item.setType(Material.YELLOW_DYE);
                    default -> {
                    }
                }
                return item;
            }

            @Override
            public List<String> getButtonDescription() {
                List<String> desc = new ArrayList<>();
                desc.add("&6BarColor Button");
                desc.add("&9Current Type: &e" + getBossBarColor().toString());
                if (isBarColorDefault)
                    desc.add("&9Inherit from TaskType");
                desc.add("");
                desc.add("&7Click to edit");
                return desc;
            }

            @Override
            public List<String> getElementDescription(BarColor element) {
                List<String> desc = new ArrayList<>();
                desc.add("&9Type: &e" + element.toString());
                return desc;
            }

            @Override
            public ItemStack getElementItem(BarColor element) {
                return switch (element) {
                    case BLUE -> new ItemBuilder(Material.BLUE_CONCRETE).setGuiProperty().build();
                    case GREEN -> new ItemBuilder(Material.GREEN_CONCRETE).setGuiProperty().build();
                    case PINK -> new ItemBuilder(Material.PINK_CONCRETE).setGuiProperty().build();
                    case PURPLE -> new ItemBuilder(Material.PURPLE_CONCRETE).setGuiProperty().build();
                    case RED -> new ItemBuilder(Material.RED_CONCRETE).setGuiProperty().build();
                    case WHITE -> new ItemBuilder(Material.WHITE_CONCRETE).setGuiProperty().build();
                    case YELLOW -> new ItemBuilder(Material.YELLOW_CONCRETE).setGuiProperty().build();
                    default -> new ItemBuilder(Material.ORANGE_CONCRETE).setGuiProperty().build();
                };
            }

            @Override
            public void onElementSelectRequest(BarColor element) {
                setBossBarColor(element);
                ATaskGuiEditor.this.updateInventory();
                getTargetPlayer().openInventory(ATaskGuiEditor.this.getInventory());
            }

            @Override
            public Collection<BarColor> getPossibleValues() {
                return Arrays.asList(BarColor.values());
            }
        }

        private class BarShowButton extends ElementSelectorButton<Boolean> {

            public BarShowButton() {
                super("&9BarShow Editor", new ItemBuilder(Material.ARMOR_STAND).setGuiProperty().build(),
                        ATaskGuiEditor.this, true, true, false);
            }

            @Override
            public List<String> getButtonDescription() {
                List<String> desc = new ArrayList<>();
                desc.add("&6BarShow Button");
                desc.add("&9Current Value: &e" + showBossBar());
                if (isShowBossBarDefault)
                    desc.add("&9Inherit from TaskType");
                desc.add("");
                desc.add("&7Click to edit");
                return desc;
            }

            @Override
            public List<String> getElementDescription(Boolean element) {
                List<String> desc = new ArrayList<>();
                desc.add("&9ShowBoss bar? &e" + element);
                return desc;
            }

            @Override
            public ItemStack getElementItem(Boolean element) {
                if (element == null)
                    return new ItemBuilder(Material.BARRIER).setGuiProperty().build();
                if (element)
                    return new ItemBuilder(Material.LIME_CONCRETE).setGuiProperty().build();
                return new ItemBuilder(Material.RED_CONCRETE).setGuiProperty().build();
            }

            @Override
            public void onElementSelectRequest(Boolean element) {
                setShowBossBar(element);
                ATaskGuiEditor.this.updateInventory();
                getTargetPlayer().openInventory(ATaskGuiEditor.this.getInventory());
            }

            @Override
            public Collection<Boolean> getPossibleValues() {
                return Arrays.asList(true, false);
            }
        }

        private class PhaseDescriptionButton extends TextEditorButton {

            private final Phase phase;

            public PhaseDescriptionButton(Phase phase) {
                super(new ItemBuilder(Material.WHITE_TERRACOTTA).setGuiProperty().build(), ATaskGuiEditor.this);
                this.phase = phase;
            }

            private Material getMaterialDescButton(Phase phase) {
                if (phaseDescriptionIsDef.get(phase))
                    return switch (phase) {
                        case COMPLETE -> Material.GREEN_STAINED_GLASS;
                        case PROGRESS -> Material.BLUE_STAINED_GLASS;
                        case UNSTARTED -> Material.LIGHT_BLUE_STAINED_GLASS;
                        default -> throw new IllegalStateException();
                    };
                else
                    return switch (phase) {
                        case COMPLETE -> Material.GREEN_TERRACOTTA;
                        case PROGRESS -> Material.BLUE_TERRACOTTA;
                        case UNSTARTED -> Material.LIGHT_BLUE_TERRACOTTA;
                        default -> throw new IllegalStateException();
                    };

            }

            public ItemStack getItem() {
                ItemStack item = super.getItem();
                if (phase == null)
                    return item;
                item.setType(getMaterialDescButton(phase));
                return item;
            }

            @Override
            public void onClick(Player clicker, ClickType click) {
                switch (click) {
                    case RIGHT, SHIFT_RIGHT -> {
                        setPhaseDescription(null, phase);
                        getGui().updateInventory();
                        return;
                    }
                    default -> {
                    }
                }
                this.requestText(clicker, getRawPhaseDescription(phase),
                        "&6Set the " + phase.name().toLowerCase() + " description");
            }

            @Override
            public List<String> getButtonDescription() {
                List<String> desc = new ArrayList<>();
                if (phase == null)
                    return desc;
                desc.add("&6" + phase.name().toLowerCase() + " Description Button");
                desc.add("&9Current value: '&r" + getRawPhaseDescription(phase) + "&9'");
                if (phaseDescriptionIsDef.get(phase))
                    desc.add("&9Inherit from TaskType");
                desc.add("");
                desc.add("&7Right Click to reset to default/update");
                desc.add("&7Left Click to edit");
                return desc;
            }

            @Override
            public void onReicevedText(String text) {
                setPhaseDescription(text, phase);
                getGui().updateInventory();
            }
        }

        private class ProgressRewardSelector extends GuiElementSelectorButton<Reward<T>> {

            public ProgressRewardSelector() {
                super("&9Select a ProgressReward", new ItemBuilder(Material.IRON_BARS).setGuiProperty().build(),
                        ATaskGuiEditor.this, false, true, false);
            }

            @Override
            public List<String> getButtonDescription() {
                return Arrays.asList("&6Click to select a ProgressReward", "", "&6each quest may contain tasks");
            }

            @Override
            public Collection<Reward<T>> getValues() {
                return getProgressRewards();
            }

            @Override
            public void onElementSelectRequest(Reward<T> element, Player p) {
                p.openInventory(element.getEditorGui(p, ATaskGuiEditor.this).getInventory());
            }
        }

        private class ProgressRewardDelete extends GuiElementSelectorButton<Reward<T>> {

            public ProgressRewardDelete() {
                super("&cDelete a ProgressReward", new ItemBuilder(Material.RED_DYE).setGuiProperty().build(),
                        ATaskGuiEditor.this, false, true, true);
            }

            @Override
            public List<String> getButtonDescription() {
                return Arrays.asList("&cClick to delete a ProgressReward", "", "&cDelete can't be undone");
            }

            @Override
            public Collection<Reward<T>> getValues() {
                return getProgressRewards();
            }

            @Override
            public void onElementSelectRequest(Reward<T> element, Player p) {
                getManager().unlinkProgressReward(element, ATask.this);
                ATaskGuiEditor.this.updateInventory();
                p.openInventory(ATaskGuiEditor.this.getInventory());
            }
        }

        private class ProgressRewardCreate extends ElementSelectorButton<RewardType<T>> {

            public ProgressRewardCreate() {
                super("&9Select a RewardType for the new ProgressReward",
                        new ItemBuilder(Material.YELLOW_DYE).setGuiProperty().build(), ATaskGuiEditor.this, false, true,
                        false);
            }

            @Override
            public List<String> getButtonDescription() {
                return List.of("&6Click to create a new ProgressReward");
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
                getManager().linkProgressReward(reward, ATask.this);
                this.getTargetPlayer()
                        .openInventory(reward.getEditorGui(this.getTargetPlayer(), ATaskGuiEditor.this).getInventory());
            }

            @Override
            public Collection<RewardType<T>> getPossibleValues() {
                return getManager().getRewardProvider().getTaskTypes();
            }
        }

        private class CompleteRewardSelector extends GuiElementSelectorButton<Reward<T>> {

            public CompleteRewardSelector() {
                super("&9Select a CompleteReward", new ItemBuilder(Material.IRON_BARS).setGuiProperty().build(),
                        ATaskGuiEditor.this, false, true, false);
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
                p.openInventory(element.getEditorGui(p, ATaskGuiEditor.this).getInventory());
            }
        }

        private class CompleteRewardDelete extends GuiElementSelectorButton<Reward<T>> {

            public CompleteRewardDelete() {
                super("&cDelete a CompleteReward", new ItemBuilder(Material.RED_DYE).setGuiProperty().build(),
                        ATaskGuiEditor.this, false, true, true);
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
                getManager().unlinkCompleteReward(element, ATask.this);
                ATaskGuiEditor.this.updateInventory();
                p.openInventory(ATaskGuiEditor.this.getInventory());
            }
        }

        private class CompleteRewardCreate extends ElementSelectorButton<RewardType<T>> {

            public CompleteRewardCreate() {
                super("&9Select a RewardType for the new CompleteReward",
                        new ItemBuilder(Material.YELLOW_DYE).setGuiProperty().build(), ATaskGuiEditor.this, false, true,
                        false);
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
                getManager().linkCompleteReward(reward, ATask.this);
                this.getTargetPlayer()
                        .openInventory(reward.getEditorGui(this.getTargetPlayer(), ATaskGuiEditor.this).getInventory());
            }

            @Override
            public Collection<RewardType<T>> getPossibleValues() {
                return getManager().getRewardProvider().getTaskTypes();
            }
        }
    }
}
