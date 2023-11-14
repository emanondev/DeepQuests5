package emanondev.deepquests.data;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.gui.button.AmountSelectorButton;
import emanondev.deepquests.gui.button.Button;
import emanondev.deepquests.gui.button.ElementSelectorButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestComponent;
import emanondev.deepquests.interfaces.User;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class LocationData<T extends User<T>, E extends QuestComponent<T>> extends QuestComponentData<T, E> {

    private String worldName;
    private int x;
    private int y;
    private int z;

    public LocationData(E parent, YMLSection section) {
        super(parent, section);
        x = getConfig().getInteger(Paths.DATA_LOCATION_X, 0);
        y = getConfig().getInteger(Paths.DATA_LOCATION_Y, 0);
        z = getConfig().getInteger(Paths.DATA_LOCATION_Z, 0);

        worldName = section.getString(Paths.DATA_LOCATION_WORLD, null);
        if (worldName != null && worldName.isEmpty())
            worldName = null;
    }

    public @Nullable Location getLocation() {
        if (worldName == null)
            return null;
        World world = Bukkit.getServer().getWorld(worldName);
        if (world == null)
            return null;
        return new Location(world, x, y, z);
    }

    public void setWorld(@Nullable World world) {
        if (world == null)
            setWorld((String) null);
        else
            setWorld(world.getName());
    }

    public void setWorld(@Nullable String value) {
        if (Objects.equals(this.worldName, value))
            return;
        if (value != null && value.isEmpty())
            return;
        this.worldName = value;
        getConfig().set(Paths.DATA_LOCATION_WORLD, worldName);
    }

    public World getWorld() {
        if (worldName == null)
            return null;
        return Bukkit.getServer().getWorld(worldName);
    }

    public String getWorldName() {
        return worldName;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int value) {
        if (this.x == value)
            return;
        this.x = value;
        getConfig().set(Paths.DATA_LOCATION_X, x);
    }

    public int getY() {
        return this.y;
    }

    public void setY(int value) {
        if (this.y == value)
            return;
        this.y = value;
        getConfig().set(Paths.DATA_LOCATION_Y, y);
    }

    public int getZ() {
        return this.z;
    }

    public void setZ(int value) {
        if (this.z == value)
            return;
        this.z = value;
        getConfig().set(Paths.DATA_LOCATION_Z, z);
    }

    public boolean isValidLocation(@Nullable Location loc) {
        if (loc == null || loc.getWorld()==null)
            return false;
        if (loc.getBlockX() != x||loc.getBlockZ() != z||loc.getBlockY() != y)
            return false;
        return Objects.equals(loc.getWorld().getName(),worldName);
    }

    public ArrayList<String> getInfo() {
        ArrayList<String> info = new ArrayList<>();
        info.add("&9Location:");
        if (this.getWorld() == null)
            info.add("&9  World: &cNot setted");
        else {
            info.add("&9  World: &e" + this.getWorldName());
            info.add("&9  X: &e" + this.getX());
            info.add("&9  Y: &e" + this.getY());
            info.add("&9  Z: &e" + this.getZ());
        }
        return info;
    }

    public Button getWorldButton(Gui gui) {
        return new WorldButton(gui);
    }

    public Button getXButton(Gui gui) {
        return new CoordButton(gui,Axis.X);
    }

    public Button getYButton(Gui gui) {
        return new CoordButton(gui,Axis.Y);
    }

    public Button getZButton(Gui gui) {
        return new CoordButton(gui,Axis.Z);
    }
/*
    public class XCoordButton extends AmountSelectorButton {

        public XCoordButton(Gui parent) {
            super("&9X Coordinate Editor", new ItemBuilder(Material.REPEATER).setGuiProperty().build(), parent, 1, 10,
                    100, 1000, 10000, 100000, 1000000);
        }

        @Override
        public List<String> getButtonDescription() {
            ArrayList<String> list = new ArrayList<>();
            list.add("&6X Coordinate Editor");
            list.add("&9Current value: &e" + getX());
            return list;
        }

        @Override
        public long getCurrentAmount() {
            return getX();
        }

        @Override
        public boolean onAmountChangeRequest(long value) {
            setX((int) value);
            return true;
        }

    }
*/
    public class CoordButton extends AmountSelectorButton {
        private Axis axis = Axis.X;//Keep as update and getbuttondescription is called on super()

        public CoordButton(Gui parent, Axis axis) {
            super("&9"+axis.name()+" Coordinate Editor", new ItemBuilder(Material.REPEATER).setGuiProperty().build(), parent, 1, 10,
                    100, 1000, 10000, 100000, 1000000);
            this.axis = axis;
            update();
        }

        @Override
        public List<String> getButtonDescription() {
            ArrayList<String> list = new ArrayList<>();
            list.add("&6"+axis.name()+" Coordinate Editor");
            list.add("&9Current value: &e" + getCurrentAmount());
            return list;
        }

        @Override
        public long getCurrentAmount() {
            return switch (axis){
                case X -> getX();
                case Y -> getY();
                case Z -> getZ();
            };
        }

        @Override
        public boolean onAmountChangeRequest(long value) {
            switch (axis){
                case X -> setX((int) value);
                case Y -> setY((int) value);
                case Z -> setZ((int) value);
            }
            return true;
        }

    }
/*
    public class ZCoordButton extends AmountSelectorButton {

        public ZCoordButton(Gui parent) {
            super("&9Z Coordinate Editor", new ItemBuilder(Material.REPEATER).setGuiProperty().build(), parent, 1, 10,
                    100, 1000, 10000, 100000, 1000000);
        }

        @Override
        public List<String> getButtonDescription() {
            ArrayList<String> list = new ArrayList<>();
            list.add("&6Z Coordinate Editor");
            list.add("&9Current value: &e" + getZ());
            return list;
        }

        @Override
        public long getCurrentAmount() {
            return getZ();
        }

        @Override
        public boolean onAmountChangeRequest(long value) {
            setZ((int) value);
            return true;
        }

    }
*/
    private class WorldButton extends ElementSelectorButton<World> {

        public WorldButton(Gui parent) {
            super("&9World Selector", new ItemBuilder(Material.COMPASS).setGuiProperty().build(), parent, false, true,
                    false);
        }

        @Override
        public List<String> getButtonDescription() {
            List<String> desc = new ArrayList<>();
            desc.add("&6Worlds Button");
            if (worldName != null)
                desc.add("&9Current World: &e" + worldName);
            else
                desc.add("&cNo world has been set");
            desc.add("");
            desc.add("&7Click to edit");
            return desc;
        }

        @Override
        public List<String> getElementDescription(World element) {
            ArrayList<String> desc = new ArrayList<>();
            desc.add("&9World: &e" + element.getName());
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
        public void onElementSelectRequest(World element) {
            setWorld(element);
            getGui().getTargetPlayer().openInventory(getGui().getInventory());
        }

        @Override
        public Collection<World> getPossibleValues() {
            return Bukkit.getWorlds();
        }

    }
}