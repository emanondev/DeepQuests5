package emanondev.deepquests.config;

import emanondev.deepquests.Quests;
import org.bukkit.plugin.Plugin;

import java.io.File;

@Deprecated
public class ConfigFile {
    private static final Plugin plugin = Quests.get();
    private final File file;
    private Navigator nav;

    public ConfigFile(File file) {
        if (file == null)
            throw new NullPointerException();
        this.file = file;
        this.nav = ConfigAPI.readFromResourceFile(this.file);
    }


    public ConfigFile(String name) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("YAML file must have a name!");
        if (!name.endsWith(".yml"))
            name += ".yml";
        this.file = new File(plugin.getDataFolder(), name);
        this.nav = ConfigAPI.readFromResourceFile(this.file);
    }

    @Deprecated
    public Navigator getNavigator() {
        return this.nav;
    }

    public File getFile() {
        return this.file;
    }

    public void save() {
        this.save(false);
    }

    public void save(boolean forced) {
        if (nav.isDirty() || forced) {
            ConfigAPI.writeToFile(file, nav);
            nav.setDirty(false);
        }
    }


    public void reload() {
        this.nav = ConfigAPI.readFromResourceFile(this.file);
    }
}
