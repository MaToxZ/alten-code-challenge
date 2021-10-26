## Alten code challenge

### Overview

Implementation a booking API for the very last hotel in Cancun assuming the scenario of Post-Covid situation. People are now free to travel everywhere but because of the pandemic, a lot of hotels went bankrupt. Some former famous travel places are left with only one hotel. Taking in count the following requirements:
- API will be maintained by the hotel’s IT department.
- As it’s the very last hotel, the quality of service must be 99.99 to 100% => no downtime
- For the purpose of the test, we assume the hotel has only one room available
- To give a chance to everyone to book the room, the stay can’t be longer than 3 days and can’t be reserved more than 30 days in advance.
- All reservations start at least the next day of booking,
- To simplify the use case, a “DAY’ in the hotel room starts from 00:00 to 23:59:59.
- Every end-user can check the room availability, place a reservation, cancel it or modify it.
- To simplify the API is insecure.

---

### Documentation
The implementation of this project was done in 3 separate Docker containers that holds:

- PostgreSQL database
- Java backend (Spring Boot)
- Angular frontend

For further technical information about the project, please refer to the [Documentation Folder](https://github.com/MaToxZ/alten-code-challenge/tree/main/Documentation).

---

### Prerequisites

To be able to run the project in your machine, you need to consider the following and mandatory requisites:

- Docker Suite needs to be installed in the host machine where this project is going to be run (install **Docker** on [Ubuntu](https://docs.docker.com/install/linux/docker-ce/ubuntu/), [Windows](https://docs.docker.com/docker-for-windows/install/) , [Mac](https://docs.docker.com/docker-for-mac/install/) .).
- The application is going to use some of the host machine ports. Be aware that ports ***8080***, ***5432*** and ***4200***  need to be available (not used) by the moment you run the application.
- You need to be connected to the internet since docker is going to download project dependencies to subsequently be able to start up the app.


---

### Run Steps

After all the prerequisites are guaranteed, you can proceed to run the project. for that, you’ll need to open a terminal and navigate to the project root folder. Once you’re there, you just need to run the following command:

```
$ docker-compose up -d
```

When you're done using the app, you can shut it down by running the following command

```
$ docker-compose down
```

Once the project is up and running you can access the following URLs:

- [Alten Challenge UI](http://localhost:4200)
- [Alten Challenge API Documentation](http://localhost:8080/api/swagger-ui)

---

### Application Related Information

#### PostgresDB

In the documentation folder, you can find an [ERD Diagram](https://github.com/MaToxZ/alten-code-challenge/blob/main/Documentation/Alten_DB_ERD_Diagram.png) with the DB Structure. On this implementation we're using an one and only schema **public**

- Host: *localhost*
- Database: *alten*
- User: *postgres*
- Password: *docker*

### Other

There is a postman collection you can use to test the API services.