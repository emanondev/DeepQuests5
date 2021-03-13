package emanondev.deepquests.generic.requiretypes;

import java.util.*;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import emanondev.core.YMLSection;
import emanondev.deepquests.data.DisplayStateData;
import emanondev.deepquests.data.TargetMissionData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.ARequire;
import emanondev.deepquests.implementations.ARequireType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Require;
import emanondev.deepquests.interfaces.User;
import emanondev.core.ItemBuilder;

public class CurrentMissionStateRequireType<T extends User<T>> extends ARequireType<T> {
	public static final String ID = "current_mission_state";

	public CurrentMissionStateRequireType(QuestManager<T> manager) {
		super(ID, manager);
	}

	@Override
	public Require<T> getInstance(int id, QuestManager<T> manager, YMLSection section) {
		return new CurrentMissionStateRequire(id, manager, section);
	}

	@Override
	public Material getGuiMaterial() {
		return Material.BLUE_BANNER;
	}

	@Override
	public ItemStack getGuiItem() {
		return new ItemBuilder(getGuiMaterial()).addPattern(DyeColor.WHITE, PatternType.RHOMBUS_MIDDLE).setGuiProperty()
				.build();
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("Require selected quest to have one of selected status");
	}

	public class CurrentMissionStateRequire extends ARequire<T> {
		private TargetMissionData<T, CurrentMissionStateRequire> targetMission = null;
		private DisplayStateData<T, CurrentMissionStateRequire> stateData = null;

		public CurrentMissionStateRequire(int id, QuestManager<T> manager, YMLSection section) {
			super(id, manager, CurrentMissionStateRequireType.this, section);
			targetMission = new TargetMissionData<T, CurrentMissionStateRequire>(this,
					getConfig().loadSection(Paths.REQUIRE_INFO_TARGETMISSION));
			stateData = new DisplayStateData<T, CurrentMissionStateRequire>(this,
					getConfig().loadSection(Paths.REQUIRE_INFO_DISPLAYSTATE));
		}

		@Override
		public boolean isAllowed(T user) {
			Mission<T> mission = targetMission.getMission();
			if (mission == null)
				return false;
			return stateData.isValidState(user.getDisplayState(mission));
		}

		public TargetMissionData<T, CurrentMissionStateRequire> getTargetMissionData() {
			return targetMission;
		}

		public DisplayStateData<T, CurrentMissionStateRequire> getDisplayStateData() {
			return stateData;
		}

		public List<String> getInfo() {
			List<String> info = super.getInfo();
			info.addAll(targetMission.getInfo());
			info.addAll(stateData.getInfo());
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
				this.putButton(28, stateData.getDisplaySelectorButton(this));
			}
		}

	}

}
