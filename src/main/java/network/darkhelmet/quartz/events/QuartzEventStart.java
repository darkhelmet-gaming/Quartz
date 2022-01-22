package network.darkhelmet.quartz.events;

import network.darkhelmet.quartz.config.EventConfiguration;

public class QuartzEventStart extends QuartzEvent {
    public QuartzEventStart(EventConfiguration eventConfiguration) {
        super(eventConfiguration);
    }
}
