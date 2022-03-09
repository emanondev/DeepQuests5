package emanondev.deepquests.config;

//import java.io.BufferedWriter;

import emanondev.deepquests.Quests;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.stream.Stream;

public class ConfigAPI {
    private static final YmlConstructor constructor = new YmlConstructor();
    private static final YmlRepresenter representer = new YmlRepresenter();
    private static final Plugin plugin = Quests.get();
    private static final ThreadLocal<Yaml> yaml = ThreadLocal.withInitial(() -> {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setAllowUnicode(true);
        return new Yaml(constructor, representer, options);
    });


    public static String asString(Navigator nav) {
        return yaml.get().dump(nav.localMap);
    }

    public static Navigator toNavigator(String raw) {
        return new Navigator(yaml.get().load(raw));
    }

    public static boolean writeToFile(File file, Navigator nav) {
        try {
            String txt = asString(nav);
			/*BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8));
				writer.write(txt);
				writer.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}*/

            Files.write(file.toPath(), Arrays.asList(txt.split("\n")), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Navigator readFromFile(File file) {
        try {
            if (!file.exists()) {
                if (!file.getParentFile().exists()) { // Create parent folders if they don't exist
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }
            StringBuilder contentBuilder = new StringBuilder();
            Stream<String> stream = Files.lines(file.toPath(), StandardCharsets.UTF_8);
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
            stream.close();
            return toNavigator(contentBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return new Navigator(null);
        }

    }

    public static Navigator readFromResourceFile(File file) {
        try {
            if (!file.exists()) {

                if (!file.getParentFile().exists()) { // Create parent folders if they don't exist
                    file.getParentFile().mkdirs();
                }
                int index = file.getPath().indexOf(plugin.getName() + File.separator) +
                        (plugin.getName() + File.separator).length();
                String path = file.getPath().substring(index);//.replace(File.separator,"/");
                if (plugin.getResource(path) != null) {
                    plugin.saveResource(path, true); // Save the one from the JAR if possible
                } else {
                    try {
                        file.createNewFile();
                    } // Create a blank file if there's not one to copy from the JAR
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            StringBuilder contentBuilder = new StringBuilder();
            Stream<String> stream = Files.lines(file.toPath(), StandardCharsets.UTF_8);
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
            stream.close();
            return toNavigator(contentBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return new Navigator(null);
        }

    }
	
	/*
	public static String mapToString(Map<String, Object> map) {
		return yaml.get().dump(map);
	}

	public static Map<String, Object> stringToMap(String raw) {
		return yaml.get().load(raw);
	}

	public static boolean writeToFile(File file, Map<String, Object> map) {
		try {
			String txt = mapToString(map);
			Files.write(file.toPath(), Arrays.asList(txt.split("\n")), StandardCharsets.UTF_8,
					StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static Map<String,Object> readFromFile(File file) {
		try {
			if (!file.exists()) {
				if (!file.getParentFile().exists()) { // Create parent folders if they don't exist
					file.getParentFile().mkdirs();
				}
				file.createNewFile();
			}
			StringBuilder contentBuilder = new StringBuilder();
			Stream<String> stream = Files.lines( file.toPath(), StandardCharsets.UTF_8);
		    stream.forEach(s -> contentBuilder.append(s).append("\n"));
		    stream.close();
		    return stringToMap(contentBuilder.toString());
		}catch (Exception e){
		    e.printStackTrace();
		    return new LinkedHashMap<String,Object>();
		}
		
	}

	public static Map<String,Object> readFromResourceFile(File file) {
		try {
			if (!file.exists()) {

				if (!file.getParentFile().exists()) { // Create parent folders if they don't exist
					file.getParentFile().mkdirs();
				}
				int index = file.getPath().indexOf(plugin.getName()+File.separator)+
						(plugin.getName()+File.separator).length();
				String path = file.getPath().substring(index).replace(File.separator,"/");
				if (plugin.getResource(path) != null) {
					plugin.saveResource(path, true); // Save the one from the JAR if possible
				} else {
					try {
						file.createNewFile();
					} // Create a blank file if there's not one to copy from the JAR
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			StringBuilder contentBuilder = new StringBuilder();
			Stream<String> stream = Files.lines( file.toPath(), StandardCharsets.UTF_8);
		    stream.forEach(s -> contentBuilder.append(s).append("\n"));
		    stream.close();
		    return stringToMap(contentBuilder.toString());
		}catch (Exception e){
		    e.printStackTrace();
		    return new LinkedHashMap<String,Object>();
		}
		
	}*/
}
