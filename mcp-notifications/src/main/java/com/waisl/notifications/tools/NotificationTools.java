package com.waisl.notifications.tools;

import com.waisl.notifications.events.AlertBuffer;
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

    @McpTool(description = "Get recent cross-domain airport alerts/events (flight delays, gate changes, parking updates, billing events). "
            + "Use this proactively when asked for updates, alerts, or 'what's new'.", generateOutputSchema = false)
    public List<Map<String, Object>> getRecentAlerts(
            @McpToolParam(description = "Only return alerts after this ISO-8601 timestamp, e.g. 2026-08-20T10:00:00Z. Leave blank for all buffered alerts.", required = false) String since,
            @McpToolParam(description = "Filter by category prefix: flight, parking, grms, or billing. Leave blank for all.", required = false) String category) {
        Instant sinceInstant = null;
        if (since != null && !since.isBlank()) {
            try {
                sinceInstant = Instant.parse(since);
            } catch (Exception ignored) {
                // fall through and return unfiltered by time
            }
        }
        return buffer.recent(sinceInstant, category);
    }
}
