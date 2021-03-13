package emanondev.deepquests.generic.rewardtypes;

import java.util.*;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import emanondev.deepquests.Translations;
import emanondev.core.YMLSection;
import emanondev.deepquests.data.TargetMissionData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.AReward;
import emanondev.deepquests.implementations.ARewardType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.Mission;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Reward;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.utils.DataUtils;
import emanondev.core.ItemBuilder;

public class FailMissionRewardType<T extends User<T>> extends ARewardType<T> {
	public FailMissionRewardType(QuestManager<T> manager) {
		super(ID, manager);
	}

	@Override
	protected boolean getStandardHiddenValue() {
		return true;
	}

	private final static String ID = "fail_mission";

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
		return Arrays.asList("&7Fail selected mission for user");
	}

	@Override
	public FailMissionReward getInstance(int id, QuestManager<T> manager, YMLSection section) {
		return new FailMissionReward(id, manager, section);
	}

	public class FailMissionReward extends AReward<T> {
		private TargetMissionData<T, FailMissionReward> missionData;

		public FailMissionReward(int id, QuestManager<T> manager, YMLSection section) {
			super(id, manager, FailMissionRewardType.this, section);
			missionData = new TargetMissionData<T, FailMissionReward>(this,
					getConfig().loadSection(Paths.REWARD_INFO_TARGET_MISSION));
		}

		public List<String> getInfo() {
			List<String> info = super.getInfo();
			info.addAll(missionData.getInfo());
			return info;
		}

		@Override
		public Gui getEditorGui(Player target, Gui parent) {
			return new GuiEditor(target, parent);
		}

		private class GuiEditor extends ARewardGuiEditor {

			public GuiEditor(Player player, Gui previusHolder) {
				super(player, previusHolder);
				this.putButton(27, missionData.getMissionSelectorButton(this));
			}
		}

		public TargetMissionData<T, FailMissionReward> getTargetMissionData() {
			return missionData;
		}

		@Override
		public void apply(T qPlayer, int amount) {
			if (amount <= 0)
				return;
			try {
				Mission<T> mission = missionData.getMission();
				if (mission == null)
					new NullPointerException("Data missing or not setted still on reward " + this.getID())
							.printStackTrace();
				qPlayer.failMission(mission);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public String getDefaultFeedback(Reward<T> reward) {
		if (!(reward instanceof FailMissionRewardType.FailMissionReward))
			return null;
		FailMissionReward r = (FailMissionReward) reward;
		YMLSection config = getProvider().getTypeConfig(this);
		String txt = config.getString(Paths.REWARD_FEEDBACK, null);
		if (txt == null) {
			txt = "&a{action:failed} &e{target}";
			config.set(Paths.REWARD_FEEDBACK, txt);

		}
		return Translations.replaceAll(txt).replace("{target}", DataUtils.getTargetHolder(r.getTargetMissionData()));
	}

}