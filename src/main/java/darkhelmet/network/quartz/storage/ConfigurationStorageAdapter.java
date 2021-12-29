package darkhelmet.network.quartz.storage;

import darkhelmet.network.quartz.models.Event;
import darkhelmet.network.quartz.models.Schedule;
import darkhelmet.network.quartz.config.EventConfiguration;
import darkhelmet.network.quartz.config.QuartzConfiguration;
import darkhelmet.network.quartz.config.ScheduleConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConfigurationStorageAdapter implements IStorageAdapter {
    /**
     * Cache of all events
     */
    private final List<Event> events = new ArrayList<>();

    /**
     * Construct a new config storage adapter.
     *
     * @param config The quartz configuration
     */
    public ConfigurationStorageAdapter(QuartzConfiguration config) {
        for (EventConfiguration eventConfig : config.events()) {
            List<Schedule> schedules = new ArrayList<>();

            for (ScheduleConfig scheduleConfig : eventConfig.schedules()) {
                Schedule schedule = new Schedule(
                    scheduleConfig.starts(),
                    scheduleConfig.ends(),
                    scheduleConfig.enabled()
                );

                schedules.add(schedule);
            }

            Event event = new Event(eventConfig.name(), eventConfig.enabled(), schedules);

            events.add(event);
        }
    }

    @Override
    public List<Event> getEnabledEvents() {
        return events.stream().filter(Event::enabled).collect(Collectors.toList());
    }

    @Override
    public Optional<Event> getEvent(String name) {
        return events.stream().filter(event -> event.name().equalsIgnoreCase(name)).findFirst();
    }
}
