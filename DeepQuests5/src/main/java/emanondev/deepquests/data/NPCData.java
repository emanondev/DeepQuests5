package emanondev.deepquests.data;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.gui.button.Button;
import emanondev.deepquests.gui.button.CollectionSelectorButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestComponent;
import emanondev.deepquests.interfaces.User;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class NPCData<T extends User<T>, E extends QuestComponent<T>> extends QuestComponentData<T, E> {
    private final Set<Integer> ids = new TreeSet<>();
    private boolean isListWhitelist;

    public NPCData(E parent, YMLSection section) {
        super(parent, section);
        isListWhitelist = getConfig().getBoolean(Paths.DATA_NPC_ID_LIST_IS_WHITELIST, true);
        ids.addAll(getConfig().getIntegerList(Paths.DATA_NPC_ID_LIST, new ArrayList<Integer>()));
    }

    public void toggleNPC(NPC npc) {
        if (npc == null)
            return;
        toggleNPC(npc.getId());
    }

    public void toggleNPC(int id) {
        if (id < 0)
            return;
        if (ids.contains(id))
            ids.remove(id);
        else
            ids.add(id);
        getConfig().set(Paths.DATA_NPC_ID_LIST, new ArrayList<>(ids));
    }

    public void toggleWhitelist() {
        isListWhitelist = !isListWhitelist;
        getConfig().set(Paths.DATA_NPC_ID_LIST_IS_WHITELIST, isListWhitelist);
    }

    public boolean isValidNPC(NPC npc) {
        if (npc == null)
            return false;
        return isValidNPCId(npc.getId());
    }

    public boolean isValidNPCId(int id) {
        if (isListWhitelist)
            return ids.contains(id);
        return !ids.contains(id);
    }

    public Set<Integer> getNpcIds() {
        return Collections.unmodifiableSet(ids);
    }

    public boolean areNpcIdsWhitelist() {
        return isListWhitelist;
    }

    public Button getNPCSelectorButton(Gui gui) {
        return new NPCSelectorButton(gui);
    }

    private class NPCSelectorButton extends CollectionSelectorButton<Integer> {

        public NPCSelectorButton(Gui parent) {
            super("&9Npc Selector", new ItemBuilder(Material.PLAYER_HEAD).setGuiProperty().build(), parent, true);
        }

        @Override
        public Collection<Integer> getPossibleValues() {
            TreeSet<Integer> set = new TreeSet<>();
            CitizensAPI.getNPCRegistry().forEach((npc) -> {
                set.add(npc.getId());
            });
            return set;
        }

        @Override
        public List<String> getButtonDescription() {
            ArrayList<String> info = new ArrayList<>();
            info.add("&6Npc Selector Button");
            info.addAll(getInfo());
            return info;
        }

        @Override
        public List<String> getElementDescription(Integer element) {
            ArrayList<String> info = new ArrayList<>();
            info.add("&9ID: &e" + element);
            NPC npc = CitizensAPI.getNPCRegistry().getById(element);
            if (npc != null) {
                info.add("&9Name: &r" + npc.getFullName());
                if (npc.getStoredLocation() != null)
                    info.add("&9Location: &e" + npc.getStoredLocation().getWorld().getName() + " "
                            + npc.getStoredLocation().getBlockX() + " " + npc.getStoredLocation().getBlockY() + " "
                            + npc.getStoredLocation().getBlockZ());
                else if (npc.getEntity() != null)
                    info.add("&9Location: &e" + npc.getEntity().getLocation().getWorld().getName() + " "
                            + npc.getEntity().getLocation().getBlockX() + " "
                            + npc.getEntity().getLocation().getBlockY() + " "
                            + npc.getEntity().getLocation().getBlockZ());
            }
            return info;
        }

        @Override
        public ItemStack getElementItem(Integer element) {
            return new ItemBuilder(Material.PINK_WOOL).setGuiProperty().build();
        }

        @Override
        public boolean isValidContains(Integer element) {
            return isValidNPCId(element);
        }

        @Override
        public boolean getIsWhitelist() {
            return areNpcIdsWhitelist();
        }

        @Override
        public boolean onToggleElementRequest(Integer element) {
            toggleNPC(element);
            return true;
        }

        @Override
        public boolean onWhitelistToggle() {
            toggleWhitelist();
            return true;
        }

    }

    public ArrayList<String> getInfo() {
        ArrayList<String> info = new ArrayList<>();
        if (areNpcIdsWhitelist()) {
            info.add("&9Npcs &aAllowed&9:");
            for (int id : getNpcIds()) {
                NPC npc = CitizensAPI.getNPCRegistry().getById(id);
                if (npc != null) {
                    info.add(" &9- &a" + id + " &r" + npc.getFullName());
                    info.add(" &9  location: &e" + npc.getStoredLocation().getWorld().getName() + " "
                            + npc.getStoredLocation().getBlockX() + " " + npc.getStoredLocation().getBlockY() + " "
                            + npc.getStoredLocation().getBlockZ());
                } else
                    info.add(" &9- &a" + id);
            }
        } else {
            info.add("&9Npcs &cUnallowed&9:");
            for (int id : getNpcIds()) {

                NPC npc = CitizensAPI.getNPCRegistry().getById(id);
                if (npc != null) {
                    info.add(" &9- &c" + id + " &r" + npc.getFullName());
                    info.add(" &9  location: &e" + npc.getStoredLocation().getWorld().getName() + " "
                            + npc.getStoredLocation().getBlockX() + " " + npc.getStoredLocation().getBlockY() + " "
                            + npc.getStoredLocation().getBlockZ());
                } else
                    info.add(" &9- &c" + id);
            }
        }

        return info;
    }
}