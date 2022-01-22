package network.darkhelmet.quartz.formatters;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;

import network.darkhelmet.quartz.config.EventConfiguration;
import network.darkhelmet.quartz.config.OutputConfiguration;

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
     * The date/time formatter.
     */
    private DateTimeFormatter dateTimeFormatter;

    /**
     * Construct a new instance.
     *
     * @param outputConfiguration The output configuration
     */
    public OutputFormatter(OutputConfiguration outputConfiguration) {
        this.outputConfiguration = outputConfiguration;

        // Format the prefix now as we'll never change it.
        prefix = MiniMessage.get().parse(outputConfiguration.prefix());

        // Cache the date formatter
        dateTimeFormatter = DateTimeFormatter.ofPattern(outputConfiguration.date());
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
     * Format a heading message.
     *
     * @param heading The heading segment
     * @param message The message segment
     * @return The formatted component
     */
    public Component heading(String heading, String message) {
        return format(outputConfiguration.heading(), heading, message);
    }

    /**
     * Format a base message.
     *
     * @param template The base template
     * @param message The message segment
     * @return The formatted component
     */
    private Component format(String template, String message) {
        return format(template, "", message);
    }

    /**
     * Format a base message.
     *
     * @param template The base template
     * @param heading The heading segment
     * @param message The message segment
     * @return The formatted component
     */
    private Component format(String template, String heading, String message) {
        Template prefixTemplate = Template.of("prefix", prefix);
        Template headingTemplate = Template.of("heading", heading);
        Template messageTemplate = Template.of("message", message);

        List<Template> templates = List.of(prefixTemplate, headingTemplate, messageTemplate);
        return MiniMessage.get().parse(template, templates);
    }

    /**
     * Format a chat message for an active event configuration.
     *
     * @param event The event
     * @return The formatted component
     */
    public Component activeEventListEntry(EventConfiguration event) {
        return eventListEntry(outputConfiguration.activeEventListEntry(), event);
    }

    /**
     * Format a chat message for an event configuration.
     *
     * @param event The event
     * @return The formatted component
     */
    public Component eventListEntry(EventConfiguration event) {
        return eventListEntry(outputConfiguration.eventListEntry(), event);
    }

    /**
     * Format a chat message for an event configuration.
     *
     * @param template The config template to use
     * @param event The event
     * @return The formatted component
     */
    private Component eventListEntry(String template, EventConfiguration event) {
        Template prefixTemplate = Template.of("prefix", prefix);
        Template eventNameTemplate = Template.of("eventName", event.name());
        Template eventDescTemplate = Template.of("eventDescription", event.description());

        String nextStartStr = "";
        Optional<ZonedDateTime> nextStart = event.getNextStartExecution();
        if (nextStart.isPresent()) {
            nextStartStr = nextStart.get().format(dateTimeFormatter);
        }

        Template nextStartTemplate = Template.of("nextStart", nextStartStr);

        String nextEndStr = "";
        Optional<ZonedDateTime> nextEnd = event.getNextEndExecution();
        if (nextEnd.isPresent()) {
            nextEndStr = nextEnd.get().format(dateTimeFormatter);
        }

        Template nextEndTemplate = Template.of("nextEnd", nextEndStr);

        List<Template> templates = List.of(
            prefixTemplate,
            eventNameTemplate,
            eventDescTemplate,
            nextStartTemplate,
            nextEndTemplate);
        return MiniMessage.get().parse(template, templates);
    }
}
