package darkhelmet.network.quartz.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ConfigSerializable
public class EventConfiguration {
    @Comment("Enable or disable this event. When disabled, no schedules can become active.")
    private boolean enabled = true;

    @Comment("Overrides displays for this event only. Leave empty to use global defaults.")
    private List<DisplayConfiguration> displays = new ArrayList<>();

    @Comment("Set the event title. This is used in event messages and lists.")
    private String name = "Example Event";

    @Comment("You can set multiple schedules for an event, even beyond what cron offers.\n" +
            "Here is a good crontab generator: https://www.freeformatter.com/cron-expression-generator-quartz.html\n" +
            "Times are always based on the timezone of the machine the server is running on.")
    private List<ScheduleConfiguration> schedules = new ArrayList<>();

    @Comment("Configure what happens when this event STARTS (a schedule's start time is reached)")
    private PhaseConfiguration start = new PhaseConfiguration();

    @Comment("Configure what happens when this event ENDS (a schedule's end time is reached)")
    private PhaseConfiguration end = new PhaseConfiguration();

    public EventConfiguration() {
        schedules.add(new ScheduleConfiguration());
    }

    public boolean enabled() {
        return this.enabled;
    }

    public List<DisplayConfiguration> displays() {
        return displays;
    }

    public String name() {
        return this.name;
    }

    public List<ScheduleConfiguration> schedules() {
        return this.schedules;
    }

    public PhaseConfiguration start() {
        return start;
    }

    public PhaseConfiguration end() {
        return end;
    }

    /**
     * Get all displays for the given phase.
     *
     * @param phase The phase
     * @return Displays for phase
     */
    public List<DisplayConfiguration> getDisplaysForPhase(String phase) {
        return displays.stream().filter(display -> display.on().equalsIgnoreCase(phase)).collect(Collectors.toList());
    }

    /**
     * Get a list of all enabled schedules.
     *
     * @return All enabled schedules
     */
    public List<ScheduleConfiguration> getEnabledSchedules() {
        return schedules.stream().filter(ScheduleConfiguration::enabled).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "EventConfiguration[" +
            "title=" + name + ", " +
            "enabled=" + enabled + "]";
    }
}
