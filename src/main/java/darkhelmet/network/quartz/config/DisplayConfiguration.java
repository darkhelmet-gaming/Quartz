package darkhelmet.network.quartz.config;

import darkhelmet.network.quartz.EventPhase;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class DisplayConfiguration {
    @Comment("Display type(s)")
    private List<String> types = new ArrayList<>();

    @Comment("Template(s) to use during the defined phases for an event\n" +
            "(applicable only if event uses this display type)\n" +
            "Formatting code reference: https://docs.adventure.kyori.net/minimessage\n" +
            "For chat display, one template entry = one line.\n" +
            "For titles, the first template is the title and second is the subtitle.")
    private List<String> templates = new ArrayList<>();

    @Comment("Enable or disable this message type. (Will prevent all events from using this type)")
    private boolean enabled = true;

    @Comment("Use this display on event 'START', 'ACTIVE', 'END', or any combination.")
    private List<EventPhase> phases = new ArrayList<>();

    public DisplayConfiguration() {}

    /**
     * Construct a new display configuration.
     *
     * @param phase An event phase
     */
    public DisplayConfiguration(EventPhase phase) {
        this();

        types.add("chat");
        types.add("title");

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

    public List<String> types() {
        return types;
    }
}
