package emanondev.deepquests.interfaces;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface BossBarManager<T extends User<T>> {

    /**
     * Clear all the current BossBar stored and reload his config
     */
    void reload();

    /**
     * Update BossBar of task for user
     *
     * @param user user
     * @param task task
     */
    void onProgress(@NotNull T user, @NotNull Task<T> task);

    /**
     * Get default or stored color for taskType
     *
     * @param taskType TaskType
     * @return not null BarColor
     */
    @NotNull BarColor getBarColor(@NotNull TaskType<T> taskType);

    /**
     * Get default or stored style for taskType
     *
     * @param taskType TaskType
     * @return not null BarStyle
     */
    @NotNull BarStyle getBarStyle(@NotNull TaskType<T> taskType);

    /**
     * Get default or stored option for showing BossBar for taskType
     *
     * @param taskType TaskType
     * @return boolean (default true)
     */
    boolean getShowBossBar(@NotNull TaskType<T> taskType);

    /**
     * Get collection of player related to user for task BossBar updates
     *
     * @param user user
     * @param task task
     * @return not null collection (might be empty)
     */
    @NotNull Collection<Player> getPlayers(@NotNull T user, @NotNull Task<T> task);

    void save();


}
