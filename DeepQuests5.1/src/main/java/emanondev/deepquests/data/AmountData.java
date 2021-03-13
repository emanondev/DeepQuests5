package emanondev.deepquests.data;

import java.util.*;

import org.bukkit.inventory.ItemStack;

import emanondev.core.YMLSection;
import emanondev.deepquests.gui.button.AmountSelectorButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestComponent;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.utils.Utils;

public class AmountData<T extends User<T>, E extends QuestComponent<T>> extends QuestComponentData<T, E> {

	private int amount = 1;
	private int minAmount = Integer.MIN_VALUE;
	private int maxAmount = Integer.MAX_VALUE;
	private int defaultAmount = 1;

	public AmountData(E parent, YMLSection section) {
		this(parent, section, Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
	}

	public AmountData(E parent, YMLSection section, int min, int max, int defaultAmount) {
		super(parent, section);
		this.defaultAmount = defaultAmount;
		this.minAmount = min;
		this.maxAmount = max;

		if (this.minAmount > this.defaultAmount || this.defaultAmount > this.maxAmount) {
			new IllegalArgumentException().printStackTrace();
			this.minAmount = Math.min(minAmount, Math.min(this.defaultAmount, maxAmount));
			this.maxAmount = Math.max(minAmount, Math.max(this.defaultAmount, maxAmount));
		}
		this.amount = section.getInteger(Paths.DATA_AMOUNT, this.defaultAmount);
	}

	public int getAmount() {
		return amount;
	}

	public int getDefaultAmount() {
		return defaultAmount;
	}

	public int getMinAmount() {
		return minAmount;
	}

	public int getMaxAmount() {
		return maxAmount;
	}

	public void setAmount(int amount) {
		amount = Math.min(Math.max(minAmount, amount), maxAmount);
		if (amount == this.amount)
			return;
		this.amount = amount;
		getConfig().set(Paths.DATA_AMOUNT,this.amount);
	}

	public boolean resetDefault() {
		if (amount == defaultAmount)
			return false;
		this.amount = this.defaultAmount;
		return true;
	}

	public AmountEditor getAmountEditorButton(String subGuiTitle, List<String> desc, ItemStack item, Gui parent) {
		return new AmountEditor(subGuiTitle, desc, item, parent);
	}

	private class AmountEditor extends AmountSelectorButton {
		private final List<String> desc;

		public AmountEditor(String subGuiTitle, List<String> desc, ItemStack item, Gui parent) {
			super(subGuiTitle, item, parent, 1, 10, 100, 1000, 10000, 100000, 1000000);
			this.desc = new ArrayList<>(desc);
		}

		@Override
		public List<String> getButtonDescription() {
			return Utils.fixList(desc, null, true, "%amount%", "" + getCurrentAmount());
		}

		@Override
		public long getCurrentAmount() {
			return getAmount();
		}

		@Override
		public boolean onAmountChangeRequest(long value) {
			setAmount((int) value);
			return true;
		}

	}
}
