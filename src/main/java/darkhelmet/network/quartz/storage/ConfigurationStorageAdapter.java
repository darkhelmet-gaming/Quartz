package darkhelmet.network.quartz.storage;

import darkhelmet.network.quartz.config.EventConfiguration;
import darkhelmet.network.quartz.config.QuartzConfiguration;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConfigurationStorageAdapter implements IStorageAdapter {
    /**
     * Cache of all events
     */
    private final List<EventConfiguration> events;

    /**
     * Construct a new config storage adapter.
     *
     * @param config The quartz configuration
     */
    public ConfigurationStorageAdapter(QuartzConfiguration config) {
        events = config.events();
    }

    @Override
    public List<EventConfiguration> getEnabledEvents() {
        return events.stream().filter(EventConfiguration::enabled).collect(Collectors.toList());
    }

    @Override
    public Optional<EventConfiguration> getEvent(String key) {
        return events.stream().filter(event -> event.key().equalsIgnoreCase(key)).findFirst();
    }
}
