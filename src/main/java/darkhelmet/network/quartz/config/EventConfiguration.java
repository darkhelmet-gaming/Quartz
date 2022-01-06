package darkhelmet.network.quartz.config;

import com.cronutils.model.Cron;
import com.cronutils.model.time.ExecutionTime;

import darkhelmet.network.quartz.EventPhase;
import darkhelmet.network.quartz.Quartz;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class EventConfiguration {
    @Comment("Enable or disable this event. When disabled, no schedules can become active.")
    private boolean enabled = true;

    @Comment("Overrides displays for this event only. Leave empty to use global defaults.")
    private List<DisplayConfiguration> displays = new ArrayList<>();

    @Comment("Set the event key. This MUST be unique and is used to uniquely identify\n" +
            "this event through commands and the API.")
    private String key = "example_event";

    @Comment("Set the event name. This is used in event messages and lists.")
    private String name = "Example Event";

    @Comment("Set the event name. This is used in event messages and lists.")
    private String description = "This event applies levitation for 3 seconds.";

    @Comment("Set a permission node this event applies to.\n" +
            "Set value to 'false' to apply to all players.\n" +
            "If a permission is set, your command(s) need the %player_name% placeholder\n" +
            "otherwise they'll be executed once without a player arg,\n" +
            "potentially applying to every player.")
    private String permission = "false";

    @Comment("You can set multiple schedules for an event, even beyond what cron offers.\n" +
            "Here is a good crontab generator: https://www.freeformatter.com/cron-expression-generator-quartz.html\n" +
            "Times are always based on the timezone of the machine the server is running on.")
    private List<ScheduleConfiguration> schedules = new ArrayList<>();

    @Comment("Configure what happens when this event STARTS (a schedule's start time is reached)")
    private PhaseConfiguration start = new PhaseConfiguration();

    @Comment("Configure what happens when this event IS ACTIVE (event started and has not yet ended)\n" +
            "Note: This phase configuration is ONLY used when a player joins\n" +
            "during an active event. Displays or commands are triggered per-player per-join")
    private PhaseConfiguration active = new PhaseConfiguration();

    @Comment("Configure what happens when this event ENDS (a schedule's end time is reached)")
    private PhaseConfiguration end = new PhaseConfiguration();

    public EventConfiguration() {
        schedules.add(new ScheduleConfiguration());
    }

    public String description() {
        return description;
    }

    public boolean enabled() {
        return this.enabled;
    }

    public List<DisplayConfiguration> displays() {
        return displays;
    }

    public String key() {
        return this.key;
    }

    public String name() {
        return this.name;
    }

    public String permission() {
        return permission;
    }

    public List<ScheduleConfiguration> schedules() {
        return this.schedules;
    }

    public PhaseConfiguration start() {
        return start;
    }

    public PhaseConfiguration active() {
        return active;
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
    public List<DisplayConfiguration> getDisplaysForPhase(EventPhase phase) {
        return displays.stream().filter(display -> display.phases().contains(phase)).collect(Collectors.toList());
    }

    /**
     * Get a list of all enabled schedules.
     *
     * @return All enabled schedules
     */
    public List<ScheduleConfiguration> getEnabledSchedules() {
        return schedules.stream().filter(ScheduleConfiguration::enabled).collect(Collectors.toList());
    }

    /**
     * Get the next end execution, if any.
     *
     * @return The next end execution, if any
     */
    public Optional<ZonedDateTime> getNextEndExecution() {
        ZonedDateTime soonestNextExec = null;
        for (ScheduleConfiguration schedule : getEnabledSchedules()) {
            Cron endCron = Quartz.getInstance().cronParser().parse(schedule.ends());

            ZonedDateTime now = ZonedDateTime.now();
            Optional<ZonedDateTime> nextExecution = ExecutionTime.forCron(endCron).nextExecution(now);

            if (nextExecution.isPresent() &&
                    (soonestNextExec == null || nextExecution.get().isBefore(soonestNextExec))) {
                soonestNextExec = nextExecution.get();
            }
        }

        return Optional.ofNullable(soonestNextExec);
    }

    /**
     * Get the phase configuration for the provided phase.
     *
     * @param phase The phase
     * @return The phase configuration
     */
    public PhaseConfiguration getPhase(EventPhase phase) {
        if (phase.equals(EventPhase.START)) {
            return start;
        } else if (phase.equals(EventPhase.ACTIVE)) {
            return active;
        } else if (phase.equals(EventPhase.END)) {
            return end;
        }

        return null;
    }

    @Override
    public String toString() {
        return "EventConfiguration[" +
            "title=" + name + ", " +
            "enabled=" + enabled + "]";
    }
}
