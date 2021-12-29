package darkhelmet.network.quartz.models;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Event {
    /**
     * The event name.
     */
    private final String name;

    /**
     * Whether this event is enabled.
     */
    private final boolean enabled;

    /**
     * All schedules for this event.
     */
    private final List<Schedule> schedules;

    public Event(String name, boolean enabled, List<Schedule> schedules) {
        this.name = name;
        this.enabled = enabled;
        this.schedules = schedules;
    }

    public List<Schedule> getEnabledSchedules() {
        return schedules.stream().filter(Schedule::enabled).collect(Collectors.toList());
    }

    public boolean enabled() {
        return enabled;
    }

    public String name() {
        return name;
    }

    public List<Schedule> schedules() {
        return schedules;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Event) obj;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Event[" +
            "title=" + name + ", " +
            "enabled=" + enabled + "]";
    }
}
