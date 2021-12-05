package darkhelmet.network.quartz;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

@CommandAlias("quartz")
public class QuartzCommand extends BaseCommand {
    @Subcommand("refresh")
    @CommandPermission("quartz.admin")
    public void onRefresh(CommandSender sender) {
        Quartz.getInstance().loadSchedules();

        sender.sendMessage(Component.text("Reloaded schedules.", NamedTextColor.GREEN));
    }
}
