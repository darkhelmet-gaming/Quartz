package darkhelmet.network.quartz.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class EventConfiguration {
    @Comment("Enable or disable this event. When disabled, no schedules can become active.")
    private boolean enabled = true;

    @Comment("Set the event title. This is used in event messages and lists.")
    private String name = "Example Event";

    @Comment("You can set multiple schedules for an event, even beyond what cron offers.\n" +
            "Here is a good crontab generator: https://www.freeformatter.com/cron-expression-generator-quartz.html\n" +
            "Times are always based on the timezone of the machine the server is running on.")
    private List<ScheduleConfig> schedules = new ArrayList<>();

    public EventConfiguration() {
        schedules.add(new ScheduleConfig());
    }

    public boolean enabled() {
        return this.enabled;
    }

    public String name() {
        return this.name;
    }

    public List<ScheduleConfig> schedules() {
        return this.schedules;
    }

    // start commands
    // end commands
    // activation commands

    // announcement title
    // announcement subtitle
    // broadcast
}
