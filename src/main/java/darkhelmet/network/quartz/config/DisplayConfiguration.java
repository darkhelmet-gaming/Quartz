package darkhelmet.network.quartz.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class DisplayConfiguration {
    @Comment("Display type")
    private String type = "title";

    @Comment("Format to use when an event begins (applicable only if event uses this display type)")
    private List<String> templates = new ArrayList<>();

    @Comment("Enable or disable this message type. (Will prevent all events from using this type)")
    private boolean enabled = true;

    @Comment("Use this display on event 'start' or 'end'")
    private String on = "start";

    @Comment("Set a permission node this type will appear for (or 'false' to disable permission use)")
    private String permission = "false";

    public DisplayConfiguration() {}

    public DisplayConfiguration(String on) {
        this();

        if (on.equalsIgnoreCase("start")) {
            templates.add("<#00ff00><eventName> <white>Has Begun!");
        } else {
            templates.add("<#00ff00><eventName> <white>Has Ended!");
        }

        this.on = on;
    }

    public boolean enabled() {
        return enabled;
    }

    public String on() {
        return on;
    }

    public String permission() {
        return permission;
    }

    public List<String> templates() {
        return templates;
    }

    public String type() {
        return type;
    }
}
