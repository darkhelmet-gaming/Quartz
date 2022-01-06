package darkhelmet.network.quartz.formatters;

import darkhelmet.network.quartz.Quartz;
import darkhelmet.network.quartz.config.LanguageConfiguration;

public class I18l {
    private I18l() {}

    public static LanguageConfiguration lang() {
        return Quartz.getInstance().langConfig();
    }
}
