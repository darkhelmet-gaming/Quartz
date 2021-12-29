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

    @Comment("Displays are used to notify players of an event phase.\n" +
            "Here you can customize the format of broadcasts, title/subtitles," +
            "and more. These are global and will be used for every event " +
            "configured to use the display types unless the event overrides" +
            "the display template itself.")
    private List<DisplayTypeConfig> displays = new ArrayList<>();

    @Comment("Configure a MySQL/MariaDB database.\n" +
            "This block is ignored if data-source value is 'config'")
    private StorageConfiguration storageConfiguration = new StorageConfiguration();

    public QuartzConfiguration() {
        // Add a sample event configuration
        events.add(new EventConfiguration());

        // Add sample displays
        displays.add(new DisplayTypeConfig());
        displays.add(new DisplayTypeConfig("end"));
    }

    public String dataSource() {
        return this.dataSource;
    }

    public List<EventConfiguration> events() {
        return this.events;
    }

    public List<DisplayTypeConfig> displays() {
        return displays;
    }

    public StorageConfiguration storageConfiguration() {
        return this.storageConfiguration;
    }
}
