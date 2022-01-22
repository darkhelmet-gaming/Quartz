package network.darkhelmet.quartz.formatters;

import network.darkhelmet.quartz.Quartz;
import network.darkhelmet.quartz.config.LanguageConfiguration;

public class I18l {
    private I18l() {}

    public static LanguageConfiguration lang() {
        return Quartz.getInstance().langConfig();
    }
}
