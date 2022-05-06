package emanondev.deepquests.interfaces;

import emanondev.deepquests.gui.button.GuiElementButton;
import emanondev.deepquests.gui.button.SortableButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public interface Reward<T extends User<T>> extends QuestComponent<T> {
    /**
     * @return the Type
     */
    RewardType<T> getType();

    /**
     * @param user
     */
    default void apply(T user) {
        apply(user, 1);
    }

    /**
     * @param user
     */
    void apply(T user, int amount);

    @Override
    default Material getGuiMaterial() {
        return Material.GOLD_INGOT;
    }

    @Override
    default SortableButton getEditorButton(Gui parent) {
        return new GuiElementButton<>(parent, this);
    }

    QuestManager<T> getManager();

    default String getTypeName() {
        return getType().getKeyID();
    }

    default void feedback(T user, int amount) {
        if (this.isHidden())
            return;
        for (Player p : user.getPlayers())
            p.sendMessage(Utils.fixString(getFeedback(user, amount), p, true));
    }

    String getRawFeedback();

    String getFeedback(T user, int amount);

    void setFeedback(String feedback);

    boolean isHidden();

    void setHidden(Boolean value);

}