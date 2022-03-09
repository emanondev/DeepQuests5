package emanondev.deepquests.interfaces;

import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

public interface HasWorlds {

    /**
     * @param world target world
     * @return true if this should be seen on selected world
     */
    boolean isWorldAllowed(@Nullable World world);


    default void toggleWorld(@Nullable World world) {
        if (world == null)
            return;
        toggleWorld(world.getName());
    }

    void toggleWorld(String world);

    default void toggleWorldWhitelist() {
        setWorldWhitelist(!isWorldListWhitelist());
    }

    boolean isWorldListWhitelist();

    void setWorldWhitelist(boolean value);
}
