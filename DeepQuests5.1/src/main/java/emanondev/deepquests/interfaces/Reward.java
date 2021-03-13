package emanondev.deepquests.interfaces;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import emanondev.deepquests.gui.button.GuiElementButton;
import emanondev.deepquests.gui.button.SortableButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.utils.Utils;

public interface Reward<T extends User<T>> extends QuestComponent<T> {
	/**
	 * 
	 * @return the Type
	 */
	public RewardType<T> getType();

	/**
	 * 
	 * @param user
	 */
	public default void apply(T user) {
		apply(user, 1);
	}

	/**
	 * 
	 * @param user
	 */
	public void apply(T user, int amount);

	@Override
	public default Material getGuiMaterial() {
		return Material.GOLD_INGOT;
	}

	@Override
	public default SortableButton getEditorButton(Gui parent) {
		return new GuiElementButton<Reward<T>>(parent, this);
	}

	public QuestManager<T> getManager();

	public default String getTypeName() {
		return getType().getKeyID();
	}

	public default void feedback(T user, int amount) {
		if (this.isHidden())
			return;
		for (Player p : user.getPlayers())
			p.sendMessage(Utils.fixString(getFeedback(user, amount), p, true));
	}

	public String getRawFeedback();

	public String getFeedback(T user, int amount);

	public void setFeedback(String feedback);
	
	public boolean isHidden();
	
	public void setHidden(Boolean value);

}
