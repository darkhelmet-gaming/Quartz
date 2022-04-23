package network.darkhelmet.quartz.formatters;

import me.clip.placeholderapi.PlaceholderAPI;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import network.darkhelmet.quartz.config.EventConfiguration;

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
        TagResolver.Single eventName = Placeholder.parsed("eventName", event.name());
        TagResolver.Single eventDescription = Placeholder.parsed("eventDescription", event.description());

        TagResolver placeholders = TagResolver.resolver(eventName, eventDescription);

        return MiniMessage.miniMessage().deserialize(rawMessage, placeholders);
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
