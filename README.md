<picture>
  <source media="(prefers-color-scheme: dark)" srcset="https://i.imgur.com/vp6ZNRK.png">
  <source media="(prefers-color-scheme: light)" srcset="https://i.imgur.com/Lp5HZ3i.png">
  <img alt="Application logo">
</picture>

-----

This project is a platform that allows users to create, discover and participate in events created by the members of community.

### Core features:
- User registration and login
- Event filtering by past, present and future ones (combinable)
- Event creation with optional uploaded image
- Sign-up and sign-out for/from events
- Commenting on events
- My Events page allowing to manage owned and attended events
- REST API for all future events and filtered by dates
- Thymeleaf frontend

### Tools used to create project:
- IntelliJ IDEA Ultimate
- MySQL Workbench
- Github
- Git
- Trello

## How to install
- Run your favourite IDE
- Prepare MySQL Workbench with
  - One main database
    - Database name: `event_aggregation_app_db`
  - One test database
    - Database name: `event_aggregation_app_db_test`
  - One user
    - Username: `agg_event_user`
    - Password: `ZAQ!2wsx`
    - Grant user all permissions to above databases
    ![permission screenshot](https://i.imgur.com/Rqd6ooI.png)
  - All above parameters can be changed in `application.properties` and/or `application-test.properties`. 
- Open your project in IDE and run it
- By default app will be available under this URL: [localhost:8080/home](http://localhost:8080/home)

## Honorable mentions

Project created with [@degorskiprzemyslaw](https://github.com/degorskiprzemyslaw) and [@Adrie0291](https://github.com/Adrie0291) 
