package darkhelmet.network.quartz.jobs;

import darkhelmet.network.quartz.EventManager;
import darkhelmet.network.quartz.Quartz;
import darkhelmet.network.quartz.config.EventConfiguration;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.Optional;

public class QuartzStartJob implements Job {
    public void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        Optional<EventConfiguration> optionalEvent = Quartz.getInstance().storageAdapter().getEvent(dataMap.getString("eventKey"));
        if (optionalEvent.isPresent()) {
            EventConfiguration event = optionalEvent.get();

            EventManager.start(event);
        }
    }
}