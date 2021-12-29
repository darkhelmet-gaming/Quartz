package darkhelmet.network.quartz.models;

import java.util.Objects;

public class Schedule {
    /**
     * The raw start cron.
     */
    private final String starts;

    /**
     * The raw end cron.
     */
    private final String ends;

    /**
     * Whether this scheduled is enabled
     */
    private final boolean enabled;

    public Schedule(String starts, String ends, boolean enabled) {
        this.starts = starts;
        this.ends = ends;
        this.enabled = enabled;
    }

    public boolean enabled() {
        return enabled;
    }

    public String starts() {
        return starts;
    }

    public String ends() {
        return ends;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Schedule) obj;
        return Objects.equals(this.starts, that.starts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(starts, ends);
    }

    @Override
    public String toString() {
        return "Schedule[" +
            "starts=" + starts + ", " +
            "ends=" + ends + ", " +
            "enabled=" + enabled + "]";
    }
}
