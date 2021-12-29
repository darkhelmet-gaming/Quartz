package darkhelmet.network.quartz.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;

import darkhelmet.network.quartz.EventManager;
import darkhelmet.network.quartz.Quartz;
import darkhelmet.network.quartz.config.EventConfiguration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

@CommandAlias("quartz")
public class QuartzCommand extends BaseCommand {
    @Subcommand("reload")
    @CommandPermission("quartz.admin")
    public void onReload(CommandSender sender) {
        // Reloads configuration files
//        Quartz.getInstance().loadEventConfiguration();

        // Reload all schedules
        Quartz.getInstance().loadSchedules();

        sender.sendMessage(Component.text("Reloaded schedules.", NamedTextColor.GREEN));
    }

    @Subcommand("simulatestart")
    @CommandPermission("quartz.admin")
    public void onSimulateStart(CommandSender sender) {
        // @todo fixme
        EventConfiguration event = Quartz.getInstance().getQuartzConfig().events().get(0);

        EventManager.simulateStart(event);
    }
}
