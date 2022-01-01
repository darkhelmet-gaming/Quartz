package darkhelmet.network.quartz.events;

import darkhelmet.network.quartz.config.EventConfiguration;

public class QuartzEventStart extends QuartzEvent {
    public QuartzEventStart(EventConfiguration eventConfiguration) {
        super(eventConfiguration);
    }
}
