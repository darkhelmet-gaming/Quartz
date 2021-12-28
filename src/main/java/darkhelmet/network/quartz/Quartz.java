package darkhelmet.network.quartz;

import co.aikar.commands.PaperCommandManager;
import co.aikar.idb.DB;
import co.aikar.idb.Database;
import co.aikar.idb.DatabaseOptions;
import co.aikar.idb.DbRow;
import co.aikar.idb.PooledDatabaseOptions;
import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import darkhelmet.network.quartz.commands.EventCommand;
import darkhelmet.network.quartz.commands.QuartzCommand;
import darkhelmet.network.quartz.config.Config;
import darkhelmet.network.quartz.listeners.PlayerJoinListener;

import org.bukkit.configuration.file.FileConfiguration;
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
     * The config.
     */
    public FileConfiguration config;

    /**
     * The scheduler.
     */
    private Scheduler scheduler;

    /**
     * The task chain factory.
     */
    private static TaskChainFactory taskChainFactory;

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
        log("Initializing " + PLUGIN_NAME + " " + this.getDescription().getVersion() + ". by Viveleroi.");

        // Load the config
        final Config mc = new Config(this);
        config = mc.getConfig();

        String database = config.getString("quartz.mysql.database");
        String username = config.getString("quartz.mysql.username");
        String password = config.getString("quartz.mysql.password");
        String hostname = config.getString("quartz.mysql.hostname");
        hostname += ":" + config.getString("quartz.mysql.port");

        DatabaseOptions options = DatabaseOptions.builder().mysql(username, password, database, hostname).build();
        Database db = PooledDatabaseOptions.builder().options(options).createHikariDatabase();
        DB.setGlobalDatabase(db);

        if (isEnabled()) {
            taskChainFactory = BukkitTaskChainFactory.create(this);

            // Initialize and configure the command system
            PaperCommandManager manager = new PaperCommandManager(this);
            manager.enableUnstableAPI("help");

            manager.registerCommand(new EventCommand());
            manager.registerCommand(new QuartzCommand());

            try {
                SchedulerFactory schedulerFactory = new StdSchedulerFactory();
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
     * Clears all existing jobs and loads job data from the database.
     */
    public void loadSchedules() {
        try {
            scheduler.clear();

            String sql = "SELECT " +
                "schedule_id," +
                "title," +
                "command," +
                "start_subtitle," +
                "start_broadcast," +
                "end_subtitle," +
                "end_broadcast," +
                "starts," +
                "ends " +
                "FROM minecraft.quartz_schedules s " +
                "JOIN quartz_events e ON e.event_id = s.event_id " +
                "WHERE s.is_active = 1 AND e.is_active = 1 AND s.is_expired = 0";

            List<DbRow> rows = DB.getResults(sql);

            log.info(String.format("Loaded %d active events.", rows.size()));

            for (DbRow row : rows) {
                scheduleJob(row);
            }
        } catch (SchedulerException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Schedule a job from a DbRow
     *
     * @param row
     * @throws SQLException
     */
    private void scheduleJob(DbRow row) throws SQLException {
        int scheduleId = row.getInt("schedule_id");

        try {
            // Build the start job detail
            JobDetail startJob = newJob(QuartzStartJob.class)
                .withIdentity("eventStart" + scheduleId, "quartzGroup")
                .usingJobData("command", row.getString("command"))
                .usingJobData("title", row.getString("title"))
                .usingJobData("subtitle", row.getString("start_subtitle"))
                .usingJobData("broadcast", row.getString("start_broadcast"))
                .build();

            // Build the start cron trigger
            CronTrigger startTrigger = TriggerBuilder.newTrigger()
                .withIdentity("eventStartCron", "quartzGroup")
                .withSchedule(CronScheduleBuilder.cronSchedule(row.getString("starts")))
                .forJob("eventStart" + scheduleId, "quartzGroup")
                .build();

            scheduler.scheduleJob(startJob, startTrigger);
        } catch (SchedulerException e) {
            log("Ignoring erroring job, because the start might be in the past. Schedule id: " + row.getInt("schedule_id"));

            e.printStackTrace();
        }

        try {
            // Build the end job detail
            JobDetail endJob = newJob(QuartzEndJob.class)
                .withIdentity("eventEnd" + scheduleId, "quartzGroup")
                .usingJobData("schedule_id", scheduleId)
                .usingJobData("command", row.getString("command"))
                .usingJobData("title", row.getString("title"))
                .usingJobData("subtitle", row.getString("end_subtitle"))
                .usingJobData("broadcast", row.getString("end_broadcast"))
                .build();

            // Build the end cron trigger
            CronTrigger endTrigger = TriggerBuilder.newTrigger()
                .withIdentity("eventEndCron", "quartzGroup")
                .withSchedule(CronScheduleBuilder.cronSchedule(row.getString("ends")))
                .forJob("eventEnd" + scheduleId, "quartzGroup")
                .build();

            scheduler.scheduleJob(endJob, endTrigger);
        } catch (SchedulerException e) {
            DB.executeUpdate("UPDATE quartz_schedules SET is_expired = 1 WHERE schedule_id = ?",
                row.getInt("schedule_id"));

            log("Expiring erroring job. Schedule id: " + row.getInt("schedule_id"));

            e.printStackTrace();
        }
    }

    public List<Event> getActiveEvents() {
        List<Event> events = new ArrayList<>();

        try {
            String sql = "SELECT " +
                "schedule_id," +
                "title," +
                "starts," +
                "ends " +
                "FROM minecraft.quartz_schedules s " +
                "JOIN quartz_events e ON e.event_id = s.event_id " +
                "WHERE s.is_active = 1 AND e.is_active = 1 AND s.is_expired = 0";

            List<DbRow> rows = DB.getResults(sql);

            for (DbRow row : rows) {
                CronDefinition definition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
                CronParser parser = new CronParser(definition);
                Cron startCron = parser.parse(row.getString("starts"));
                Cron endCron = parser.parse(row.getString("ends"));

                ZonedDateTime now = ZonedDateTime.now();
                Optional<ZonedDateTime> lastStartExec = ExecutionTime.forCron(startCron).lastExecution(now);
                Optional<ZonedDateTime> lastEndExec = ExecutionTime.forCron(endCron).nextExecution(now);

                if (lastStartExec.isPresent() && lastEndExec.isPresent()) {
                    ZonedDateTime starts = lastStartExec.get();
                    ZonedDateTime ends = lastEndExec.get();

                    if (starts.isBefore(now) && ends.isAfter(now)) {
                        String title = row.getString("title");

                        events.add(new Event(title));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
}
