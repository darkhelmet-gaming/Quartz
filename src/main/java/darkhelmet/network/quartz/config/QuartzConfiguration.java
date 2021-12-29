package darkhelmet.network.quartz.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class QuartzConfiguration {
    @Comment("Configure the data source for events.\n" +
            "Use 'config' to load events from this configuration file.\n" +
            "Use 'mysql' to load events from a MySQL/MariaDB database.\n")
    private String dataSource = "config";

    @Comment("Configure a list of events.\n" +
            "This block is ignored if data-source value is anything but 'config'")
    private List<EventConfiguration> events = new ArrayList<>();

    @Comment("Configure a MySQL/MariaDB database.\n" +
            "This block is ignored if data-source value is 'config'")
    private StorageConfiguration storageConfiguration = new StorageConfiguration();

    public QuartzConfiguration() {
        // Add a sample event configuration
        events.add(new EventConfiguration());
    }

    public String dataSource() {
        return this.dataSource;
    }

    public List<EventConfiguration> events() {
        return this.events;
    }

    public StorageConfiguration storageConfiguration() {
        return this.storageConfiguration;
    }
}
