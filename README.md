# Restaurant Reservation System

A full-stack restaurant reservation system built with **Spring Boot 4**,
**PostgreSQL 16**, and a lightweight **Vanilla JS frontend**.

This project demonstrates clean layered architecture, business
validation logic, recommendation scoring, and full Docker-based
environment setup.

------------------------------------------------------------------------

## Features

-   View real-time table availability
-   Intelligent table recommendation system
-   Reservation creation with overlap validation
-   Capacity and feature matching logic
-   Visual restaurant floor layout (HTML + CSS Grid)
-   Unit & integration tests
-   Fully containerized (App + Database)

------------------------------------------------------------------------

## Tech Stack

### Backend

-   Java 25 (LTS)
-   Spring Boot 4
-   Spring Data JPA
-   Hibernate ORM
-   PostgreSQL 16
-   Gradle

### Frontend

-   HTML5
-   CSS3 (Grid layout)
-   Vanilla JavaScript (Fetch API)

### DevOps

-   Docker
-   Docker Compose

------------------------------------------------------------------------

## Project Architecture

Controller → Service → Repository → Database

-   Controller -- HTTP layer
-   Service -- Business logic & validation
-   Repository -- Data access (Spring Data JPA)
-   Database -- PostgreSQL

The database is designed in 3rd Normal Form (3NF).

------------------------------------------------------------------------

## Running the Full Stack with Docker

Make sure Docker Desktop is running.

### Build and start everything:

``` bash
docker compose up --build
```

Application URL:

http://localhost:8080

To stop:

``` bash
docker compose down
```

------------------------------------------------------------------------

## Running Locally (Without Docker for App)

Start PostgreSQL only:

``` bash
docker compose up -d postgres
```

Run application:

Windows:

``` bash
.\gradlew bootRun
```

Mac/Linux:

``` bash
./gradlew bootRun
```

------------------------------------------------------------------------

## Running Tests

``` bash
./gradlew test
```

### Test Coverage Includes:

-   Successful reservation creation
-   Capacity validation failure
-   Overlapping reservation detection
-   Recommendation scoring logic

------------------------------------------------------------------------

## API Endpoints

### Check Availability

GET:

    /api/availability?date=2026-03-01&time=18:00&partySize=4&zone=MAIN_HALL

Returns: - Table list - Occupied status - Suitability indicator

------------------------------------------------------------------------

### Get Recommendation

GET:

    /api/recommendation?date=2026-03-01&time=18:00&partySize=4&zone=MAIN_HALL&features=WINDOW,QUIET

Returns: - Best recommended table - Top 3 candidates - Score breakdown

Scoring considers: - Capacity fit (minimal unused seats preferred) -
Zone match - Feature match

------------------------------------------------------------------------

### Create Reservation

POST:

    /api/reservations

Body:

``` json
{
  "tableId": 1,
  "date": "2026-03-01",
  "time": "18:00",
  "partySize": 4
}
```

Validation rules: - Table must exist - Party size ≤ table capacity - No
overlapping reservations (2-hour fixed duration) - Returns proper HTTP
status codes (400 / 409)

------------------------------------------------------------------------

## Design Decisions

-   Fixed reservation duration: 2 hours
-   Overlap detection via time-window intersection
-   Recommendation prioritizes minimal unused capacity
-   Separation of concerns across layers
-   DTOs used to isolate API contract from entities

------------------------------------------------------------------------
## Time Spent

Total development time: ~18 hours

Breakdown:
- Project setup and architecture design – 2h
- Database model and entities – 2h
- Reservation logic and validation – 3h
- Recommendation algorithm – 2h
- Availability calculation and filtering – 2h
- Frontend floor layout and UI – 3h
- Tests – 2h
- Docker setup and environment configuration – 1h
- Documentation and cleanup – 1h
------------------------------------------------------------------------
## Challenges & Solutions

One challenge was implementing correct reservation overlap detection.

Two reservations overlap when:

existing.startTime < requestedEnd 
AND
existing.endTime > requestedStart

This ensures that partially overlapping reservations are also detected.

Another challenge was designing a simple but meaningful table
recommendation algorithm.

The implemented scoring system prioritizes:

1. Minimal unused seats (capacity fit)
2. Matching requested zone
3. Matching requested features
------------------------------------------------------------------------
## Assumptions

Several assumptions were made while implementing the system:

- Reservation duration is fixed to 2 hours.
- Each reservation occupies a single table.
- Party size cannot exceed table capacity.
- Tables cannot be merged automatically.
- Table positions are represented using simple X/Y coordinates.

------------------------------------------------------------------------
## External Resources
The following resources were used during development:

- Spring Boot documentation
- Mockito and JUnit documentation

AI tools were occasionally used for small code suggestions and
documentation wording, but the architecture and core logic
were implemented manually.
------------------------------------------------------------------------
## Future Improvements

-   Admin panel for managing tables
-   Authentication & authorization
-   Integration tests with Testcontainers
-   CI pipeline setup
-   Deployment to cloud (Render / Railway / VPS)

------------------------------------------------------------------------

## Author

Artur Kütt
