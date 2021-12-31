package darkhelmet.network.quartz.listeners;

import darkhelmet.network.quartz.EventManager;
import darkhelmet.network.quartz.config.EventConfiguration;
import darkhelmet.network.quartz.Quartz;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class PlayerJoinListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Quartz.newChain().async(() -> {
            List<EventConfiguration> events = Quartz.getInstance().getActiveEvents();

            for (EventConfiguration activeEvent : events) {
                EventManager.showDisplays(activeEvent, "active");
            }
        }).execute();
    }
}
