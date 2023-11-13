package emanondev.deepquests.config;

import emanondev.deepquests.utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

@Deprecated
public class Navigator {
    protected final Map<String, Object> localMap;
    private final Navigator parent;
    private final HashSet<Navigator> sons = new HashSet<>();
    private String globalPath;
    private boolean dirty = false;

	/*
	public String getCurrentPath() {
		return globalPath == null ? "":globalPath;
	}*/

    public Navigator() {
        this(null);
    }

    public Navigator(Map<String, Object> map) {
        if (map == null)
            map = new LinkedHashMap<>();
        this.localMap = map;
        this.dirty = false;
        this.parent = null;
        this.globalPath = null;
    }

    private Navigator(Navigator parent, String path) {
        this.parent = parent;
        this.localMap = parent.getOrLoadMap(path);
        this.dirty = parent.dirty;
        this.globalPath = parent.getGlobalPathTo(path);
    }

    public String getGlobalPathTo(String path) {
        pathValidator(path);
        return globalPath == null ? path : globalPath + "." + path;
    }

    public void setDirty(boolean value) {
        if (this.dirty == value)
            return;
        this.dirty = value;
        if (value && this.parent != null && this.isAlive())
            this.parent.setDirty(true);
        if (!value)
            for (Navigator son : sons)
                son.setDirty(false);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getOrLoadMap(String path) {
        pathValidator(path);
        try {
            if (!path.contains(".")) {
                try {
                    if (localMap.get(path) == null) {
                        localMap.put(path, new LinkedHashMap<String, Object>());
                    }
                    return (Map<String, Object>) localMap.get(path);
                } catch (Exception e) {
                    try {
                        Map<Integer, Object> intMap = (Map<Integer, Object>) localMap.get(path);
                        Map<String, Object> subMap = new LinkedHashMap<>();
                        for (Integer value : intMap.keySet())
                            subMap.put("" + value, intMap.get(value));
                        localMap.put(path, subMap);
                        return (Map<String, Object>) localMap.get(path);
                    } catch (Exception e2) {
                        new IllegalStateException("Can't reach '" + this.getGlobalPathTo(path)
                                + "' while getting a subMap, resetting value").printStackTrace();
                        localMap.put(path, new LinkedHashMap<String, Object>());
                        return (Map<String, Object>) localMap.get(path);
                    }
                }
            }

            String[] paths = path.split("\\.");
            Map<String, Object> current = localMap;
            for (int i = 0; i < paths.length; i++) {
                if (current.get(paths[i]) == null)
                    current.put(paths[i], new LinkedHashMap<String, Object>());
                try {
                    current = (Map<String, Object>) current.get(paths[i]);
                } catch (Exception e) {
                    try {
                        Map<Integer, Object> intMap = (Map<Integer, Object>) current.get(paths[i]);
                        Map<String, Object> subMap = new LinkedHashMap<>();
                        for (Integer value : intMap.keySet())
                            subMap.put("" + value, intMap.get(value));
                        current.put(paths[i], subMap);
                        current = subMap;
                    } catch (Exception e2) {
                        new IllegalStateException("Can't reach '" + this.getGlobalPathTo(path)
                                + "' while getting a subMap, resetting value").printStackTrace();
                    }
                }
            }
            return current;
        } catch (Exception e) {
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            this.set(path, map);
            return map;
        }
    }

    public boolean isAlive() {
        if (this.parent == null)
            return true;
        Navigator root = this.parent;
        while (root.parent != null)
            root = root.parent;
        Object value = root.get(globalPath);
        if (value == null)
            return false;
        return value == this.localMap;
    }

    public Map<String, Object> getLocalRootMap() {
        return localMap;
    }

    public Navigator getNavigator(String path) {
        pathValidator(path);
        Navigator son = new Navigator(this, path);
        sons.add(son);
        return son;
    }

    private void pathValidator(String path) {
        if (path == null || path.isEmpty() || path.startsWith(".") || path.endsWith(".") || path.contains(" "))
            throw new IllegalArgumentException("invalid path '" + path + "'");
    }

    @SuppressWarnings("unchecked")
    private Object get(String path) {
        pathValidator(path);
        if (!path.contains("."))
            return localMap.get(path);

        String[] paths = path.split("\\.");
        Map<String, Object> current = localMap;
        for (int i = 0; i < paths.length - 1; i++) {
            if (current.get(paths[i]) == null)
                current.put(paths[i], new LinkedHashMap<String, Object>());
            try {
                current = (Map<String, Object>) current.get(paths[i]);
            } catch (Exception e) {
                try {
                    Map<Integer, Object> intMap = (Map<Integer, Object>) current.get(paths[i]);
                    Map<String, Object> subMap = new LinkedHashMap<>();
                    for (Integer value : intMap.keySet())
                        subMap.put("" + value, intMap.get(value));
                    current.put(paths[i], subMap);
                    current = subMap;
                } catch (Exception e2) {
                    new IllegalStateException("Can't reach '" + this.getGlobalPathTo(path) + "'")
                            .printStackTrace();
                    return null;
                }
            }
        }
        return current.get(paths[paths.length - 1]);
    }

    /**
     * for internal use mostly
     *
     * @param path  string with dot for deeper paths
     * @param value value to set
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public void set(String path, Object value) {
        if (value instanceof Navigator) {
            this.set(path, ((Navigator) value).localMap);
            return;
        }

        pathValidator(path);
        Object currentVal = get(path);
        if (currentVal == value)
            return;
        if (!(value instanceof Map) && currentVal != null && currentVal.equals(value))
            return;

        this.setDirty(true);
        if (!path.contains(".")) {
            localMap.put(path, value);
            return;
        }
        String[] paths = path.split("\\.");
        Map<String, Object> current = localMap;
        for (int i = 0; i < paths.length - 1; i++) {
            if (current.get(paths[i]) == null)
                current.put(paths[i], new LinkedHashMap<String, Object>());
            try {
                current = (Map<String, Object>) current.get(paths[i]);
            } catch (Exception e) {
                try {
                    Map<Integer, Object> intMap = (Map<Integer, Object>) current.get(paths[i]);
                    Map<String, Object> subMap = new LinkedHashMap<>();
                    for (Integer val : intMap.keySet())
                        subMap.put("" + val, intMap.get(val));
                    current.put(paths[i], subMap);
                    current = subMap;
                } catch (Exception e2) {
                    new IllegalStateException("Can't reach '" + this.getGlobalPathTo(path)
                            + "' overriding it at '" + paths[i] + "'").printStackTrace();
                    current.put(paths[i], new LinkedHashMap<String, Object>());
                    current = (Map<String, Object>) current.get(paths[i]);
                }
            }
        }
        current.put(paths[paths.length - 1], value);
    }

    public Set<String> keySet() {
        return localMap.keySet();
    }

    public Boolean getBoolean(String path, Boolean def) {
        Object value = this.get(path);
        if (value == null)
            return def;
        if (value instanceof Boolean)
            return (Boolean) value;
        new ClassCastException("Attemping to read " + value.getClass().getName() + " on '" + this.getGlobalPathTo(path) + "' as Boolean, returning null").printStackTrace();
        return def;
    }

    public Integer getInteger(String path, Integer def) {
        Object value = this.get(path);
        if (value == null)
            return def;
        if (value instanceof Number)
            return ((Number) value).intValue();
        new ClassCastException("Attemping to read " + value.getClass().getName() + " on '" + this.getGlobalPathTo(path) + "' as Integer, returning null").printStackTrace();
        return def;
    }

    public Long getLong(String path, Long def) {
        Object value = this.get(path);
        if (value == null)
            return def;
        if (value instanceof Number)
            return ((Number) value).longValue();
        new ClassCastException("Attemping to read " + value.getClass().getName() + " on '" + this.getGlobalPathTo(path) + "' as Long, returning null").printStackTrace();
        return def;
    }

    public Float getFloat(String path, Float def) {
        Object value = this.get(path);
        if (value == null)
            return def;
        if (value instanceof Number)
            return ((Number) value).floatValue();
        new ClassCastException("Attemping to read " + value.getClass().getName() + " on '" + this.getGlobalPathTo(path) + "' as Float, returning null").printStackTrace();
        return def;
    }

    public Double getDouble(String path, Double def) {
        Object value = this.get(path);
        if (value == null)
            return def;
        if (value instanceof Number)
            return ((Number) value).doubleValue();
        new ClassCastException("Attemping to read " + value.getClass().getName() + " on '" + this.getGlobalPathTo(path) + "' as Double, returning null").printStackTrace();
        return def;
    }

    public <T extends Enum<T>> T getEnum(Class<T> clazz, String path, T def) {
        Object value = this.get(path);
        if (value == null)
            return def;
        if (!(value instanceof String)) {
            new IllegalArgumentException("Attemping to read Enum on '" + this.getGlobalPathTo(path)
                    + "' but value is not a String to convert").printStackTrace();
            return def;
        }
        try {
            return Enum.valueOf(clazz, (String) value);
        } catch (Exception e) {
            try {
                return Enum.valueOf(clazz, ((String) value).toUpperCase());
            } catch (Exception e2) {
                new IllegalArgumentException("Attemping to read Enum on '" + this.getGlobalPathTo(path)
                        + "' unable to find a value for '" + value + "'").printStackTrace();
                return def;
            }
        }

    }

    public <T extends Enum<T>> void setEnum(String path, T value) {
        this.set(path, value == null ? null : value.toString());
    }

    public void setItemStack(String path, ItemStack item) {
        if (item == null) {
            this.set(path, null);
            return;
        }
        if (!item.hasItemMeta()) {
            this.set(path, item.getType() + (item.getAmount() == 1 ? "" : ":amount-" + item.getAmount()));
            return;
        }
        ItemMeta meta = item.getItemMeta();
        boolean unbreakable = false;
        boolean hide = false;
        int damage = 0;
        if (meta.isUnbreakable())
            unbreakable = true;
        if (meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS))
            hide = true;
        if (hide == false && unbreakable == false && damage == 0) {
            this.set(path, item);
            return;
        }
        ItemStack clone = new ItemStack(item.getType());
        ItemMeta metaClone = clone.getItemMeta();
        if (meta instanceof Damageable)
            damage = ((Damageable) meta).getDamage();
        if (unbreakable)
            metaClone.setUnbreakable(true);
        if (hide)
            metaClone.addItemFlags(ItemFlag.values());
        if (damage != 0 && metaClone instanceof Damageable)
            ((Damageable) metaClone).setDamage(damage);
        clone.setItemMeta(metaClone);
        if (clone.isSimilar(item)) {
            this.set(path,
                    item.getType() + (item.getAmount() == 1 ? "" : ":amount-" + item.getAmount())
                            + (unbreakable == false ? "" : ":unbreakable") + (hide == false ? "" : ":hideall")
                            + (damage == 0 ? "" : ":damage-" + damage));
            return;
        }
        this.set(path, item);
    }

    public ItemStack getItemStack(String path, ItemStack def) {
        Object value = get(path);
        if (value == null)
            return def;
        if (value instanceof ItemStack)
            return (ItemStack) value;
        if (value instanceof String) {
            String[] infos = ((String) value).split(":");
            try {
                ItemStack item = new ItemStack(Material.valueOf(infos[0].toUpperCase()));
                if (infos.length == 1)
                    return item;
                ItemMeta meta = item.getItemMeta();
                for (int i = 1; i < infos.length; i++) {
                    if (infos[i].startsWith("amount-"))
                        item.setAmount(Integer.valueOf(infos[i].replace("amount-", "")));
                    else if (infos[i].startsWith("damage-"))
                        ((Damageable) meta).setDamage(Integer.valueOf(infos[i].replace("damage-", "")));
                    else if (infos[i].equals("unbreakable"))
                        meta.setUnbreakable(true);
                    else if (infos[i].equals("hideall"))
                        meta.addItemFlags(ItemFlag.values());
                }
                item.setItemMeta(meta);
                return item;
            } catch (Exception e) {
                new IllegalArgumentException("Attemping to read ItemStack on '" + this.getGlobalPathTo(path)
                        + "' unable to convert value for '" + value + "' returning default").printStackTrace();
                return def;
            }

        }
        new IllegalArgumentException("Attemping to read ItemStack on '" + this.getGlobalPathTo(path)
                + "' unable to convert value for '" + value + "' returning default").printStackTrace();
        return def;
    }

    @SuppressWarnings("unchecked")
    public List<String> getStringList(String path, List<String> def, boolean color) {
        Object value = get(path);
        if (value == null)
            return color ? Utils.fixList(def, null, true) : def;
        if (value instanceof List) {
            try {
                return color ? Utils.fixList((List<String>) value, null, true) : (List<String>) value;
            } catch (Exception e) {
                new IllegalArgumentException("Attemping to read String List on '" + this.getGlobalPathTo(path)
                        + "' unable to convert value returning default").printStackTrace();
                return color ? Utils.fixList(def, null, true) : def;
            }
        }
        new IllegalArgumentException("Attemping to read String List on '" + this.getGlobalPathTo(path)
                + "' unable to convert value returning default").printStackTrace();
        return color ? Utils.fixList(def, null, true) : def;
    }

    public String getString(String path, String def, boolean color) {
        Object value = get(path);
        if (value == null)
            return color ? Utils.fixString(def, null, true) : def;
        if (value instanceof String) {
            try {
                return color ? Utils.fixString((String) value, null, true) : (String) value;
            } catch (Exception e) {
                new IllegalArgumentException("Attemping to read String on '" + this.getGlobalPathTo(path)
                        + "' unable to convert value returning default").printStackTrace();
                return color ? Utils.fixString(def, null, true) : def;
            }
        }
        new IllegalArgumentException("Attemping to read String on '" + this.getGlobalPathTo(path)
                + "' unable to convert value returning default").printStackTrace();
        return color ? Utils.fixString(def, null, true) : def;
    }

    public void setString(String path, String value) {
        set(path, value);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getMap(String path, Map<String, Object> def, boolean color) {
        Object value = get(path);
        if (value == null)
            return def;
        if (value instanceof Map) {
            try {
                return (Map<String, Object>) value;
            } catch (Exception e) {
                new IllegalArgumentException("Attemping to read Map on '" + this.getGlobalPathTo(path)
                        + "' unable to convert value returning default").printStackTrace();
                return def;
            }
        }
        new IllegalArgumentException("Attemping to read Map on '" + this.getGlobalPathTo(path)
                + "' unable to convert value returning default").printStackTrace();
        return def;
    }

    public void setStringList(String path, Collection<String> value) {
        if (value == null || value instanceof List) {
            set(path, value);
            return;
        }
        set(path, new ArrayList<>(value));
    }

    public <T extends Enum<T>> void setEnumCollection(String path, Collection<T> value) {
        if (value == null) {
            set(path, value);
            return;
        }
        ArrayList<String> list = new ArrayList<>();
        for (T val : value)
            list.add(val.toString());
        set(path, list);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T extends Enum<T>> EnumSet<T> getEnumSet(Class<T> clazz, String path, EnumSet<T> def) {
        List<String> value = this.getStringList(path, null, false);
        if (value == null)
            return def;
        try {
            EnumSet set = EnumSet.noneOf(clazz);
            for (String val : value) {
                if (val == null)
                    continue;
                try {
                    set.add(Enum.valueOf(clazz, val));
                } catch (Exception e) {
                    try {
                        set.add(Enum.valueOf(clazz, val.toUpperCase()));
                    } catch (Exception e2) {
                        new IllegalArgumentException(
                                "Attemping to convert Enum " + clazz.getName() + " on '" + this.getGlobalPathTo(path) + "' unable to find a value for '" + val + "' skipping it")
                                .printStackTrace();
                        continue;
                    }
                }
            }
            return set;
        } catch (Exception e) {
            new IllegalStateException("Error while to convert Enum " + clazz.getName() + " on '" + this.getGlobalPathTo(path) + "' returning def").printStackTrace();
            return def;
        }
    }

    @SuppressWarnings("unchecked")
    public SortedSet<Integer> getIntegerSet(String path, SortedSet<Integer> def) {
        Object value = get(path);
        if (value == null)
            return def;
        if (value instanceof Collection) {
            try {
                return new TreeSet<Integer>((Collection<Integer>) value);
            } catch (Exception e) {
                new IllegalArgumentException("Attemping to read Integer Set on '" + this.getGlobalPathTo(path)
                        + "' unable to convert value returning default").printStackTrace();
                return def;
            }
        }
        new IllegalArgumentException("Attemping to read Integer Set on '" + this.getGlobalPathTo(path)
                + "' unable to convert value returning default").printStackTrace();
        return def;
    }

    public void setIntegerCollection(String path, Collection<Integer> value) {
        if (value == null) {
            set(path, null);
            return;
        }
        set(path, new ArrayList<Integer>(value));
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setInteger(String path, Integer value) {
        set(path, value);
    }

    public void setLong(String path, Long value) {
        set(path, value);
    }

    public void setDouble(String path, Double value) {
        set(path, value);
    }

    public void setFloat(String path, Float value) {
        set(path, value);
    }

    public void setBoolean(String path, Boolean value) {
        set(path, value);
    }

    @SuppressWarnings("unchecked")
    public Set<String> getStringSet(String path, Set<String> def) {
        Object value = get(path);
        if (value == null)
            return def;
        if (value instanceof Collection) {
            try {
                return new LinkedHashSet<String>((Collection<String>) value);
            } catch (Exception e) {
                new IllegalArgumentException("Attemping to read String Set on '" + this.getGlobalPathTo(path)
                        + "' unable to convert value returning default").printStackTrace();
                return def;
            }
        }
        new IllegalArgumentException("Attemping to read String Set on '" + this.getGlobalPathTo(path)
                + "' unable to convert value returning default").printStackTrace();
        return def;
    }

    public void setNull(String path) {
        set(path, null);
    }

    public void setNavigator(String path, Navigator nav) {
        set(path, new LinkedHashMap<>(nav.localMap));
    }

    public boolean isNull(String path) {
        return get(path) == null;
    }
}
