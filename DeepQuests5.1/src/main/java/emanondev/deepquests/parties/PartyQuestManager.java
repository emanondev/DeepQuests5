package emanondev.deepquests.parties;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.Party;

import emanondev.core.CorePlugin;
import emanondev.deepquests.gui.button.GuiElementButton;
import emanondev.deepquests.gui.button.SortableButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.AQuestManager;
import emanondev.deepquests.parties.rewardtypes.ConsoleCommandRewardType;

public class PartyQuestManager extends AQuestManager<QuestParty> {

	private PartyUserManager userManager;
	public final static String NAME = "parties";

	public PartyQuestManager(String name, CorePlugin plugin) {
		super(name, plugin);
		userManager = new PartyUserManager(this);

		// TODO types
		this.getRewardProvider().registerType(new ConsoleCommandRewardType(this));
	}

	@Override
	public PartyUserManager getUserManager() {
		return userManager;
	}

	@Override
	public SortableButton getEditorButton(Gui parent) {
		return new GuiElementButton<PartyQuestManager>(parent, this);
	}

	@Override
	public Gui getEditorGui(Player target, Gui parent) {
		return new EditorGui(target, parent);
	}

	@Override
	public List<String> getInfo() {
		return Arrays.asList("&6&lParties", "&6Quests related to parties");
	}

	protected class EditorGui extends AQuestManager<QuestParty>.EditorGui {

		public EditorGui(Player player, Gui previusHolder) {
			super(player, previusHolder);
		}

	}

	@Override
	public Material getGuiMaterial() {
		return Material.IRON_SWORD;
	}

	@Override
	public QuestParty getArgomentUser(String argoment) {
		Party p = Parties.getApi().getParty(argoment);
		if (p == null)
			return null;
		return getUserManager().getUser(p);
	}

	@Override
	public Collection<String> getUsersArguments() {
		return Collections.emptySet();
	}

}
