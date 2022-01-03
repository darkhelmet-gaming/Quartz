package darkhelmet.network.quartz.config;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class EventStateConfiguration {
    public List<String> activeEvents = new ArrayList<>();
}
