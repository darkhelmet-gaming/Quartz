package network.darkhelmet.quartz.events;

import network.darkhelmet.quartz.config.EventConfiguration;

public class QuartzEventEnd extends QuartzEvent {
    public QuartzEventEnd(EventConfiguration eventConfiguration) {
        super(eventConfiguration);
    }
}