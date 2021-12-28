package darkhelmet.network.quartz.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Config extends ConfigBase {
    /**
     * Constructor.
     *
     * @param plugin Plugin.
     */
    public Config(Plugin plugin) {
        super(plugin);
    }

    /**
     * Get the fileConfig.
     */
    @Override
    public FileConfiguration getConfig() {
        config = plugin.getConfig();

        // MySql
        config.addDefault("quartz.datasource", "mysql");
        config.addDefault("quartz.mysql.hostname", "127.0.0.1");
        config.addDefault("quartz.mysql.username", "root");
        config.addDefault("quartz.mysql.password", "");
        config.addDefault("quartz.mysql.database", "minecraft");
        config.addDefault("quartz.mysql.port", 3306);

        // Copy defaults
        config.options().copyDefaults(true);

        // Save the defaults/config
        plugin.saveConfig();

        return config;
    }
}