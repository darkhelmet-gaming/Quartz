package darkhelmet.network.quartz.events;

import darkhelmet.network.quartz.config.EventConfiguration;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class QuartzEvent extends Event {
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
     * <p>Required by bukkit for proper event handling.</p>
     *
     * @return The handlers
     */
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Get the handlers list.
     *
     * <p>Required by bukkit for proper event handling.</p>
     *
     * @return The handlers
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}