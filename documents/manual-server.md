## Server 

Manual for setting up the server side of acskl. All the steps assume you are in the `server` directory


### Database

Before setting up the server. Make sure you have sqlite3 installed. 
To start the server you first have to make sure you have the database setup. The server assumes a sqlite database inside the projects resource folder.

If the folder doesn't exist, you can create by first `cd src/main` then running 

```mkdir resources && cd resources```

Then:

```touch database.db``` 

Will create a sample database (Note* make sure you are in the resouce directory when creating the database). After creating the database you have to run the migrations for the database. 

```sqlite3 database.db < ../../../migrations/migrations.sql``` 

### Setup & Run

Install dependencies with the following command

```mvn install```

To run and compile the server with the following command 

```mvn compile exec:java -Dexec.mainClass=server.App```

### Tests

Run all unit and integration test with the following command

```mvn test```

### Jacoco report

To generate `jacoco` first install the `jacoco:prepare-agent`

```mvn jacoco:prepare-agent install```

Then create a test coverage report with the following command

```mvn test jacoco:report``` 

A jacoco report will be generated and can be found in `target/site/jacoco/index.html`

### Checkstyle

The project uses checkstyle for making sure that the code follows the specified code formatting style.
`checkstyle.xml` includes the rules that the project follows.

Generate a checkstyle report with the following command

```mvn jxr:jxr checkstyle:checkstyle```

A checkstyle report will be generated and can be found in `target/site/checkstyle.html`