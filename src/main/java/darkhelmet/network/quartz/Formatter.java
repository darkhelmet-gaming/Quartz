package darkhelmet.network.quartz;

import darkhelmet.network.quartz.config.EventConfiguration;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;

import org.bukkit.entity.Player;

import java.util.List;

public class Formatter {
    private Formatter() {}

    public static Component format(EventConfiguration event, String rawMessage) {
        Template eventName = Template.of("eventName", event.name());
        Template eventDescription = Template.of("eventDescription", event.description());

        List<Template> templates = List.of(eventName, eventDescription);
        return MiniMessage.get().parse(rawMessage, templates);
    }

    public static Component format(Player player, EventConfiguration event, String rawMessage) {
        rawMessage = PlaceholderAPI.setPlaceholders(player, rawMessage);

        return format(event, rawMessage);
    }
}
