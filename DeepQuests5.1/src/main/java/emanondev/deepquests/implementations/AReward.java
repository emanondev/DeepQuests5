package emanondev.deepquests.implementations;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.deepquests.Holders;
import emanondev.deepquests.Quests;
import emanondev.deepquests.gui.button.StaticFlagButton;
import emanondev.deepquests.gui.button.TextEditorButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.interfaces.QuestManager;
import emanondev.deepquests.interfaces.Reward;
import emanondev.deepquests.interfaces.RewardType;
import emanondev.deepquests.interfaces.User;
import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public abstract class AReward<T extends User<T>> extends AQuestComponent<T> implements Reward<T> {

	private boolean isHidden = false;

	public boolean isHidden() {
		return isHidden;
	}

	public void setHidden(Boolean value) {
		if (value!=null && isHidden==value)
			return;
		if (value == null)
			isHidden = getType().getDefaultIsHidden();
		else
			isHidden = value;
		getConfig().set(Paths.IS_HIDDEN, value==null?null:isHidden);
	}

	private RewardType<T> type;

	public AReward(int id, QuestManager<T> manager, RewardType<T> type, YMLSection section) {
		super(id, section,manager);
		this.type = type;
		isHidden = getConfig().getBoolean(Paths.IS_HIDDEN, getType().getDefaultIsHidden());
		getConfig().set(Paths.TYPE_NAME, type.getKeyID());
		Bukkit.getScheduler().runTaskLater(Quests.get(), new Runnable() {
			public void run() {
				isFeedbackDefault = getConfig().getBoolean(Paths.REWARD_IS_FEEDBACK_DEFAULT, true);
				if (isFeedbackDefault)
					feedback = getDefaultFeedback();
				else
					feedback = getConfig().loadMessage(Paths.REWARD_FEEDBACK, getDefaultFeedback(), true);
			}
		}, 1L);

	}

	@Override
	public List<String> getInfo() {
		List<String> info = new ArrayList<String>();
		info.add("&9&lReward: &6" + this.getDisplayName());
		info.add("&8Type: &7" + getType() != null ? getType().getKeyID() : "&cError");

		info.add("&8KEY: " + this.getID());
		info.add("");
		info.add("&9Priority: &e" + getPriority());
		return info;
	}

	@Override
	public final RewardType<T> getType() {
		return type;
	}


	protected class ARewardGuiEditor extends AGuiEditor {

		public ARewardGuiEditor(Player player, Gui previusHolder) {
			super("&9Reward: &r" + getDisplayName() + " &9ID: &e" + getID() + " &9Type: &e" + getType().getKeyID(),
					player, previusHolder);
			this.putButton(7, new HiddenButton());
			this.putButton(8, new FeedbackButton());
		}

		private class FeedbackButton extends TextEditorButton {

			public FeedbackButton() {
				super(new ItemBuilder(Material.BLUE_TERRACOTTA).setGuiProperty().build(), ARewardGuiEditor.this);
			}

			public ItemStack getItem() {
				ItemStack item = super.getItem();
				if (isFeedbackDefault)
					item.setType(Material.BLUE_STAINED_GLASS);
				else
					item.setType(Material.BLUE_TERRACOTTA);
				return item;
			}

			@Override
			public void onClick(Player clicker, ClickType click) {
				switch (click) {
				case RIGHT:
				case SHIFT_RIGHT:
					setFeedback(null);
					getGui().updateInventory();
					return;
				default:
					break;
				}
				this.requestText(clicker, getRawFeedback(), "&6Set the Feedback");
			}

			@Override
			public List<String> getButtonDescription() {
				List<String> desc = new ArrayList<String>();
				desc.add("&6Feedback Button");
				desc.add("&9Current value: '&r" + getRawFeedback() + "&9'");
				if (isFeedbackDefault)
					desc.add("&9Inherit from RewardType");
				desc.add("");
				desc.add("&7Right Click to reset to default/update");
				desc.add("&7Left Click to edit");
				return desc;
			}

			@Override
			public void onReicevedText(String text) {
				setFeedback(text);
				getGui().updateInventory();
			}
		}

		private class HiddenButton extends StaticFlagButton {

			public HiddenButton() {
				super(Utils.setDescription(
						new ItemBuilder(Material.HEAVY_WEIGHTED_PRESSURE_PLATE).setGuiProperty().build(),
						Arrays.asList("&6Hidden Button", "&9Current value: '&r" + isHidden() + "&9'", "",
								"&7An hidden reward do not send the feedback", "&7message when a player gains it",
								"&7Right Click to reset to default"),
						null, true),
						Utils.setDescription(
								new ItemBuilder(Material.LIGHT_WEIGHTED_PRESSURE_PLATE).setGuiProperty().build(),
								Arrays.asList("&6Hidden Button", "&9Current value: '&r" + isHidden() + "&9'", "",
										"&7An hidden reward do not send the feedback",
										"&7message when a player gains it", "&7Right Click to reset to default"),
								null, true),
						ARewardGuiEditor.this);

			}

			@Override
			public boolean getCurrentValue() {
				return isHidden;
			}

			@Override
			public boolean onValueChangeRequest(boolean value) {
				setHidden(value);
				return true;
			}
			
			@Override
			public void onClick(Player clicker,ClickType click) {
				if (click == ClickType.RIGHT) {
					setHidden(null);
				}
				else
					super.onClick(clicker,click);
			}

		}
	}

	private String feedback = null;
	private boolean isFeedbackDefault = true;

	public String getRawFeedback() {
		if (feedback == null)
			this.feedback = getDefaultFeedback();
		return feedback;
	}

	public String getFeedback(T user, int amount) {
		return getRawFeedback();
	}

	public void setFeedback(String feedback) {
		if (feedback == null) {
			isFeedbackDefault = true;
			this.feedback = getDefaultFeedback();
		} else {
			isFeedbackDefault = false;
			this.feedback = feedback;
		}
		getConfig().set(Paths.REWARD_IS_FEEDBACK_DEFAULT, isFeedbackDefault);
		if (isFeedbackDefault)
			getConfig().set(Paths.REWARD_FEEDBACK, null);
		else
			getConfig().set(Paths.REWARD_FEEDBACK, feedback);
	}

	private String getDefaultFeedback() {
		String desc = getType().getDefaultFeedback(this);
		return desc != null ? desc : ChatColor.GREEN + "Reiceved reward " + Holders.DISPLAY_NAME;
	}
}
