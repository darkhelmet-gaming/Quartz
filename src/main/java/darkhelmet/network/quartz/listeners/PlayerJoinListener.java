package darkhelmet.network.quartz.listeners;

import darkhelmet.network.quartz.EventManager;
import darkhelmet.network.quartz.Quartz;
import darkhelmet.network.quartz.config.EventConfiguration;

import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    /**
     * Listen to player join events.
     *
     * @param event The player join event
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        List<EventConfiguration> events = Quartz.getInstance().getActiveEvents();

        for (EventConfiguration activeEvent : events) {
            EventManager.active(activeEvent);
        }
    }
}
