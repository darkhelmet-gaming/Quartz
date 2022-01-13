# Quartz

Quartz is a powerful command/event scheduling system for (currently) Paper-based Minecraft servers.

- Schedule events and commands using the powerful cron syntax.
- "Events" have start/end times and can run commands, display messages, play sounds, and more.
- "Commands" are literally just commands set to run at specific times.
- Powerful configuration system lets you control the displays and formatting of both events and command output.
- All output configurations support Minimessage formatting and placeholders from PlaceholderAPI.
- Commented configuration files help explain the options.

[Discord](https://discord.gg/Q6sHDfnMAc)

Created by viveleroi for the `play.darkhelmet.network` Minecraft server.

# Commands

- `events` - List all enabled events.
- `activevents` - List all currently active events. (or `/events active`)
- `quartz forcestart [eventKey]` - Force start an event. Does not schedule an end.
- `quartz forceend [eventKey]` - Force end an event.
- `quartz simulatestart [eventKey]` - Simulate the start of an event. Shows any in-game displays but does not run commands.
- `quartz simulateend [eventKey]` - Simulate the end of an event. Shows any in-game displays but does not run commands.
- `quartz reload` - Reloads configuration files.

# Permissions

- `quartz.events.list.active` - List currently active events.
- `quartz.events.list` - List all enabled events.
- `quartz.admin` - Allows admin commands (anything under the `/quartz` command).