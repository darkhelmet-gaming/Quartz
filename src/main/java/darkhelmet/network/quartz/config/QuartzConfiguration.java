package darkhelmet.network.quartz.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ConfigSerializable
public class QuartzConfiguration {
//    @Comment("Configure the data source for events.\n" +
//            "Use 'config' to load events from this configuration file.\n" +
//            "Use 'mysql' to load events from a MySQL/MariaDB database.\n")
//    private String dataSource = "config";

    @Comment("Enable plugin debug mode. Produces extra logging to help diagnose issues.")
    private boolean debug = false;

    @Comment("Configure a list of events.\n" +
            "This block is ignored if data-source value is anything but 'config'")
    private List<EventConfiguration> events = new ArrayList<>();

    @Comment("Displays are used to notify players of an event phase.\n" +
            "Here you can customize the format of broadcasts, title/subtitles," +
            "and more. These are global and will be used for every event " +
            "configured to use the display types unless the event overrides" +
            "the display template itself.")
    private List<DisplayConfiguration> displays = new ArrayList<>();

    @Comment("Determines which language configuration file will be loaded from the 'langs' directory")
    private String language = "en_us";

    public QuartzConfiguration() {
        // Add a sample event configuration
        events.add(new EventConfiguration());

        // Add sample displays
        displays.add(new DisplayConfiguration("start"));
        displays.add(new DisplayConfiguration("end"));
    }

//    public String dataSource() {
//        return this.dataSource;
//    }

    public boolean debug() {
        return debug;
    }

    public List<EventConfiguration> events() {
        return this.events;
    }

    public List<DisplayConfiguration> displays() {
        return displays;
    }

    public String language() {
        return language;
    }

    public List<DisplayConfiguration> getDisplaysForPhase(String phase) {
        return displays.stream().filter(display -> display.on().equalsIgnoreCase(phase)).collect(Collectors.toList());
    }
}
