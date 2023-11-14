package emanondev.deepquests.data;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.gui.button.TextEditorButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestComponent;
import emanondev.deepquests.interfaces.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CommandData<T extends User<T>, E extends QuestComponent<T>> extends QuestComponentData<T, E> {

    private String command;

    public CommandData(@NotNull E parent, @NotNull YMLSection section) {
        super(parent, section);
        command = getConfig().getString(Paths.DATA_COMMAND, null);
        if (command != null && command.isEmpty())
            command = null;
    }

    public @Nullable String getCommand() {
        return command;
    }

    public void setCommand(@Nullable String value) {
        if (Objects.equals(this.command, value))
            return;
        if (value != null && value.isEmpty())
            return;
        this.command = value;
        getConfig().set(Paths.DATA_COMMAND, command);
    }

    public List<String> getInfo() {
        ArrayList<String> list = new ArrayList<>();
        if (getCommand() != null)
            list.add("&9Command: '&e/" + getCommand() + "&9'");
        else
            list.add("&9Command: &cnot set");
        return list;
    }

    public @NotNull CommandEditor getCommandEditorButton(@NotNull Gui gui) {
        return new CommandEditor(gui);
    }

    private class CommandEditor extends TextEditorButton {

        public CommandEditor(@NotNull Gui parent) {
            super(new ItemBuilder(Material.COMMAND_BLOCK).setGuiProperty().build(), parent);
        }

        @Override
        public void onClick(Player clicker, ClickType click) {
            this.requestText(clicker, command, "&6Click and write the command");
        }

        @Override
        public List<String> getButtonDescription() {
            return Arrays.asList("&6Command Editor Button", "&9Current value: '&e" + getCommand() + "&9'", "",
                    "&7Click to edit", "&7Note: %player% is replaced with player name");
        }

        @Override
        public void onReicevedText(String text) {
            setCommand(text);
        }

    }

}