package network.darkhelmet.quartz.jobs;

import network.darkhelmet.quartz.EventManager;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

public class QuartzCommandJob implements Job {
    /**
     * Execute this job.
     *
     * @param context The context
     */
    public void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        String command = dataMap.getString("command");
        if (command != null) {
            EventManager.runCommand(command);
        }
    }
}