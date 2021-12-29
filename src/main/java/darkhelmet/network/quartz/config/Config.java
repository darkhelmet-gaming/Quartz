package darkhelmet.network.quartz.config;

import darkhelmet.network.quartz.Quartz;

import org.bukkit.plugin.Plugin;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private Config() {}

    /**
     * Get or create the plugin configuration files.
     *
     * @param plugin The plugin
     * @return The quartz configuration object
     */
    public static QuartzConfiguration getOrCreate(Plugin plugin) {
        File dataFolder = plugin.getDataFolder();

        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
            .file(new File(dataFolder, "quartz.conf"))
            .build();

        try {
            CommentedConfigurationNode root = loader.load();
            final QuartzConfiguration config = root.get(QuartzConfiguration.class);
            root.set(QuartzConfiguration.class, config);
            loader.save(root);

            return config;
        } catch (final ConfigurateException e) {
            Quartz.error("An error occurred while loading this configuration: " + e.getMessage());
            if (e.getCause() != null) {
                e.getCause().printStackTrace();
            }
        }

        return null;
    }

    /**
     * Load or copy a new installation of the quartz scheduler properties file.
     *
     * @param plugin The plugin
     * @return The properties file
     * @throws IOException File read/write exception
     */
    public static Properties loadOrCopyQuartzProperties(Plugin plugin) throws IOException {
        File quartzProperties = new File(plugin.getDataFolder(), "quartz.properties");

        if (!quartzProperties.exists()) {
            InputStream input = plugin.getResource("quartz.properties");

            java.nio.file.Files.copy(input, quartzProperties.toPath());
            input.close();
        }

        Properties prop = new Properties();
        InputStream targetStream = new FileInputStream(quartzProperties);
        prop.load(targetStream);

        return prop;
    }
}