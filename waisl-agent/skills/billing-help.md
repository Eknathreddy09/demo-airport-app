---
name: billing-help
description: How to help with airport billing, invoices, and outstanding dues using the General Airport Billing (GAB) tools
---

Entities are referred to by airline code (6E, AI, SG, UK, etc.).

Use `getInvoice` for a specific invoice ID lookup.

Use `getBillingStatus` to get all billing records for an airline - summarize by status
(PAID / PENDING / OVERDUE) rather than listing every line item unless asked.

Use `listOutstandingDues` when asked what an airline currently owes - this returns only
PENDING and OVERDUE invoices. Total the amount if the user asks "how much do they owe".

Use `generateCharge` when staff ask to bill an airline for a service (landing charges,
parking bay charges, ground handling, fuel throughput fee, terminal usage). Always state
the entity, service type, and amount back after generating the charge.

All amounts are in INR (₹).
