package darkhelmet.network.quartz;

import darkhelmet.network.quartz.config.EventConfiguration;

import darkhelmet.network.quartz.config.QuartzConfiguration;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.Optional;

public class QuartzEndJob implements Job {
    public void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        Optional<EventConfiguration> optionalEvent = Quartz.getInstance().storageAdapter().getEvent(dataMap.getString("eventName"));
        if (optionalEvent.isPresent()) {
            QuartzConfiguration config = Quartz.getInstance().quartzConfig();
            EventConfiguration event = optionalEvent.get();

            EventManager.end(event);
        }
    }
}