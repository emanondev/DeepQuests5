package emanondev.deepquests.data;

import java.util.*;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import emanondev.deepquests.gui.button.TextEditorButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestComponent;
import emanondev.deepquests.interfaces.User;
import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;

public class CommandData<T extends User<T>,E extends QuestComponent<T>> extends QuestComponentData<T,E> {
	
	private String command = null;

	public CommandData(E parent, YMLSection section) {
		super(parent, section);
		command = getConfig().getString(Paths.DATA_COMMAND, null);
		if (command != null && command.isEmpty())
				command = null;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String value) {
		if (this.command == value)
			return;
		if (value != null && value.isEmpty())
			return;
		if (this.command != null && this.command.equals(value))
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

	public CommandEditor getCommandEditorButton(Gui gui) {
		return new CommandEditor(gui);
	}

	private class CommandEditor extends TextEditorButton {

		public CommandEditor(Gui parent) {
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