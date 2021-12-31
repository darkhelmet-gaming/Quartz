package darkhelmet.network.quartz;

import darkhelmet.network.quartz.config.*;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.title.Title;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {
    private EventManager() {}

    public static boolean isEventActive(EventConfiguration event) {
        return Quartz.getInstance().eventStateConfig().activeEvents.contains(event.key());
    }

    private static Map<String, DisplayConfiguration> getDisplays(EventConfiguration event, String phaseKey) {
        QuartzConfiguration config = Quartz.getInstance().quartzConfig();

        // First, identify which displays this phase uses
        PhaseConfiguration phase = event.start();
        if (phaseKey.equalsIgnoreCase("end")) {
            phase = event.end();
        }

        List<String> displayKeys = phase.useDisplays();

        // Start building a list of all displays
        Map<String, DisplayConfiguration> displays = new HashMap<>();

        // First, check for event-specific display formats
        for (DisplayConfiguration eventDisplay : event.getDisplaysForPhase(phaseKey)) {
            if (displayKeys.contains(eventDisplay.type()) && eventDisplay.enabled()) {
                displays.put(eventDisplay.type(), eventDisplay);
            }
        }

        // Second, merge in generic display configs
        for (DisplayConfiguration globalDisplay : config.getDisplaysForPhase(phaseKey)) {
            if (displayKeys.contains(globalDisplay.type()) && globalDisplay.enabled() && !displays.containsKey(globalDisplay.type())) {
                displays.put(globalDisplay.type(), globalDisplay);
            }
        }

        if (displayKeys.isEmpty()) {
            Quartz.getInstance().debug(String.format("No displays used for phase [%s] of event [%s]",
                phaseKey,
                event.name()));
        }

        for (String displayKey : displayKeys) {
            if (displays.containsKey(displayKey)) {
                Quartz.getInstance().debug(String.format("Using valid, enabled display for event [%s]: %s",
                    event.name(),
                    displayKey));
            } else {
                Quartz.getInstance().debug(String.format("Event [%s] wants a display of type [%s] but it's " +
                    "either not enabled or doesn't exist.",
                    event.name(),
                    displayKey));
            }
        }

        return displays;
    }

    public static void simulateStart(EventConfiguration event) {
        showDisplays(event, "start");
    }

    public static void simulateEnd(EventConfiguration event) {
        showDisplays(event, "end");
    }

    public static boolean start(EventConfiguration event) {
        if (!isEventActive(event)) {
            Quartz.getInstance().eventStateConfig().activeEvents.add(event.key());

            showDisplays(event, "start");
            runCommands(event, "start");

            Config.saveEventStateConfiguration(Quartz.getInstance(), Quartz.getInstance().eventStateConfig());

            return true;
        }

        return false;
    }

    public static boolean end(EventConfiguration event) {
        if (isEventActive(event)) {
            Quartz.getInstance().eventStateConfig().activeEvents.remove(event.key());

            showDisplays(event, "end");
            runCommands(event, "end");

            Config.saveEventStateConfiguration(Quartz.getInstance(), Quartz.getInstance().eventStateConfig());

            return true;
        }

        return false;
    }

    private static void runCommands(EventConfiguration event, String phase) {
        for (String command : event.getPhase(phase).commands()) {
            Quartz.getInstance().debug(String.format("Running command for event [%s]: %s",
                event.name(),
                command));

            // Events via the job scheduler are async, so we need to execute commands on the game thread
            Bukkit.getScheduler().runTask(Quartz.getInstance(), () -> {
                if (command.contains("%")) {
                    // If commands contain placeholders, run for every player
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (event.permission().equalsIgnoreCase("false") || player.hasPermission(event.permission())) {
                            String parsedCommand = PlaceholderAPI.setPlaceholders(player, command);
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), parsedCommand);
                        }
                    }
                } else {
                    // Otherwise just run as a server command
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            });
        }
    }

    public static void showDisplays(EventConfiguration event, String phase) {
        Map<String, DisplayConfiguration> displays = getDisplays(event, phase);

        if (displays.containsKey("title")) {
            showTitle(event, displays.get("title"));
        }

        if (displays.containsKey("chat")) {
            showChatMessage(event, displays.get("chat"));
        }
    }

    private static void showChatMessage(EventConfiguration event, DisplayConfiguration display) {
        if (display.templates().size() == 1) {
            // Build replacement templates
            Template template = Template.of("eventName", event.name());

            String rawMessage = display.templates().get(0);
            Component message = MiniMessage.get().parse(rawMessage, template);

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (event.permission().equalsIgnoreCase("false") || player.hasPermission(event.permission())) {
                    player.sendMessage(message);
                }
            }
        }
    }

    private static void showTitle(EventConfiguration event, DisplayConfiguration display) {
        String titleStr = "";
        String subtitleStr = "";

        if (display.templates().size() == 1) {
            titleStr = display.templates().get(0);
        }

        if (display.templates().size() == 2) {
            subtitleStr = display.templates().get(1);
        }

        // Build replacement templates
        Template template = Template.of("eventName", event.name());

        // Build the title/components
        Component mainTitle = MiniMessage.get().parse(titleStr, template);
        Component subTitle = MiniMessage.get().parse(subtitleStr, template);
        Title title = Title.title(mainTitle, subTitle);

        // Display for appropriate players
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (event.permission().equalsIgnoreCase("false") || player.hasPermission(event.permission())) {
                player.showTitle(title);
            }
        }
    }
}
