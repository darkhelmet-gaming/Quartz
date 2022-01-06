package darkhelmet.network.quartz.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;

import darkhelmet.network.quartz.Quartz;
import darkhelmet.network.quartz.config.EventConfiguration;
import darkhelmet.network.quartz.formatters.I18l;

import java.util.List;

import org.bukkit.command.CommandSender;

@CommandAlias("events")
public class EventsCommand extends BaseCommand {
    /**
     * List active events.
     *
     * @param sender The command sender
     */
    @Subcommand("active")
    @Description("Lists all currently active events")
    @CommandPermission("quartz.events.active")
    public void onActiveEvents(CommandSender sender) {
        List<EventConfiguration> events = Quartz.getInstance().getActiveEvents();

        if (events.isEmpty()) {
            sender.sendMessage(Quartz.getInstance().messenger().error(I18l.lang().noActiveEvents));

            return;
        }

        sender.sendMessage(Quartz.getInstance().messenger()
            .heading(I18l.lang().listHeading, I18l.lang().activeEvents));

        for (EventConfiguration event : events) {
            sender.sendMessage(Quartz.getInstance().messenger().activeEventListEntry(event));
        }
    }
}
