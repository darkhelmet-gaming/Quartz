package darkhelmet.network.quartz.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;

import darkhelmet.network.quartz.I18l;
import darkhelmet.network.quartz.Messenger;
import darkhelmet.network.quartz.Quartz;
import darkhelmet.network.quartz.config.EventConfiguration;

import org.bukkit.command.CommandSender;

import java.util.List;

@CommandAlias("events")
public class EventsCommand extends BaseCommand {
    @Subcommand("active")
    @Description("Lists all currently active events")
    @CommandPermission("quartz.events.active")
    public void onActiveEvents(CommandSender sender) {
        List<EventConfiguration> events = Quartz.getInstance().getActiveEvents();

        if (events.isEmpty()) {
            sender.sendMessage(Messenger.error(I18l.lang().noActiveEvents));

            return;
        }

        sender.sendMessage(Messenger.message(String.format(I18l.lang().listHeading, I18l.lang().activeEvents)));

        for (EventConfiguration event : events) {
            sender.sendMessage(event.name());
        }
    }
}
