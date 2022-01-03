package darkhelmet.network.quartz;

import co.aikar.commands.PaperCommandManager;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.google.common.collect.ImmutableList;

import darkhelmet.network.quartz.commands.EventsCommand;
import darkhelmet.network.quartz.commands.IfEventCommand;
import darkhelmet.network.quartz.commands.QuartzCommand;
import darkhelmet.network.quartz.config.CommandConfiguration;
import darkhelmet.network.quartz.config.Config;
import darkhelmet.network.quartz.config.EventConfiguration;
import darkhelmet.network.quartz.config.LanguageConfiguration;
import darkhelmet.network.quartz.config.QuartzConfiguration;
import darkhelmet.network.quartz.config.ScheduleConfiguration;
import darkhelmet.network.quartz.jobs.QuartzCommandJob;
import darkhelmet.network.quartz.jobs.QuartzEndJob;
import darkhelmet.network.quartz.jobs.QuartzStartJob;
import darkhelmet.network.quartz.listeners.PlayerJoinListener;
import darkhelmet.network.quartz.storage.ConfigurationStorageAdapter;
import darkhelmet.network.quartz.storage.IStorageAdapter;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bukkit.Sound;
import org.bukkit.plugin.java.JavaPlugin;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.newJob;

public class Quartz extends JavaPlugin {
    /**
     * Cache static instance.
     */
    private static Quartz instance;

    /**
     * Define the plugin name for prefixing/logs.
     */
    public static final String PLUGIN_NAME = "Quartz";

    /**
     * The logger.
     */
    private static final Logger log = Logger.getLogger("Minecraft");

    /**
     * The cron parser.
     */
    private CronParser parser;

    /**
     * The quartz config.
     */
    private QuartzConfiguration quartzConfig;

    /**
     * The language config.
     */
    private LanguageConfiguration langConfig;

    /**
     * The scheduler.
     */
    private Scheduler scheduler;

    /**
     * The event configuration storage adapter.
     */
    private static IStorageAdapter storageAdapter;

    /**
     * Get this instance.
     *
     * @return The plugin instance
     */
    public static Quartz getInstance() {
        return instance;
    }

    /**
     * Constructor.
     */
    public Quartz() {
        instance = this;
    }

    /**
     * On Enable.
     */
    @Override
    public void onEnable() {
        log("Initializing " + PLUGIN_NAME + " " + this.getDescription().getVersion() + ". by viveleroi.");

        CronDefinition definition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
        parser = new CronParser(definition);

        // Load or create the quartz properties file
        Properties quartzProperties = null;
        try {
            quartzProperties = Config.loadOrCopyQuartzProperties(this);
        } catch (IOException e) {
            // Note: if this fails it will use the copy in the resources folder
            handleException(e);
        }

        // Load the plugin configuration
        loadConfigurations();

        if (isEnabled()) {
            // Initialize and configure the command system
            PaperCommandManager manager = new PaperCommandManager(this);
            manager.enableUnstableAPI("help");

            List<String> eventKeys = storageAdapter.getEnabledEvents().stream()
                .map(event -> event.key().toLowerCase()).toList();
            manager.getCommandCompletions().registerCompletion("eventKeys", c -> ImmutableList.copyOf(eventKeys));

            List<String> soundKeys = Arrays.stream(Sound.values())
                .map(sound -> sound.toString().toLowerCase()).collect(Collectors.toList());
            manager.getCommandCompletions().registerCompletion("sounds", c -> ImmutableList.copyOf(soundKeys));

            manager.registerCommand(new EventsCommand());
            manager.registerCommand(new IfEventCommand());
            manager.registerCommand(new QuartzCommand());

            try {
                SchedulerFactory schedulerFactory = new StdSchedulerFactory(quartzProperties);
                scheduler = schedulerFactory.getScheduler();

                loadSchedules();

                scheduler.start();
            } catch (SchedulerException e) {
                e.printStackTrace();
            }

            getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        }
    }

    /**
     * Reloads all configuration files.
     */
    public void loadConfigurations() {
        // Load the main config
        File quartzConfigFile = new File(getDataFolder(), "quartz.conf");
        quartzConfig = Config.getOrWriteConfiguration(QuartzConfiguration.class, quartzConfigFile);

        // Load the language file
        File langFile = new File(getDataFolder(), "lang/" + quartzConfig.language() + ".conf");
        langConfig = Config.getOrWriteConfiguration(LanguageConfiguration.class, langFile);

        storageAdapter = new ConfigurationStorageAdapter(quartzConfig);
    }

    /**
     * Get the cron parser.
     *
     * @return The cron parser
     */
    public CronParser cronParser() {
        return parser;
    }

    /**
     * Get the quartz configuration.
     *
     * @return The quartz configuration
     */
    public QuartzConfiguration quartzConfig() {
        return quartzConfig;
    }

    /**
     * Get the active language configuration.
     *
     * @return The lang configuration
     */
    public LanguageConfiguration langConfig() {
        return langConfig;
    }

    /**
     * Get the storage adapter.
     *
     * @return The storage adapter
     */
    public IStorageAdapter storageAdapter() {
        return storageAdapter;
    }

    /**
     * Clears all existing jobs and schedules jobs.
     */
    public void loadSchedules() {
        try {
            scheduler.clear();

            List<CommandConfiguration> enabledCommands = storageAdapter.getEnabledCommands();
            log(String.format("Loaded %d enabled commands.", enabledCommands.size()));

            for (CommandConfiguration command : enabledCommands) {
                scheduleJob(command);
            }

            List<EventConfiguration> enabledEvents = storageAdapter.getEnabledEvents();
            log(String.format("Loaded %d enabled events.", enabledEvents.size()));

            for (EventConfiguration event : enabledEvents) {
                for (ScheduleConfiguration schedule : event.getEnabledSchedules()) {
                    scheduleJob(event, schedule);
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private void scheduleJob(CommandConfiguration command) {
        String jobKey = command.command();
        ZonedDateTime now = ZonedDateTime.now();

        // Verify the schedule start date is in the future
        Cron starts = cronParser().parse(command.cron());
        Optional<ZonedDateTime> startExec = ExecutionTime.forCron(starts).nextExecution(now);
        if (startExec.isPresent()) {
            try {
                // Build the start job detail
                JobDetail commandJob = newJob(QuartzCommandJob.class)
                        .withIdentity("commandExec" + jobKey, "quartzGroup")
                        .usingJobData("command", command.command())
                        .build();

                // Build the start cron trigger
                CronTrigger commandTrigger = TriggerBuilder.newTrigger()
                        .withIdentity("commandCron" + jobKey, "quartzGroup")
                        .withSchedule(CronScheduleBuilder.cronSchedule(command.cron()))
                        .forJob("commandExec" + jobKey, "quartzGroup")
                        .build();

                scheduler.scheduleJob(commandJob, commandTrigger);
            } catch (SchedulerException e) {
                error("Error thrown while scheduling command: " + command);

                handleException(e);
            }
        } else {
            log("Skipping command due to an execution time with no future executions: " + command);
        }
    }

    /**
     * Schedule a job for the given event and schedule.
     *
     * @param event The event
     * @param schedule The schedule
     */
    private void scheduleJob(EventConfiguration event, ScheduleConfiguration schedule) {
        String jobKey = event.name() + schedule.starts() + schedule.ends();
        ZonedDateTime now = ZonedDateTime.now();

        // Verify the schedule start date is in the future
        Cron starts = cronParser().parse(schedule.starts());
        Optional<ZonedDateTime> startExec = ExecutionTime.forCron(starts).nextExecution(now);
        if (startExec.isPresent()) {
            try {
                // Build the start job detail
                JobDetail startJob = newJob(QuartzStartJob.class)
                    .withIdentity("eventStart" + jobKey, "quartzGroup")
                    .usingJobData("eventKey", event.key())
                    .build();

                // Build the start cron trigger
                CronTrigger startTrigger = TriggerBuilder.newTrigger()
                    .withIdentity("eventStartCron" + jobKey, "quartzGroup")
                    .withSchedule(CronScheduleBuilder.cronSchedule(schedule.starts()))
                    .forJob("eventStart" + jobKey, "quartzGroup")
                    .build();

                scheduler.scheduleJob(startJob, startTrigger);
            } catch (SchedulerException e) {
                error("Error thrown while scheduling start job for event schedule: " + schedule);

                handleException(e);
            }
        } else {
            log("Skipping schedule due to a start time with no future executions: " + schedule);
        }

        Cron ends = cronParser().parse(schedule.ends());
        Optional<ZonedDateTime> endExec = ExecutionTime.forCron(ends).nextExecution(now);
        if (endExec.isPresent()) {
            try {
                // Build the end job detail
                JobDetail endJob = newJob(QuartzEndJob.class)
                    .withIdentity("eventEnd" + jobKey, "quartzGroup")
                    .usingJobData("eventKey", event.key())
                    .build();

                // Build the end cron trigger
                CronTrigger endTrigger = TriggerBuilder.newTrigger()
                    .withIdentity("eventEndCron" + jobKey, "quartzGroup")
                    .withSchedule(CronScheduleBuilder.cronSchedule(schedule.ends()))
                    .forJob("eventEnd" + jobKey, "quartzGroup")
                    .build();

                scheduler.scheduleJob(endJob, endTrigger);
            } catch (SchedulerException e) {
                error("Error thrown while scheduling end job for event schedule: " + schedule);

                handleException(e);
            }
        } else {
            log("Skipping schedule due to an end time with no future executions: " + schedule);
        }
    }

    /**
     * Gets a list of all events with an active schedule.
     *
     * @return The list of active events
     */
    public List<EventConfiguration> getActiveEvents() {
        return storageAdapter().getEnabledEvents().stream()
            .filter(EventManager::isEventActive).collect(Collectors.toList());
    }

    /**
     * Log a message to console.
     *
     * @param message String
     */
    public static void log(String message) {
        log.info("[" + PLUGIN_NAME + "]: " + message);
    }

    /**
     * Log a message to console.
     *
     * @param message String
     */
    public static void error(String message) {
        log.warning("[" + PLUGIN_NAME + "]: " + message);
    }

    /**
     * Log a debug message to console.
     *
     * @param message String
     */
    public void debug(String message) {
        if (quartzConfig.debug()) {
            log.info("[" + PLUGIN_NAME + "]: " + message);
        }
    }

    /**
     * Handle exceptions.
     *
     * @param e The exception
     */
    public static void handleException(Exception e) {
        e.printStackTrace();
    }
}
