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

    @Comment("Use this display on event 'start', 'active', 'end', or any combination.")
    private List<String> phases = new ArrayList<>();

    public DisplayConfiguration() {}

    public DisplayConfiguration(String on) {
        this();

        if (on.equalsIgnoreCase("start")) {
            templates.add("<#00ff00><eventName> <white>Has Begun!");
        } else {
            templates.add("<#00ff00><eventName> <white>Has Ended!");
        }

        this.phases.add("start");
        this.phases.add("end");
    }

    public boolean enabled() {
        return enabled;
    }

    public List<String> phases() {
        return phases;
    }

    public List<String> templates() {
        return templates;
    }

    public String type() {
        return type;
    }
}
