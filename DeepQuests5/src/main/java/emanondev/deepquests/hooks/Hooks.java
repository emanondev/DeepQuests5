package emanondev.deepquests.hooks;

import emanondev.virginblock.VirginBlockAPI;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

public class Hooks {

    public static boolean isPAPIEnabled() {
        return isEnabled("PlaceholderAPI");
    }

    public static boolean isMcmmoEnabled() {
        return isEnabled("Mcmmo");
    }

    public static boolean isBlockVirgin(Block block) {
        if (!isVirginBlockPluginEnabled())
            return true;
        else
            return VirginBlockAPI.isBlockVirgin(block);
    }

    public static boolean isCitizenEnabled() {
        return isEnabled("Citizens");
    }

    public static boolean isVirginBlockPluginEnabled() {
        return isEnabled("VirginBlock");
    }

    public static boolean isRegionAPIEnabled() {
        return isEnabled("WorldGuard") &&
                isEnabled("WorldGuardRegionAPI");
    }

    public static boolean isJobsEnabled() {
        return isEnabled("Jobs");
    }

    public static boolean isMythicMobsEnabled() {
        return isEnabled("MythicMobs");
    }

    public static boolean isSkillAPIEnabled() {
        return isEnabled("SkillAPI");
    }

    public static boolean isTownyEnabled() {
        return isEnabled("Towny");
    }

    public static boolean isPartiesEnabled() {
        return isEnabled("Parties");
    }

    public static boolean isItemEditEnable() {
        return isEnabled("ItemEdit");
    }

    private static boolean isEnabled(String pluginName) {
        return Bukkit.getPluginManager().isPluginEnabled(pluginName);
    }

    public static boolean isCMIEnabled() {
        return isEnabled("CMI");
    }

}
