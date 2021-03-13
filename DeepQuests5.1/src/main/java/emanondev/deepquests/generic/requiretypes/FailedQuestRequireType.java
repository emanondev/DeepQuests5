package emanondev.deepquests.generic.requiretypes;

import java.util.*;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import emanondev.core.YMLSection;
import emanondev.deepquests.data.AmountData;
import emanondev.deepquests.data.TargetQuestData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.ARequire;
import emanondev.deepquests.implementations.ARequireType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.Quest;
import emanondev.deepquests.interfaces.QuestData;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Require;
import emanondev.deepquests.interfaces.User;
import emanondev.core.ItemBuilder;

public class FailedQuestRequireType<T extends User<T>> extends ARequireType<T> {

	public FailedQuestRequireType(QuestManager<T> manager) {
		super(ID, manager);
	}

	public static final String ID = "failed_quest";

	@Override
	public Material getGuiMaterial() {
		return Material.RED_BANNER;
	}

	@Override
	public ItemStack getGuiItem() {
		return new ItemBuilder(getGuiMaterial()).addPattern(DyeColor.WHITE, PatternType.CURLY_BORDER).setGuiProperty()
				.build();
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("&7Player require to fail quest n times");
	}

	@Override
	public Require<T> getInstance(int id, QuestManager<T> manager, YMLSection section) {
		return new FailedQuestRequire(id, manager, section);
	}

	public class FailedQuestRequire extends ARequire<T> {

		private TargetQuestData<T, FailedQuestRequire> targetQuest = null;
		private AmountData<T, FailedQuestRequire> amountData = null;

		public FailedQuestRequire(int id, QuestManager<T> manager, YMLSection section) {
			super(id, manager, FailedQuestRequireType.this, section);
			targetQuest = new TargetQuestData<T, FailedQuestRequire>(this,
					getConfig().loadSection(Paths.REQUIRE_INFO_TARGETMISSION));
			amountData = new AmountData<T, FailedQuestRequire>(this,
					getConfig().loadSection(Paths.REQUIRE_INFO_AMOUNT), 1, Integer.MAX_VALUE, 1);
		}

		@Override
		public boolean isAllowed(T user) {
			Quest<T> quest = targetQuest.getQuest();
			if (quest == null)
				return false;
			QuestData<T> questData = user.getQuestData(quest);
			if (questData.failedTimes() < amountData.getAmount())
				return false;
			return true;
		}

		public TargetQuestData<T, FailedQuestRequire> getTargetQuestData() {
			return targetQuest;
		}

		public AmountData<T, FailedQuestRequire> getAmountData() {
			return amountData;
		}

		public List<String> getInfo() {
			List<String> info = super.getInfo();
			info.addAll(targetQuest.getInfo());
			if (amountData.getAmount() != 1)
				info.add("&9Must be Failed: &e" + amountData.getAmount() + " &9times");
			return info;
		}

		@Override
		public Gui getEditorGui(Player target, Gui parent) {
			return new GuiEditor(target, parent);
		}

		private class GuiEditor extends ARequireGuiEditor {

			public GuiEditor(Player player, Gui previusHolder) {
				super(player, previusHolder);
				this.putButton(27, targetQuest.getQuestSelectorButton(this));
				this.putButton(28,
						amountData.getAmountEditorButton("&9Failed Times Selector",
								Arrays.asList("&6Failed Times Selector", "&9Amount: &e%amount%"),
								new ItemBuilder(Material.REPEATER).setGuiProperty().build(), this));
			}
		}
	}

}