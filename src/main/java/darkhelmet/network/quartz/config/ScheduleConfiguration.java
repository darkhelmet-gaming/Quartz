package darkhelmet.network.quartz.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class ScheduleConfiguration {
    @Comment("Set the starting time(s). Uses cron syntax.\n" +
            "This example starts the event every Tuesday at midnight.")
    private String starts = "0 0 0 ? * TUE *";

    @Comment("Set the ending time(s). Uses cron syntax.\n" +
            "This example ends the event every Wednesday at midnight.\n" +
            "This means the event was \"active\" all day Tuesday.")
    private String ends = "0 0 0 ? * WED *";

    @Comment("Enable or disable this schedule. When disabled, it will never become active.")
    private boolean enabled = true;

    public String starts() {
        return this.starts;
    }

    public String ends() {
        return this.ends;
    }

    public boolean enabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return "Schedule[" +
            "starts=" + starts + ", " +
            "ends=" + ends + ", " +
            "enabled=" + enabled + "]";
    }
}
