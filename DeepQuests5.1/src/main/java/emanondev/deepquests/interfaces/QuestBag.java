package emanondev.deepquests.interfaces;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

public interface QuestBag<T extends User<T>> {
	
	/**
	 * Adds selected amount of item to this.
	 * @param id item identifier
	 * @param amount amount to be given
	 * @throws NullPointerException if id is null
	 * @throws IllegalArgumentException if id is invalid
	 * @throws IllegalArgumentException if amount is less than 0
	 */
	public void addQuestItem(@NotNull String id, int amount);

	/**
	 * Returns true if this has at least 1 of selected item.
	 * @param id item identifier
	 * @return true if this has at least 1 of selected item
	 * @throws NullPointerException if id is null
	 * @throws IllegalArgumentException if id is invalid
	 */
	public default boolean hasQuestItem(@NotNull String id) {
		return hasQuestItem(id,1);
	}
	/**
	 * Returns true if this has at least amount of selected item.
	 * @param id item identifier
	 * @param amount minimal amount required
	 * @return true if this has at least amount of selected item
	 * @throws NullPointerException if id is null
	 * @throws IllegalArgumentException if id is invalid
	 * @throws IllegalArgumentException if amount is less than 0
	 */
	public boolean hasQuestItem(@NotNull String id, int amount);
	/**
	 * Removes up to selected amount of item from this, and return removed amount.
	 * @param id item identifier
	 * @param amount amount to be given
	 * @return removed amount
	 * @throws NullPointerException if id is null
	 * @throws IllegalArgumentException if id is invalid
	 * @throws IllegalArgumentException if amount is less than 0
	 */
	public int removeQuestItem(@NotNull String id,int amount);
	/**
	 * Returns a map containing id of items and their amount on this.
	 * @return a map containing id of items and their amount on this.
	 */
	public @NotNull Map<String,Integer> getQuestItems();

	/**
	 * Remove any item stored on this.
	 */
	public void reset();

	int getQuestItemAmount(String id);
	
	public T getUser();

}
