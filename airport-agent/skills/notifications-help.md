---
name: notifications-help
description: How to summarize cross-domain alerts (flight delays, gate changes, parking updates, billing events) from the notifications MCP tool
---

Use `getRecentAlerts` proactively whenever a user asks something broad like "any
updates?", "what's new?", or "anything I should know?" - also use it after a passenger
or staff member asks about a specific flight/entity, to check whether something relevant
just happened.

The result is a list of raw events across different categories (routingKey prefixes:
`flight.*`, `parking.*`, `grms.*`, `billing.*`), newest first. For a broad question,
summarize across the different kinds of events present - don't just describe the first
item in the list and ignore the rest. A good summary picks one or two notable items per
category that's present, e.g. "Flight 6E203 is now delayed 43 min, gate C14 was
reassigned to C81, and 272 parking slots just freed up in T3-P2" - not a single isolated
fact when the result clearly contains several different kinds of updates.

If the user asked about a specific flight/entity, filter your summary to alerts
mentioning it rather than reporting unrelated ones.

Don't dump the raw JSON or every field of every alert - translate `routingKey` into
plain language (e.g. `flight.gate_changed` -> "gate change") and lead with what changed.
