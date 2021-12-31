package darkhelmet.network.quartz;

import darkhelmet.network.quartz.config.EventConfiguration;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;

import org.bukkit.entity.Player;

public class Formatter {
    private Formatter() {}

    public static Component format(EventConfiguration event, String rawMessage) {
        Template template = Template.of("eventName", event.name());

        return MiniMessage.get().parse(rawMessage, template);
    }

    public static Component format(Player player, EventConfiguration event, String rawMessage) {
        rawMessage = PlaceholderAPI.setPlaceholders(player, rawMessage);

        return format(event, rawMessage);
    }
}
