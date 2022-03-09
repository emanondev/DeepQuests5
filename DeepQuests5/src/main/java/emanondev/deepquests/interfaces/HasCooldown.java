package emanondev.deepquests.interfaces;

import emanondev.deepquests.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

public interface HasCooldown<T extends User<T>> {

    long getCooldownMinutes();

    default long getCooldownTime() {
        return getCooldownMinutes() * 60 * 1000;
    }

    long getCooldownLeft(@NotNull T user);

    default String getStringCooldownLeft(@NotNull T user) {
        return StringUtils.getStringCooldown(getCooldownLeft(user));
    }


    boolean isRepeatable();

    void setCooldownMinutes(long cooldown);

    void setRepeatable(boolean value);

}
