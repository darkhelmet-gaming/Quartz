package darkhelmet.network.quartz.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;

import java.util.List;

import darkhelmet.network.quartz.Event;
import darkhelmet.network.quartz.Messenger;
import darkhelmet.network.quartz.Quartz;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.command.CommandSender;

@CommandAlias("event")
public class EventCommand extends BaseCommand {
    public void onEvent(CommandSender sender) {
        Quartz.newChain().async(() -> {
            List<Event> events = Quartz.getInstance().getActiveEvents();

            if (events.isEmpty()) {
                sender.sendMessage(Messenger.prefix()
                    .append(Component.text("There are no active events.", NamedTextColor.RED)));

                return;
            }

            for (Event event : events) {
                sender.sendMessage(Messenger.message("Active Event: " + event.title()));
            }
        }).execute();
    }
}
