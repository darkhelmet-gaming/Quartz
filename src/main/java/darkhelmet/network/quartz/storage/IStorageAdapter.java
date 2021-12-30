package darkhelmet.network.quartz.storage;

import darkhelmet.network.quartz.config.EventConfiguration;

import java.util.List;
import java.util.Optional;

public interface IStorageAdapter {
    /**
     * Returns all enabled events.
     *
     * @return List of enabled events, if any.
     */
    List<EventConfiguration> getEnabledEvents();

    /**
     * Get an event by its key.
     *
     * @param key The key
     * @return The event, if one exists
     */
    Optional<EventConfiguration> getEvent(String key);
}
