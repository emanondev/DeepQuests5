package emanondev.deepquests.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.deepquests.Translations;

public class StringUtils {
	private static DecimalFormat format = loadDecimalFormat();
	private static DecimalFormat loadDecimalFormat() {
		DecimalFormat format = new DecimalFormat();
		format.setMaximumFractionDigits(2);
		format.setMinimumFractionDigits(2);
		format.setRoundingMode(RoundingMode.FLOOR);
		return format;
	}
	
	
	public static DecimalFormat getDecimalFormat() {
		return format;
	}
	public static String getStringCooldown(long cooldown) {
		cooldown = cooldown/1000;
		StringBuilder result = new StringBuilder("");
		if (cooldown>=Time.WEEK.seconds) {//week
			int val = (int) (cooldown/Time.WEEK.seconds);
			if (val>1)
				result.append(val+" "+Time.WEEK.getMultipleName());
			else
				result.append(val+" "+Time.WEEK.getSingleName());
			val =  (int) (cooldown%Time.WEEK.seconds/Time.DAY.seconds);
			if (val>1)
				result.append(" "+Translations.translateConjunction("and")+" "+ val+" "+Time.DAY.getMultipleName());
			else
				if (val==1)
					result.append(" "+Translations.translateConjunction("and")+" "+ val+" "+Time.DAY.getSingleName());
			return result.toString();
		}
		if (cooldown>=Time.DAY.seconds) {//day
			int val = (int) (cooldown/Time.DAY.seconds);
			if (val>1)
				result.append(val+" "+Time.DAY.getMultipleName());
			else
				result.append(val+" "+Time.DAY.getSingleName());
			val =  (int) (cooldown%Time.DAY.seconds/Time.HOUR.seconds);
			if (val>1)
				result.append(" "+Translations.translateConjunction("and")+" "+ val+" "+Time.HOUR.getMultipleName());
			else
				if (val==1)
					result.append(" "+Translations.translateConjunction("and")+" "+ val+" "+Time.HOUR.getSingleName());
			return result.toString();
		}
		if (cooldown>=Time.HOUR.seconds) {//hour
			int val = (int) (cooldown/Time.HOUR.seconds);
			if (val>1)
				result.append(val+" "+Time.HOUR.getMultipleName());
			else
				result.append(val+" "+Time.HOUR.getSingleName());
			val =  (int) (cooldown%Time.HOUR.seconds/Time.MINUTE.seconds);
			if (val>1)
				result.append(" "+Translations.translateConjunction("and")+" "+ val+" "+Time.MINUTE.getMultipleName());
			else
				if (val==1)
					result.append(" "+Translations.translateConjunction("and")+" "+ val+" "+Time.MINUTE.getSingleName());
			return result.toString();
		}
		if (cooldown>=Time.MINUTE.seconds) {//minute
			int val = (int) (cooldown/Time.MINUTE.seconds);
			if (val>1)
				result.append(val+" "+Time.MINUTE.getMultipleName());
			else
				result.append(val+" "+Time.MINUTE.getSingleName());
			val =  (int) (cooldown%Time.MINUTE.seconds/Time.SECOND.seconds);
			if (val>1)
				result.append(" "+Translations.translateConjunction("and")+" "+ val+" "+Time.SECOND.getMultipleName());
			else
				if (val==1)
					result.append(" "+Translations.translateConjunction("and")+" "+ val+" "+Time.SECOND.getSingleName());
			return result.toString();
		}
		int val = (int) (cooldown/Time.SECOND.seconds);
		if (val>1)
			result.append(val+" "+Time.SECOND.getMultipleName());
		else
			result.append(val+" "+Time.SECOND.getSingleName());
		return result.toString();
	}
	public static String itemToString(ItemStack item) {
		StringBuilder txt = new StringBuilder(item.getType().toString() +" x"+item.getAmount());
		if (item.hasItemMeta()) {
			ItemMeta meta = item.getItemMeta();
			if (meta.hasDisplayName())
				txt.append(" named: '"+meta.getDisplayName()+"'");
			if (meta.hasLore())
				txt.append(" lore: "+Arrays.toString(meta.getLore().toArray()));
			if (meta.hasEnchants()) {
				txt.append(" enchants: [");
				for (Enchantment ench:meta.getEnchants().keySet())
					txt.append(ench.getKey().getKey()+" "+meta.getEnchantLevel(ench)+" ");
				txt.append("]");
			}
			if (!meta.getItemFlags().isEmpty())
				txt.append(" flags: "+Arrays.toString(meta.getItemFlags().toArray()));
			
			if (meta.isUnbreakable())
				txt.append(" unbreakable: true ");
		}
		return txt.toString();
	}
}
