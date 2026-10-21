package com.mueventhub.web;

import com.mueventhub.model.College;
import com.mueventhub.model.Event;
import com.mueventhub.model.EventType;
import com.mueventhub.repo.CollegeRepository;
import com.mueventhub.repo.EventRepository;
import com.mueventhub.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PublicController {

    private final EventService eventService;
    private final EventRepository events;
    private final CollegeRepository colleges;

    public PublicController(EventService eventService, EventRepository events, CollegeRepository colleges) {
        this.eventService = eventService;
        this.events = events;
        this.colleges = colleges;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Event> upcoming = eventService.search(null, null, null, false);
        model.addAttribute("featured", upcoming.stream().limit(6).toList());
        model.addAttribute("types", EventType.values());
        model.addAttribute("collegeCount", colleges.count());
        model.addAttribute("upcomingCount", events.countByEndDateGreaterThanEqual(LocalDate.now()));
        model.addAttribute("hackathonCount", upcoming.stream().filter(e -> e.getType() == EventType.HACKATHON).count());
        return "index";
    }

    @GetMapping("/events")
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) EventType type,
                       @RequestParam(required = false) Long college,
                       @RequestParam(defaultValue = "false") boolean past,
                       Model model) {
        model.addAttribute("results", eventService.search(q, type, college, past));
        model.addAttribute("types", EventType.values());
        model.addAttribute("colleges", colleges.findAllByOrderByNameAsc());
        model.addAttribute("q", q);
        model.addAttribute("selType", type);
        model.addAttribute("selCollege", college);
        model.addAttribute("past", past);
        return "events";
    }

    @GetMapping("/events/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Event event = events.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("event", event);
        return "event";
    }

    @GetMapping("/colleges")
    public String colleges(Model model) {
        List<College> all = colleges.findAllByOrderByNameAsc();
        Map<Long, Long> upcoming = new HashMap<>();
        for (College c : all) {
            upcoming.put(c.getId(), events.countByCollegeIdAndEndDateGreaterThanEqual(c.getId(), LocalDate.now()));
        }
        model.addAttribute("colleges", all);
        model.addAttribute("upcoming", upcoming);
        return "colleges";
    }

    @GetMapping("/colleges/{id}")
    public String college(@PathVariable Long id, Model model) {
        College c = colleges.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        List<Event> all = events.findByCollegeIdOrderByStartDateAsc(id);
        model.addAttribute("college", c);
        model.addAttribute("upcomingEvents", all.stream().filter(e -> !e.isPast()).toList());
        model.addAttribute("pastEvents", all.stream().filter(Event::isPast).toList());
        return "college";
    }
}
