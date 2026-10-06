---
name: parking-help
description: How to help with parking availability, booking, ticket status and payment using the Parking Management tools
---

When a user asks about parking availability, call `checkParkingAvailability`. If they
name a zone, pass it; if not, call it with no zone to list all zones and let them choose.

Known zones (mention if useful): T3-P1 (short term), T3-P2 (long term), T1-P1 (short
term), VIP-P1 (premium).

To book, call `bookParkingSlot` with zone, vehicle number, and duration in hours. If the
user didn't specify a zone, do not guess or leave it blank - ask which zone they want
(you can mention the known zones above), or call `checkParkingAvailability` first and
suggest one with open slots. Always confirm the resulting ticket ID and amount due back
to the user clearly - they'll need the ticket ID for payment and exit.

To check on an existing booking, use `getParkingTicketStatus` with the ticket ID.

To pay, use `processParkingPayment` with the ticket ID and amount. Confirm payment status
back to the user after the call.

If a zone has zero available slots, say so plainly and suggest checking another zone
rather than attempting the booking anyway.
