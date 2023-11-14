package emanondev.deepquests.utils;

import emanondev.deepquests.Translations;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;

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
        cooldown = cooldown / 1000;
        StringBuilder result = new StringBuilder("");
        if (cooldown >= Time.WEEK.seconds) {//week
            int val = (int) (cooldown / Time.WEEK.seconds);
            if (val > 1)
                result.append(val).append(" ").append(Time.WEEK.getMultipleName());
            else
                result.append(val).append(" ").append(Time.WEEK.getSingleName());
            val = (int) (cooldown % Time.WEEK.seconds / Time.DAY.seconds);
            if (val > 1)
                result.append(" ").append(Translations.translateConjunction("and")).append(" ").append(val).append(" ").append(Time.DAY.getMultipleName());
            else if (val == 1)
                result.append(" ").append(Translations.translateConjunction("and")).append(" ").append(val).append(" ").append(Time.DAY.getSingleName());
            return result.toString();
        }
        if (cooldown >= Time.DAY.seconds) {//day
            int val = (int) (cooldown / Time.DAY.seconds);
            if (val > 1)
                result.append(val).append(" ").append(Time.DAY.getMultipleName());
            else
                result.append(val).append(" ").append(Time.DAY.getSingleName());
            val = (int) (cooldown % Time.DAY.seconds / Time.HOUR.seconds);
            if (val > 1)
                result.append(" ").append(Translations.translateConjunction("and")).append(" ").append(val).append(" ").append(Time.HOUR.getMultipleName());
            else if (val == 1)
                result.append(" ").append(Translations.translateConjunction("and")).append(" ").append(val).append(" ").append(Time.HOUR.getSingleName());
            return result.toString();
        }
        if (cooldown >= Time.HOUR.seconds) {//hour
            int val = (int) (cooldown / Time.HOUR.seconds);
            if (val > 1)
                result.append(val).append(" ").append(Time.HOUR.getMultipleName());
            else
                result.append(val).append(" ").append(Time.HOUR.getSingleName());
            val = (int) (cooldown % Time.HOUR.seconds / Time.MINUTE.seconds);
            if (val > 1)
                result.append(" ").append(Translations.translateConjunction("and")).append(" ").append(val).append(" ").append(Time.MINUTE.getMultipleName());
            else if (val == 1)
                result.append(" ").append(Translations.translateConjunction("and")).append(" ").append(val).append(" ").append(Time.MINUTE.getSingleName());
            return result.toString();
        }
        if (cooldown >= Time.MINUTE.seconds) {//minute
            int val = (int) (cooldown / Time.MINUTE.seconds);
            if (val > 1)
                result.append(val).append(" ").append(Time.MINUTE.getMultipleName());
            else
                result.append(val).append(" ").append(Time.MINUTE.getSingleName());
            val = (int) (cooldown % Time.MINUTE.seconds / Time.SECOND.seconds);
            if (val > 1)
                result.append(" ").append(Translations.translateConjunction("and")).append(" ").append(val).append(" ").append(Time.SECOND.getMultipleName());
            else if (val == 1)
                result.append(" ").append(Translations.translateConjunction("and")).append(" ").append(val).append(" ").append(Time.SECOND.getSingleName());
            return result.toString();
        }
        int val = (int) (cooldown / Time.SECOND.seconds);
        if (val > 1)
            result.append(val).append(" ").append(Time.SECOND.getMultipleName());
        else
            result.append(val).append(" ").append(Time.SECOND.getSingleName());
        return result.toString();
    }

}
