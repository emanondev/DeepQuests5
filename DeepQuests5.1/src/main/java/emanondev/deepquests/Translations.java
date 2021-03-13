package emanondev.deepquests;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;

import emanondev.deepquests.config.ConfigFile;
import emanondev.deepquests.utils.Time;
import net.md_5.bungee.api.ChatColor;

public class Translations {
	private static ConfigFile config = new ConfigFile("translations.yml");

	private static final EnumMap<Time,String> timeSingle = loadTimeSingle();
	private static final EnumMap<Time,String> timeMulti = loadTimeMulti();
	private static final EnumMap<EntityType,String> entities = loadEntities();
	private static final EnumMap<Material,String> materials = loadMaterials();
	private static final Map<Enchantment,String> enchantments = loadEnchantments();
	private static final Map<String,String> actions = new HashMap<>();
	private static final Map<String,String> regions = new HashMap<>();
	private static final Map<String, String> mythicMobs = new HashMap<>();
	private static final Map<String,String> worlds = new HashMap<>();
	private static final Map<String,String> conjunctions = new HashMap<>();
	
	public static void reload() {
		config.reload();
		
		timeSingle.clear();
		timeMulti.clear();
		entities.clear();
		materials.clear();
		enchantments.clear();
		actions.clear();
		regions.clear();
		worlds.clear();
		conjunctions.clear();

		timeSingle.putAll( loadTimeSingle() );
		timeMulti.putAll( loadTimeMulti() );
		entities.putAll( loadEntities() );
		materials.putAll( loadMaterials() );
		enchantments.putAll( loadEnchantments() );
		
		Command.reload();
	}
	private static EnumMap<Time, String> loadTimeSingle() {
		EnumMap<Time, String> map = new EnumMap<>(Time.class);
		for (Time type:Time.values())
			map.put(type,read("time.single."+type.toString().toLowerCase(),type.toString().toLowerCase()));
		return map;
	}
	private static EnumMap<Time, String> loadTimeMulti() {
		EnumMap<Time, String> map = new EnumMap<>(Time.class);
		for (Time type:Time.values())
			map.put(type,read("time.multi."+type.toString().toLowerCase(),type.toString().toLowerCase()));
		return map;
	}
	private static EnumMap<Material, String> loadMaterials() {
		EnumMap<Material, String> map = new EnumMap<>(Material.class);
		for (Material type:Material.values())
			map.put(type,read("materials."+type.toString().toLowerCase(),type.toString().toLowerCase()));
		return map;
	}
	private static EnumMap<EntityType, String> loadEntities() {
		EnumMap<EntityType, String> map = new EnumMap<>(EntityType.class);
		for (EntityType type:EntityType.values())
			if (type.isAlive())
				map.put(type,read("entities."+type.toString().toLowerCase(),type.toString().toLowerCase()));
		return map;
	}
	private static Map<Enchantment, String> loadEnchantments() {
		HashMap<Enchantment, String> map = new HashMap<>();
		for (Enchantment ench:Enchantment.values())
			map.put(ench,read("enchants."+ench.getKey().getKey(),ench.getKey().getKey()));
		return map;
	}

	public static String translateSingle(Time type) {
		return timeSingle.get(type);
	}
	public static String translateMulti(Time type) {
		return timeMulti.get(type);
	}
	public static String translate(EntityType entity) {
		return entities.get(entity);
	}
	public static String translate(Material mat) {
		return materials.get(mat);
	}
	public static String trasnlate(Enchantment ench) {
		return enchantments.get(ench);
	}
	public static String translateConjunction(String conjunction) {
		return getOrRead(conjunctions,conjunction,"conjunctions",conjunction);
	}
	
	public static String translateAction(String actionName) {
		return getOrRead(actions,actionName,"actions",actionName);
	}
	public static String translateRegion(String regionName) {
		return getOrRead(regions,regionName,"regions",regionName);
	}
	public static String translate(World world) {
		return getOrRead(worlds,world.getName(),"worlds",world.getName());
	}
	
	
	private static String getOrRead(Map<String,String> map,String key,String pathNoKey,String defaultName) {
		if (map.containsKey(key))
			return map.get(key);
		String value = read(pathNoKey+"."+key,defaultName);
		map.put(key,value);
		return value;
	}
	private static String read(String path,String defaultName) {
		if (config.getNavigator().getString(path,null,false)!=null)
			return config.getNavigator().getString(path,null,false);
		config.getNavigator().setString(path, defaultName);
		config.save();
		return defaultName;
	}
	public static class Command {
		
		private static void reload() {
			PLAYERS_ONLY = read("players-only",ChatColor.RED+"Command for players only");
			LACK_PERMISSION = read("lack-permission",ChatColor.RED+"You lack of permission "+Holders.PERMISSION);
			NOT_IMPLEMENTED = read("not-implemented",ChatColor.RED+"This is not implemented yet");
			SUCCESS = read("success",ChatColor.GREEN+"Command successfully executed");
			FAIL = read("fail",ChatColor.RED+"Command generated errors");
			
		}
		
		private static String read(String path,String defaultName) {
			return Translations.read("command."+path,defaultName);
		}
		
		public static String PLAYERS_ONLY = read("players-only",ChatColor.RED+"Command for players only");
		public static String LACK_PERMISSION = read("lack-permission",ChatColor.RED+"You lack of permission "+Holders.PERMISSION);
		public static String NOT_IMPLEMENTED = read("not-implemented",ChatColor.RED+"This is not implemented yet");
		public static String SUCCESS = read("success",ChatColor.GREEN+"Command successfully executed");
		public static String FAIL = read("fail",ChatColor.RED+"Command generated errors");
		public static String RELOAD = read("reload",ChatColor.GREEN+"Plugin Reloaded");
		
	}
	public static String translateMythicMob(String internalName) {
		return getOrRead(mythicMobs,internalName,"mythic_mobs",internalName);
	}
	
	public static String replaceAll(String base) {
		return base==null?null:replaceActions(replaceConjunctions(base));
	}
	
	public static String replaceActions(String base) {
		if (base==null)
			return base;
		if (!base.contains("{action:"))
			return base;
		while (base.contains("{action:")) {
			int start = base.indexOf("{action:")+8;
			int end = base.indexOf("}",base.indexOf("{action:")+8);
			if (end<=start)
				break;
			String action = base.substring(start,end);
			base = base.replace("{action:"+action+"}", Translations.translateAction(action));
		}
		return base;
	}
	public static String replaceConjunctions(String base) {
		if (base==null)
			return base;
		if (!base.contains("{conjun:"))
			return base;
		while (base.contains("{conjun:")) {
			int start = base.indexOf("{conjun:")+8;
			int end = base.indexOf("}",base.indexOf("{conjun:")+8);
			if (end<=start)
				break;
			String conj = base.substring(start,end);
			base = base.replace("{conjun:"+conj+"}", Translations.translateConjunction(conj));
		}
		return base;
	}
}
