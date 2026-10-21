package com.mueventhub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "events")
public class Event {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String title;

    @NotNull(message = "Choose a category")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType type;

    @NotBlank(message = "Description is required")
    @Size(max = 3000)
    @Column(nullable = false, length = 3000)
    private String description;

    @NotBlank(message = "Venue is required")
    @Size(max = 200)
    private String venue;

    @NotNull(message = "Start date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate registrationDeadline;

    /** Direct link to the registration form (Google Form, Unstop, Devfolio, college portal...). */
    @Size(max = 500)
    @Pattern(regexp = "^$|^https?://\\S+$", message = "Must be a full link starting with http:// or https://")
    @Column(length = 500)
    private String registrationUrl;

    @Size(max = 60)
    private String fee;

    @Size(max = 100)
    private String prizePool;

    @Email
    @Size(max = 120)
    private String contactEmail;

    @ManyToOne(optional = false)
    private College college;

    public Event() {
    }

    // ---- derived helpers used by the templates ----

    public boolean isPast() {
        return endDate != null && endDate.isBefore(LocalDate.now());
    }

    public boolean isOngoing() {
        LocalDate today = LocalDate.now();
        return startDate != null && endDate != null && !startDate.isAfter(today) && !endDate.isBefore(today);
    }

    public boolean isHasLink() {
        return registrationUrl != null && !registrationUrl.isBlank();
    }

    public boolean isRegistrationOpen() {
        return isHasLink() && !isPast()
                && (registrationDeadline == null || !registrationDeadline.isBefore(LocalDate.now()));
    }

    public String getDateRange() {
        if (startDate == null) return "";
        if (endDate == null || endDate.equals(startDate)) return startDate.format(FMT);
        return startDate.format(FMT) + " – " + endDate.format(FMT);
    }

    public String getDeadlineText() {
        return registrationDeadline == null ? null : registrationDeadline.format(FMT);
    }

    /** Copies the editable fields (never id/college) from a submitted form. */
    public void copyFrom(Event o) {
        this.title = o.title;
        this.type = o.type;
        this.description = o.description;
        this.venue = o.venue;
        this.startDate = o.startDate;
        this.endDate = o.endDate;
        this.registrationDeadline = o.registrationDeadline;
        this.registrationUrl = o.registrationUrl;
        this.fee = o.fee;
        this.prizePool = o.prizePool;
        this.contactEmail = o.contactEmail;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public EventType getType() { return type; }
    public void setType(EventType type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public LocalDate getRegistrationDeadline() { return registrationDeadline; }
    public void setRegistrationDeadline(LocalDate d) { this.registrationDeadline = d; }
    public String getRegistrationUrl() { return registrationUrl; }
    public void setRegistrationUrl(String registrationUrl) { this.registrationUrl = registrationUrl; }
    public String getFee() { return fee; }
    public void setFee(String fee) { this.fee = fee; }
    public String getPrizePool() { return prizePool; }
    public void setPrizePool(String prizePool) { this.prizePool = prizePool; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public College getCollege() { return college; }
    public void setCollege(College college) { this.college = college; }
}
