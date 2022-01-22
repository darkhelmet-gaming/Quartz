package network.darkhelmet.quartz.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;

import java.util.List;

import net.kyori.adventure.text.Component;

import network.darkhelmet.quartz.Quartz;
import network.darkhelmet.quartz.config.EventConfiguration;
import network.darkhelmet.quartz.formatters.I18l;

import org.bukkit.command.CommandSender;

@CommandAlias("events")
public class EventsCommand extends BaseCommand {
    /**
     * List active events.
     *
     * @param sender The command sender
     */
    @Subcommand("active")
    @CommandAlias("activeevents")
    @Description("Lists all currently active events")
    @CommandPermission("quartz.events.list.active")
    public void onActiveEvents(CommandSender sender) {
        List<EventConfiguration> events = Quartz.getInstance().getActiveEvents();

        if (events.isEmpty()) {
            Component message = Quartz.getInstance().messenger().error(I18l.lang().noActiveEvents);
            Quartz.getInstance().audiences().sender(sender).sendMessage(message);

            return;
        }

        Component message = Quartz.getInstance().messenger()
            .heading(I18l.lang().listHeading, I18l.lang().activeEvents);
        Quartz.getInstance().audiences().sender(sender).sendMessage(message);

        for (EventConfiguration event : events) {
            Component entryMessage = Quartz.getInstance().messenger().activeEventListEntry(event);
            Quartz.getInstance().audiences().sender(sender).sendMessage(entryMessage);
        }
    }

    /**
     * List enabled events.
     *
     * @param sender The command sender
     */
    @Default
    @Description("Lists all currently enabled events")
    @CommandPermission("quartz.events.list")
    public void onEvents(CommandSender sender) {
        List<EventConfiguration> events = Quartz.getInstance().quartzConfig().getEnabledEvents();

        if (events.isEmpty()) {
            Component message = Quartz.getInstance().messenger().error(I18l.lang().noEvents);
            Quartz.getInstance().audiences().sender(sender).sendMessage(message);

            return;
        }

        Component message = Quartz.getInstance().messenger()
            .heading(I18l.lang().listHeading, I18l.lang().events);

        for (EventConfiguration event : events) {
            Component entryMessage = Quartz.getInstance().messenger().eventListEntry(event);
            Quartz.getInstance().audiences().sender(sender).sendMessage(entryMessage);
        }
    }
}
