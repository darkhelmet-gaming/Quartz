package darkhelmet.network.quartz;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class Messenger {
    private static final char SEPARATOR = '\u300b';
    private static final String PLUGIN = "Quartz";
    private static final TextColor primaryColor = TextColor.fromCSSHexString("#ffbb6e");

    /**
     * Prevent instantiation.
     */
    private Messenger() {}

    /**
     * Build a prefixed error message.
     *
     * @param message The message
     * @return The prefixed message component
     */
    public static Component error(String message) {
        return prefix().append(Component.text(message, NamedTextColor.RED)).build();
    }

    /**
     * Build a prefixed message.
     *
     * @param message The message
     * @return The prefixed message component
     */
    public static Component message(String message) {
        return prefix().append(Component.text(message, NamedTextColor.WHITE)).build();
    }

    /**
     * A prefix method for text component builders.
     *
     * @return The prefix
     */
    public static TextComponent.Builder prefix() {
        return prefix(PLUGIN);
    }

    /**
     * A prefix method for text component builders.
     *
     * @param prefix The prefix string
     * @return The prefix
     */
    private static TextComponent.Builder prefix(String prefix) {
        return Component.text().append(Component.text("(", NamedTextColor.DARK_GRAY))
            .append(Component.text("\uFF01", primaryColor))
            .append(Component.text(") ", NamedTextColor.DARK_GRAY))
            .append(Component.text(prefix, primaryColor))
            .append(Component.text(" " + SEPARATOR + "", NamedTextColor.GRAY));
    }

    /**
     * Build a prefixed success message.
     *
     * @param message The message
     * @return The prefixed message component
     */
    public static Component success(String message) {
        return prefix().append(Component.text(message, NamedTextColor.GREEN)).build();
    }
}