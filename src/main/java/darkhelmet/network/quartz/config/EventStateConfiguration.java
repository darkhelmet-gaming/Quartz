package darkhelmet.network.quartz.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class EventStateConfiguration {
    public List<String> activeEvents = new ArrayList<>();
}
