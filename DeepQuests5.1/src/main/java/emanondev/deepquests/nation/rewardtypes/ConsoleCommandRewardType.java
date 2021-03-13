package emanondev.deepquests.nation.rewardtypes;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import emanondev.deepquests.Translations;
import emanondev.core.YMLSection;
import emanondev.deepquests.data.CommandData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.AReward;
import emanondev.deepquests.implementations.ARewardType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Reward;
import emanondev.deepquests.nation.QuestNation;
import emanondev.deepquests.utils.DataUtils;
import emanondev.deepquests.utils.Utils;

public class ConsoleCommandRewardType extends ARewardType<QuestNation> {
	public ConsoleCommandRewardType(QuestManager<QuestNation> manager) {
		super(ID, manager);
	}

	private final static String ID = "console_command";

	@Override
	public Material getGuiMaterial() {
		return Material.COMMAND_BLOCK;
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("&7Perform a console command,", "&7%nation% is replaced with nation name",
				"&7Command must &cNOT &7contain the starting '/'");
	}

	@Override
	public ConsoleCommandReward getInstance(int id, QuestManager<QuestNation> manager, YMLSection section) {
		return new ConsoleCommandReward(id, manager, section);
	}

	public class ConsoleCommandReward extends AReward<QuestNation> {
		private CommandData<QuestNation, ConsoleCommandReward> cmdData;

		public ConsoleCommandReward(int id, QuestManager<QuestNation> manager, YMLSection section) {
			super(id, manager, ConsoleCommandRewardType.this, section);
			cmdData = new CommandData<QuestNation, ConsoleCommandReward>(this,
					getConfig().loadSection(Paths.REWARD_INFO_COMMAND));
		}

		public List<String> getInfo() {
			List<String> info = super.getInfo();
			info.addAll(cmdData.getInfo());
			return info;
		}

		public CommandData<QuestNation, ConsoleCommandReward> getCommandData() {
			return cmdData;
		}

		@Override
		public void apply(QuestNation qNation, int amount) {
			if (cmdData.getCommand() == null)
				return;
			String command = Utils
					.fixString(cmdData.getCommand().replace("%nation%", qNation.getNation().getName())
							.replace("%nation_name%", qNation.getNation().getName()), null, false);
			for (int i = 0; i < amount; i++)
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
		}

		@Override
		public Gui getEditorGui(Player target, Gui parent) {
			return new GuiEditor(target, parent);
		}

		private class GuiEditor extends ARewardGuiEditor {

			public GuiEditor(Player player, Gui previusHolder) {
				super(player, previusHolder);
				this.putButton(27, cmdData.getCommandEditorButton(this));
			}
		}
	}

	@Override
	public String getDefaultFeedback(Reward<QuestNation> reward) {
		if (!(reward instanceof ConsoleCommandRewardType.ConsoleCommandReward))
			return null;
		ConsoleCommandReward r = (ConsoleCommandReward) reward;
		YMLSection config = getProvider().getTypeConfig(this);
		String txt = config.getString(Paths.REWARD_FEEDBACK, null);
		if (txt == null) {
			txt = "&a{action:console_execute} &e{command}";
			config.set(Paths.REWARD_FEEDBACK, txt);

		}
		return Translations.replaceAll(txt).replace("{command}", DataUtils.getCommandHolder(r.getCommandData()));
	}

	@Override
	protected boolean getStandardHiddenValue() {
		return true;
	}
}