package emanondev.deepquests.data;

import java.util.*;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;

import emanondev.deepquests.gui.button.Button;
import emanondev.deepquests.gui.button.ElementSelectorButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestComponent;
import emanondev.deepquests.interfaces.User;
import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;

public class McMMOSkillTypeData<T extends User<T>, E extends QuestComponent<T>> extends QuestComponentData<T, E> {
	private PrimarySkillType skillType = null;

	public McMMOSkillTypeData(E parent, YMLSection section) {
		super(parent, section);
		skillType = getConfig().loadEnum(Paths.DATA_MCMMO_SKILLTYPE, null, PrimarySkillType.class);
	}

	public PrimarySkillType getSkillType() {
		return skillType;
	}

	public void setSkillType(PrimarySkillType skillType) {
		if (this.skillType == skillType)
			return;
		if (this.skillType != null && this.skillType.equals(skillType))
			return;
		this.skillType = skillType;
		getConfig().setEnumAsString(Paths.DATA_MCMMO_SKILLTYPE, skillType);
	}

	public Button getSkillTypeSelector(Gui gui) {
		return new SkillTypeSelector(gui);
	}

	public ArrayList<String> getInfo() {
		ArrayList<String> list = new ArrayList<>();
		if (skillType != null)
			list.add("&9SkillType: &e" + skillType.getName());
		else
			list.add("&9SkillType: &cnot set");
		return list;
	}

	private class SkillTypeSelector extends ElementSelectorButton<PrimarySkillType> {

		public SkillTypeSelector(Gui parent) {
			super("&9Select a SkillType", new ItemBuilder(Material.IRON_PICKAXE).setGuiProperty().build(), parent, true,
					true, false);
		}

		@Override
		public List<String> getButtonDescription() {
			ArrayList<String> list = getInfo();
			list.add(0, "&6SkillType Selector");
			return list;
		}

		@Override
		public List<String> getElementDescription(PrimarySkillType element) {
			ArrayList<String> list = new ArrayList<>();
			list.add("&9SkillType: &e" + skillType.getName());
			return list;
		}

		@Override
		public ItemStack getElementItem(PrimarySkillType element) {
			return new ItemBuilder(Material.IRON_SWORD).setGuiProperty().build();
		}

		@Override
		public void onElementSelectRequest(PrimarySkillType element) {
			setSkillType(element);
			getGui().updateInventory();
			getGui().getTargetPlayer().openInventory(getGui().getInventory());
		}

		@Override
		public Collection<PrimarySkillType> getPossibleValues() {
			return Arrays.asList(PrimarySkillType.values());
		}

	}
}
