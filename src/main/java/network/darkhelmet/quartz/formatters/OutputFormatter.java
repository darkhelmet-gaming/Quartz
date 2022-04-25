package network.darkhelmet.quartz.formatters;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import network.darkhelmet.quartz.config.EventConfiguration;
import network.darkhelmet.quartz.config.OutputConfiguration;

public class OutputFormatter {
    /**
     * Output configuration.
     */
    private OutputConfiguration outputConfiguration;

    /**
     * The date/time formatter.
     */
    private DateTimeFormatter dateTimeFormatter;

    /**
     * The prefix.
     */
    private TagResolver prefix;

    /**
     * Construct a new instance.
     *
     * @param outputConfiguration The output configuration
     */
    public OutputFormatter(OutputConfiguration outputConfiguration) {
        this.outputConfiguration = outputConfiguration;

        this.prefix = Placeholder.component("prefix",
            MiniMessage.miniMessage().deserialize(outputConfiguration.prefix()));

        // Cache the date formatter
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(outputConfiguration.date());
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
        TagResolver.Single headingTemplate = Placeholder.parsed("heading", heading);
        TagResolver.Single messageTemplate = Placeholder.parsed("message", message);

        TagResolver placeholders = TagResolver.resolver(prefix, headingTemplate, messageTemplate);

        return MiniMessage.miniMessage().deserialize(template, placeholders);
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
        String nextStartStr = "";
        Optional<ZonedDateTime> nextStart = event.getNextStartExecution();
        if (nextStart.isPresent()) {
            nextStartStr = nextStart.get().format(dateTimeFormatter);
        }

        String nextEndStr = "";
        Optional<ZonedDateTime> nextEnd = event.getNextEndExecution();
        if (nextEnd.isPresent()) {
            nextEndStr = nextEnd.get().format(dateTimeFormatter);
        }

        TagResolver.Single eventNameTemplate = Placeholder.parsed("event_name", event.name());
        TagResolver.Single eventDescTemplate = Placeholder.parsed("event_description", event.description());
        TagResolver.Single nextStartTemplate = Placeholder.parsed("next_start", nextStartStr);
        TagResolver.Single nextEndTemplate = Placeholder.parsed("next_end", nextEndStr);

        TagResolver placeholders = TagResolver.resolver(
            prefix, eventNameTemplate, eventDescTemplate, nextStartTemplate, nextEndTemplate);

        return MiniMessage.miniMessage().deserialize(template, placeholders);
    }
}
