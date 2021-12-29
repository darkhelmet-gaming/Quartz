package darkhelmet.network.quartz.config;

import darkhelmet.network.quartz.Quartz;

import org.bukkit.plugin.Plugin;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.File;

public class Config {
    private Config() {}

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
}