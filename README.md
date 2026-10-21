# MU EventHub

A Java (Spring Boot + Thymeleaf + H2) website listing hackathons, tech fests and other events from
colleges affiliated to the University of Mumbai. College representatives log in to publish and edit
their own events and add a direct registration link.

## Run

Requirements: JDK 17+ and Maven 3.8+ (internet needed the first time to download dependencies).

```bash
mvn spring-boot:run
```

Open http://localhost:8080

Or build a jar: `mvn package` then `java -jar target/mu-eventhub-1.0.0.jar`.

In IntelliJ/Eclipse/VS Code: open the folder as a Maven project and run `EventHubApplication`.

## Demo data (loaded on first start)

6 colleges and 11 placeholder events (dates are relative to today, links point to example.com).

| Username | College |
|---|---|
| `spit_rep` | Sardar Patel Institute of Technology |
| `tsec_rep` | Thadomal Shahani Engineering College |
| `frcrce_rep` | Fr. Conceicao Rodrigues College of Engineering |
| `tcet_rep` | Thakur College of Engineering and Technology |
| `xaviers_rep` | St. Xavier's College |
| `mithibai_rep` | Mithibai College |

Password for all: `password123`

New representatives sign up at `/signup` with the invite code from `application.properties`
(`app.invite-code`, default `MU2026`). Change it and the demo passwords before real use.

To reset all data, stop the app and delete the `data/` folder.

## What it does

- Public: home page, search and filter (category, college, include past), event detail with a
  Register button, college list and college pages.
- Representatives (`/rep/**`, login required): dashboard of own college's events, add / edit / delete
  events, set the registration link, deadline, fee, prizes and contact, edit the college profile.
- A representative can only change events of their own college (checked on the server).
- Registration links must start with http:// or https://. Passwords are stored with BCrypt. CSRF protection is on.

## Layout

```
src/main/java/com/mueventhub
  model/    Event, College, Representative, EventType (JPA entities)
  repo/     Spring Data repositories
  service/  EventService (search/filter)
  config/   SecurityConfig, DataSeeder (demo data)
  web/      PublicController, AuthController, RepController
src/main/resources
  templates/  Thymeleaf pages     static/css/style.css
  application.properties
```

## Ideas to extend

Poster image upload, calendar (.ics) export, email verification for representatives, an admin role to
approve new colleges, switching H2 to MySQL (change the datasource in `application.properties`).
