package com.mueventhub.web;

import com.mueventhub.model.College;
import com.mueventhub.model.Event;
import com.mueventhub.model.EventType;
import com.mueventhub.model.Representative;
import com.mueventhub.repo.CollegeRepository;
import com.mueventhub.repo.EventRepository;
import com.mueventhub.repo.RepresentativeRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Everything under /rep/** requires a logged-in college representative (see SecurityConfig). */
@Controller
@RequestMapping("/rep")
public class RepController {

    private final RepresentativeRepository reps;
    private final EventRepository events;
    private final CollegeRepository colleges;

    public RepController(RepresentativeRepository reps, EventRepository events, CollegeRepository colleges) {
        this.reps = reps;
        this.events = events;
        this.colleges = colleges;
    }

    // Never let a form post overwrite ids, ownership or the college name.
    @InitBinder("event")
    void eventBinder(WebDataBinder binder) {
        binder.setDisallowedFields("id", "college");
    }

    @InitBinder("college")
    void collegeBinder(WebDataBinder binder) {
        binder.setDisallowedFields("id", "name");
    }

    private Representative me(Authentication auth) {
        return reps.findByUsername(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    /** Loads an event and makes sure it belongs to the logged-in rep's college. */
    private Event ownedEvent(Long id, Representative rep) {
        Event e = events.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!e.getCollege().getId().equals(rep.getCollege().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This event belongs to another college");
        }
        return e;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        Representative rep = me(auth);
        model.addAttribute("rep", rep);
        model.addAttribute("college", rep.getCollege());
        model.addAttribute("events", events.findByCollegeIdOrderByStartDateAsc(rep.getCollege().getId()));
        return "dashboard";
    }

    @GetMapping("/events/new")
    public String newEvent(Authentication auth, Model model) {
        model.addAttribute("college", me(auth).getCollege());
        model.addAttribute("event", new Event());
        model.addAttribute("types", EventType.values());
        model.addAttribute("formAction", "/rep/events");
        model.addAttribute("heading", "Add an event");
        return "event-form";
    }

    @PostMapping("/events")
    public String create(@Valid @ModelAttribute("event") Event form, BindingResult result,
                         Authentication auth, Model model, RedirectAttributes flash) {
        Representative rep = me(auth);
        validateDates(form, result);
        if (result.hasErrors()) {
            return reshow(model, rep, "/rep/events", "Add an event");
        }
        normalize(form);
        form.setCollege(rep.getCollege());
        events.save(form);
        flash.addFlashAttribute("msg", "Event published.");
        return "redirect:/rep/dashboard";
    }

    @GetMapping("/events/{id}/edit")
    public String edit(@PathVariable Long id, Authentication auth, Model model) {
        Representative rep = me(auth);
        model.addAttribute("college", rep.getCollege());
        model.addAttribute("event", ownedEvent(id, rep));
        model.addAttribute("types", EventType.values());
        model.addAttribute("formAction", "/rep/events/" + id);
        model.addAttribute("heading", "Edit event");
        return "event-form";
    }

    @PostMapping("/events/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("event") Event form, BindingResult result,
                         Authentication auth, Model model, RedirectAttributes flash) {
        Representative rep = me(auth);
        Event existing = ownedEvent(id, rep);
        validateDates(form, result);
        if (result.hasErrors()) {
            return reshow(model, rep, "/rep/events/" + id, "Edit event");
        }
        normalize(form);
        existing.copyFrom(form);
        events.save(existing);
        flash.addFlashAttribute("msg", "Event updated.");
        return "redirect:/rep/dashboard";
    }

    @PostMapping("/events/{id}/delete")
    public String delete(@PathVariable Long id, Authentication auth, RedirectAttributes flash) {
        events.delete(ownedEvent(id, me(auth)));
        flash.addFlashAttribute("msg", "Event deleted.");
        return "redirect:/rep/dashboard";
    }

    @GetMapping("/college")
    public String collegeForm(Authentication auth, Model model) {
        model.addAttribute("college", me(auth).getCollege());
        return "college-form";
    }

    @PostMapping("/college")
    public String collegeSave(@Valid @ModelAttribute("college") College form, BindingResult result,
                              Authentication auth, Model model, RedirectAttributes flash) {
        College existing = me(auth).getCollege();
        if (result.hasErrors()) {
            form.setName(existing.getName()); // name is not editable, but the page shows it
            return "college-form";
        }
        existing.setArea(form.getArea());
        existing.setAbout(form.getAbout());
        existing.setWebsite(form.getWebsite() == null || form.getWebsite().isBlank() ? null : form.getWebsite().trim());
        colleges.save(existing);
        flash.addFlashAttribute("msg", "College profile saved.");
        return "redirect:/rep/dashboard";
    }

    // ---- helpers ----

    private String reshow(Model model, Representative rep, String action, String heading) {
        model.addAttribute("college", rep.getCollege());
        model.addAttribute("types", EventType.values());
        model.addAttribute("formAction", action);
        model.addAttribute("heading", heading);
        return "event-form";
    }

    private void validateDates(Event e, BindingResult result) {
        if (e.getStartDate() != null && e.getEndDate() != null && e.getEndDate().isBefore(e.getStartDate())) {
            result.rejectValue("endDate", "date.order", "End date can't be before the start date");
        }
        if (e.getRegistrationDeadline() != null && e.getEndDate() != null
                && e.getRegistrationDeadline().isAfter(e.getEndDate())) {
            result.rejectValue("registrationDeadline", "deadline.order", "Deadline can't be after the event ends");
        }
    }

    private void normalize(Event e) {
        e.setTitle(e.getTitle().trim());
        e.setRegistrationUrl(blankToNull(e.getRegistrationUrl()));
        e.setFee(blankToNull(e.getFee()));
        e.setPrizePool(blankToNull(e.getPrizePool()));
        e.setContactEmail(blankToNull(e.getContactEmail()));
    }

    private String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
