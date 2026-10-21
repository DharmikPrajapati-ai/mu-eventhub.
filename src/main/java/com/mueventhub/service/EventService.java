package com.mueventhub.service;

import com.mueventhub.model.Event;
import com.mueventhub.model.EventType;
import com.mueventhub.repo.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class EventService {

    private final EventRepository events;

    public EventService(EventRepository events) {
        this.events = events;
    }

    /** Filters in memory - plenty fast for the few hundred events a student project will hold. */
    public List<Event> search(String q, EventType type, Long collegeId, boolean includePast) {
        String needle = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);
        return events.findAllByOrderByStartDateAsc().stream()
                .filter(e -> includePast || !e.isPast())
                .filter(e -> type == null || e.getType() == type)
                .filter(e -> collegeId == null || e.getCollege().getId().equals(collegeId))
                .filter(e -> needle.isEmpty()
                        || e.getTitle().toLowerCase(Locale.ROOT).contains(needle)
                        || e.getDescription().toLowerCase(Locale.ROOT).contains(needle)
                        || e.getCollege().getName().toLowerCase(Locale.ROOT).contains(needle))
                .toList();
    }
}
