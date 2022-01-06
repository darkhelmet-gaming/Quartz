package darkhelmet.network.quartz.formatters;

import darkhelmet.network.quartz.config.EventConfiguration;

import java.util.List;

import me.clip.placeholderapi.PlaceholderAPI;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;

import org.bukkit.entity.Player;

public class DisplayFormatter {
    private DisplayFormatter() {}

    /**
     * Formats a message given an event.
     *
     * @param event The event
     * @param rawMessage The raw message
     * @return The component
     */
    public static Component format(EventConfiguration event, String rawMessage) {
        Template eventName = Template.of("eventName", event.name());
        Template eventDescription = Template.of("eventDescription", event.description());

        List<Template> templates = List.of(eventName, eventDescription);
        return MiniMessage.get().parse(rawMessage, templates);
    }

    /**
     * Formats a message given an event and player.
     *
     * @param player The player
     * @param event The event
     * @param rawMessage The raw message
     * @return The component
     */
    public static Component format(Player player, EventConfiguration event, String rawMessage) {
        rawMessage = PlaceholderAPI.setPlaceholders(player, rawMessage);

        return format(event, rawMessage);
    }
}
