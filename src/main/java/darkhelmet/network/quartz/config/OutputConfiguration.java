package darkhelmet.network.quartz.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class OutputConfiguration {
    private String prefix = "<dark_gray>(<#ffbb6e>\uFF01<dark_gray>) <#ffbb6e>Quartz<gray> \u300b";

    private String success = "<prefix><green><message>";

    private String error = "<prefix><red><message>";

    private String info = "<prefix><white><message>";

    public String prefix() {
        return prefix;
    }

    public String info() {
        return info;
    }

    public String error() {
        return error;
    }

    public String success() {
        return success;
    }
}
