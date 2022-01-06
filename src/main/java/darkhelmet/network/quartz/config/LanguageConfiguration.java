package darkhelmet.network.quartz.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class LanguageConfiguration {
    public String reloadedEventsAndSchedules = "Reloaded configurations and schedules.";
    public String forceStartingEvent = "Force starting event %s";
    public String forceEndingEvent = "Force ending event %s";
    public String simulatingEventStart = "Simulating start for event %s";
    public String simulatingEventActive = "Simulating active phase for event %s";
    public String simulatingEventEnd = "Simulating end for event %s";
    public String noActiveEvents = "There are no active events.";
    public String noEvents = "There are no events.";
    public String listHeading = "Listing";
    public String activeEvents = "Active Events";
    public String events = "Events";
}
