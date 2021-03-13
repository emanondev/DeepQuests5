package emanondev.deepquests.town;

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
import emanondev.deepquests.town.rewardtypes.ConsoleCommandRewardType;

public class TownQuestManager extends AQuestManager<QuestTown> {

	private TownUserManager userManager;
	public final static String NAME = "towns";

	public TownQuestManager(String name, CorePlugin plugin) {
		super(name, plugin);
		userManager = new TownUserManager(this);

		// TODO types
		this.getRewardProvider().registerType(new ConsoleCommandRewardType(this));
	}

	@Override
	public TownUserManager getUserManager() {
		return userManager;
	}

	@Override
	public SortableButton getEditorButton(Gui parent) {
		return new GuiElementButton<TownQuestManager>(parent, this);
	}

	@Override
	public Gui getEditorGui(Player target, Gui parent) {
		return new EditorGui(target, parent);
	}

	@Override
	public List<String> getInfo() {
		return Arrays.asList("&6&lTowns", "&6Quests related to towns");
	}

	protected class EditorGui extends AQuestManager<QuestTown>.EditorGui {

		public EditorGui(Player player, Gui previusHolder) {
			super(player, previusHolder);
		}

	}

	@Override
	public Material getGuiMaterial() {
		return Material.BRICK;
	}

	@Override
	public QuestTown getArgomentUser(String argoment) {
		try {
			return getUserManager().getUser(TownyAPI.getInstance().getDataSource().getTown(argoment));
		} catch (NotRegisteredException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Collection<String> getUsersArguments() {
		return TownyAPI.getInstance().getDataSource().getTownsKeys();
	}

}
