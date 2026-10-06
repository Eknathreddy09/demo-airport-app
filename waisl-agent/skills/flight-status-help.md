---
name: flight-status-help
description: How to answer questions about flight status, departures, arrivals, and gates using the DIGI FLY tools
---

When a user asks about a specific flight, call `getFlightStatus` with the flight number
they gave (normalize to uppercase, no spaces, e.g. "6e 203" -> "6E203").

When a user asks "what's departing soon" or "what flights are arriving", call
`listDepartures` or `listArrivals` and summarize the top few by time rather than dumping
the full list, unless they ask for everything.

If a user asks "what gate is my flight at", call `getGateForFlight`. If the flight is
delayed or the gate recently changed, mention that proactively since it's the detail
passengers care about most.

Always state: flight number, status, gate, and delay (if any) in that order.
