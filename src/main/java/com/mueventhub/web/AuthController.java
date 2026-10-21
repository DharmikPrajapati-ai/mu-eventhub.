package com.mueventhub.web;

import com.mueventhub.model.College;
import com.mueventhub.model.Representative;
import com.mueventhub.repo.CollegeRepository;
import com.mueventhub.repo.RepresentativeRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    public static class SignupForm {
        @NotBlank @Size(max = 100)
        private String fullName;
        @NotBlank @Size(min = 3, max = 30)
        @Pattern(regexp = "^[A-Za-z0-9_.-]+$", message = "Letters, digits, . _ - only")
        private String username;
        @NotBlank @Size(min = 8, max = 72, message = "Use at least 8 characters")
        private String password;
        @NotBlank @Size(max = 200)
        private String collegeName;
        @Size(max = 100)
        private String area;
        @NotBlank
        private String inviteCode;

        public String getFullName() { return fullName; }
        public void setFullName(String v) { this.fullName = v; }
        public String getUsername() { return username; }
        public void setUsername(String v) { this.username = v; }
        public String getPassword() { return password; }
        public void setPassword(String v) { this.password = v; }
        public String getCollegeName() { return collegeName; }
        public void setCollegeName(String v) { this.collegeName = v; }
        public String getArea() { return area; }
        public void setArea(String v) { this.area = v; }
        public String getInviteCode() { return inviteCode; }
        public void setInviteCode(String v) { this.inviteCode = v; }
    }

    private final RepresentativeRepository reps;
    private final CollegeRepository colleges;
    private final PasswordEncoder encoder;
    private final String inviteCode;

    public AuthController(RepresentativeRepository reps, CollegeRepository colleges, PasswordEncoder encoder,
                          @Value("${app.invite-code}") String inviteCode) {
        this.reps = reps;
        this.colleges = colleges;
        this.encoder = encoder;
        this.inviteCode = inviteCode;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("form", new SignupForm());
        model.addAttribute("colleges", colleges.findAllByOrderByNameAsc());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("form") SignupForm form, BindingResult result, Model model) {
        if (!inviteCode.equals(form.getInviteCode())) {
            result.rejectValue("inviteCode", "invite.bad", "Invalid invite code");
        }
        if (form.getUsername() != null && reps.existsByUsernameIgnoreCase(form.getUsername())) {
            result.rejectValue("username", "username.taken", "Username already taken");
        }
        if (result.hasErrors()) {
            model.addAttribute("colleges", colleges.findAllByOrderByNameAsc());
            return "signup";
        }
        String name = form.getCollegeName().trim();
        College college = colleges.findByNameIgnoreCase(name).orElseGet(() -> {
            College c = new College();
            c.setName(name);
            c.setArea(form.getArea());
            return colleges.save(c);
        });
        reps.save(new Representative(form.getUsername(), encoder.encode(form.getPassword()),
                form.getFullName().trim(), college));
        return "redirect:/login?registered";
    }
}
