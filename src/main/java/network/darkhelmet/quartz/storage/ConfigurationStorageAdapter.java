package network.darkhelmet.quartz.storage;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import network.darkhelmet.quartz.Quartz;
import network.darkhelmet.quartz.config.CommandConfiguration;
import network.darkhelmet.quartz.config.Config;
import network.darkhelmet.quartz.config.EventConfiguration;
import network.darkhelmet.quartz.config.EventStateConfiguration;
import network.darkhelmet.quartz.config.QuartzConfiguration;

public class ConfigurationStorageAdapter implements IStorageAdapter {
    /**
     * Cache of all commands.
     */
    private final List<CommandConfiguration> commands;

    /**
     * Cache of all events.
     */
    private final List<EventConfiguration> events;

    /**
     * The event state config.
     */
    private EventStateConfiguration eventStateConfig;

    /**
     * File we read/write event state from/to.
     */
    private File eventStateFile;

    /**
     * Construct a new config storage adapter.
     *
     * @param config The quartz configuration
     */
    public ConfigurationStorageAdapter(QuartzConfiguration config) {
        commands = config.commands();
        events = config.events();

        eventStateFile = new File(Quartz.getInstance().getDataFolder(), "cache/event_state.conf");
        eventStateConfig = Config.getOrWriteConfiguration(EventStateConfiguration.class, eventStateFile);
    }

    @Override
    public List<CommandConfiguration> getEnabledCommands() {
        return commands.stream().filter(CommandConfiguration::enabled).collect(Collectors.toList());
    }

    @Override
    public List<EventConfiguration> getEnabledEvents() {
        return events.stream().filter(EventConfiguration::enabled).collect(Collectors.toList());
    }

    @Override
    public Optional<EventConfiguration> getEvent(String key) {
        return events.stream().filter(event -> event.key().equalsIgnoreCase(key)).findFirst();
    }

    @Override
    public EventStateConfiguration getState() {
        return eventStateConfig;
    }

    @Override
    public void saveState() {
        Config.getOrWriteConfiguration(EventStateConfiguration.class, eventStateFile, eventStateConfig);
    }
}
