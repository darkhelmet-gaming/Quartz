package darkhelmet.network.quartz.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Values;

import darkhelmet.network.quartz.EventManager;
import darkhelmet.network.quartz.EventPhase;
import darkhelmet.network.quartz.I18l;
import darkhelmet.network.quartz.Messenger;
import darkhelmet.network.quartz.Quartz;
import darkhelmet.network.quartz.config.EventConfiguration;

import java.util.Optional;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("quartz")
public class QuartzCommand extends BaseCommand {
    /**
     * Reload configurations.
     *
     * @param sender The command sender
     */
    @Subcommand("reload")
    @CommandPermission("quartz.admin")
    @Description("Reloads the configuration and events (and regenerates all event tasks)")
    public void onReload(CommandSender sender) {
        // Reloads configuration files
        Quartz.getInstance().loadConfigurations();

        // Reload all schedules
        Quartz.getInstance().loadSchedules();

        // Send message
        sender.sendMessage(Messenger.success(I18l.lang().reloadedEventsAndSchedules));
    }

    /**
     * Force start an event.
     *
     * @param sender The command sender
     * @param eventKey The event key
     */
    @Subcommand("forcestart")
    @CommandCompletion("@eventKeys")
    @CommandPermission("quartz.admin")
    @Description("Forces an event to start (does not alter configured/saved data)")
    public void onForceStart(CommandSender sender, @Values("@eventKeys") String eventKey) {
        Optional<EventConfiguration> optionalEventConfig = Quartz.getInstance().storageAdapter().getEvent(eventKey);

        if (optionalEventConfig.isPresent()) {
            EventConfiguration event = optionalEventConfig.get();
            EventManager.start(event);

            sender.sendMessage(Messenger.success(String.format(I18l.lang().forceStartingEvent, event.name())));
        }
    }

    /**
     * Force end an event.
     *
     * @param sender The command sender
     * @param eventKey The event key
     */
    @Subcommand("forceend")
    @CommandCompletion("@eventKeys")
    @CommandPermission("quartz.admin")
    @Description("Forces an event to end (does not alter configured/saved data)")
    public void onForceEnd(CommandSender sender, @Values("@eventKeys") String eventKey) {
        Optional<EventConfiguration> optionalEventConfig = Quartz.getInstance().storageAdapter().getEvent(eventKey);

        if (optionalEventConfig.isPresent()) {
            EventConfiguration event = optionalEventConfig.get();
            EventManager.end(event);

            sender.sendMessage(Messenger.message(String.format(I18l.lang().forceEndingEvent, event.name())));
        }
    }

    /**
     * Simulate start phase of an event.
     *
     * @param sender The command sender
     * @param eventKey The event key
     */
    @Subcommand("simulatestart")
    @CommandCompletion("@eventKeys")
    @CommandPermission("quartz.admin")
    @Description("Activates displays as if event was starting.")
    public void onSimulateStart(CommandSender sender, @Values("@eventKeys") String eventKey) {
        Optional<EventConfiguration> optionalEventConfig = Quartz.getInstance().storageAdapter().getEvent(eventKey);

        if (optionalEventConfig.isPresent()) {
            EventConfiguration event = optionalEventConfig.get();
            EventManager.simulatePhase(event, EventPhase.START);

            sender.sendMessage(Messenger.message(String.format(I18l.lang().simulatingEventStart, event.name())));
        }
    }

    /**
     * Simulate active phase of an event.
     *
     * @param sender The command sender
     * @param eventKey The event key
     */
    @Subcommand("simulateactive")
    @CommandCompletion("@eventKeys")
    @CommandPermission("quartz.admin")
    @Description("Activates displays as if event was starting.")
    public void onSimulateActive(CommandSender sender, @Values("@eventKeys") String eventKey) {
        Optional<EventConfiguration> optionalEventConfig = Quartz.getInstance().storageAdapter().getEvent(eventKey);

        if (optionalEventConfig.isPresent()) {
            EventConfiguration event = optionalEventConfig.get();
            EventManager.simulatePhase(event, EventPhase.ACTIVE);

            sender.sendMessage(Messenger.message(String.format(I18l.lang().simulatingEventActive, event.name())));
        }
    }

    /**
     * Simulate end phase of an event.
     *
     * @param sender The command sender
     * @param eventKey The event key
     */
    @Subcommand("simulateend")
    @CommandCompletion("@eventKeys")
    @CommandPermission("quartz.admin")
    @Description("Activates displays as if event was ending.")
    public void onSimulateEnd(CommandSender sender, @Values("@eventKeys") String eventKey) {
        Optional<EventConfiguration> optionalEventConfig = Quartz.getInstance().storageAdapter().getEvent(eventKey);

        if (optionalEventConfig.isPresent()) {
            EventConfiguration event = optionalEventConfig.get();
            EventManager.simulatePhase(event, EventPhase.END);

            sender.sendMessage(Messenger.message(String.format(I18l.lang().simulatingEventEnd, event.name())));
        }
    }

    @Subcommand("testsound")
    @CommandCompletion("@sounds")
    @CommandPermission("quartz.admin")
    @Description("Plays a sound.")
    public void onTestSound(Player player, @Values("@sounds") String soundKey) {
        Sound sound = Sound.valueOf(soundKey.toUpperCase());
        player.playSound(player.getLocation(), sound, 1, 1);
    }
}
