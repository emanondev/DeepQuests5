package emanondev.deepquests.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Completer {
    public static final int MAX_COMPLETES = 75;
    private static final String[] boolValues = new String[]{"true", "false"};

    /**
     * @param l         - where to put results
     * @param prefix    - the prefix to complete
     * @param enumClass - the class of the enums
     */
    public static void complete(List<String> l, String prefix, Class<? extends Enum<?>> enumClass) {
        prefix = prefix.toUpperCase();
        Enum<?>[] list = enumClass.getEnumConstants();
        for (int i = 0, c = 0; i < list.length && c < MAX_COMPLETES; i++)
            if (list[i].toString().startsWith(prefix)) {
                l.add(list[i].toString().toLowerCase());
                c++;
            }
        return;
    }

    /**
     * @param l      - where to put results
     * @param prefix - the prefix to complete
     * @param list   - the list of possible results
     */
    public static void complete(List<String> l, String prefix, List<String> list) {
        String tempPrefix = prefix.toLowerCase();
        list.forEach((key) -> {
            if (l.size() <= MAX_COMPLETES && key.toLowerCase().startsWith(tempPrefix)) {
                l.add(key);
            }
        });
        return;
    }

    /**
     * @param l      - where to put results
     * @param prefix - the prefix to complete
     * @param list   - the list of possible results
     */
    public static void complete(List<String> l, String prefix, String... list) {
        prefix = prefix.toLowerCase();
        for (int i = 0, c = 0; i < list.length && c < MAX_COMPLETES; i++)
            if (list[i].toLowerCase().startsWith(prefix)) {
                l.add(list[i]);
                c++;
            }
        return;
    }

    /**
     * @param l      - where to put results
     * @param prefix - the prefix to complete
     * @param coll   - the list of possible results
     */
    public static void complete(List<String> l, String prefix, Collection<String> coll) {
        String tempPrefix = prefix.toLowerCase();
        coll.forEach((key) -> {
            if (l.size() <= MAX_COMPLETES && key.toLowerCase().startsWith(tempPrefix)) {
                l.add(key);
            }
        });
        return;
    }

    /**
     * add to list all player names matching with prefix
     *
     * @param list   target list
     * @param prefix prefix
     */
    public static void completePlayerNames(List<String> list, String prefix) {
        String text = prefix.toLowerCase();
        Bukkit.getOnlinePlayers().forEach((p) -> {
            if (list.size() < Completer.MAX_COMPLETES && p.getName().toLowerCase().startsWith(text))
                list.add(p.getName());
        });
        return;
    }

    public static void completeWorlds(ArrayList<String> l, String prefix, List<World> worlds) {
        String text = prefix.toLowerCase();
        worlds.forEach((w) -> {
            if (l.size() < Completer.MAX_COMPLETES && w.getName().toLowerCase().startsWith(text))
                l.add(w.getName());
        });
        return;

    }

    public static void completeBoolean(ArrayList<String> l, String prefix) {
        complete(l, prefix, boolValues);
    }
}
