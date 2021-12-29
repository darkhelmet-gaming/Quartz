package darkhelmet.network.quartz.config;

import org.jetbrains.annotations.Nullable;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class StorageConfiguration {
    @Comment("The database host")
    private @Nullable String hostname = "localhost";

    @Comment("The database name")
    private @Nullable String database = "";

    @Comment("The database port")
    private @Nullable String port = "3306";

    @Comment("The database username")
    private @Nullable String username = "quartz";

    @Comment("The database password")
    private @Nullable String password = "";

    public @Nullable String hostname() {
        return this.hostname;
    }

    public @Nullable String database() {
        return this.database;
    }

    public @Nullable String port() {
        return this.port;
    }

    public @Nullable String password() {
        return this.password;
    }

    public @Nullable String username() {
        return this.username;
    }
}