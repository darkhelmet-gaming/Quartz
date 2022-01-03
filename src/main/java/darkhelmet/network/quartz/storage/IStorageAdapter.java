package darkhelmet.network.quartz.storage;

import darkhelmet.network.quartz.config.CommandConfiguration;
import darkhelmet.network.quartz.config.EventConfiguration;
import darkhelmet.network.quartz.config.EventStateConfiguration;

import java.util.List;
import java.util.Optional;

public interface IStorageAdapter {
    /**
     * Get all enabled commands.
     *
     * @return List of enabled commands, if any.
     */
    List<CommandConfiguration> getEnabledCommands();

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

    /**
     * Get the event state object.
     *
     * @return The event state
     */
    EventStateConfiguration getState();

    /**
     * Save the state configuration.
     */
    void saveState();
}
