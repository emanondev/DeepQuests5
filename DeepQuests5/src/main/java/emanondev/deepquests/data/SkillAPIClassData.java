package emanondev.deepquests.data;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import emanondev.core.ItemBuilder;
import emanondev.core.YMLSection;
import emanondev.deepquests.gui.button.Button;
import emanondev.deepquests.gui.button.CollectionSelectorButton;
import emanondev.deepquests.gui.inventory.Gui;
import emanondev.deepquests.implementations.Paths;
import emanondev.deepquests.interfaces.QuestComponent;
import emanondev.deepquests.interfaces.User;
import emanondev.deepquests.utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SkillAPIClassData<T extends User<T>, E extends QuestComponent<T>> extends QuestComponentData<T, E> {
    private final HashSet<RPGClass> classes = new HashSet<>();
    private final HashSet<String> groups = new HashSet<>();
    private boolean classesIsWhitelist = false;
    private boolean groupsIsWhitelist = false;

    public SkillAPIClassData(E parent, YMLSection section) {
        super(parent, section);
        List<String> classNames = getConfig().getStringList(Paths.DATA_SKILLAPI_CLASSES, null);
        for (String className : classNames) {
            RPGClass clazz = SkillAPI.getClass(className);
            if (clazz == null)
                new IllegalArgumentException("Couldn't find RPGClass '" + className + "' skipping it")
                        .printStackTrace();
            else
                classes.add(clazz);
        }
        List<String> groupNames = getConfig().getStringList(Paths.DATA_SKILLAPI_GROUPS, null);
        for (String groupName : groupNames) {
            if (SkillAPI.getGroups().contains(groupName))
                new IllegalArgumentException("Couldn't find SkillAPI group '" + groupName + "' skipping it")
                        .printStackTrace();
            else
                groups.add(groupName);
        }
        // groups.add(SkillAPI.getSettings().getMainGroup());
        classesIsWhitelist = getConfig().getBoolean(Paths.DATA_SKILLAPI_CLASSES_IS_WHITELIST, classesIsWhitelist);
        groupsIsWhitelist = getConfig().getBoolean(Paths.DATA_SKILLAPI_GROUPS_IS_WHITELIST, groupsIsWhitelist);
    }

    public void toggleGroup(String name) {
        if (name == null)
            return;
        if (groups.contains(name))
            groups.remove(name);
        else
            groups.add(name);
        getConfig().set(Paths.DATA_SKILLAPI_GROUPS, groups);
    }

    public void toggleRPGClass(RPGClass rpgClass) {
        if (rpgClass == null)
            return;
        if (classes.contains(rpgClass))
            classes.remove(rpgClass);
        else
            classes.add(rpgClass);
        if (!classes.isEmpty()) {
            ArrayList<String> classNames = new ArrayList<>();
            for (RPGClass clazz : classes)
                classNames.add(clazz.getName());
            getConfig().set(Paths.DATA_SKILLAPI_CLASSES, classNames);
        }
        getConfig().set(Paths.DATA_SKILLAPI_CLASSES, null);
    }

    public boolean isValidRPGClass(RPGClass type) {
        if (type == null)
            return false;
        if (!isValidGroup(type.getGroup()))
            return false;
        if (classes.isEmpty())
            return true;
        if (classesIsWhitelist)
            return classes.contains(type);
        else
            return !classes.contains(type);
    }

    public boolean isValidGroup(String group) {
        if (groups.isEmpty())
            return true;
        if (groupsIsWhitelist)
            return groups.contains(group);
        else
            return !groups.contains(group);
    }

    public void toggleRPGClassWhitelist() {
        classesIsWhitelist = !classesIsWhitelist;
        getConfig().set(Paths.DATA_SKILLAPI_CLASSES_IS_WHITELIST, classesIsWhitelist);
    }

    public void toggleGroupWhitelist() {
        groupsIsWhitelist = !groupsIsWhitelist;
        getConfig().set(Paths.DATA_SKILLAPI_GROUPS_IS_WHITELIST, groupsIsWhitelist);
    }

    public Button getGroupButton(Gui gui) {
        return new GroupButton(gui);
    }

    public Button getRPGClassButton(Gui gui) {
        return new RPGClassButton(gui);
    }

    public ArrayList<String> getInfo() {
        ArrayList<String> info = new ArrayList<String>();
        if (classes.isEmpty() && groups.isEmpty())
            info.add("&9Any Class is &aAllowed");
        else if (classes.isEmpty()) {
            info.add("&9Any Class from Enabled Groups is &aAllowed:");
            info.add("&9Enabled Groups are:");
            for (String group : SkillAPI.getGroups())
                if (isValidGroup(group))
                    info.add("&9  - &a" + group);
        } else {
            info.add("&aAllowed &9Classes:");
            for (RPGClass clazz : SkillAPI.getClasses().values())
                if (isValidRPGClass(clazz))
                    info.add("&9  - &a" + clazz.getName());
        }
        return info;
    }

    public Set<RPGClass> getRPGClasses() {
        LinkedHashSet<RPGClass> set = new LinkedHashSet<>();
        for (RPGClass clazz : SkillAPI.getClasses().values())
            if (isValidRPGClass(clazz))
                set.add(clazz);
        return set;
    }

    public Set<String> getGroups() {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        for (String group : SkillAPI.getGroups())
            if (isValidGroup(group))
                set.add(group);
        return set;
    }

    public Set<RPGClass> getStoredRPGClasses() {
        return Collections.unmodifiableSet(classes);
    }

    public Set<String> getStoredGroups() {
        return Collections.unmodifiableSet(groups);
    }

    public boolean getStoredClassesIsWhitelist() {
        return classesIsWhitelist;
    }

    public boolean getStoredGroupsIsWhitelist() {
        return groupsIsWhitelist;
    }

    private class RPGClassButton extends CollectionSelectorButton<RPGClass> {

        public RPGClassButton(Gui parent) {
            super("&6RPGClass Button", new ItemBuilder(Material.IRON_SWORD).setGuiProperty().build(), parent, true);
        }

        @Override
        public Collection<RPGClass> getPossibleValues() {
            LinkedHashSet<RPGClass> set = new LinkedHashSet<>();
            for (RPGClass clazz : SkillAPI.getClasses().values())
                if (isValidGroup(clazz.getGroup()))
                    set.add(clazz);
            return set;
        }

        @Override
        public List<String> getButtonDescription() {
            ArrayList<String> list = new ArrayList<>();
            list.add("&6RPGClass Button");

            if (classes.isEmpty())
                list.add("&9Any RPGClass class &aAllowed");
            else {
                if (classesIsWhitelist) {
                    list.add("&9RPGClasss Allowed:");
                    for (RPGClass type : classes)
                        list.add("&9  - &a" + type.getName());
                } else {
                    list.add("&9RPGClasss Unallowed:");
                    for (RPGClass type : classes)
                        list.add("&9  - &c" + type.toString());
                }
            }
            list.add("");
            list.add("&7Click to edit");
            return list;
        }

        @Override
        public List<String> getElementDescription(RPGClass type) {

            ArrayList<String> desc = new ArrayList<String>();
            desc.add("&6RPGClass: '&e" + type.getName() + "&6'");
            if (isValidRPGClass(type))
                desc.add(Utils.fixString("&7This type is &aAllowed", null, true));
            else
                desc.add(Utils.fixString("&7This type is &cUnallowed", null, true));
            return desc;
        }

        @Override
        public ItemStack getElementItem(RPGClass element) {
            return new ItemBuilder(Material.IRON_SWORD).setGuiProperty().build();
        }

        @Override
        public boolean isValidContains(RPGClass element) {
            return isValidRPGClass(element);
        }

        @Override
        public boolean getIsWhitelist() {
            return classesIsWhitelist;
        }

        @Override
        public boolean onToggleElementRequest(RPGClass element) {
            toggleRPGClass(element);
            return true;
        }

        @Override
        public boolean onWhitelistToggle() {
            toggleRPGClassWhitelist();
            return true;
        }
    }

    private class GroupButton extends CollectionSelectorButton<String> {

        public GroupButton(Gui parent) {
            super("&6Group Button", new ItemBuilder(Material.IRON_PICKAXE).setGuiProperty().build(), parent, true);
        }

        @Override
        public Collection<String> getPossibleValues() {
            LinkedHashSet<String> set = new LinkedHashSet<>();
            set.addAll(SkillAPI.getGroups());
            return set;
        }

        @Override
        public List<String> getButtonDescription() {
            ArrayList<String> list = new ArrayList<>();
            list.add("&6Group Button");

            if (classes.isEmpty())
                list.add("&9Any Group class &aAllowed");
            else {
                if (classesIsWhitelist) {
                    list.add("&9Groups Allowed:");
                    for (String type : groups)
                        list.add("&9  - &a" + type);
                } else {
                    list.add("&9Groups Unallowed:");
                    for (String type : groups)
                        list.add("&9  - &c" + type);
                }
            }
            list.add("");
            list.add("&7Click to edit");
            return list;
        }

        @Override
        public List<String> getElementDescription(String type) {
            ArrayList<String> desc = new ArrayList<String>();
            desc.add("&6Group: '&e" + type + "&6'");
            if (isValidGroup(type))
                desc.add(Utils.fixString("&7This type is &aAllowed", null, true));
            else
                desc.add(Utils.fixString("&7This type is &cUnallowed", null, true));
            return desc;
        }

        @Override
        public ItemStack getElementItem(String element) {
            return new ItemBuilder(Material.IRON_PICKAXE).setGuiProperty().build();
        }

        @Override
        public boolean isValidContains(String element) {
            return isValidGroup(element);
        }

        @Override
        public boolean getIsWhitelist() {
            return classesIsWhitelist;
        }

        @Override
        public boolean onToggleElementRequest(String element) {
            toggleGroup(element);
            return true;
        }

        @Override
        public boolean onWhitelistToggle() {
            toggleGroupWhitelist();
            return true;
        }
    }
}
