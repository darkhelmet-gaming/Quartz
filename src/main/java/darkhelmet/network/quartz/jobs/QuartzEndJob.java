package darkhelmet.network.quartz.jobs;

import darkhelmet.network.quartz.EventManager;
import darkhelmet.network.quartz.Quartz;
import darkhelmet.network.quartz.config.EventConfiguration;

import java.util.Optional;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

public class QuartzEndJob implements Job {
    /**
     * Execute this job.
     *
     * @param context The context
     */
    public void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        Optional<EventConfiguration> optionalEvent = Quartz.getInstance().storageAdapter()
            .getEvent(dataMap.getString("eventKey"));
        if (optionalEvent.isPresent()) {
            EventConfiguration event = optionalEvent.get();

            EventManager.end(event);
        }
    }
}