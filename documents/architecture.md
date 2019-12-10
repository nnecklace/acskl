
# Architecture

## Structure

The application is split into two projects. One _server_ project, and one _client_ project.
Both projects are built using a hierarchical structure. Add class in a package below an other one can only and should only have dependencies from classes in a package _directly_ above itself.

## Packages

Server package hierarchy

<img src="https://raw.githubusercontent.com/nnecklace/acskl/master/documents/diagrams/packages-server.png" width="400px"/>

## Business Logic

All business logic is handled on the server side of the application. The main classes which handles all of the business logic are: _UserService_, _MessageService_, and _Database_. _Database_ is the class which handles all communication with the database itself. No other class communicates directly with the database, all have to go through the _Database_ class. Both _User-_ and _MessageService_ use _Database_ as dependency and it is inject in to both services using dependency injection. 

```java
public class MessageService() {
    private Database database;

    public MessageService(Database database) {
        this.database = database;
    }
}
```
[Figure for dependency injection]

Each service will create or use their respective data models; _User_, _Message_. Datamodels relations can be seen in the following diagram:

<img src="https://raw.githubusercontent.com/nnecklace/acskl/master/documents/diagrams/server-data-uml.png" width="400px"/>

The relationship of the whole architecture can be seen in the following diagram

<img src="https://raw.githubusercontent.com/nnecklace/acskl/master/documents/diagrams/architecture-relationship.png" width="300px"/>

## Main Features

### Login 

The following sequence flow dipicts a successful login, and it works as follows. The above sequence is the return sequence.

<img src="https://raw.githubusercontent.com/nnecklace/acskl/master/documents/diagrams/login-sequence.png" width="800px"/>

### Creating message

<img src="https://raw.githubusercontent.com/nnecklace/acskl/master/documents/diagrams/create-message-sequence.png" width="800px"/>

## Main weaknesses

### Multi threading
Server can only handle one connection currently. Multiple clients trying to connect will result in the clients deadlocking since no response will ever be returned to them
since the server has one client already connected to it. This could easily be fixed with designing a multi threading system that would allow concurrent clients connecting.
Implementing such a feature is quite heavy and time consuming.

### UI
All ui logic has been added into one method which makes it extermely hard to deal with. A possible fix would be to _clear the decks_ and rewrite the client as an electron app instead and use a proper ui library. The choice of javafx made the UI much harder to implement.