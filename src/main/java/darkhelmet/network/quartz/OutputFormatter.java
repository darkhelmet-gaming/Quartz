package darkhelmet.network.quartz;

import darkhelmet.network.quartz.config.OutputConfiguration;

import java.util.List;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;

public class OutputFormatter {
    /**
     * Output configuration.
     */
    private OutputConfiguration outputConfiguration;

    /**
     * Prefix.
     */
    private Component prefix;

    /**
     * Construct a new instance.
     *
     * @param outputConfiguration The output configuration
     */
    public OutputFormatter(OutputConfiguration outputConfiguration) {
        this.outputConfiguration = outputConfiguration;

        // Format the prefix now as we'll never change it.
        prefix = MiniMessage.get().parse(outputConfiguration.prefix());
    }

    /**
     * Format an error message.
     *
     * @param message The message
     * @return The formatted component
     */
    public Component error(String message) {
        return format(outputConfiguration.error(), message);
    }

    /**
     * Format a success message.
     *
     * @param message The message
     * @return The formatted component
     */
    public Component success(String message) {
        return format(outputConfiguration.success(), message);
    }

    /**
     * Format an info message.
     *
     * @param message The message
     * @return The formatted component
     */
    public Component info(String message) {
        return format(outputConfiguration.info(), message);
    }

    /**
     * Format a base message.
     *
     * @param template The base template
     * @param message The message
     * @return The formatted component
     */
    private Component format(String template, String message) {
        Template messageTemplate = Template.of("message", message);
        Template prefixTemplate = Template.of("prefix", prefix);

        List<Template> templates = List.of(messageTemplate, prefixTemplate);
        return MiniMessage.get().parse(template, templates);
    }
}
