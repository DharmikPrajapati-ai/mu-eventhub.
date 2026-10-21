package com.mueventhub.config;

import com.mueventhub.model.College;
import com.mueventhub.model.Event;
import com.mueventhub.model.EventType;
import com.mueventhub.model.Representative;
import com.mueventhub.repo.CollegeRepository;
import com.mueventhub.repo.EventRepository;
import com.mueventhub.repo.RepresentativeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Loads a few DEMO colleges, representatives and events on first start (empty database only).
 * The events are made-up placeholders with example.com registration links.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final String DEMO_PASSWORD = "password123";

    private final CollegeRepository colleges;
    private final RepresentativeRepository reps;
    private final EventRepository events;
    private final PasswordEncoder encoder;

    public DataSeeder(CollegeRepository colleges, RepresentativeRepository reps,
                      EventRepository events, PasswordEncoder encoder) {
        this.colleges = colleges;
        this.reps = reps;
        this.events = events;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (colleges.count() > 0) {
            return;
        }

        College spit = colleges.save(new College("Sardar Patel Institute of Technology", "Andheri West",
                "Engineering college in Andheri affiliated to the University of Mumbai.", "https://www.spit.ac.in"));
        College tsec = colleges.save(new College("Thadomal Shahani Engineering College", "Bandra West",
                "Engineering college in Bandra affiliated to the University of Mumbai.", "https://tsec.edu"));
        College frcrce = colleges.save(new College("Fr. Conceicao Rodrigues College of Engineering", "Bandra West",
                "Engineering college in Bandra affiliated to the University of Mumbai.", "https://frcrce.ac.in"));
        College tcet = colleges.save(new College("Thakur College of Engineering and Technology", "Kandivali East",
                "Engineering college in Kandivali affiliated to the University of Mumbai.", "https://tcetmumbai.in"));
        College xavier = colleges.save(new College("St. Xavier's College (Autonomous)", "Fort",
                "Arts, science and commerce college in South Mumbai affiliated to the University of Mumbai.",
                "https://xaviers.edu"));
        College mithibai = colleges.save(new College("Mithibai College", "Vile Parle West",
                "Arts, science and commerce college in Vile Parle affiliated to the University of Mumbai.",
                "https://www.mithibai.ac.in"));

        rep("spit_rep", "SPIT Events Team", spit);
        rep("tsec_rep", "TSEC Events Team", tsec);
        rep("frcrce_rep", "FrCRCE Events Team", frcrce);
        rep("tcet_rep", "TCET Events Team", tcet);
        rep("xaviers_rep", "St. Xavier's Events Team", xavier);
        rep("mithibai_rep", "Mithibai Events Team", mithibai);

        LocalDate today = LocalDate.now();

        event(spit, "CodeStorm 24-Hour Hackathon (Demo)", EventType.HACKATHON, "Main Auditorium and Labs",
                today.plusDays(12), today.plusDays(13), today.plusDays(9), "₹300 per team", "₹1,00,000",
                "Build something useful in 24 hours. Tracks: FinTech, HealthTech, Sustainability and Open Innovation. "
                + "Teams of 2-4. Mentors from industry will be around all night. Food and snacks provided.\n\n"
                + "This is a demo listing created for the project.");
        event(spit, "Robo Wars (Demo)", EventType.COMPETITION, "Workshop Block",
                today.plusDays(30), today.plusDays(30), today.plusDays(25), "₹200 per team", "₹25,000",
                "Bring your bot and battle it out in the arena. Weight categories: 8 kg and 15 kg.");

        event(tsec, "TechNova Fest (Demo)", EventType.TECHFEST, "TSEC Campus",
                today.plusDays(20), today.plusDays(22), today.plusDays(15), "Free entry", "₹2,00,000 across events",
                "Three days of coding contests, a gaming arena, robotics, project expos and guest talks.");
        event(tsec, "Intro to Cloud Workshop (Demo)", EventType.WORKSHOP, "Seminar Hall 2",
                today.plusDays(5), today.plusDays(5), today.plusDays(3), "₹100", null,
                "A hands-on beginner workshop: deploy your first web app to the cloud. Bring a laptop.");

        event(frcrce, "Crescendo Cultural Fest (Demo)", EventType.CULTURAL, "Open Air Theatre",
                today.plusDays(45), today.plusDays(47), today.plusDays(35), "₹150 pass", "Prizes worth ₹75,000",
                "Dance, music, drama, fashion show and a celebrity night. Inter-college participation welcome.");
        event(frcrce, "HackFr Web3 and AI Sprint (Demo)", EventType.HACKATHON, "Computer Labs",
                today.plusDays(8), today.plusDays(9), today.plusDays(6), "Free", "₹50,000",
                "A 36-hour sprint on AI and Web3 ideas. Beginners welcome; team formation help on day one.");

        event(tcet, "Elevate Innovation Challenge (Demo)", EventType.HACKATHON, "TCET Innovation Centre",
                today.plusDays(35), today.plusDays(36), today.plusDays(28), "₹250 per team", "₹1,50,000",
                "Pitch a prototype to a jury of founders and investors. Winners get incubation support.");
        event(tcet, "Inter-College Cricket Cup (Demo)", EventType.SPORTS, "TCET Ground",
                today.plusDays(18), today.plusDays(19), today.plusDays(14), "₹2,500 per team", "Trophy and ₹30,000",
                "Knockout T10 tournament for Mumbai University colleges.");

        event(xavier, "Insight Business and Arts Fest (Demo)", EventType.CULTURAL, "College Quadrangle",
                today.plusDays(60), today.plusDays(62), today.plusDays(50), "Free for students", null,
                "Debates, quizzes, photography, theatre and a business case-study contest.");
        event(mithibai, "Quiz League (Demo)", EventType.COMPETITION, "Seminar Hall",
                today.plusDays(3), today.plusDays(3), today.plusDays(2), "Free", "Cash prizes",
                "General, business and pop-culture rounds. Teams of 3.");
        event(mithibai, "Last Year's Fest Recap (Demo, past event)", EventType.CULTURAL, "Campus",
                today.minusDays(40), today.minusDays(38), today.minusDays(45), "Free", null,
                "A past event kept in the list to show how finished events appear.");
    }

    private void rep(String username, String fullName, College college) {
        reps.save(new Representative(username, encoder.encode(DEMO_PASSWORD), fullName, college));
    }

    private void event(College college, String title, EventType type, String venue, LocalDate start, LocalDate end,
                       LocalDate deadline, String fee, String prize, String description) {
        Event e = new Event();
        e.setCollege(college);
        e.setTitle(title);
        e.setType(type);
        e.setVenue(venue);
        e.setStartDate(start);
        e.setEndDate(end);
        e.setRegistrationDeadline(deadline);
        e.setFee(fee);
        e.setPrizePool(prize);
        e.setDescription(description);
        e.setRegistrationUrl("https://example.com/register/" + title.toLowerCase().replaceAll("[^a-z0-9]+", "-"));
        e.setContactEmail("events@example.com");
        events.save(e);
    }
}
