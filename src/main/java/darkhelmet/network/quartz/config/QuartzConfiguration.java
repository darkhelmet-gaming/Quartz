package darkhelmet.network.quartz.config;

import darkhelmet.network.quartz.EventPhase;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ConfigSerializable
public class QuartzConfiguration {
    @Comment("Enable plugin debug mode. Produces extra logging to help diagnose issues.")
    private boolean debug = false;

    @Comment("Configure a list of events.")
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
        displays.add(new DisplayConfiguration(EventPhase.START));
        displays.add(new DisplayConfiguration(EventPhase.END));
    }

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

    public List<DisplayConfiguration> getDisplaysForPhase(EventPhase phase) {
        return displays.stream().filter(display -> display.phases().contains(phase)).collect(Collectors.toList());
    }
}
