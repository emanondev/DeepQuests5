package emanondev.deepquests.implementations;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.gui.button.CollectionSelectorButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.interfaces.HasWorlds;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

abstract class AQuestComponentWithWorlds<T extends User<T>> extends AQuestComponent<T>
        implements HasWorlds {

    private final Set<String> worlds = new HashSet<>();
    private boolean isWhitelist;

    public AQuestComponentWithWorlds(int id, @NotNull YMLSection section, @NotNull QuestManager<T> manager) {
        super(id, section, manager);
        worlds.addAll(getConfig().loadStringSet(Paths.WORLDS_LIST, getDefaultWorldsList()));
        isWhitelist = getConfig().loadBoolean(Paths.WORLDS_IS_WHITELIST, getDefaultWorldsAreWhitelist());
    }

    protected abstract Set<String> getDefaultWorldsList();

    protected abstract boolean getDefaultWorldsAreWhitelist();

    @Override
    public boolean isWorldAllowed(World world) {
        if (isWhitelist)
            return worlds.contains(world.getName());
        else
            return !worlds.contains(world.getName());
    }


    @Override
    public void toggleWorld(String world) {
        if (world == null || world.isEmpty())
            return;
        if (worlds.contains(world))
            worlds.remove(world);
        else
            worlds.add(world);
        getConfig().set(Paths.WORLDS_LIST, new ArrayList<>(worlds));
        getConfig().saveAsync();
    }

    @Override
    public void setWorldWhitelist(boolean value) {
        isWhitelist = value;
        getConfig().set(Paths.WORLDS_IS_WHITELIST, isWhitelist);
        getConfig().saveAsync();
    }

    @Override
    public boolean isWorldListWhitelist() {
        return isWhitelist;
    }

    protected class AAGuiEditor extends AGuiEditor {

        public AAGuiEditor(String title, Player player, Gui previousHolder) {
            super(title, player, previousHolder);
            this.putButton(2, new WorldsButton());
        }

        private class WorldsButton extends CollectionSelectorButton<World> {

            public WorldsButton() {
                super("&9World Selector", new ItemBuilder(Material.COMPASS).setGuiProperty().build(), AAGuiEditor.this, true);
            }

            @Override
            public Collection<World> getPossibleValues() {
                return Bukkit.getWorlds();
            }

            @Override
            public List<String> getButtonDescription() {
                List<String> desc = new ArrayList<>();
                desc.add("&6Worlds Button");
                desc.add("&9Current Worlds:");
                if (isWhitelist) {
                    desc.add("&9Current &aAllowed&9 Worlds:");
                    for (String world : worlds)
                        desc.add("  &9- &a" + world);
                } else {
                    desc.add("&9Current &cDisabled&9 Worlds:");
                    for (String world : worlds)
                        desc.add("  &9- &c" + world);
                }

                desc.add("");
                desc.add("&7Click to edit");
                return desc;
            }

            @Override
            public List<String> getElementDescription(World element) {
                ArrayList<String> desc = new ArrayList<>();
                desc.add("&9World: &6" + element.getName());
                desc.add("");
                desc.add("&9Level Type: &e" + element.getEnvironment());
                desc.add("&9Difficulty: &e" + element.getDifficulty());
                desc.add("&9Pvp: &e" + (element.getPVP() ? "on" : "off"));
                desc.add("&9Monster: &e" + (element.getAllowMonsters() ? "Allowed" : "Disabled"));
                return desc;
            }

            @Override
            public ItemStack getElementItem(World element) {
                return switch (element.getEnvironment()) {
                    case NETHER -> new ItemBuilder(Material.NETHERRACK).setGuiProperty().build();
                    case NORMAL -> new ItemBuilder(Material.GRASS_BLOCK).setGuiProperty().build();
                    case THE_END -> new ItemBuilder(Material.END_STONE).setGuiProperty().build();
                    default -> new ItemBuilder(Material.BEDROCK).setGuiProperty().build();
                };
            }

            @Override
            public boolean isValidContains(World element) {
                if (isWhitelist)
                    return worlds.contains(element.getName());
                else
                    return !worlds.contains(element.getName());
            }

            @Override
            public boolean getIsWhitelist() {
                return isWhitelist;
            }

            @Override
            public boolean onToggleElementRequest(World element) {
                return false;//toggleWorld(element);
            }

            @Override
            public boolean onWhitelistToggle() {
                return false;//toggleWorldWhitelist();
            }

        }
    }

}
