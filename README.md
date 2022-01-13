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

# The ifevent Command

Any plugins that support running commands can take advantage of events. You simply need to prefix a command
with the `ifevent` syntax below. If the given event is active, the provided command will execute. If the event
is not active, the command will not run.

`ifevent [eventKey] [command]`

For example, say you give users a vote key when they vote. In your vote plugin you might have something like
this:

```
vote-rewards:
  commands:
  - givekey Vote %player_name%
```

You can add an entry using ifevent that will give them a second key *only* when the event is active:

```
vote-rewards:
  commands:
  - givekey Vote %player_name%
  - ifevent doubleVoteDay givekey Vote %player_name%
```

If the event `doubleVoteDay` is active (the current time is between a scheduled start and end time),
the command will be run and the user will get a second key.

# Permissions

- `quartz.events.list.active` - List currently active events.
- `quartz.events.list` - List all enabled events.
- `quartz.admin` - Allows admin commands (anything under the `/quartz` command).