package darkhelmet.network.quartz;

import co.aikar.idb.DB;
import darkhelmet.network.quartz.models.Event;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Optional;

public class QuartzEndJob implements Job {
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        Optional<Event> event = Quartz.getInstance().getStorageAdapter().getEvent(dataMap.getString("eventName"));
        if (event.isPresent()) {
            Quartz.log("ENDING EVENT " + event);
        }

//        String titleStr = dataMap.getString("title");
//        String subtitleStr = dataMap.getString("subtitle");
//        int scheduleId = dataMap.getInt("schedule_id");
//
//        Component mainTitle = Component.text(titleStr, TextColor.fromCSSHexString("#b0a9fc"));
//        Component subTitle = Component.text(subtitleStr);
//        Title title = Title.title(mainTitle, subTitle);
//
//        for (Player player : Bukkit.getOnlinePlayers()) {
//            player.showTitle(title);
//        }
//
//        Bukkit.broadcast(Messenger.message(titleStr + " has ended!"));
//
//        DB.executeUpdateAsync("UPDATE quartz_schedules SET is_expired = 1 WHERE schedule_id = ?",
//            scheduleId);
    }
}