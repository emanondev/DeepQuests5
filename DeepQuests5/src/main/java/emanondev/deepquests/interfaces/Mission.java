package emanondev.deepquests.interfaces;

import emanondev.deepquests.Holders;
import emanondev.deepquests.gui.button.GuiElementButton;
import emanondev.deepquests.gui.button.SortableButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface Mission<T extends User<T>> extends HasWorlds, HasDisplay<T>, HasCooldown<T>, QuestComponent<T> {

    /**
     * Get mission task with key equals to task.getKey() if exist
     *
     * @param id id of the task
     * @return task or null if can't find
     */
    Task<T> getTask(int id);

    /**
     * Get all registered Task for this
     *
     * @return not null immutable collection of tasks
     */
    @NotNull Collection<Task<T>> getTasks();

    /**
     * Register Task for this
     *
     * @param task the task to add
     * @return true if successfully added
     * @throws IllegalArgumentException if task.getParent() != null
     * @throws IllegalArgumentException if getTask(task.getKey()) != null
     */
    boolean addTask(@NotNull Task<T> task);

    /**
     * Unregister Task with key equals task.getKey() for this if exist
     *
     * @param task target to remove
     * @return true if successfully removed
     */
    boolean removeTask(@NotNull Task<T> task);

    /**
     * Get the quest which contains this
     *
     * @return parent quest or null
     */
    @NotNull Quest<T> getQuest();

    /**
     * Get all registered Require for this
     *
     * @return not null immutable collection of requires
     */
    @NotNull Collection<Require<T>> getRequires();

    /**
     * Get mission require with key equals to require.getKey() if exist
     *
     * @param id id of the require
     * @return require or null if can't find
     */
    Require<T> getRequire(int id);

    /**
     * Register require for this
     *
     * @param require the require to add
     * @return true if successfully added
     * @throws IllegalArgumentException if require.getParent() != null
     * @throws IllegalArgumentException if getRequire(require.getKey()) != null
     */
    boolean addRequire(@NotNull Require<T> require);

    /**
     * Unregister require with key equals require.getKey() for this if exist
     *
     * @param require the require to remove
     * @return true if successfully removed
     */
    boolean removeRequire(@NotNull Require<T> require);

    /**
     * Get all registered Start Rewards for this
     *
     * @return not null immutable collection of rewards
     */
    @NotNull Collection<Reward<T>> getStartRewards();

    /**
     * Get start reward with key equals to reward.getKey() if exist
     *
     * @param id id of the reward
     * @return reward or null if can't find
     */
    Reward<T> getStartReward(int id);

    /**
     * Register a start reward for this
     *
     * @param reward the require to add
     * @return true if successfully added
     * @throws IllegalArgumentException if reward.getParent() != null
     * @throws IllegalArgumentException if getStartReward(require.getKey()) != null
     */
    boolean addStartReward(@NotNull Reward<T> reward);

    /**
     * Unregister getStartReward(key) if exist
     *
     * @param reward target to remove
     * @return true if successfully removed
     */
    boolean removeStartReward(@NotNull Reward<T> reward);

    /**
     * Get all registered Complete Rewards for this
     *
     * @return not null immutable collection of rewards
     */
    @NotNull Collection<Reward<T>> getCompleteRewards();

    /**
     * Get complete reward with key equals to reward.getKey() if exist
     *
     * @param id id of the reward
     * @return reward or null if can't find
     */
    Reward<T> getCompleteReward(int id);

    /**
     * Register a complete reward for this
     *
     * @param reward the require to add
     * @return true if successfully added
     * @throws IllegalArgumentException if reward.getParent() != null
     * @throws IllegalArgumentException if getCompleteReward(require.getKey()) != null
     */
    boolean addCompleteReward(@NotNull Reward<T> reward);

    /**
     * Unregister getCompleteReward(key) if exist
     *
     * @param reward target to remove
     * @return true if successfully removed
     */
    boolean removeCompleteReward(@NotNull Reward<T> reward);

    /**
     * Get all registered Fail Rewards for this
     *
     * @return not null immutable collection of rewards
     */
    @NotNull Collection<Reward<T>> getFailRewards();

    /**
     * Get fail reward with key equals to reward.getKey() if exist
     *
     * @param id id of the reward
     * @return reward or null if can't find
     */
    Reward<T> getFailReward(int id);

    /**
     * Register a fail reward for this
     *
     * @param reward the require to add
     * @return true if successfully added
     * @throws IllegalArgumentException if reward.getParent() != null
     * @throws IllegalArgumentException if getFailReward(require.getKey()) != null
     */
    boolean addFailReward(@NotNull Reward<T> reward);

    /**
     * Unregister getFailReward(key) if exist
     *
     * @param reward target to remove
     * @return true if successfully removed
     */
    boolean removeFailReward(@NotNull Reward<T> reward);

    default @NotNull String[] getHolders(T user) {
        String[] list = new String[4];
        list[0] = Holders.DISPLAY_NAME;
        list[1] = this.getDisplayName();
        list[2] = Holders.COOLDOWN_LEFT;
        list[3] = this.getStringCooldownLeft(user);
        return list;
    }

    default @NotNull List<String> getDisplayDescription(ArrayList<String> desc, T user, Player player) {
        if (desc != null)
            for (int i = 0; i < desc.size(); i++) {
                if (desc.get(i) != null) {
                    if (desc.get(i).startsWith("{foreach-task}")) {
                        String base = desc.remove(i).replace("{foreach-task}", "");
                        ArrayList<Task<T>> taskList = new ArrayList<>(this.getTasks());
                        Collections.sort(taskList);
                        ArrayList<String> list = new ArrayList<>();
                        for (Task<T> t : taskList) {
                            if (!t.isHidden())
                                list.add(Utils.fixString(base, null, false, t.getHolders(user)));// "<task>",t.getKey()));
                        }
                        desc.addAll(i, list);
                        i = i + list.size();
                    } else if (desc.get(i).startsWith("{foreach-require}")) {
                        String base = desc.remove(i).replace("{foreach-require}", "");
                        ArrayList<Require<T>> requireList = new ArrayList<>(this.getRequires());
                        Collections.sort(requireList);
                        ArrayList<String> list = new ArrayList<>();
                        for (Require<T> r : requireList) {
                            if (!r.isHidden())
                                list.add(Utils.fixString(base, null, false, r.getHolders(user)));// "<require>",r.getKey()));
                        }
                        desc.addAll(i, list);
                        i = i + list.size();

                    } else if (desc.get(i).startsWith("{foreach-uncompleted-require}")) {
                        String base = desc.remove(i).replace("{foreach-uncompleted-require}", "");
                        ArrayList<Require<T>> requireList = new ArrayList<>();
                        for (Require<T> r : this.getRequires())
                            if (r.isAllowed(user)) {
                                if (!r.isHidden())
                                    requireList.add(r);
                            }
                        Collections.sort(requireList);
                        ArrayList<String> list = new ArrayList<>();
                        for (Require<T> r : requireList) {
                            list.add(Utils.fixString(base, null, false, r.getHolders(user)));// "<require>",r.getKey()));
                        }
                        desc.addAll(i, list);
                        i = i + list.size();

                    }
                    // TODO for each reward

                }
            }
        return Utils.fixList(desc, player, true, getHolders(user));
    }

    @Override
    default @NotNull Material getGuiMaterial() {
        return Material.BOOK;
    }

    @Override
    default @NotNull SortableButton getEditorButton(@NotNull Gui parent) {
        return new GuiElementButton<>(parent, this);
    }

    default List<String> getPhaseMessage(T user, Player p, PhaseChange phase) {
        return getDisplayDescription(getRawPhaseMessage(phase), user, p);
    }

    void setPhaseMessage(@NotNull PhaseChange phase, List<String> message);

    boolean showPhaseMessage(@NotNull PhaseChange phase);

    void toggleShowPhaseMessage(@NotNull PhaseChange phase);

    ArrayList<String> getRawPhaseMessage(@NotNull PhaseChange phase);

    enum PhaseChange {
        START(List.of("&aMission &l{name} &a➤ Started!")), PAUSE(
                List.of("&9Mission &l{name} &9➤ Paused!")), UNPAUSE(
                List.of("&9Mission &l{name} &9➤ Unpaused!")), FAIL(
                List.of("&cMission &l{name} &c➤ Failed!")), COMPLETE(
                List.of("&aMission &l{name} &a➤ Completed!"));
        public final List<String> def;

        PhaseChange(List<String> def) {
            this.def = def;
        }

        public String toString() {
            return this.name().toLowerCase();
        }
    }
}
