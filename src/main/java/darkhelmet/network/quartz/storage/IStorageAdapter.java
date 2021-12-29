package darkhelmet.network.quartz.storage;

import darkhelmet.network.quartz.models.Event;

import java.util.List;
import java.util.Optional;

public interface IStorageAdapter {
    /**
     * Returns all enabled events.
     *
     * @return List of enabled events, if any.
     */
    List<Event> getEnabledEvents();

    /**
     * Get an event by its name.
     *
     * @param name The name
     * @return The event, if one exists
     */
    Optional<Event> getEvent(String name);
}
