package emanondev.deepquests.interfaces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import emanondev.deepquests.Holders;
import emanondev.deepquests.gui.button.GuiElementButton;
import emanondev.deepquests.gui.button.SortableButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.utils.Utils;

public interface Mission<T extends User<T>> extends HasWorlds, HasDisplay<T>, HasCooldown<T>, QuestComponent<T> {

	/**
	 * Get mission task with key equals to task.getKey() if exist
	 * 
	 * @param id
	 *            id of the task
	 * @return task or null if can't find
	 */
	public Task<T> getTask(int id);

	/**
	 * Get all registered Task for this
	 * 
	 * @return not null immutable collection of tasks
	 */
	public Collection<Task<T>> getTasks();

	/**
	 * Register Task for this
	 * 
	 * @throws IllegalArgumentException
	 *             if task.getParent() != null
	 * @throws IllegalArgumentException
	 *             if getTask(task.getKey()) != null
	 * 
	 * @param task
	 *            the task to add
	 * @return true if sucessfully added
	 */
	public boolean addTask(Task<T> task);

	/**
	 * Unregister Task with key equals task.getKey() for this if exist
	 * 
	 * @param task
	 *            target to remove
	 * @return true if sucessfully removed
	 */
	public boolean removeTask(Task<T> task);

	/**
	 * Get the quest which contains this
	 * 
	 * @return parent quest or null
	 */
	public Quest<T> getQuest();

	/**
	 * Get all registered Require for this
	 * 
	 * @return not null immutable collection of requires
	 */
	public Collection<Require<T>> getRequires();

	/**
	 * Get mission require with key equals to require.getKey() if exist
	 * 
	 * @param id
	 *            id of the require
	 * @return require or null if can't find
	 */
	public Require<T> getRequire(int id);

	/**
	 * Register require for this
	 * 
	 * @throws IllegalArgumentException
	 *             if require.getParent() != null
	 * @throws IllegalArgumentException
	 *             if getRequire(require.getKey()) != null
	 * 
	 * @param require
	 *            the require to add
	 * @return true if sucessfully added
	 */
	public boolean addRequire(Require<T> require);

	/**
	 * Unregister require with key equals require.getKey() for this if exist
	 * 
	 * @param require
	 *            the require to remove
	 * @return true if sucessfully removed
	 */
	public boolean removeRequire(Require<T> require);

	/**
	 * Get all registered Start Rewards for this
	 * 
	 * @return not null immutable collection of rewards
	 */
	public Collection<Reward<T>> getStartRewards();

	/**
	 * Get start reward with key equals to reward.getKey() if exist
	 * 
	 * @param id
	 *            id of the reward
	 * @return reward or null if can't find
	 */
	public Reward<T> getStartReward(int id);

	/**
	 * Register a start reward for this
	 * 
	 * @throws IllegalArgumentException
	 *             if reward.getParent() != null
	 * @throws IllegalArgumentException
	 *             if getStartReward(require.getKey()) != null
	 * 
	 * @param reward
	 *            the require to add
	 * @return true if sucessfully added
	 */
	public boolean addStartReward(Reward<T> reward);

	/**
	 * Unregister getStartReward(key) if exist
	 * 
	 * @param reward
	 *            target to remove
	 * @return true if sucessfully removed
	 */
	public boolean removeStartReward(Reward<T> reward);

	/**
	 * Get all registered Complete Rewards for this
	 * 
	 * @return not null immutable collection of rewards
	 */
	public Collection<Reward<T>> getCompleteRewards();

	/**
	 * Get complete reward with key equals to reward.getKey() if exist
	 * 
	 * @param id
	 *            id of the reward
	 * @return reward or null if can't find
	 */
	public Reward<T> getCompleteReward(int id);

	/**
	 * Register a complete reward for this
	 * 
	 * @throws IllegalArgumentException
	 *             if reward.getParent() != null
	 * @throws IllegalArgumentException
	 *             if getCompleteReward(require.getKey()) != null
	 * 
	 * @param reward
	 *            the require to add
	 * @return true if sucessfully added
	 */
	public boolean addCompleteReward(Reward<T> reward);

	/**
	 * Unregister getCompleteReward(key) if exist
	 * 
	 * @param reward
	 *            target to remove
	 * @return true if sucessfully removed
	 */
	public boolean removeCompleteReward(Reward<T> reward);

	/**
	 * Get all registered Fail Rewards for this
	 * 
	 * @return not null immutable collection of rewards
	 */
	public Collection<Reward<T>> getFailRewards();

	/**
	 * Get fail reward with key equals to reward.getKey() if exist
	 * 
	 * @param key
	 *            id of the reward
	 * @return reward or null if can't find
	 */
	public Reward<T> getFailReward(int id);

	/**
	 * Register a fail reward for this
	 * 
	 * @throws IllegalArgumentException
	 *             if reward.getParent() != null
	 * @throws IllegalArgumentException
	 *             if getFailReward(require.getKey()) != null
	 * 
	 * @param reward
	 *            the require to add
	 * @return true if sucessfully added
	 */
	public boolean addFailReward(Reward<T> reward);

	/**
	 * Unregister getFailReward(key) if exist
	 * 
	 * @param reward
	 *            target to remove
	 * @return true if sucessfully removed
	 */
	public boolean removeFailReward(Reward<T> reward);

	public default String[] getHolders(T user) {
		String[] list = new String[4];
		list[0] = Holders.DISPLAY_NAME;
		list[1] = this.getDisplayName();
		list[2] = Holders.COOLDOWN_LEFT;
		list[3] = this.getStringCooldownLeft(user);
		return list;
	}

	// @SuppressWarnings("unchecked")
	public default List<String> getDisplayDescription(ArrayList<String> desc, T user, Player player) {
		if (desc != null)
			for (int i = 0; i < desc.size(); i++) {
				if (desc.get(i) != null) {
					if (desc.get(i).startsWith("{foreach-task}")) {
						String base = desc.remove(i).replace("{foreach-task}", "");
						ArrayList<Task<T>> taskList = new ArrayList<Task<T>>(this.getTasks());
						Collections.sort(taskList);
						ArrayList<String> list = new ArrayList<>();
						for (Task<T> t : taskList) {
							if (!t.isHidden())
								list.add(Utils.fixString(base, null, false, t.getHolders((T) user)));// "<task>",t.getKey()));
						}
						desc.addAll(i, list);
						i = i + list.size();
					} else if (desc.get(i).startsWith("{foreach-require}")) {
						String base = desc.remove(i).replace("{foreach-require}", "");
						ArrayList<Require<T>> requireList = new ArrayList<Require<T>>(this.getRequires());
						Collections.sort(requireList);
						ArrayList<String> list = new ArrayList<>();
						for (Require<T> r : requireList) {
							if (!r.isHidden())
								list.add(Utils.fixString(base, null, false, r.getHolders((T) user)));// "<require>",r.getKey()));
						}
						desc.addAll(i, list);
						i = i + list.size();

					} else if (desc.get(i).startsWith("{foreach-uncompleted-require}")) {
						String base = desc.remove(i).replace("{foreach-uncompleted-require}", "");
						ArrayList<Require<T>> requireList = new ArrayList<Require<T>>();
						for (Require<T> r : this.getRequires())
							if (r.isAllowed((T) user)) {
								if (!r.isHidden())
									requireList.add(r);
							}
						Collections.sort(requireList);
						ArrayList<String> list = new ArrayList<>();
						for (Require<T> r : requireList) {
							list.add(Utils.fixString(base, null, false, r.getHolders((T) user)));// "<require>",r.getKey()));
						}
						desc.addAll(i, list);
						i = i + list.size();

					}
					// TODO for each reward

				}
			}
		return Utils.fixList(desc, player, true, getHolders((T) user));
	}

	@Override
	public default Material getGuiMaterial() {
		return Material.BOOK;
	}

	@Override
	public default SortableButton getEditorButton(Gui parent) {
		return new GuiElementButton<Mission<T>>(parent, this);
	}

	public default List<String> getPhaseMessage(T user, Player p, PhaseChange phase) {
		return getDisplayDescription(getRawPhaseMessage(phase), user, p);
	}

	public void setPhaseMessage(PhaseChange phase, List<String> message);

	public boolean showPhaseMessage(PhaseChange phase);

	public void toggleShowPhaseMessage(PhaseChange phase);

	public ArrayList<String> getRawPhaseMessage(PhaseChange phase);

	public enum PhaseChange {
		START(Arrays.asList("&aMission &l{name} &a➤ Started!")), PAUSE(
				Arrays.asList("&9Mission &l{name} &9➤ Paused!")), UNPAUSE(
						Arrays.asList("&9Mission &l{name} &9➤ Unpaused!")), FAIL(
								Arrays.asList("&cMission &l{name} &c➤ Failed!")), COMPLETE(
										Arrays.asList("&aMission &l{name} &a➤ Completed!"));
		public List<String> def;

		PhaseChange(List<String> def) {
			this.def = def;
		}

		public String toString() {
			return this.name().toLowerCase();
		}
	}
}
