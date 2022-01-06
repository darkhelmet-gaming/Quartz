package darkhelmet.network.quartz.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class OutputConfiguration {
    @Comment("Used to prefix any other messages with <prefix> placeholders.")
    private String prefix = "<dark_gray>(<#ffbb6e>\uFF01<dark_gray>) <#ffbb6e>Quartz<gray> \u300b";

    @Comment("Used for all \"success\" messages.")
    private String success = "<prefix><green><message>";

    @Comment("Used for all \"error\" messages.")
    private String error = "<prefix><red><message>";

    @Comment("Used for all \"info\" messages.")
    private String info = "<prefix><white><message>";

    @Comment("Used for the heading message before outputting a list (of events, etc).")
    private String heading = "<prefix><white><heading> <underlined><#0ccfcb><bold><message>";

    @Comment("Configure the date format used in output where are used. Refer to java's DateFormatter for syntax.")
    private String date = "eee MMMM dd yyyy hh:mm a Z";

    @Comment("Configure active event entry formats when used in list output.")
    private String activeEventListEntry = "<green><eventName>\n" +
            "      <yellow><eventDescription>\n" +
            "      <gray>Ends: <white><nextEnd>";

    @Comment("Configure event entry formats when used in list output.")
    private String eventListEntry = "<green><eventName>\n" +
            "      <yellow><eventDescription>\n" +
            "      <gray>Starts: <white><nextStart>\n" +
            "      <gray>Ends: <white><nextEnd>";

    public String prefix() {
        return prefix;
    }

    public String info() {
        return info;
    }

    public String error() {
        return error;
    }

    public String success() {
        return success;
    }

    public String heading() {
        return heading;
    }

    public String date() {
        return date;
    }

    public String activeEventListEntry() {
        return activeEventListEntry;
    }

    public String eventListEntry() {
        return eventListEntry;
    }
}
