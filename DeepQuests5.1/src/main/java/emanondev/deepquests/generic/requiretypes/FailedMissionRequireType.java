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

public class FailedMissionRequireType<T extends User<T>> extends ARequireType<T> {

	public FailedMissionRequireType(QuestManager<T> manager) {
		super(ID, manager);
	}

	public static final String ID = "failed_mission";

	@Override
	public Material getGuiMaterial() {
		return Material.RED_BANNER;
	}

	@Override
	public ItemStack getGuiItem() {
		return new ItemBuilder(getGuiMaterial()).addPattern(DyeColor.WHITE, PatternType.RHOMBUS_MIDDLE).setGuiProperty()
				.build();
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("&7Player require to fail mission n times");
	}

	@Override
	public Require<T> getInstance(int id, QuestManager<T> manager, YMLSection section) {
		return new FailedMissionRequire(id, manager, section);
	}

	public class FailedMissionRequire extends ARequire<T> {

		private TargetMissionData<T, FailedMissionRequire> targetMission = null;
		private AmountData<T, FailedMissionRequire> amountData = null;

		public FailedMissionRequire(int id, QuestManager<T> manager, YMLSection section) {
			super(id, manager, FailedMissionRequireType.this, section);
			targetMission = new TargetMissionData<T, FailedMissionRequire>(this,
					getConfig().loadSection(Paths.REQUIRE_INFO_TARGETMISSION));
			amountData = new AmountData<T, FailedMissionRequire>(this,
					getConfig().loadSection(Paths.REQUIRE_INFO_AMOUNT), 1, Integer.MAX_VALUE, 1);
		}

		@Override
		public boolean isAllowed(T user) {
			Mission<T> mission = targetMission.getMission();
			if (mission == null)
				return false;
			MissionData<T> missionData = user.getMissionData(mission);
			if (missionData.failedTimes() < amountData.getAmount())
				return false;
			return true;
		}

		public TargetMissionData<T, FailedMissionRequire> getTargetMissionData() {
			return targetMission;
		}

		public AmountData<T, FailedMissionRequire> getAmountData() {
			return amountData;
		}

		public List<String> getInfo() {
			List<String> info = super.getInfo();
			info.addAll(targetMission.getInfo());
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
				this.putButton(27, targetMission.getMissionSelectorButton(this));
				this.putButton(28,
						amountData.getAmountEditorButton("&9Failed Times Selector",
								Arrays.asList("&6Failed Times Selector", "&9Amount: &e%amount%"),
								new ItemBuilder(Material.REPEATER).setGuiProperty().build(), this));
			}
		}
	}

}