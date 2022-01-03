package darkhelmet.network.quartz.config;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Sound;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class PhaseConfiguration {
    @Comment("List all commands that will be executed when this phase is activated.\n" +
            "Commands will be executed by the console and will effectively have OP permissions.")
    private List<String> commands = new ArrayList<>();

    @Comment("Configure a sound to play when this phase begins.")
    private Sound sound;

    @Comment("List all display types (by name) this phase will use.")
    private List<String> useDisplays = new ArrayList<>();

    /**
     * Construct a new phase configuration.
     */
    public PhaseConfiguration() {
        commands.add("effect give %player_name% levitation 3 0");

        useDisplays.add("title");
    }

    public List<String> commands() {
        return commands;
    }

    public Sound sound() {
        return sound;
    }

    public List<String> useDisplays() {
        return useDisplays;
    }
}
