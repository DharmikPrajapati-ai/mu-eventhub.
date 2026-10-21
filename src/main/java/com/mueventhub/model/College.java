package com.mueventhub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "colleges")
public class College {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 200)
    private String name;

    @Size(max = 100)
    private String area;

    @Size(max = 1000)
    @Column(length = 1000)
    private String about;

    @Size(max = 300)
    @Pattern(regexp = "^$|^https?://\\S+$", message = "Must start with http:// or https://")
    private String website;

    public College() {
    }

    public College(String name, String area, String about, String website) {
        this.name = name;
        this.area = area;
        this.about = about;
        this.website = website;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
}
