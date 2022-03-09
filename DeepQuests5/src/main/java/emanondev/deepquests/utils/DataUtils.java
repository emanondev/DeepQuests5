package emanondev.deepquests.utils;

import com.sucy.skill.api.classes.RPGClass;
import emanondev.deepquests.Translations;
import emanondev.deepquests.data.*;
import emanondev.deepquests.interfaces.QuestComponent;
import emanondev.deepquests.interfaces.User;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DataUtils {

    public static <T extends User<T>, E extends QuestComponent<T>> String getBlockHolder(
            BlockTypeData<T, E> blockType) {
        StringBuilder txt = new StringBuilder("");
        if (blockType.getMaterials().isEmpty())
            txt.append(Translations.translateAction("blocks"));
        else {
            if (!blockType.areMaterialsWhitelist())
                txt.append(Translations.translateAction("any_block_except") + " ");
            List<Material> list = new ArrayList<Material>(blockType.getMaterials());
            for (int i = 0; i < list.size() - 2; i++)
                txt.append(Translations.translate(list.get(i)) + ", ");
            if (list.size() >= 2)
                txt.append(Translations.translate(list.get(list.size() - 2)) + " "
                        + Translations.translateConjunction("or") + " ");
            txt.append(Translations.translate(list.get(list.size() - 1)));
        }
        return txt.toString();
    }

    public static <T extends User<T>, E extends QuestComponent<T>> String getEntityHolder(EntityData<T, E> data) {
        StringBuilder txt = new StringBuilder("");
        if (data.getTypes().isEmpty())
            txt.append(Translations.translateAction("entities"));
        else {
            if (!data.areTypesWhitelist())
                txt.append(Translations.translateAction("any_entity_except") + " ");
            List<EntityType> list = new ArrayList<EntityType>(data.getTypes());
            for (int i = 0; i < list.size() - 2; i++)
                txt.append(Translations.translate(list.get(i)) + ", ");
            if (list.size() >= 2)
                txt.append(Translations.translate(list.get(list.size() - 2)) + " "
                        + Translations.translateConjunction("or") + " ");
            txt.append(Translations.translate(list.get(list.size() - 1)));
        }
        return txt.toString();
    }

    public static <T extends User<T>, E extends QuestComponent<T>> String getRegionHolder(RegionsData<T, E> data) {
        StringBuilder txt = new StringBuilder("");
        if (data.getRegionNames().isEmpty())
            txt.append(Translations.translateAction("regions"));
        else {
            if (!data.areRegionNamesWhitelist())
                txt.append(Translations.translateAction("any_region_except") + " ");
            List<String> regions = new ArrayList<String>(data.getRegionNames());
            HashSet<String> set = new HashSet<>();
            for (String region : regions)
                set.add(Translations.translateRegion(region));
            regions = new ArrayList<String>(set);
            for (int i = 0; i < regions.size() - 2; i++)
                txt.append(regions.get(i) + ", ");
            if (regions.size() >= 2)
                txt.append(regions.get(regions.size() - 2) + " " + Translations.translateConjunction("or") + " ");
            txt.append(regions.get(regions.size() - 1));
        }
        return txt.toString();
    }

    public static <T extends User<T>, E extends QuestComponent<T>> String getLocationHolder(LocationData<T, E> loc) {
        Location l = loc.getLocation();
        if (l == null)
            return "?";
        return Translations.translate(l.getWorld()) + " " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ();
    }

    public static <T extends User<T>, E extends QuestComponent<T>> String getMythicMobsHolder(
            MythicMobsData<T, E> data) {
        StringBuilder txt = new StringBuilder("");
        if (data.getInternalNames().isEmpty())
            txt.append(Translations.translateAction("mythicmobs"));
        else {
            if (!data.areInternalNamesWhitelist())
                txt.append(Translations.translateAction("any_entity_except") + " ");
            List<String> list = new ArrayList<String>(data.getInternalNames());
            for (int i = 0; i < list.size() - 2; i++)
                txt.append(Translations.translateMythicMob(list.get(i)) + ", ");
            if (list.size() >= 2)
                txt.append(Translations.translateMythicMob(list.get(list.size() - 2)) + " "
                        + Translations.translateConjunction("or") + " ");
            txt.append(Translations.translateMythicMob(list.get(list.size() - 1)));
        }
        return txt.toString();
    }

    public static <T extends User<T>, E extends QuestComponent<T>> String getNPCHolder(NPCData<T, E> data) {
        try {
            return NPCModule.getNPCHolder(data);
        } catch (Error e) {
            e.printStackTrace();
            return "?";
        }
    }

    public static <T extends User<T>, E extends QuestComponent<T>> String getItemsHolder(ItemStackData<T, E> data) {
        ItemStack item = data.getItem();
        if (item == null)
            return "?";
        if (!item.hasItemMeta())
            return Translations.translate(item.getType());
        ItemMeta meta = item.getItemMeta();
        if (meta.hasDisplayName())
            return meta.getDisplayName();
        return Translations.translate(item.getType());
    }

    private static class NPCModule {
        private static <T extends User<T>, E extends QuestComponent<T>> String getNPCHolder(NPCData<T, E> data) {
            StringBuilder txt = new StringBuilder("");
            if (data.getNpcIds().isEmpty())
                txt.append(Translations.translateAction("?"));
            else {
                if (!data.areNpcIdsWhitelist())
                    txt.append(Translations.translateAction("any_npc_except") + " ");
                List<Integer> list = new ArrayList<Integer>(data.getNpcIds());
                for (int i = 0; i < list.size() - 2; i++) {
                    NPC npc = CitizensAPI.getNPCRegistry().getById(list.get(i));
                    txt.append((npc == null ? "?" : npc.getFullName()) + ", ");
                }
                if (list.size() >= 2) {
                    NPC npc = CitizensAPI.getNPCRegistry().getById(list.get(list.size() - 2));
                    txt.append((npc == null ? "?" : npc.getFullName()) + " " + Translations.translateConjunction("or") + " ");
                }
                NPC npc = CitizensAPI.getNPCRegistry().getById(list.get(list.size() - 1));
                txt.append(npc == null ? "?" : npc.getFullName());
            }
            return txt.toString();
        }
    }

    public static <T extends User<T>, E extends QuestComponent<T>> String getTargetHolder(TargetMissionData<T, E> data) {
        if (data.getMission() == null)
            return "?";
        return data.getMission().getDisplayName();
    }

    public static <T extends User<T>, E extends QuestComponent<T>> String getTargetHolder(TargetQuestData<T, E> data) {
        if (data.getQuest() == null)
            return "?";
        return data.getQuest().getDisplayName();
    }

    public static <T extends User<T>, E extends QuestComponent<T>> String getAmountHolder(AmountData<T, E> data) {
        return String.valueOf(data.getAmount());
    }

    public static <T extends User<T>, E extends QuestComponent<T>> String getCommandHolder(CommandData<T, E> data) {
        return data.getCommand() == null ? "?" : "/" + data.getCommand();
    }

    public static <T extends User<T>, E extends QuestComponent<T>> String getJobsHolder(JobTypeData<T, E> data) {
        return data.getJob() == null ? "?" : data.getJob().getName();
    }

    public static <T extends User<T>, E extends QuestComponent<T>> String getMcmmoSkillTypeHolder(
            McMMOSkillTypeData<T, E> data) {
        return data.getSkillType() == null ? "?" : data.getSkillType().getName();
    }

    public static <T extends User<T>, E extends QuestComponent<T>> String getSkillAPIClassHolder(
            SkillAPIClassData<T, E> data) {
        return SkillAPIModule.getSkillAPIClassHolder(data);
    }

    private static class SkillAPIModule {
        public static <T extends User<T>, E extends QuestComponent<T>> String getSkillAPIClassHolder(
                SkillAPIClassData<T, E> data) {
            StringBuilder text = new StringBuilder("");
            if (data.getStoredRPGClasses().isEmpty() && data.getStoredGroups().isEmpty())
                text.append(Translations.translateConjunction("any") + " " + Translations.translateAction("skillapiclass"));
            else if (data.getStoredRPGClasses().isEmpty()) {
                ArrayList<String> groups = new ArrayList<>(data.getGroups());
                text.append(Translations.translateConjunction("any") + " " + Translations.translateAction("skillapiclass")
                        + " " + Translations.translateConjunction("of") + " ");
                for (int i = 0; i < groups.size() - 2; i++)
                    text.append(groups.get(i) + ", ");
                if (groups.size() > 2)
                    text.append(groups.get(groups.size() - 2) + " " + Translations.translateConjunction("or") + " ");
                text.append(groups.get(groups.size() - 1));
            } else {
                ArrayList<RPGClass> classes = new ArrayList<>(data.getRPGClasses());
                for (int i = 0; i < classes.size() - 2; i++)
                    text.append(classes.get(i).getName() + ", ");
                if (classes.size() > 2)
                    text.append(classes.get(classes.size() - 2).getName() + " " + Translations.translateConjunction("or") + " ");
                text.append(classes.get(classes.size() - 1).getName());
            }
            return text.toString();
        }
    }


}
