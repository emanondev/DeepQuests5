package emanondev.deepquests.data;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.Quests;
import emanondev.deepquests.Translations;
import emanondev.deepquests.gui.button.Button;
import emanondev.deepquests.gui.button.CollectionSelectorButton;
import emanondev.deepquests.gui.button.TextEditorButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestComponent;
import emanondev.deepquests.interfaces.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RegionsData<T extends User<T>, E extends QuestComponent<T>> extends QuestComponentData<T, E> {
    private final static BaseComponent[] chatText = new ComponentBuilder(
            ChatColor.GOLD + "****************************\n" + ChatColor.GOLD + " Click Me and write the name\n"
                    + ChatColor.GOLD + "****************************")
            .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/dqtext "))
            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GOLD
                    + "Write the region name\n" + ChatColor.YELLOW + "/dqtext <region name>")))
            .create();
    private final Set<String> regionNames = new TreeSet<>();
    private boolean isRegionListWhitelist;

    public RegionsData(E parent, YMLSection section) {
        super(parent, section);
        regionNames.addAll(getConfig().getStringList(Paths.DATA_REGION_LIST, new ArrayList<>()));
        isRegionListWhitelist = getConfig().getBoolean(Paths.DATA_REGION_LIST_IS_WHITELIST, true);

    }

    private static Set<String> getRegionNames(Player p) {
        Set<String> regionNames = new LinkedHashSet<>();
        Set<String> sortedSet = new TreeSet<>();

        World userWorld = null;
        if (p != null) {
            userWorld = p.getWorld();
            RegionManager rgManager = getRegionManager(userWorld);
            if (rgManager != null)
                for (ProtectedRegion region : rgManager.getRegions().values())
                    sortedSet.add(region.getId());
            regionNames.addAll(sortedSet);
            sortedSet.clear();
        }
        for (World world : Quests.get().getServer().getWorlds()) {
            if (world.equals(userWorld))
                continue;
            RegionManager rgManager = getRegionManager(world);
            if (rgManager != null)
                for (ProtectedRegion region : rgManager.getRegions().values())
                    sortedSet.add(region.getId());
            regionNames.addAll(sortedSet);
            sortedSet.clear();
        }
        return regionNames;
    }

    private static RegionManager getRegionManager(World world) {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
    }

    public boolean isValidRegion(ProtectedRegion region) {
        return isValidRegion(region.getId());
    }

    private boolean isValidRegion(String region) {
        if (isRegionListWhitelist)
            return regionNames.contains(region.toLowerCase());
        else
            return !regionNames.contains(region.toLowerCase());
    }

    public Set<String> getRegionNames() {
        return Collections.unmodifiableSet(regionNames);
    }

    public boolean areRegionNamesWhitelist() {
        return isRegionListWhitelist;
    }

    public void resetNames() {
        regionNames.clear();
        getConfig().set(Paths.DATA_REGION_LIST, new ArrayList<>(regionNames));
    }

    public void toggleName(String name) {
        name = name.toLowerCase();
        if (regionNames.contains(name))
            regionNames.remove(name);
        else
            regionNames.add(name);
        getConfig().set(Paths.DATA_REGION_LIST, new ArrayList<>(regionNames));
    }

    public void toggleRegionsWhitelist() {
        isRegionListWhitelist = !isRegionListWhitelist;
        getConfig().set(Paths.DATA_REGION_LIST_IS_WHITELIST, isRegionListWhitelist);
    }

    public Button getRegionSelectorButton(Gui parent) {
        return new RegionSelectorButton(parent);
    }

    public List<String> getInfo() {
        List<String> info = new ArrayList<>();

        if (regionNames.isEmpty()) {
            if (areRegionNamesWhitelist())
                info.add("&cNo &9region is &cValid");
            else
                info.add("&aAll &9regions are &aValid");
        } else {
            if (areRegionNamesWhitelist()) {
                info.add("&9Valid Regions:");
                for (String regionName : getRegionNames())
                    info.add("  &9- &a" + regionName);
            } else {
                info.add("&9Invalid Regions:");
                for (String regionName : getRegionNames())
                    info.add("  &9- &c" + regionName);
            }
        }
        return info;
    }

    private class RegionSelectorButton extends CollectionSelectorButton<String> {

        public RegionSelectorButton(Gui parent) {
            super("&8Region Selector", new ItemBuilder(Material.END_CRYSTAL).setGuiProperty().build(), parent, true);
        }

        @Override
        public List<String> getButtonDescription() {
            List<String> desc = new ArrayList<>();
            desc.add("&6&lRegionName Editor");
            desc.add("&7Click to edit");
            desc.add("&7(left click to select)");
            desc.add("&7(right click to write)");
            desc.add("");
            desc.addAll(getInfo());
            return desc;
        }

        @Override
        public List<String> getElementDescription(String element) {
            ArrayList<String> desc = new ArrayList<>();
            desc.add("&6Region: '&e" + element + "&6'");
            if (isValidRegion(element))
                desc.add("&7This type is &aAllowed");
            else
                desc.add("&7This type is &cUnallowed");
            desc.add("");
            desc.add("&9Translation Name: " + Translations.translateRegion(element));
            return desc;
        }

        @Override
        public ItemStack getElementItem(String element) {
            return new ItemBuilder(Material.END_CRYSTAL).setGuiProperty().build();
        }

        @Override
        public void onClick(Player clicker, ClickType click) {
            if (click == ClickType.RIGHT || click == ClickType.SHIFT_RIGHT) {
                new RegionWriterButton(this.getGui()).onClick(clicker, click);
                return;
            }
            super.onClick(clicker, click);
        }

        @Override
        public Collection<String> getPossibleValues() {
            return getRegionNames(getGui().getTargetPlayer());
        }

        @Override
        public boolean isValidContains(String element) {
            return isValidRegion(element);
        }

        @Override
        public boolean getIsWhitelist() {
            return areRegionNamesWhitelist();
        }

        @Override
        public boolean onToggleElementRequest(String element) {
            toggleName(element);
            return true;
        }

        @Override
        public boolean onWhitelistToggle() {
            toggleRegionsWhitelist();
            return true;
        }

        private class RegionWriterButton extends TextEditorButton {

            public RegionWriterButton(Gui parent) {
                super(new ItemBuilder(Material.END_CRYSTAL).setGuiProperty().build(), parent);
            }

            @Override
            public List<String> getButtonDescription() {
                return new ArrayList<>();
            }

            @Override
            public void onReicevedText(String text) {
                onToggleElementRequest(text);
            }

            @Override
            public void onClick(Player clicker, ClickType click) {
                requestText(clicker, chatText);
            }

        }

    }

}
