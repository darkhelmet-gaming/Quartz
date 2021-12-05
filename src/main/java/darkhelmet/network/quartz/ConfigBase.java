package darkhelmet.network.quartz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigBase {
    protected final Plugin plugin;
    protected FileConfiguration config;

    /**
     * Constructor.
     *
     * @param plugin The plugin
     */
    ConfigBase(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Get the config.
     *
     * @return The config
     */
    public FileConfiguration getConfig() {
        config = plugin.getConfig();
        return config;
    }

    /**
     * Loads language configuration.
     *
     * @return FileConfig
     */
    public FileConfiguration getLang(String langString) {
        String langFile = langString;

        if (langFile == null) {
            langFile = "en-us";
        }

        // Read the base config
        return loadConfig("languages/", langFile);
    }

    /**
     * Returns base directory for config.
     *
     * @return File
     */
    private File getDirectory() {
        return new File(plugin.getDataFolder() + "");
    }

    /**
     * Returns chosen filename with directory.
     *
     * @return File
     */
    private File getFilename(String filename) {
        return new File(getDirectory(), filename + ".yml");
    }

    /**
     * Loads the config.
     *
     * @param defaultFolder default folder
     * @param filename      filename
     * @return FileConfig.
     */
    private FileConfiguration loadConfig(String defaultFolder, String filename) {
        final File file = getFilename(filename);

        if (file.exists()) {
            return YamlConfiguration.loadConfiguration(file);
        } else {
            // Look for defaults in the jar
            final InputStream defConfigStream = plugin.getResource(defaultFolder + filename + ".yml");
            if (defConfigStream != null) {
                return YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
            }

            return null;
        }
    }

    /**
     * Save the config.
     *
     * @param filename The file name
     * @param config   FileConfig to save
     */
    private void saveConfig(String filename, FileConfiguration config) {
        final File file = getFilename(filename);
        try {
            config.save(file);
        } catch (final IOException e) {
            Quartz.log(e.getMessage());
        }
    }

    /**
     * Write to file.
     *
     * @param filename String
     * @param config   FileConfig
     */
    protected void write(String filename, FileConfiguration config) {
        try {
            final BufferedWriter bw = new BufferedWriter(new FileWriter(getFilename(filename), true));
            saveConfig(filename, config);
            bw.flush();
            bw.close();
        } catch (final IOException e) {
            Quartz.log("IOException: " + e.getMessage());
        }
    }
}
