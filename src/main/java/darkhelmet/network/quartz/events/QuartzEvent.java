package darkhelmet.network.quartz.events;

import darkhelmet.network.quartz.config.EventConfiguration;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

abstract public class QuartzEvent extends Event {
    /**
     * The handlers.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * The event.
     */
    private final EventConfiguration eventConfiguration;

    /**
     * Construct a new quartz event.
     *
     * @param eventConfiguration The event configuration
     */
    public QuartzEvent(EventConfiguration eventConfiguration) {
        this.eventConfiguration = eventConfiguration;
    }

    /**
     * Get the event.
     *
     * @return The event configuration
     */
    public EventConfiguration getEventConfiguration() {
        return eventConfiguration;
    }

    /**
     * Get the handlers.
     *
     * Required by bukkit for proper event handling.
     *
     * @return The handlers
     */
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Get the handlers list.
     *
     * Required by bukkit for proper event handling.
     *
     * @return The handlers
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}