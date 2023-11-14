package emanondev.deepquests.interfaces;

import emanondev.deepquests.Holders;
import emanondev.deepquests.gui.button.GuiElementButton;
import emanondev.deepquests.gui.button.SortableButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.utils.DisplayState;
import emanondev.deepquests.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface Quest<T extends User<T>> extends HasWorlds, HasDisplay<T>, HasCooldown<T>, QuestComponent<T> {

    /**
     * @param id id of the mission
     * @return selected mission or null
     */
    @Nullable Mission<T> getMission(int id);

    /**
     * @return immutable collection of missions of this
     */
    @NotNull Collection<Mission<T>> getMissions();


    /**
     * @param mission the mission to add
     * @return true if successfully added
     * @throws IllegalArgumentException if mission.getParent() != null
     * @throws IllegalArgumentException if getMission(mission.getKey()) != null
     */
    boolean addMission(@NotNull Mission<T> mission);

    /**
     * @param mission target to remove
     * @return true if successfully removed
     */
    boolean removeMission(@NotNull Mission<T> mission);

    /**
     * @return get requires of this
     */
    @NotNull Collection<Require<T>> getRequires();

    /**
     * @param id key of require
     * @return get require with key or null
     */
    @Nullable Require<T> getRequire(int id);

    /**
     * @param require the requirement to add
     * @return true if successfully added
     * @throws IllegalArgumentException if require.getParent() != null
     * @throws IllegalArgumentException if getRequire(require.getKey()) != null
     */
    boolean addRequire(@NotNull Require<T> require);

    /**
     * @param require target to remove
     * @return true if successfully removed
     */
    boolean removeRequire(@NotNull Require<T> require);

    /**
     * @return true if Quest is on developing stage (aka hidden)
     */
    boolean isDeveloped();

    @SuppressWarnings({"unchecked", "rawtypes"})
    default @NotNull List<String> getDisplayDescription(ArrayList<String> desc, T user, Player player) {

        if (desc != null)
            for (int i = 0; i < desc.size(); i++) {
                if (desc.get(i) != null) {
                    if (desc.get(i).startsWith("{foreach-mission}")) {

                        String base = desc.remove(i).replace("{foreach-mission}", "");
                        ArrayList<Mission<T>> missionList = new ArrayList<>(this.getMissions());
                        Collections.sort(missionList);
                        ArrayList<String> list = new ArrayList<>();
                        for (Mission m : missionList) {
                            if (!m.getDisplayInfo().isHidden(user.getDisplayState(m)))
                                list.add(Utils.fixString(base, null, false, m.getHolders(user)));//"<task>",t.getKey()));
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
                                list.add(Utils.fixString(base, null, false, r.getHolders(user)));//"<require>",r.getKey()));
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
                            if (!r.isHidden())
                                list.add(Utils.fixString(base, null, false, r.getHolders(user)));//"<require>",r.getKey()));
                        }
                        desc.addAll(i, list);
                        i = i + list.size();

                    }
                }
            }
        return Utils.fixList(desc, player, true, getHolders(user));
    }


    default @NotNull String[] getHolders(T user) {
        String[] list = new String[18];
        list[0] = Holders.DISPLAY_NAME;
        list[1] = getDisplayName();
        list[2] = Holders.COOLDOWN_LEFT;
        list[3] = getStringCooldownLeft(user);
        list[4] = Holders.QUEST_MISSIONS_AMOUNT;
        list[5] = String.valueOf(getMissions().size());
        EnumMap<DisplayState, Integer> map = user.getMissionsStates(this);
        list[6] = Holders.QUEST_COMPLETED_MISSIONS;
        list[7] = map.getOrDefault(DisplayState.COMPLETED, 0).toString();
        list[8] = Holders.QUEST_FAILED_MISSIONS;
        list[9] = map.getOrDefault(DisplayState.FAILED, 0).toString();
        list[10] = Holders.QUEST_UNSTARTED_MISSIONS;
        list[11] = map.getOrDefault(DisplayState.UNSTARTED, 0).toString();
        list[12] = Holders.QUEST_COOLDOWN_MISSIONS;
        list[13] = map.getOrDefault(DisplayState.COOLDOWN, 0).toString();
        list[14] = Holders.QUEST_ONPROGRESS_MISSIONS;
        list[15] = map.getOrDefault(DisplayState.ONPROGRESS, 0).toString();
        list[16] = Holders.QUEST_LOCKED_MISSIONS;
        list[17] = map.getOrDefault(DisplayState.LOCKED, 0).toString();
        return list;
    }

    @Override
    default @NotNull Material getGuiMaterial() {
        return Material.KNOWLEDGE_BOOK;
    }

    @Override
    default @NotNull SortableButton getEditorButton(@NotNull Gui parent) {
        return new GuiElementButton<>(parent, this);
    }

    @NotNull QuestManager<T> getManager();

    void setDeveloped(boolean value);


}
