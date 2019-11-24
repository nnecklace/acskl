## Server 

Manual for setting up the server side of acskl. All the steps assume you are in the `server` directory


### Database

Before setting up the server. Make sure you have sqlite3 installed. 
To start the server you first have to make sure you have the database setup. The server assumes a sqlite database. 

```touch database.db``` 

Will create a sample database (Note* make sure you are in the server directory when creating the database). After creating the database you have to run the migrations for the database. 

```sqlite3 database.db < migrations/migrations.sql``` 

### Setup & Run

Install dependencies with the following command

```mvn install```

To run and compile the server with the following command 

```mvn compile exec:java -Dexec.mainClass=server.App```

### Tests

Run all unit and integration test with the following command

```mvn test```

#### Jacoco report

To generate `jacoco` first install the `jacoco:prepare-agent`

```mvn jacoco:prepare-agent install```

Then create a test coverage report with the following command

```mvn test jacoco:report``` 