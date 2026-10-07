package com.airport.notifications.tools;

import com.airport.notifications.events.AlertBuffer;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class NotificationTools {

    private final AlertBuffer buffer;

    public NotificationTools(AlertBuffer buffer) {
        this.buffer = buffer;
    }

    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 200;

    @McpTool(description = "Get recent cross-domain airport alerts/events (flight delays, gate changes, parking updates, billing "
            + "events), newest first. Use this proactively when asked for updates, alerts, or 'what's new'. Returns at most "
            + "`limit` alerts (default 20) spanning whatever categories are actually present - summarize across them, don't just "
            + "report the first one.", generateOutputSchema = false)
    public List<Map<String, Object>> getRecentAlerts(
            @McpToolParam(description = "Only return alerts after this ISO-8601 timestamp, e.g. 2026-08-20T10:00:00Z. Leave blank for the most recent alerts regardless of age.", required = false) String since,
            @McpToolParam(description = "Filter by category prefix: flight, parking, grms, or billing. Leave blank for all categories.", required = false) String category,
            @McpToolParam(description = "Max number of alerts to return, newest first (default 20, max 200).", required = false) String limit) {
        Instant sinceInstant = null;
        if (since != null && !since.isBlank()) {
            try {
                sinceInstant = Instant.parse(since);
            } catch (Exception ignored) {
                // fall through and return unfiltered by time
            }
        }
        int cap = DEFAULT_LIMIT;
        if (limit != null && !limit.isBlank()) {
            try {
                cap = Math.max(1, Math.min(MAX_LIMIT, Integer.parseInt(limit.trim())));
            } catch (NumberFormatException ignored) {
                // fall through and use the default
            }
        }
        List<Map<String, Object>> results = buffer.recent(sinceInstant, category);
        return results.size() > cap ? results.subList(0, cap) : results;
    }
}
