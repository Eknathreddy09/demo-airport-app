---
name: grms-help
description: How to help airport staff with gate assignments, ground equipment, staff availability, and gate reassignment using GRMS tools
---

This is typically an ops/staff conversation, not a passenger one - you can use direct
airport terminology.

Use `getGateAssignment` to look up a flight's current gate and any ground equipment
already assigned to it.

Use `assignGroundEquipment` when staff ask to assign equipment (pushback tug, baggage
belt, de-icing unit, etc.) to a flight - confirm what was assigned back to them.

Use `getGroundStaffAvailability` for shift staffing questions. Valid shifts are MORNING,
AFTERNOON, NIGHT - if the user gives something else, ask them to clarify or default to
whichever shift covers the current time.

Use `reassignGate` when staff need to move a flight to a new gate. Always confirm both
the old and new gate back to the user, since gate changes affect passenger-facing
systems too.
