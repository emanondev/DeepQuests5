package emanondev.deepquests.interfaces;

import emanondev.deepquests.utils.DisplayState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface DisplayInfo<T extends User<T>> extends Navigable {
    /**
     * forceShow = false
     *
     * @param state  status
     * @param user   target user
     * @param player who
     * @return display item for this or null if user/player should not see this
     */
    default ItemStack getGuiItem(@NotNull DisplayState state, T user, Player player) {
        return getGuiItem(state, user, player, false);
    }

    /**
     * @param state     status
     * @param user      target user
     * @param player    who
     * @param forceShow skips limit and always returns an item
     * @return display item for this
     */
    ItemStack getGuiItem(@NotNull DisplayState state, T user, Player player, boolean forceShow);

    /**
     * @param state
     * @return raw itemstack no description applied
     */
    ItemStack getRawItem(@NotNull DisplayState state);

    /**
     * @param state
     * @return raw description no holders applied
     */
    List<String> getRawDescription(@NotNull DisplayState state);

    /**
     * @param state
     * @return true if should be hidden
     */
    boolean isHidden(@NotNull DisplayState state);

    /**
     * sets raw item
     *
     * @param state
     * @param item
     */
    void setItem(@NotNull DisplayState state, ItemStack item);

    /**
     * sets if should be hidden when displaystate equals state
     *
     * @param state
     * @param value
     */
    void setHide(@NotNull DisplayState state, Boolean value);

    /**
     * sets raw description
     *
     * @param state
     * @param description
     */
    void setDescription(@NotNull DisplayState state, List<String> description);
}