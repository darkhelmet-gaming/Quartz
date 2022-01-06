package darkhelmet.network.quartz;

import darkhelmet.network.quartz.config.DisplayConfiguration;
import darkhelmet.network.quartz.config.EventConfiguration;
import darkhelmet.network.quartz.config.PhaseConfiguration;
import darkhelmet.network.quartz.config.QuartzConfiguration;
import darkhelmet.network.quartz.events.QuartzEventEnd;
import darkhelmet.network.quartz.events.QuartzEventStart;
import darkhelmet.network.quartz.formatters.DisplayFormatter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.clip.placeholderapi.PlaceholderAPI;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EventManager {
    private EventManager() {}

    /**
     * Check if an event is currently active.
     *
     * @param event The event
     * @return If active
     */
    public static boolean isEventActive(EventConfiguration event) {
        return Quartz.getInstance().storageAdapter().getState().activeEvents.contains(event.key());
    }

    /**
     * Get a final map of displays for an event/phase.
     *
     * @param event The event
     * @param phase The phase
     * @return A map of displays
     */
    private static Map<String, DisplayConfiguration> getDisplays(EventConfiguration event, EventPhase phase) {
        QuartzConfiguration config = Quartz.getInstance().quartzConfig();

        // First, identify which displays this phase uses
        PhaseConfiguration phaseConfig = event.getPhase(phase);
        List<String> displayKeys = phaseConfig.useDisplays();

        // Start building a list of all displays
        Map<String, DisplayConfiguration> displays = new HashMap<>();

        // First, check for event-specific display formats
        for (DisplayConfiguration eventDisplay : event.getDisplaysForPhase(phase)) {
            if (eventDisplay.enabled()) {
                for (String type : eventDisplay.types()) {
                    if (displayKeys.contains(type)) {
                        displays.put(type, eventDisplay);
                    }
                }
            }
        }

        // Second, merge in generic display configs
        for (DisplayConfiguration globalDisplay : config.getDisplaysForPhase(phase)) {
            if (globalDisplay.enabled()) {
                for (String type : globalDisplay.types()) {
                    if (displayKeys.contains(type) && globalDisplay.enabled() && !displays.containsKey(type)) {
                        displays.put(type, globalDisplay);
                    }
                }
            }
        }

        if (displayKeys.isEmpty()) {
            Quartz.getInstance().debug(String.format("No displays used for phase [%s] of event [%s]",
                phase,
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

    /**
     * Show displays/play sounds on for a specific event/phase. Does not active the event or run commands.
     *
     * @param event The event
     * @param phase The phase
     */
    public static void simulatePhase(EventConfiguration event, EventPhase phase) {
        showDisplays(event, phase);
        playSounds(event, phase);
    }

    /**
     * Runs all "active" phase displays/commands for an event.
     *
     * @param event The event
     * @return Whether the event is active
     */
    public static boolean active(EventConfiguration event) {
        if (isEventActive(event)) {
            showDisplays(event, EventPhase.ACTIVE);
            runCommands(event, EventPhase.ACTIVE);
            playSounds(event, EventPhase.ACTIVE);

            return true;
        }

        return false;
    }

    /**
     * Runs all "start" phase displays/commands for an event.
     *
     * @param event Tne event
     * @return Whether the event can start
     */
    public static boolean start(EventConfiguration event) {
        if (!isEventActive(event)) {
            Quartz.getInstance().storageAdapter().getState().activeEvents.add(event.key());

            showDisplays(event, EventPhase.START);
            runCommands(event, EventPhase.START);
            playSounds(event, EventPhase.START);

            Quartz.getInstance().storageAdapter().saveState();

            QuartzEventStart bukkitEvent = new QuartzEventStart(event);
            Bukkit.getServer().getPluginManager().callEvent(bukkitEvent);

            return true;
        }

        return false;
    }

    /**
     * Runs all "end" phase displays/commands for an event.
     *
     * @param event Tne event
     * @return Whether the event can end
     */
    public static boolean end(EventConfiguration event) {
        if (isEventActive(event)) {
            Quartz.getInstance().storageAdapter().getState().activeEvents.remove(event.key());

            showDisplays(event, EventPhase.END);
            runCommands(event, EventPhase.END);
            playSounds(event, EventPhase.END);

            Quartz.getInstance().storageAdapter().saveState();

            QuartzEventEnd bukkitEvent = new QuartzEventEnd(event);
            Bukkit.getServer().getPluginManager().callEvent(bukkitEvent);

            return true;
        }

        return false;
    }

    /**
     * Runs all commands for event/phase.
     *
     * @param event The event
     * @param phase The phase
     */
    private static void runCommands(EventConfiguration event, EventPhase phase) {
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

    /**
     * Runs a command.
     *
     * @param command The command
     */
    public static void runCommand(String command) {
        Quartz.getInstance().debug(String.format("Running command: %s", command));

        // Events via the job scheduler are async, so we need to execute commands on the game thread
        Bukkit.getScheduler().runTask(Quartz.getInstance(), () -> {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
        });
    }

    /**
     * Show displays for the given event/phase.
     *
     * @param event The event
     * @param phase The phase
     */
    public static void showDisplays(EventConfiguration event, EventPhase phase) {
        Map<String, DisplayConfiguration> displays = getDisplays(event, phase);

        if (displays.containsKey("title")) {
            showTitle(event, displays.get("title"));
        }

        if (displays.containsKey("chat")) {
            showChatMessage(event, displays.get("chat"));
        }
    }

    /**
     * Play sounds for an event/phase.
     *
     * @param event The event
     * @param phase The phase
     */
    public static void playSounds(EventConfiguration event, EventPhase phase) {
        PhaseConfiguration phaseConfig = event.getPhase(phase);

        if (phaseConfig.sound() != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (event.permission().equalsIgnoreCase("false") || player.hasPermission(event.permission())) {
                    player.playSound(player.getLocation(), phaseConfig.sound(), 1, 1);
                }
            }
        }
    }

    /**
     * Show a chat message for an event.
     *
     * @param event The event
     * @param display The display
     */
    private static void showChatMessage(EventConfiguration event, DisplayConfiguration display) {
        if (!display.templates().isEmpty()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (event.permission().equalsIgnoreCase("false") || player.hasPermission(event.permission())) {
                    for (String rawMessage : display.templates()) {
                        player.sendMessage(DisplayFormatter.format(player, event, rawMessage));
                    }
                }
            }
        }
    }

    /**
     * Show a title/subtitle for an event.
     *
     * @param event The event
     * @param display The display
     */
    private static void showTitle(EventConfiguration event, DisplayConfiguration display) {
        String titleStr = "";
        String subtitleStr = "";

        if (display.templates().size() >= 1) {
            titleStr = display.templates().get(0);
        }

        if (display.templates().size() == 2) {
            subtitleStr = display.templates().get(1);
        }

        // Display for appropriate players
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (event.permission().equalsIgnoreCase("false") || player.hasPermission(event.permission())) {
                // Build the title/components
                Component mainTitle = DisplayFormatter.format(player, event, titleStr);
                Component subTitle = DisplayFormatter.format(player, event, subtitleStr);
                Title title = Title.title(mainTitle, subTitle);

                player.showTitle(title);
            }
        }
    }
}
