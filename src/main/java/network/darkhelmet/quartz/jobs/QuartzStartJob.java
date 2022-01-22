package network.darkhelmet.quartz.jobs;

import java.util.Optional;

import network.darkhelmet.quartz.EventManager;
import network.darkhelmet.quartz.Quartz;
import network.darkhelmet.quartz.config.EventConfiguration;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

public class QuartzStartJob implements Job {
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

            EventManager.start(event);
        }
    }
}