package emanondev.deepquests.interfaces;

import org.bukkit.World;

public interface HasWorlds {

	/**
	 * 
	 * @param world target world
	 * @return true if this should be seen on selected world
	 */
	public boolean isWorldAllowed(World world);
	
	
	public default void toggleWorld(World world) {
		if (world == null)
			return;
		toggleWorld(world.getName());
	}
	public void toggleWorld(String world);
	
	public default void toggleWorldWhitelist() {
		setWorldWhitelist(!isWorldListWhitelist());
	}

	public boolean isWorldListWhitelist();
	
	public void setWorldWhitelist(boolean value);
}
