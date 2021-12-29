package darkhelmet.network.quartz;

import darkhelmet.network.quartz.models.Event;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Optional;

public class QuartzStartJob implements Job {
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        Optional<Event> event = Quartz.getInstance().getStorageAdapter().getEvent(dataMap.getString("eventName"));
        if (event.isPresent()) {
            Quartz.log("ACTIVING EVENT " + event);
        }

//        String titleStr = dataMap.getString("title");
//        String subtitleStr = dataMap.getString("subtitle");
//        String command = dataMap.getString("command");
//
//        Bukkit.getScheduler().runTask(Quartz.getInstance(), () -> {
//            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
//        });
//
//        Component mainTitle = Component.text(titleStr, TextColor.fromCSSHexString("#b0a9fc"));
//        Component subTitle = Component.text(subtitleStr);
//        Title title = Title.title(mainTitle, subTitle);
//
//        for (Player player : Bukkit.getOnlinePlayers()) {
//            player.showTitle(title);
//        }
//
//        Bukkit.broadcast(Messenger.message(titleStr + " has begun!"));
    }
}