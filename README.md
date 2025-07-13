# EventSync

**EventSync** is a full-stack event management application built with Angular and Spring Boot. It allows users to register, log in, browse, and book events, while admins can manage events and view participant feedback.

*Backend deployed url* `https://eventsync-production.up.railway.app/swagger-ui/index.html`

## Features

### 👥 Users
- Register and log in with JWT-based authentication
- View available events
- Leave feedback after attending events

### 🛠️ Admins
- Create events

## Tech Stack

- **Frontend:** Angular 20, SCSS
- **Backend:** Java 21, Spring Boot 3+, Spring Security, WebSocket
- **Database:** MySQL
- **Deployment:** Railway (backend)

## UI Preview

<img src="./frontend/public/demo/events_page.png" alt="Events page" width="300" />
<img src="./frontend/public/demo/login_page.png" alt="Login page" width="300" />

## Getting Started

1. Make sure you have installed node.js. (version 22+) If not download it and install.
2. Download or clone this repository.
3. Create a local MySQL database. If you’re using Docker, a sample container setup is recommended.
4. Open `backend` folder with our ide.
5. In the root project directory you will find `.env.example`
- Copy this file in the root project directory.
- Rename it `.env`
- Fill in your database credentials, JWT secret, and any other required values.
- Launch the backend with your ide or `./mvnw spring-boot:run` or `docker compose up --build`

6. Open frontend folder with your favorite ide. Open terminal and make sure you are in frontend directory.
And type `npm install`
7. Start the frontend with `ng serve -o` or `docker compose up --build`
8. If browser is not opened automatically type in url `localhost:4200`


### Endpoints 

Base url `localhost:8080/api/v1`

### Authentication

- POST `/auth/register` - Register new user
- POST `/auth/login` - User login
- POST `/auth/logout` - User logout

### Events

- GET `/events` - Get all events
- POST `/events` - Create new event (Admin only)
- POST `/events/{eventId}` - Get event by id

### Feedback

- GET `/events/{eventId}` - Get event feedback by id
- POST `/events{eventId}` - Create feedback for event
- GET `/events/{eventId}/summary` - Get event summary by id

## Author
Ovidijus Eitminavičius