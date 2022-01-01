package darkhelmet.network.quartz.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;

import darkhelmet.network.quartz.EventManager;
import darkhelmet.network.quartz.Quartz;
import darkhelmet.network.quartz.config.EventConfiguration;

import org.bukkit.command.CommandSender;

import java.util.Optional;

public class IfEventCommand extends BaseCommand {
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

        if (!Quartz.getInstance().getActiveEvents().contains(optionalEventConfig.get())) {
            return;
        }

        EventManager.runCommand(command);
    }
}
