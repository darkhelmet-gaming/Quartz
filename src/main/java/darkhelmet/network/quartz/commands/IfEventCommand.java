package darkhelmet.network.quartz.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Values;

import darkhelmet.network.quartz.EventManager;
import darkhelmet.network.quartz.Quartz;
import darkhelmet.network.quartz.config.EventConfiguration;

import java.util.Optional;

import org.bukkit.command.CommandSender;

public class IfEventCommand extends BaseCommand {
    /**
     * Conditionally run a provided command if an event is active.
     *
     * @param sender The sender
     * @param eventKey The event key
     * @param command The command
     */
    @CommandAlias("ifevent")
    @CommandPermission("quartz.admin")
    @CommandCompletion("@eventKeys")
    @Description("Run a command ONLY if the event with the given key is active.")
    public void onIfEvent(CommandSender sender, @Values("@eventKeys") String eventKey, String command) {
        Optional<EventConfiguration> optionalEventConfig = Quartz.getInstance().storageAdapter().getEvent(eventKey);
        if (optionalEventConfig.isEmpty()) {
            Quartz.error(String.format("Failed to find event [%s] during ifevent use.", eventKey));

            return;
        }

        if (!EventManager.isEventActive(optionalEventConfig.get())) {
            return;
        }

        EventManager.runCommand(command);
    }
}
