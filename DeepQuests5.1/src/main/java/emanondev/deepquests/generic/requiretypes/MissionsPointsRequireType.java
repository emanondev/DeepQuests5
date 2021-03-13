package emanondev.deepquests.generic.requiretypes;

import java.util.*;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import emanondev.core.YMLSection;
import emanondev.deepquests.data.AmountData;
import emanondev.deepquests.data.TargetQuestData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.ARequire;
import emanondev.deepquests.implementations.ARequireType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.Quest;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.User;
import emanondev.core.ItemBuilder;

public class MissionsPointsRequireType<T extends User<T>> extends ARequireType<T> {

	public MissionsPointsRequireType(QuestManager<T> manager) {
		super(ID, manager);
	}

	private static final String ID = "missions_points";

	@Override
	public Material getGuiMaterial() {
		return Material.IRON_NUGGET;
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("&7Player require missions points");
	}

	@Override
	public MissionsPointsRequire getInstance(int id, QuestManager<T> manager, YMLSection section) {
		return new MissionsPointsRequire(id, manager, section);
	}

	public class MissionsPointsRequire extends ARequire<T> {

		private AmountData<T, MissionsPointsRequire> amountData = null;
		private TargetQuestData<T, MissionsPointsRequire> targetQuestData = null;

		public MissionsPointsRequire(int id, QuestManager<T> manager, YMLSection section) {
			super(id, manager, MissionsPointsRequireType.this, section);
			amountData = new AmountData<T, MissionsPointsRequire>(this,
					getConfig().loadSection(Paths.REQUIRE_INFO_AMOUNT));
			targetQuestData = new TargetQuestData<T, MissionsPointsRequire>(this,
					getConfig().loadSection(Paths.REQUIRE_INFO_TARGETQUEST));
		}

		public AmountData<T, MissionsPointsRequire> getAmountData() {
			return amountData;
		}

		public boolean isAllowed(T p) {
			Quest<T> quest = (Quest<T>) targetQuestData.getQuest();
			if (quest == null)
				return false;

			return p.getQuestData(quest).getPoints() >= amountData.getAmount();
		}

		public List<String> getInfo() {
			List<String> info = super.getInfo();
			info.add("&9Required Points: &e" + amountData.getAmount());
			info.addAll(targetQuestData.getInfo());
			return info;
		}

		public TargetQuestData<T, MissionsPointsRequire> getTargetQuestData() {
			return targetQuestData;
		}

		@Override
		public Gui getEditorGui(Player target, Gui parent) {
			return new GuiEditor(target, parent);
		}

		private class GuiEditor extends ARequireGuiEditor {

			public GuiEditor(Player player, Gui previusHolder) {
				super(player, previusHolder);
				this.putButton(27, targetQuestData.getQuestSelectorButton(this));
				this.putButton(28,
						amountData.getAmountEditorButton("&9Required Points Selector",
								Arrays.asList("&6Required Points Selector", "&9Amount: &e%amount%"),
								new ItemBuilder(Material.REPEATER).setGuiProperty().build(), this));
			}
		}
	}

}