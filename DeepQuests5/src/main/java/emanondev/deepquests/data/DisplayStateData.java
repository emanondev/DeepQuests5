package emanondev.deepquests.data;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.gui.button.Button;
import emanondev.deepquests.gui.button.CollectionSelectorButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestComponent;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.utils.DisplayState;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class DisplayStateData<T extends User<T>, E extends QuestComponent<T>> extends QuestComponentData<T, E> {

    private final Set<DisplayState> states = EnumSet.noneOf(DisplayState.class);

    public DisplayStateData(E parent, YMLSection section) {
        super(parent, section);
        states.addAll(getConfig().loadEnumSet(Paths.DATA_DISPLAY_STATES, EnumSet.of(DisplayState.COMPLETED),
                DisplayState.class));
    }

    public void toggleState(DisplayState state) {
        if (states.contains(state))
            states.remove(state);
        else
            states.add(state);
        getConfig().setEnumsAsStringList(Paths.DATA_DISPLAY_STATES, states);
    }

    public boolean isValidState(DisplayState state) {
        return states.contains(state);
    }

    public List<String> getInfo() {
        ArrayList<String> list = new ArrayList<>();
        if (states.isEmpty()) {
            list.add("&9Valid DisplayStates:");
            list.add("&cNo DisplayState has been set");
        } else {
            list.add("&9Valid DisplayStates:");
            for (DisplayState state : states)
                list.add("  &9- &a" + state.toString());
        }
        return list;
    }

    public Button getDisplaySelectorButton(Gui gui) {
        return new DisplaySelectorButton(gui);
    }

    private class DisplaySelectorButton extends CollectionSelectorButton<DisplayState> {

        public DisplaySelectorButton(Gui parent) {
            super("&9Select which states are allowed",
                    new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).setGuiProperty().build(), parent, false);
        }

        @Override
        public Collection<DisplayState> getPossibleValues() {
            return Arrays.asList(DisplayState.values());
        }

        @Override
        public List<String> getButtonDescription() {
            List<String> list = getInfo();
            list.add(0, "&6DisplayState Editor");
            return list;
        }

        @Override
        public List<String> getElementDescription(DisplayState element) {
            List<String> list = new ArrayList<>();
            list.add("&9DisplayState: &e" + element.toString());
            list.add("&9" + element.getDescription());
            return list;
        }

        @Override
        public ItemStack getElementItem(DisplayState element) {
            switch (element) {
                case COMPLETED:
                    return new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setGuiProperty().build();
                case COOLDOWN:
                    return new ItemBuilder(Material.ORANGE_STAINED_GLASS_PANE).setGuiProperty().build();
                case FAILED:
                    return new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setGuiProperty().build();
                case LOCKED:
                    return new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setGuiProperty().build();
                case ONPROGRESS:
                    return new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).setGuiProperty().build();
                case UNSTARTED:
                    return new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).setGuiProperty().build();
                default:
                    break;
            }
            return new ItemBuilder(Material.GLASS_PANE).setGuiProperty().build();
        }

        @Override
        public boolean isValidContains(DisplayState element) {
            return isValidState(element);
        }

        @Override
        public boolean getIsWhitelist() {
            return true;
        }

        @Override
        public boolean onToggleElementRequest(DisplayState element) {
            toggleState(element);
            return true;
        }

        @Override
        public boolean onWhitelistToggle() {
            return false;
        }
    }
}
