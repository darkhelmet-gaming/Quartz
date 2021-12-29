package darkhelmet.network.quartz;

import darkhelmet.network.quartz.config.*;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {
    private EventManager() {}

    private static Map<String, DisplayConfiguration> getDisplays(EventConfiguration event, String phaseKey) {
        QuartzConfiguration config = Quartz.getInstance().getQuartzConfig();

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

    public static void start(EventConfiguration event) {
        showDisplays(event, "start");
        runCommands(event, "start");
    }

    public static void end(EventConfiguration event) {
        showDisplays(event, "end");
        runCommands(event, "end");
    }

    private static void runCommands(EventConfiguration event, String phase) {
        Bukkit.getScheduler().runTask(Quartz.getInstance(), () -> {
            List<String> commands = event.start().commands();
            if (phase.equalsIgnoreCase("end")) {
                commands = event.end().commands();
            }

            for (String command : commands) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        });
    }

    private static void showDisplays(EventConfiguration event, String phase) {
        // Displays
        Map<String, DisplayConfiguration> displays = getDisplays(event, phase);

        // For each display type, display it!
        if (displays.containsKey("title")) {
            DisplayConfiguration titleConfig = displays.get("title");

            String titleStr = "";
            String subtitleStr = "";

            if (titleConfig.templates().size() == 1) {
                titleStr = titleConfig.templates().get(0);
            }

            if (titleConfig.templates().size() == 2) {
                subtitleStr = titleConfig.templates().get(1);
            }

            Component mainTitle = LegacyComponentSerializer.legacy('&').deserialize(titleStr);
            Component subTitle = LegacyComponentSerializer.legacy('&').deserialize(subtitleStr);
            Title title = Title.title(mainTitle, subTitle);

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (titleConfig.permission().equalsIgnoreCase("false") || player.hasPermission(titleConfig.permission())) {
                    player.showTitle(title);
                }
            }
        }

        if (displays.containsKey("broadcast")) {
            DisplayConfiguration broadcastConfig = displays.get("broadcast");
            if (broadcastConfig.templates().size() == 1) {
                String template = broadcastConfig.templates().get(0);

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (broadcastConfig.permission().equalsIgnoreCase("false") || player.hasPermission(broadcastConfig.permission())) {
                        player.sendMessage(template);
                    }
                }
            }
        }
    }
}
