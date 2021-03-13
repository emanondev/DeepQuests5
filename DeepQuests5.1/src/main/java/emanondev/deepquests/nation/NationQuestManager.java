package emanondev.deepquests.nation;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;

import emanondev.core.CorePlugin;
import emanondev.deepquests.gui.button.GuiElementButton;
import emanondev.deepquests.gui.button.SortableButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.AQuestManager;
import emanondev.deepquests.nation.NationQuestManager;
import emanondev.deepquests.nation.rewardtypes.ConsoleCommandRewardType;

public class NationQuestManager extends AQuestManager<QuestNation> {

	private NationUserManager userManager;
	public final static String NAME = "nations";

	public NationQuestManager(String name, CorePlugin plugin) {
		super(name, plugin);
		userManager = new NationUserManager(this);

		// TODO types
		this.getRewardProvider().registerType(new ConsoleCommandRewardType(this));
	}

	@Override
	public NationUserManager getUserManager() {
		return userManager;
	}

	@Override
	public SortableButton getEditorButton(Gui parent) {
		return new GuiElementButton<NationQuestManager>(parent, this);
	}

	@Override
	public Gui getEditorGui(Player target, Gui parent) {
		return new EditorGui(target, parent);
	}

	@Override
	public List<String> getInfo() {
		return Arrays.asList("&6&lNations", "&6Quests related to nations");
	}

	protected class EditorGui extends AQuestManager<QuestNation>.EditorGui {

		public EditorGui(Player player, Gui previusHolder) {
			super(player, previusHolder);
		}

	}

	@Override
	public Material getGuiMaterial() {
		return Material.BRICKS;
	}

	@Override
	public QuestNation getArgomentUser(String argoment) {
		try {
			return getUserManager().getUser(TownyAPI.getInstance().getDataSource().getNation(argoment));
		} catch (NotRegisteredException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Collection<String> getUsersArguments() {
		return TownyAPI.getInstance().getDataSource().getNationsKeys();
	}

}
