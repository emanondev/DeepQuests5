package emanondev.deepquests.implementations;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.gui.button.AButton;
import emanondev.deepquests.gui.button.ElementSelectorButton;
import emanondev.deepquests.gui.button.GuiElementSelectorButton;
import emanondev.deepquests.gui.button.StaticFlagButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.interfaces.*;
import emanondev.deepquests.utils.StringUtils;
import emanondev.deepquests.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AQuest<T extends User<T>> extends AQuestComponentWithCooldown<T> implements Quest<T> {

    private final QuestDisplayInfo<T> displayInfo;

    private final Map<Integer, Mission<T>> missions = new HashMap<>();
    private final Map<Integer, Require<T>> requires = new HashMap<>();
    private boolean isDeveloped;

    public AQuest(int id, QuestManager<T> manager, YMLSection section) {
        super(id, section, manager);
        isDeveloped = getConfig().getBoolean(Paths.QUEST_IS_DEVELOPED, false);
        displayInfo = new QuestDisplayInfo<>(getConfig().loadSection(Paths.QUESTCOMPONENT_DISPLAY_INFO), this);
    }

    @Override
    public long getCooldownLeft(@NotNull T user) {
        QuestData<T> data = user.getQuestData(this);
        return Math.max(0, data.getCooldownTimeLeft());
    }

    @Override
    public Gui getEditorGui(Player target, Gui parent) {
        return new AQuestGuiEditor(target, parent);
    }

    @Override
    public List<String> getInfo() {
        List<String> info = new ArrayList<>();
        info.add("&9&lQuest: &6" + this.getDisplayName());
        info.add("&8ID: " + this.getID());
        info.add("");
        if (!isDeveloped()) {
            info.add("&cPlayers can't advance nor see this Quest");
            info.add("&cbecause it's marked as uncompleted.");
            info.add("");
        }

        info.add("&9Priority: &e" + getPriority());

        if (!this.isRepeatable())
            info.add("&9Repeatable: &cFalse");
        else {
            info.add("&9Repeatable: &aTrue");
            info.add("&9Cooldown: &e" + StringUtils.getStringCooldown(this.getCooldownTime()));
        }
        if (missions.size() > 0) {
            info.add("&9Missions:");
            for (Mission<T> mission : missions.values()) {
                info.add("&9 - &e" + mission.getDisplayName());
            }
        }
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
        if (requires.size() > 0) {
            info.add("&9Requires:");
            for (Require<T> require : requires.values()) {
                info.add("&9 - &e" + require.getDisplayName());
                info.add("   &8" + (require.getType() == null ? "&cError" : require.getType().getKeyID()));
            }
        }
        return info;
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
    public final Mission<T> getMission(int id) {
        return missions.get(id);
    }

    @Override
    public final @NotNull Collection<Mission<T>> getMissions() {
        return Collections.unmodifiableCollection(missions.values());
    }

    @Override
    public final boolean addMission(@NotNull Mission<T> mission) {
        if (missions.containsKey(mission.getID()))
            throw new IllegalArgumentException();
        missions.put(mission.getID(), mission);
        return true;
    }

    @Override
    public final boolean removeMission(@NotNull Mission<T> mission) {
        return missions.remove(mission.getID()) != null;
    }

    @Override
    public final @NotNull Collection<Require<T>> getRequires() {
        return Collections.unmodifiableCollection(requires.values());
    }

    @Override
    public final Require<T> getRequire(int id) {
        return requires.get(id);
    }

    @Override
    public final boolean addRequire(@NotNull Require<T> require) {
        if (requires.containsKey(require.getID()))
            throw new IllegalArgumentException();
        requires.put(require.getID(), require);
        return true;
    }

    @Override
    public final boolean removeRequire(@NotNull Require<T> require) {
        return requires.remove(require.getID()) != null;
    }

    @Override
    public final boolean isDeveloped() {
        return isDeveloped;
    }

    @Override
    public final void setDeveloped(boolean value) {
        if (isDeveloped == value)
            return;
        this.isDeveloped = value;
        getConfig().set(Paths.QUEST_IS_DEVELOPED, isDeveloped ? true : null);
    }

    @Override
    public final QuestDisplayInfo<T> getDisplayInfo() {
        return displayInfo;
    }

    protected class AQuestGuiEditor extends AAAGuiEditor {

        public AQuestGuiEditor(Player player, Gui parent) {
            super("&9Quest: &r" + getDisplayName() + " &9ID: &e" + getID(), player, parent);
            this.putButton(10, displayInfo.getDisplayEditorButton(this));
            this.putButton(11, new DevelopButton());
            this.putButton(27, new MissionCreate());
            this.putButton(28, new MissionSelector());
            this.putButton(29, new MissionDelete());
            this.putButton(36, new RequireCreate());
            this.putButton(37, new RequireSelector());
            this.putButton(38, new RequireDelete());
        }

        private class DevelopButton extends StaticFlagButton {

            public DevelopButton() {
                super(Utils.setDescription(new ItemBuilder(Material.RED_BANNER).setGuiProperty().build(),
                                Arrays.asList("&cQuest LOCKED", "", "&7avoid users from starting or seeing it",
                                        "&7useful to block a quest until completed", "&7click to unlock"),
                                null, true),
                        Utils.setDescription(new ItemBuilder(Material.LIME_BANNER).setGuiProperty().build(),
                                Arrays.asList("&aQuest UNLOCKED", "", "&7allow users to starting and seeing it",
                                        "&7useful to block a quest until completed", "&7click to lock"),
                                null, true),
                        AQuestGuiEditor.this);
            }

            @Override
            public boolean getCurrentValue() {
                return isDeveloped();
            }

            @Override
            public boolean onValueChangeRequest(boolean value) {
                setDeveloped(value);
                return true;
            }

        }

        private class MissionSelector extends GuiElementSelectorButton<Mission<T>> {

            public MissionSelector() {
                super("&9Select a Mission", new ItemBuilder(Material.BOOK).setGuiProperty().build(),
                        AQuestGuiEditor.this, false, true, false);
            }

            @Override
            public List<String> getButtonDescription() {
                return Arrays.asList("&6Click to select a Mission", "", "&6each quest may contain missions");
            }

            @Override
            public Collection<Mission<T>> getValues() {
                return getMissions();
            }

            @Override
            public void onElementSelectRequest(Mission<T> element, Player p) {
                p.openInventory(element.getEditorGui(p, AQuestGuiEditor.this).getInventory());
            }
        }

        private class MissionDelete extends GuiElementSelectorButton<Mission<T>> {

            public MissionDelete() {
                super("&cDelete a Mission", new ItemBuilder(Material.RED_DYE).setGuiProperty().build(),
                        AQuestGuiEditor.this, false, true, true);
            }

            @Override
            public List<String> getButtonDescription() {
                return Arrays.asList("&cClick to delete a Mission", "", "&cAlso delete related Tasks",
                        "&cDelete can't be undone");
            }

            @Override
            public Collection<Mission<T>> getValues() {
                return getMissions();
            }

            @Override
            public void onElementSelectRequest(Mission<T> element, Player p) {
                getManager().delete(element);
                AQuestGuiEditor.this.updateInventory();
                p.openInventory(AQuestGuiEditor.this.getInventory());
            }
        }

        private class MissionCreate extends AButton {

            private final ItemStack item = new ItemBuilder(Material.YELLOW_DYE).setGuiProperty().build();

            public MissionCreate() {
                super(AQuestGuiEditor.this);
            }

            @Override
            public ItemStack getItem() {
                Utils.updateDescription(item, List.of("&6Click to create a new Mission"),
                        getGui().getTargetPlayer(), true);
                return item;
            }

            @Override
            public boolean update() {
                return true;
            }

            @Override
            public void onClick(Player clicker, ClickType click) {
                Mission<T> mission = getManager().createMission(AQuest.this, clicker);
                clicker.openInventory(mission.getEditorGui(clicker, AQuestGuiEditor.this).getInventory());
            }

        }

        private class RequireSelector extends GuiElementSelectorButton<Require<T>> {

            public RequireSelector() {
                super("&9Select a Require", new ItemBuilder(Material.IRON_BARS).setGuiProperty().build(),
                        AQuestGuiEditor.this, false, true, false);
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
                p.openInventory(element.getEditorGui(p, AQuestGuiEditor.this).getInventory());
            }
        }

        private class RequireDelete extends GuiElementSelectorButton<Require<T>> {

            public RequireDelete() {
                super("&cDelete a Require", new ItemBuilder(Material.RED_DYE).setGuiProperty().build(),
                        AQuestGuiEditor.this, false, true, true);
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
                getManager().unlinkRequire(element, AQuest.this);
                AQuestGuiEditor.this.updateInventory();
                p.openInventory(AQuestGuiEditor.this.getInventory());
            }
        }

        private class RequireCreate extends ElementSelectorButton<RequireType<T>> {

            public RequireCreate() {
                super("&9Select a RequireType for the new Require",
                        new ItemBuilder(Material.YELLOW_DYE).setGuiProperty().build(), AQuestGuiEditor.this, false,
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
                getManager().linkRequire(require, AQuest.this);
                this.getTargetPlayer().openInventory(
                        require.getEditorGui(this.getTargetPlayer(), AQuestGuiEditor.this).getInventory());
            }

            @Override
            public Collection<RequireType<T>> getPossibleValues() {
                return getManager().getRequireProvider().getQuestTypes();
            }
        }
    }
}
