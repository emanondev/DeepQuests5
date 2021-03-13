package emanondev.deepquests.generic.rewardtypes;

import java.util.*;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import emanondev.core.ItemBuilder;
import emanondev.deepquests.Translations;
import emanondev.core.YMLSection;
import emanondev.deepquests.data.AmountData;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.AReward;
import emanondev.deepquests.implementations.ARewardType;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Reward;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.utils.DataUtils;

public class QuestsPointsRewardType<T extends User<T>> extends ARewardType<T> {
	public QuestsPointsRewardType(QuestManager<T> manager) {
		super(ID, manager);
	}

	@Override
	protected boolean getStandardHiddenValue() {
		return false;
	}

	private final static String ID = "quests_points";

	@Override
	public Material getGuiMaterial() {
		return Material.GOLD_NUGGET;
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("&7give missions points");
	}

	@Override
	public QuestsPointsReward getInstance(int id, QuestManager<T> manager, YMLSection section) {
		return new QuestsPointsReward(id, manager, section);
	}

	public class QuestsPointsReward extends AReward<T> {
		private AmountData<T, QuestsPointsReward> amountData;

		public QuestsPointsReward(int id, QuestManager<T> manager, YMLSection section) {
			super(id, manager, QuestsPointsRewardType.this, section);
			amountData = new AmountData<T, QuestsPointsReward>(this,
					getConfig().loadSection(Paths.REWARD_INFO_AMOUNT));
		}

		public List<String> getInfo() {
			List<String> info = super.getInfo();
			info.add("&9Points: &e%amount%");
			return info;
		}

		public AmountData<T, QuestsPointsReward> getAmountData() {
			return amountData;
		}

		@Override
		public void apply(T qPlayer, int amount) {
			if (amount <= 0)
				return;
			try {
				qPlayer.setPoints(qPlayer.getPoints() + amount * amountData.getAmount());

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public Gui getEditorGui(Player target, Gui parent) {
			return new GuiEditor(target, parent);
		}

		private class GuiEditor extends ARewardGuiEditor {

			public GuiEditor(Player player, Gui previusHolder) {
				super(player, previusHolder);
				this.putButton(27,
						amountData.getAmountEditorButton("&9Points Selector",
								Arrays.asList("&6Points Selector", "&9Points: &e%amount%"),
								new ItemBuilder(Material.REPEATER).setGuiProperty().build(), this));
			}
		}
	}

	@Override
	public String getDefaultFeedback(Reward<T> reward) {
		if (!(reward instanceof QuestsPointsRewardType.QuestsPointsReward))
			return null;
		QuestsPointsReward r = (QuestsPointsReward) reward;
		YMLSection config = getProvider().getTypeConfig(this);
		String txt = config.getString(Paths.REWARD_FEEDBACK, null);
		if (txt == null) {
			txt = "&a{action:obtained} &e{amount}";
			config.set(Paths.REWARD_FEEDBACK, txt);

		}
		return Translations.replaceAll(txt).replace("{amount}", DataUtils.getAmountHolder(r.getAmountData()));
	}
}