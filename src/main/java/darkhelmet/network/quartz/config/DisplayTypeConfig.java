package darkhelmet.network.quartz.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class DisplayTypeConfig {
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

    public DisplayTypeConfig() {
        templates.add("&#aaaaaa<eventName> &fHas Begun!");
    }

    public DisplayTypeConfig(String on) {
        this();

        this.on = on;
    }
}
