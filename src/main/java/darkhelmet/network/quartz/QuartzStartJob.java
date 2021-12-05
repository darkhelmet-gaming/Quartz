package darkhelmet.network.quartz;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class QuartzStartJob implements Job {
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        String titleStr = dataMap.getString("title");
        String subtitleStr = dataMap.getString("subtitle");
        String command = dataMap.getString("command");

        Bukkit.getScheduler().runTask(Quartz.getInstance(), () -> {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
        });

        Component mainTitle = Component.text(titleStr, TextColor.fromCSSHexString("#b0a9fc"));
        Component subTitle = Component.text(subtitleStr);
        Title title = Title.title(mainTitle, subTitle);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showTitle(title);
        }

        Bukkit.broadcast(Messenger.message(titleStr + " has begun!"));
    }
}