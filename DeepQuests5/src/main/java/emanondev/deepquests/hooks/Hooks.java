package emanondev.deepquests.hooks;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;

@Deprecated
public class Hooks {

    @Deprecated
    public static boolean isPAPIEnabled() {
        return isEnabled("PlaceholderAPI");
    }

    @Deprecated
    public static boolean isMcmmoEnabled() {
        return isEnabled("Mcmmo");
    }

    @Deprecated
    public static boolean isBlockVirgin(Block block) {
        if (!isVirginBlockPluginEnabled())
            return true;
        else
            return emanondev.core.Hooks.isBlockVirgin(block);
    }

    @Deprecated
    public static boolean isCitizenEnabled() {
        return isEnabled("Citizens");
    }

    @Deprecated
    public static boolean isVirginBlockPluginEnabled() {
        return isEnabled("VirginBlock");
    }

    @Deprecated
    public static boolean isRegionAPIEnabled() {
        return isEnabled("WorldGuard") &&
                isEnabled("WorldGuardRegionAPI");
    }

    @Deprecated
    public static boolean isJobsEnabled() {
        return isEnabled("Jobs");
    }

    @Deprecated
    public static boolean isMythicMobsEnabled() {
        return isEnabled("MythicMobs");
    }

    @Deprecated
    public static boolean isSkillAPIEnabled() {
        return isEnabled("SkillAPI");
    }

    @Deprecated
    public static boolean isTownyEnabled() {
        return isEnabled("Towny");
    }

    @Deprecated
    public static boolean isPartiesEnabled() {
        return isEnabled("Parties");
    }

    @Deprecated
    public static boolean isItemEditEnable() {
        return isEnabled("ItemEdit");
    }

    @Deprecated
    private static boolean isEnabled(String pluginName) {
        return Bukkit.getPluginManager().isPluginEnabled(pluginName);
    }

    @Deprecated
    public static boolean isCMIEnabled() {
        return isEnabled("CMI");
    }

}
