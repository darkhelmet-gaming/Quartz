package network.darkhelmet.quartz.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import network.darkhelmet.quartz.Quartz;

import org.bukkit.plugin.Plugin;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

public class Config {
    private Config() {}

    /**
     * Get or create a configuration file.
     *
     * @param clz The configuration class.
     * @param file The file path we'll read/write to.
     * @param <T> The configuration class type.
     * @return The configuration class instance
     */
    public static <T> T getOrWriteConfiguration(Class<T> clz, File file) {
        return getOrWriteConfiguration(clz, file, null);
    }

    /**
     * Get or create a configuration file.
     *
     * @param clz The configuration class
     * @param file The file path we'll read/write to
     * @param config The existing config object to write
     * @param <T> The configuration class type
     * @return The configuration class instance
     */
    public static <T> T getOrWriteConfiguration(Class<T> clz, File file, T config) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }

        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .file(file)
                .build();

        try {
            CommentedConfigurationNode root = loader.load();

            // If config is not provided, load it
            if (config == null) {
                config = root.get(clz);
            }

            root.set(clz, config);
            loader.save(root);

            return config;
        } catch (final ConfigurateException e) {
            Quartz.error("An error occurred while loading the configuration: " + e.getMessage());
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