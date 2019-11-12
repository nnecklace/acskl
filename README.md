# acskl

Acskl -- _anagram for slack_ -- is a simple tcp server client chat application built with Java. The app allows you to send message from a tcp message client to the tcp server. This application is also my submission for Helsinki Univeristiy's _Ohjelmisto tekniikka (Software technique)_ course. 

## Server 

Before setting up the server. Make sure you have sqlite3 installed. 

To start the server you first have to make sure you have the database setup. The server assumes a sqlite database. `touch database.db` will create a sample database (Note* make sure you are in the server directory when creating the database). After creating the database you have to run the migrations for the database. 

```sqlite3 database.db < migrations/migrations.sql``` 

Again, make sure you are in the server directory when running this command.

To run and compile the server, cd into to `server` directory and run `mvn compile exec:java -Dexec.mainClass=acskl.server.App`

### Server tests

Run full test coverage report for the server by cd:ing into `server` directory and running:

```mvn test```

To generate `jacoco` report run:

```mvn jacoco:prepare-agent install```

and then:

```mvn test jacoco:report``` 