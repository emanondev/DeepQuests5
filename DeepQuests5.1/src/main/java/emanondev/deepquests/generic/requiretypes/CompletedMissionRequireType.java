package emanondev.deepquests.generic.requiretypes;

import java.util.*;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import emanondev.core.YMLSection;
import emanondev.deepquests.data.AmountData;
import emanondev.deepquests.data.TargetMissionData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.ARequire;
import emanondev.deepquests.implementations.ARequireType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.MissionData;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Require;
import emanondev.deepquests.interfaces.User;
import emanondev.core.ItemBuilder;

public class CompletedMissionRequireType<T extends User<T>> extends ARequireType<T> {

	public CompletedMissionRequireType(QuestManager<T> manager) {
		super(ID, manager);
	}

	public static final String ID = "completed_mission";

	@Override
	public Material getGuiMaterial() {
		return Material.LIME_BANNER;
	}

	@Override
	public ItemStack getGuiItem() {
		return new ItemBuilder(getGuiMaterial()).addPattern(DyeColor.WHITE, PatternType.RHOMBUS_MIDDLE).setGuiProperty()
				.build();
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("&7Player require to complete mission n times");
	}

	@Override
	public Require<T> getInstance(int id, QuestManager<T> manager, YMLSection section) {
		return new CompletedMissionRequire(id, manager, section);
	}

	public class CompletedMissionRequire extends ARequire<T> {

		private TargetMissionData<T, CompletedMissionRequire> targetMission = null;
		private AmountData<T, CompletedMissionRequire> amountData = null;

		public CompletedMissionRequire(int id, QuestManager<T> manager, YMLSection section) {
			super(id, manager, CompletedMissionRequireType.this, section);
			targetMission = new TargetMissionData<T, CompletedMissionRequire>(this,
					getConfig().loadSection(Paths.REQUIRE_INFO_TARGETMISSION));
			amountData = new AmountData<T, CompletedMissionRequire>(this,
					getConfig().loadSection(Paths.REQUIRE_INFO_AMOUNT), 1, Integer.MAX_VALUE, 1);
		}

		@Override
		public boolean isAllowed(T user) {
			Mission<T> mission = targetMission.getMission();
			if (mission == null)
				return false;
			MissionData<T> missionData = user.getMissionData(mission);
			if (missionData.successfullyCompletedTimes() < amountData.getAmount())
				return false;
			return true;
		}

		public TargetMissionData<T, CompletedMissionRequire> getTargetMissionData() {
			return targetMission;
		}

		public AmountData<T, CompletedMissionRequire> getAmountData() {
			return amountData;
		}

		public List<String> getInfo() {
			List<String> info = super.getInfo();
			info.addAll(targetMission.getInfo());
			if (amountData.getAmount() != 1)
				info.add("&9Must be Completed: &e" + amountData.getAmount() + " &9times");
			return info;
		}

		@Override
		public Gui getEditorGui(Player target, Gui parent) {
			return new GuiEditor(target, parent);
		}

		private class GuiEditor extends ARequireGuiEditor {

			public GuiEditor(Player player, Gui previusHolder) {
				super(player, previusHolder);
				this.putButton(27, targetMission.getMissionSelectorButton(this));
				this.putButton(28,
						amountData.getAmountEditorButton("&9Completed Times Selector",
								Arrays.asList("&6Completed Times Selector", "&9Amount: &e%amount%"),
								new ItemBuilder(Material.REPEATER).setGuiProperty().build(), this));
			}
		}
	}
}
