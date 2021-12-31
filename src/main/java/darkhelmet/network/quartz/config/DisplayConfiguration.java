package darkhelmet.network.quartz.config;

import darkhelmet.network.quartz.EventPhase;

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
    private List<EventPhase> phases = new ArrayList<>();

    public DisplayConfiguration() {}

    public DisplayConfiguration(EventPhase phase) {
        this();

        if (phase.equals(EventPhase.START)) {
            templates.add("<#00ff00><eventName> <white>Has Begun!");
        } else {
            templates.add("<#00ff00><eventName> <white>Has Ended!");
        }

        this.phases.add(phase);
    }

    public boolean enabled() {
        return enabled;
    }

    public List<EventPhase> phases() {
        return phases;
    }

    public List<String> templates() {
        return templates;
    }

    public String type() {
        return type;
    }
}
