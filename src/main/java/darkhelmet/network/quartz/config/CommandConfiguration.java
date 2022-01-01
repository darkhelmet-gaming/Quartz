package darkhelmet.network.quartz.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class CommandConfiguration {
    @Comment("The command to be executed.\n" +
            "Commands will be executed by the console and will effectively have OP permissions.")
    private String command = "broadcast example scheduled command";

    @Comment("Configure the execution time(s) of this command using cron syntax.\n" +
            "Here is a good crontab generator: https://www.freeformatter.com/cron-expression-generator-quartz.html\n" +
            "Times are always based on the timezone of the machine the server is running on.\n" +
            "The example cron runs every minute.")
    private String cron = "0 * * ? * * *";

    @Comment("Enable or disable this command. When disabled, it will never run.")
    private boolean enabled = true;

    public String command() {
        return command;
    }

    public String cron() {
        return cron;
    }

    public boolean enabled() {
        return this.enabled;
    }

    @Override
    public String toString() {
        return "Command[" +
            "cron=" + cron + ", " +
            "command=" + command + ", " +
            "enabled=" + enabled + "]";
    }
}
