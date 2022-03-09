package emanondev.deepquests.data;

import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.gui.button.AmountSelectorButton;
import emanondev.deepquests.gui.button.Button;
import emanondev.deepquests.gui.button.CollectionSelectorButton;
import emanondev.deepquests.gui.button.StaticFlagButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestComponent;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.utils.Utils;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MythicMobsData<T extends User<T>, E extends QuestComponent<T>> extends QuestComponentData<T, E> {
    private final Set<String> internalNames = new TreeSet<String>();
    private boolean internalNamesIsWhitelist = true;
    private int minLv = 0;
    private int maxLv = 1;
    private boolean checkLv = false;

    public MythicMobsData(E parent, YMLSection section) {
        super(parent, section);
        checkLv = getConfig().getBoolean(Paths.MYTHICMOBDATA_CHECK_LV, checkLv);
        internalNamesIsWhitelist = getConfig().getBoolean(Paths.MYTHICMOBDATA_INTERNAL_NAMES_IS_WHITELIST,
                internalNamesIsWhitelist);
        minLv = getConfig().getInteger(Paths.MYTHICMOBDATA_MIN_LV, minLv);
        maxLv = getConfig().getInteger(Paths.MYTHICMOBDATA_MAX_LV, maxLv);
        internalNames.addAll(getConfig().getStringList(Paths.MYTHICMOBDATA_INTERNAL_NAMES, new ArrayList<String>()));
    }

    public boolean isValidMythicMob(ActiveMob mob) {
        if (mob == null)
            return false;
        if (checkLv)
            if (mob.getLevel() < minLv || mob.getLevel() > maxLv)
                return false;

        return isValidMythicMobInternalName(mob.getType().getInternalName());
    }

    private boolean isValidMythicMobInternalName(String internalName) {
        if (internalNamesIsWhitelist)
            return internalNames.contains(internalName);
        return !internalNames.contains(internalName);
    }

    public void toggleInternalName(String internalName) {
        if (internalName == null || internalName.isEmpty())
            return;
        if (internalNames.contains(internalName))
            internalNames.remove(internalName);
        else
            internalNames.add(internalName);
        getConfig().set(Paths.MYTHICMOBDATA_INTERNAL_NAMES, new ArrayList<>(internalNames));
    }

    public void toggleInternalNamesWhitelist() {
        internalNamesIsWhitelist = !internalNamesIsWhitelist;
        getConfig().set(Paths.MYTHICMOBDATA_INTERNAL_NAMES_IS_WHITELIST, internalNamesIsWhitelist);
    }

    public void setMaxLv(int lv) {
        lv = Math.max(Math.max(minLv, 1), lv);
        if (lv == maxLv)
            return;
        maxLv = lv;
        getConfig().set(Paths.MYTHICMOBDATA_MAX_LV, maxLv);
    }

    public void setMinLv(int lv) {
        lv = Math.min(Math.max(lv, 0), maxLv);
        if (lv == minLv)
            return;
        minLv = lv;
        getConfig().set(Paths.MYTHICMOBDATA_MIN_LV, minLv);
    }

    public Set<String> getInternalNames() {
        return Collections.unmodifiableSet(internalNames);
    }

    public boolean areInternalNamesWhitelist() {
        return internalNamesIsWhitelist;
    }

    public int getMinLevel() {
        return minLv;
    }

    public int getMaxLevel() {
        return maxLv;
    }

    public boolean checkLevel() {
        return checkLv;
    }

    public Button getCheckLevelFlag(Gui gui) {
        return new CheckLevelFlag(gui);
    }

    public Button getMaxLevelButton(Gui gui) {
        return new MaxLevelButton(gui);
    }

    public Button getMinLevelButton(Gui gui) {
        return new MinLevelButton(gui);
    }

    private boolean toggleLevelCheck() {
        checkLv = !checkLv;
        getConfig().set(Paths.MYTHICMOBDATA_CHECK_LV, checkLv);
        return true;
    }

    private class MinLevelButton extends AmountSelectorButton {

        public MinLevelButton(Gui parent) {
            super("&9Minimal Level Selector", new ItemBuilder(Material.REPEATER).setGuiProperty().build(), parent, 1, 5,
                    10, 50, 100, 500, 1000);
        }

        @Override
        public List<String> getButtonDescription() {
            ArrayList<String> desc = new ArrayList<>();
            desc.add("&6Minimal Level Editor");
            desc.add("&9Current: &e" + getMinLevel());
            return desc;
        }

        @Override
        public long getCurrentAmount() {
            return getMinLevel();
        }

        @Override
        public boolean onAmountChangeRequest(long value) {
            setMinLv((int) value);
            return true;
        }

        @Override
        public ItemStack getItem() {
            if (!checkLv)
                return null;
            return super.getItem();
        }
    }

    private class MaxLevelButton extends AmountSelectorButton {

        public MaxLevelButton(Gui parent) {
            super("&9Maximus Level Selector", new ItemBuilder(Material.REPEATER).setGuiProperty().build(), parent, 1, 5,
                    10, 50, 100, 500, 1000);
        }

        @Override
        public List<String> getButtonDescription() {
            ArrayList<String> desc = new ArrayList<>();
            desc.add("&6Maximus Level Editor");
            desc.add("&9Current: &e" + getMaxLevel());
            return desc;
        }

        @Override
        public long getCurrentAmount() {
            return getMaxLevel();
        }

        @Override
        public boolean onAmountChangeRequest(long value) {
            setMaxLv((int) value);
            return true;
        }

        @Override
        public ItemStack getItem() {
            if (!checkLv)
                return null;
            return super.getItem();
        }
    }

    private class CheckLevelFlag extends StaticFlagButton {

        public CheckLevelFlag(Gui parent) {
            super(Utils.setDescription(new ItemBuilder(Material.LEVER).setGuiProperty().build(),
                            Arrays.asList("&6Level Check", "&9Status: &cDisabled"), null, true),
                    Utils.setDescription(new ItemBuilder(Material.LEVER).setGuiProperty()
                                    .addEnchantment(Enchantment.DURABILITY, 1).build(),
                            Arrays.asList("&6Level Check", "&9Status: &aEnabled"), null, true),
                    parent);
        }

        @Override
        public boolean getCurrentValue() {
            return checkLv;
        }

        @Override
        public boolean onValueChangeRequest(boolean value) {
            return toggleLevelCheck();
        }
    }

    public Button getMythicMobsSelectorButton(Gui gui) {
        return new MythicMobsSelectorButton(gui);
    }

    private class MythicMobsSelectorButton extends CollectionSelectorButton<String> {

        public MythicMobsSelectorButton(Gui parent) {
            super("&9MythicMob Selector", new ItemBuilder(Material.WITHER_SKELETON_SKULL).setGuiProperty().build(),
                    parent, true);
        }

        @Override
        public Collection<String> getPossibleValues() {
            TreeSet<String> set = new TreeSet<>();
            for (MythicMob mob : MythicMobs.inst().getMobManager().getMobTypes())
                set.add(mob.getInternalName());
            return set;
        }

        @Override
        public List<String> getButtonDescription() {
            ArrayList<String> desc = new ArrayList<>();
            desc.add("&6Mythic Mob Type Editor");
            if (internalNames.isEmpty()) {
                if (areInternalNamesWhitelist())
                    desc.add("&cNo Type selected");
                else
                    desc.add("&9Any type is Type &aAllowed");
            } else {
                if (areInternalNamesWhitelist()) {
                    desc.add("&9Allowed Types:");
                    for (String name : internalNames)
                        desc.add("  &9- &a" + name);
                } else {
                    desc.add("&9Unallowed Types:");
                    for (String name : internalNames)
                        desc.add("  &9- &c" + name);
                }
            }
            return desc;
        }

        @Override
        public List<String> getElementDescription(String element) {
            MythicMob mob = MythicMobs.inst().getMobManager().getMythicMob(element);
            ArrayList<String> desc = new ArrayList<>();
            desc.add("&9Type: &e" + element);
            if (mob != null) {
                desc.add("&9DisplayName: '&r" + mob.getDisplayName() + "&9'");
                desc.add("&9EntityType: &e" + mob.getEntityType());
                desc.add("&9Health: &e" + mob.getHealth());
                desc.add("&9Damage: &e" + mob.getDamage());
                desc.add("&9Armor: &e" + mob.getArmor());
            }
            return desc;
        }

        @Override
        public ItemStack getElementItem(String element) {
            return new ItemBuilder(Material.IRON_SWORD).setGuiProperty().build();
        }

        @Override
        public boolean isValidContains(String element) {
            return isValidMythicMobInternalName(element);
        }

        @Override
        public boolean getIsWhitelist() {
            return areInternalNamesWhitelist();
        }

        @Override
        public boolean onToggleElementRequest(String element) {
            toggleInternalName(element);
            return true;
        }

        @Override
        public boolean onWhitelistToggle() {
            toggleInternalNamesWhitelist();
            return true;
        }

    }
}
