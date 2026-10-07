package com.airport.notifications.events;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

@Component
public class AlertBuffer {

    private static final int MAX_SIZE = 200;
    private final Deque<Map<String, Object>> alerts = new ArrayDeque<>();

    public synchronized void add(Map<String, Object> event) {
        alerts.addFirst(event);
        while (alerts.size() > MAX_SIZE) {
            alerts.removeLast();
        }
    }

    public synchronized List<Map<String, Object>> recent(Instant since, String category) {
        return alerts.stream()
                .filter(e -> since == null || isAfter(e, since))
                .filter(e -> category == null || category.isBlank()
                        || String.valueOf(e.get("routingKey")).startsWith(category))
                .toList();
    }

    private boolean isAfter(Map<String, Object> event, Instant since) {
        Object ts = event.get("timestamp");
        if (ts == null) return true;
        try {
            return Instant.parse(ts.toString()).isAfter(since);
        } catch (Exception e) {
            return true;
        }
    }
}
