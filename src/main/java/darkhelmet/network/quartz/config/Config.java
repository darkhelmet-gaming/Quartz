package darkhelmet.network.quartz.config;

import darkhelmet.network.quartz.Quartz;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.bukkit.plugin.Plugin;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

public class Config {
    private Config() {}

    /**
     * Get or create the plugin configuration files.
     *
     * @param plugin The plugin
     * @return The quartz configuration object
     */
    public static QuartzConfiguration getOrCreateQuartzConfiguration(Plugin plugin) {
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
            Quartz.error("An error occurred while loading the quartz configuration: " + e.getMessage());
            if (e.getCause() != null) {
                e.getCause().printStackTrace();
            }
        }

        return null;
    }

    /**
     * Get or create the event state data files.
     *
     * @param plugin The plugin
     * @return The event state object
     */
    public static EventStateConfiguration getOrCreateEventStateConfiguration(Plugin plugin) {
        File cacheFolder = new File(plugin.getDataFolder(), "cache");

        // If the lang directory doesn't exist, make it
        if (!cacheFolder.exists()) {
            cacheFolder.mkdir();
        }

        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .file(new File(cacheFolder, "event_state.conf"))
                .build();

        try {
            CommentedConfigurationNode root = loader.load();
            final EventStateConfiguration config = root.get(EventStateConfiguration.class);
            root.set(EventStateConfiguration.class, config);
            loader.save(root);

            return config;
        } catch (final ConfigurateException e) {
            Quartz.error("An error occurred while loading the event state configuration: " + e.getMessage());
            if (e.getCause() != null) {
                e.getCause().printStackTrace();
            }
        }

        return null;
    }

    /**
     * Get or create the event state data files.
     *
     * @param plugin The plugin
     * @param config The event state config
     */
    public static void saveEventStateConfiguration(Plugin plugin, EventStateConfiguration config) {
        File cacheFolder = new File(plugin.getDataFolder(), "cache");

        // If the lang directory doesn't exist, make it
        if (!cacheFolder.exists()) {
            cacheFolder.mkdir();
        }

        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .file(new File(cacheFolder, "event_state.conf"))
                .build();

        try {
            CommentedConfigurationNode root = loader.load();
            root.set(EventStateConfiguration.class, config);
            loader.save(root);
        } catch (final ConfigurateException e) {
            Quartz.error("An error occurred while loading the event state configuration: " + e.getMessage());
            if (e.getCause() != null) {
                e.getCause().printStackTrace();
            }
        }
    }

    /**
     * Get or create the language configuration file.
     *
     * @param plugin The plugin
     * @return The language configuration object
     */
    public static LanguageConfiguration getOrCreateLanguageConfiguration(Plugin plugin, String lang) {
        File langFolder = new File(plugin.getDataFolder(), "lang");

        // If the lang directory doesn't exist, make it
        if (!langFolder.exists()) {
            langFolder.mkdir();
        }

        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .file(new File(langFolder, lang + ".conf"))
                .build();

        try {
            CommentedConfigurationNode root = loader.load();
            final LanguageConfiguration config = root.get(LanguageConfiguration.class);
            root.set(LanguageConfiguration.class, config);
            loader.save(root);

            return config;
        } catch (final ConfigurateException e) {
            Quartz.error("An error occurred while loading the lang configuration: " + e.getMessage());
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