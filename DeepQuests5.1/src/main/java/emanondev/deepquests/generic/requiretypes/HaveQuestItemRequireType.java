package emanondev.deepquests.generic.requiretypes;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.data.*;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.*;
import emanondev.deepquests.interfaces.*;

public class HaveQuestItemRequireType<T extends User<T>> extends ARequireType<T> {

	public HaveQuestItemRequireType(QuestManager<T> manager) {
		super(ID, manager);
	}

	private static final String ID = "have_quest_items";

	@Override
	public Material getGuiMaterial() {
		return Material.IRON_NUGGET;
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("&7Player require missions points");
	}

	@Override
	public HaveQuestItemRequire getInstance(int id, QuestManager<T> manager, YMLSection section) {
		return new HaveQuestItemRequire(id, manager, section);
	}

	public class HaveQuestItemRequire extends ARequire<T> {

		private AmountData<T, HaveQuestItemRequire> amountData = null;
		private QuestItemData<T, HaveQuestItemRequire> questItemData = null;

		public HaveQuestItemRequire(int id, QuestManager<T> manager, YMLSection section) {
			super(id, manager, HaveQuestItemRequireType.this, section);
			amountData = new AmountData<T, HaveQuestItemRequire>(this,
					getConfig().loadSection(Paths.REQUIRE_INFO_AMOUNT), 1, Integer.MAX_VALUE, 1);
			questItemData = new QuestItemData<T, HaveQuestItemRequire>(this,
					getConfig().loadSection(Paths.REQUIRE_INFO_TARGETQUEST));
		}

		public AmountData<T, HaveQuestItemRequire> getAmountData() {
			return amountData;
		}

		public boolean isAllowed(T p) {
			if (this.questItemData.getQuestItemID() == null)
				return false;
			return p.getQuestBag().hasQuestItem(this.questItemData.getQuestItemID(), amountData.getAmount());
		}

		public List<String> getInfo() {
			List<String> info = super.getInfo();
			info.add("&9Required Points: &e" + amountData.getAmount());
			info.addAll(questItemData.getInfo());
			return info;
		}

		public QuestItemData<T, HaveQuestItemRequire> getQuestItemData() {
			return questItemData;
		}

		@Override
		public Gui getEditorGui(Player target, Gui parent) {
			return new GuiEditor(target, parent);
		}

		private class GuiEditor extends ARequireGuiEditor {

			public GuiEditor(Player player, Gui previusHolder) {
				super(player, previusHolder);
				questItemData.setupButtons(this, 27);
				this.putButton(28,
						amountData.getAmountEditorButton("&9Required Points Selector",
								Arrays.asList("&6Required Points Selector", "&9Amount: &e%amount%"),
								new ItemBuilder(Material.REPEATER).setGuiProperty().build(), this));
			}
		}
	}

}
