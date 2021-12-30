package darkhelmet.network.quartz;

import co.aikar.commands.PaperCommandManager;
import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableList;
import darkhelmet.network.quartz.commands.EventCommand;
import darkhelmet.network.quartz.commands.QuartzCommand;
import darkhelmet.network.quartz.config.*;
import darkhelmet.network.quartz.listeners.PlayerJoinListener;
import darkhelmet.network.quartz.storage.ConfigurationStorageAdapter;
import darkhelmet.network.quartz.storage.IStorageAdapter;

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
     * The task chain factory.
     */
    private static TaskChainFactory taskChainFactory;

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
            taskChainFactory = BukkitTaskChainFactory.create(this);

            // Initialize and configure the command system
            PaperCommandManager manager = new PaperCommandManager(this);
            manager.enableUnstableAPI("help");

            List<String> eventKeys = storageAdapter.getEnabledEvents().stream().map(event -> event.key().toLowerCase()).toList();
            manager.getCommandCompletions().registerCompletion("eventKeys", c -> ImmutableList.copyOf(eventKeys));

            manager.registerCommand(new EventCommand());
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
        quartzConfig = Config.getOrCreateQuartzConfiguration(this);
        langConfig = Config.getOrCreateLanguageConfiguration(this, quartzConfig.language());
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
                    .usingJobData("eventName", event.name())
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
                    .usingJobData("eventName", event.name())
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

    public List<EventConfiguration> getActiveEvents() {
        List<EventConfiguration> events = new ArrayList<>();

//        try {
//            String sql = "SELECT " +
//                "schedule_id," +
//                "title," +
//                "starts," +
//                "ends " +
//                "FROM minecraft.quartz_schedules s " +
//                "JOIN quartz_events e ON e.event_id = s.event_id " +
//                "WHERE s.is_active = 1 AND e.is_active = 1 AND s.is_expired = 0";
//
//            List<DbRow> rows = DB.getResults(sql);
//
//            for (DbRow row : rows) {

//                Cron startCron = parser.parse(row.getString("starts"));
//                Cron endCron = parser.parse(row.getString("ends"));
//
//                ZonedDateTime now = ZonedDateTime.now();
//                Optional<ZonedDateTime> lastStartExec = ExecutionTime.forCron(startCron).lastExecution(now);
//                Optional<ZonedDateTime> lastEndExec = ExecutionTime.forCron(endCron).nextExecution(now);
//
//                if (lastStartExec.isPresent() && lastEndExec.isPresent()) {
//                    ZonedDateTime starts = lastStartExec.get();
//                    ZonedDateTime ends = lastEndExec.get();
//
//                    if (starts.isBefore(now) && ends.isAfter(now)) {
//                        String title = row.getString("title");
//
//                        events.add(new Event(title));
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        return events;
    }

    /**
     * Create a new task chain.
     *
     * @param <T> Type
     * @return The chain
     */
    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
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
