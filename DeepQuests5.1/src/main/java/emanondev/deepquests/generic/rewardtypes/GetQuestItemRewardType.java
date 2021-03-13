package emanondev.deepquests.generic.rewardtypes;

import java.util.*;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import emanondev.core.ItemBuilder;
import emanondev.deepquests.Translations;
import emanondev.core.YMLSection;
import emanondev.deepquests.data.*;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.*;
import emanondev.deepquests.interfaces.*;
import emanondev.deepquests.utils.DataUtils;

public class GetQuestItemRewardType<T extends User<T>> extends ARewardType<T> {
	public GetQuestItemRewardType(QuestManager<T> manager) {
		super(ID, manager);
	}

	@Override
	protected boolean getStandardHiddenValue() {
		return false;
	}

	private final static String ID = "questitem";

	@Override
	public Material getGuiMaterial() {
		return Material.DIAMOND;
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("&7Reward the player with an Item");
	}

	@Override
	public GetQuestItemReward getInstance(int id, QuestManager<T> manager, YMLSection section) {
		return new GetQuestItemReward(id, manager, section);
	}

	public class GetQuestItemReward extends AReward<T> {
		private QuestItemData<T, GetQuestItemReward> itemData;
		private AmountData<T, GetQuestItemReward> amountData;

		public GetQuestItemReward(int id, QuestManager<T> manager, YMLSection section) {
			super(id, manager, GetQuestItemRewardType.this, section);
			itemData = new QuestItemData<T, GetQuestItemReward>(this,
					getConfig().loadSection(Paths.REWARD_INFO_QUESTITEM));
			amountData = new AmountData<T, GetQuestItemReward>(this,
					getConfig().loadSection(Paths.REWARD_INFO_AMOUNT), Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
		}

		public List<String> getInfo() {
			List<String> info = super.getInfo();
			info.add("  &9Amount: &e" + amountData.getAmount());
			info.addAll(itemData.getInfo());
			return info;
		}

		@Override
		public void apply(T user, int amount) {
			if (amount <= 0 || itemData.getQuestItemID() == null)
				return;
			if (amountData.getAmount() > 0)
				user.getQuestBag().addQuestItem(itemData.getQuestItemID(), amount * amountData.getAmount());
			else if (amountData.getAmount() < 0)
				user.getQuestBag().removeQuestItem(itemData.getQuestItemID(), -amount * amountData.getAmount());
		}

		public AmountData<T, GetQuestItemReward> getAmountData() {
			return amountData;
		}

		public QuestItemData<T, GetQuestItemReward> getQuestItemData() {
			return itemData;
		}

		@Override
		public Gui getEditorGui(Player target, Gui parent) {
			return new GuiEditor(target, parent);
		}

		private class GuiEditor extends ARewardGuiEditor {

			public GuiEditor(Player player, Gui previusHolder) {
				super(player, previusHolder);
				itemData.setupButtons(this, 27);
				this.putButton(28,
						amountData.getAmountEditorButton("&9Amount Selector",
								Arrays.asList("&6Amount Selector", "&9Amount: &e%amount%"),
								new ItemBuilder(Material.REPEATER).setGuiProperty().build(), this));
			}
		}
	}

	@Override
	public String getDefaultFeedback(Reward<T> reward) {
		if (!(reward instanceof GetQuestItemRewardType.GetQuestItemReward))
			return null;
		GetQuestItemReward r = (GetQuestItemReward) reward;
		YMLSection config = getProvider().getTypeConfig(this);
		String txt = config.getString(Paths.REWARD_FEEDBACK, null);
		if (txt == null) {
			txt = "&a{action:obtained} &e{item} &ax &e{amount}";
			config.set(Paths.REWARD_FEEDBACK, txt);
		}
		return Translations.replaceAll(txt).replace("{item}", r.getQuestItemData().getQuestItemNick())
				.replace("{amount}", DataUtils.getAmountHolder(r.getAmountData()));
	}
}