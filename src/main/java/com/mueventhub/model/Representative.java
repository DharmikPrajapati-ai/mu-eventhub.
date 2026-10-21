package com.mueventhub.model;

import jakarta.persistence.*;

@Entity
@Table(name = "representatives")
public class Representative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String fullName;

    @ManyToOne(optional = false)
    private College college;

    public Representative() {
    }

    public Representative(String username, String passwordHash, String fullName, College college) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.college = college;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public College getCollege() { return college; }
    public void setCollege(College college) { this.college = college; }
}
