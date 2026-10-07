# Airport Assistant - Agentic Demo

An agentic demo for an airport customer, built to run natively on **Tanzu Platform**: `cf push`-deployable services, the platform's **Agent Buildpack**, MCP servers running as their own app instances, the **MCP Gateway** brokering access to them,
an **AI Services (GenAI)** instance backed by llama3.2, and **RabbitMQ** as an async event
bus so the demo has a proactive, event-driven story rather than a static Q&A bot.

> The Agent Buildpack and MCP Gateway are **Technical Preview** features (Tanzu Platform
> 10.4+ AI Services tile) — confirm they're enabled on your foundation before the run,
> see [Prerequisites](#prerequisites).

## Architecture

```
                         ┌──────────────────────┐
                         │  airport-agent       │  Agent Buildpack (AGENTS.md + skills)
                         │  (built-in chat UI)  │  bound to: demo-ai-airport, airport-mcp-gateway
                         └──────────┬───────────┘
                                     │ MCP (single endpoint)
                         ┌──────────▼────────────┐
                         │  airport-mcp-gateway  │  MCP Gateway service instance
                         │  (Tech Preview)       │
                         └─────┬─────┬─────┬────┬────┘
                registers   │       │       │       │  registers
              ┌─────────────┘   ┌───┘   ┌───┘       └───────────┐
        ┌─────▼─────┐    ┌──────▼────┐ ┌─▼───────┐   ┌──────────▼────────┐
        │mcp-digifly│    │mcp-parking│ │mcp-grms │   │     mcp-gab       │
        │ (FIDS)    │    │           │ │         │   │ (billing)         │
        └─────┬─────┘    └─────┬─────┘ └────┬────┘   └─────────┬─────────┘
              │ publish        │ publish     │ publish          │ publish
              └────────────────┴─────┬───────┴──────────────────┘
                                      ▼
                          ┌─────────▼──────────┐
                          │  airport-rabbitmq  │  exchange: airport.events
                          │  (RabbitMQ tile)   │  keys: flight.*, parking.*,
                          └─────────┬──────────┘        grms.*, billing.*
                                      │ consume (queue bound to #)
                          ┌───────────▼─────────────┐
                          │   mcp-notifications       │  also registered on gateway
                          │   exposes getRecentAlerts  │
                          └───────────────────────────┘

                          airport-agent also bound to:
                          demo-ai-airport  → genai service instance → llama3.2
```

## Components

| App | Type | Purpose |
|---|---|---|
| `airport-agent` | Agent Buildpack (config only, no code) | Passenger/staff conversational assistant with built-in chat UI |
| `mcp-digifly` | Spring Boot + Spring AI MCP server | Flight status, departures/arrivals, gate lookup (FIDS) |
| `mcp-parking` | Spring Boot + Spring AI MCP server | Parking availability, booking, ticket status, payment |
| `mcp-grms` | Spring Boot + Spring AI MCP server | Gate assignment, ground equipment, staff availability, gate reassignment |
| `mcp-gab` | Spring Boot + Spring AI MCP server | Invoices, billing status, charges, outstanding dues |
| `mcp-notifications` | Spring Boot + Spring AI MCP server | Consumes `airport.events` from RabbitMQ, exposes `getRecentAlerts` |

Each domain server also seeds mock data and runs a background simulator
(`@Scheduled`, ~every 45-60s) that mutates a random record and publishes an event to
RabbitMQ, so the demo feels alive without manual intervention. Each also exposes
`POST /debug/...` endpoints (not part of the MCP tool surface) so a presenter can trigger
a specific event on cue — see each module's `events/DebugController.java`.

## Prerequisites

Confirm on your foundation before deploying:

```bash
cf buildpacks | grep agent_buildpack      # Agent Buildpack installed?
cf marketplace | grep -i mcp-gateway      # MCP Gateway offering available?
cf marketplace -e genai                   # confirms exact plan name exposing llama3.2
cf marketplace | grep -i rabbit           # exact RabbitMQ tile/offering name
```

If `agent_buildpack` or `mcp-gateway` are missing, your platform operator needs to run
the relevant install errand in Ops Manager first (both are Tech Preview features of the
AI Services tile).

## Build

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
mvn -q -f pom.xml package -DskipTests
```

This builds all 5 Java MCP server jars under each module's `target/` directory (matches
the `path:` in each module's `manifest.yml`).

## Deploy

```bash
# 1. Provision services (adjust plan/offering names per the prerequisite check above)
cf create-service genai <plan-with-llama3.2> demo-ai-airport
cf create-service mcp-gateway gateway airport-mcp-gateway --wait
cf create-service <rabbitmq-offering> <plan> airport-rabbitmq

# 2. Push the 5 MCP server apps

cf push -f mcp-digifly/manifest.yml
cf push -f mcp-parking/manifest.yml
cf push -f mcp-grms/manifest.yml
cf push -f mcp-gab/manifest.yml
cf push -f mcp-notifications/manifest.yml

# 3. The MCP Gateway requires the bound app to have an internal route (apps.internal) -
#    map one to each server before binding, or the bind fails with "the bound application
#    must have an internal route". This is in addition to each app's default external
#    route, which stays intact for the /debug/... endpoints used in the demo script.

cf map-route mcp-digifly apps.internal --hostname mcp-digifly
cf map-route mcp-parking apps.internal --hostname mcp-parking
cf map-route mcp-grms apps.internal --hostname mcp-grms
cf map-route mcp-gab apps.internal --hostname mcp-gab
cf map-route mcp-notifications apps.internal --hostname mcp-notifications

# 4. Register each on the gateway

cf bind-service mcp-digifly airport-mcp-gateway -c '{"metadata":{"description":"Flight info (DIGI FLY/FIDS)"}}'
cf bind-service mcp-parking airport-mcp-gateway -c '{"metadata":{"description":"Parking management"}}'
cf bind-service mcp-grms airport-mcp-gateway -c '{"metadata":{"description":"Ground resource management"}}'
cf bind-service mcp-gab airport-mcp-gateway -c '{"metadata":{"description":"General airport billing"}}'
cf bind-service mcp-notifications airport-mcp-gateway -c '{"metadata":{"description":"Cross-domain alerts"}}'

# 5. Bind RabbitMQ to all 5 (4 publish, 1 consumes)

cf bind-service mcp-digifly airport-rabbitmq
cf bind-service mcp-parking airport-rabbitmq
cf bind-service mcp-grms airport-rabbitmq
cf bind-service mcp-gab airport-rabbitmq
cf bind-service mcp-notifications airport-rabbitmq

cf restage mcp-digifly && cf restage mcp-parking && cf restage mcp-grms && cf restage mcp-gab && cf restage mcp-notifications

# 6. Push and bind the agent last (so the gateway already has all servers registered).
#    airport-agent's manifest intentionally has no `services:` block - the gateway bind
#    needs an internal route to exist first (same requirement as step 3), and cf push
#    would otherwise try to bind as part of the same push, before that route exists.

cf push -f airport-agent/manifest.yml
cf map-route airport-agent apps.internal --hostname airport-agent
cf bind-service airport-agent demo-ai-airport
cf bind-service airport-agent airport-mcp-gateway

# 7. The bind in step 6 only registers airport-agent AS a server on the gateway (its own
#    /airport-agent/mcp route) - it does NOT make the agent discover the other 5 servers.
#    The Agent Buildpack's MCP discovery only scans VCAP_SERVICES entries tagged
#    `mcp-server`; the managed mcp-gateway binding is tagged `mcp-gateway`/
#    `tanzu-mcp-gateway` instead, so it's invisible to discovery and the chat UI's "MCP
#    Servers" panel shows "No MCP servers connected" even though the gateway itself looks
#    fine. Give the agent one user-provided service per domain server, each tagged
#    mcp-server and pointing at its gateway-routed URL (listed at
#    https://airport-mcp-gateway.<your-domain>/mcp-servers.json):

GW=https://airport-mcp-gateway.<your-domain>
cf create-user-provided-service mcp-digifly-upstream -t mcp-server -p "{\"url\":\"$GW/mcp-digifly/mcp\"}"
cf create-user-provided-service mcp-parking-upstream -t mcp-server -p "{\"url\":\"$GW/mcp-parking/mcp\"}"
cf create-user-provided-service mcp-grms-upstream -t mcp-server -p "{\"url\":\"$GW/mcp-grms/mcp\"}"
cf create-user-provided-service mcp-gab-upstream -t mcp-server -p "{\"url\":\"$GW/mcp-gab/mcp\"}"
cf create-user-provided-service mcp-notifications-upstream -t mcp-server -p "{\"url\":\"$GW/mcp-notifications/mcp\"}"

cf bind-service airport-agent mcp-digifly-upstream
cf bind-service airport-agent mcp-parking-upstream
cf bind-service airport-agent mcp-grms-upstream
cf bind-service airport-agent mcp-gab-upstream
cf bind-service airport-agent mcp-notifications-upstream

cf restage airport-agent
```

Then open the `airport-agent` route — the Agent Buildpack ships a built-in chat UI. The
"MCP Servers" panel should list all 5 domain servers as connected (each tagged `VCAP`),
and tool calls should show up there as they're invoked during the conversation.

> The exact `-c` binding config schema for a plain, unauthenticated MCP server may differ
> from the `auth.service-instance` pattern used for externally-authenticated servers
> (e.g. GitHub) — confirm the expected shape against your foundation's MCP Gateway docs
> if step 4 rejects the metadata-only payload above.

## Demo script

1. Ask the assistant about a specific flight: *"What's the status of 6E203?"*
2. Ask about parking: *"Any parking available in T3-P1?"* then *"Book a slot for vehicle
   DL01AB1234 for 3 hours."*
3. Ask a staff-style question: *"What gate is 6E203 at, and reassign it to B07."*
4. Ask about billing: *"What does 6E currently owe?"*
5. Trigger a live event from another terminal for dramatic effect, then ask *"Any
   updates?"*:
   ```bash
   curl -X POST https://mcp-digifly.<your-domain>/debug/simulate-delay/6E203
   ```
   The assistant should surface the new delay via `getRecentAlerts` on the next turn.
