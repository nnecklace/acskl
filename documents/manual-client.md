## Client 

Manual for setting up the client side of acskl. All the steps assume you are in the `client` directory

### Setup & Run

Install dependencies with the following command

```mvn install```

To run and compile the server with the following command 

```mvn compile exec:java -Dexec.mainClass=client.App```

### Tests

Run all unit and integration test with the following command

```mvn test```

### Jacoco report

To generate and create a test coverage report, run the following command

```mvn test jacoco:report``` 

A jacoco report will be generated and can be found in `target/site/jacoco/index.html`

### Checkstyle

The project uses checkstyle for making sure that the code follows the specified code formatting style.
`checkstyle.xml` includes the rules that the project follows.

Generate a checkstyle report with the following command

```mvn jxr:jxr checkstyle:checkstyle```

A checkstyle report will be generated and can be found in `target/site/checkstyle.html`

### Javadoc

Generating javadoc can be done with running:

```mvn javadoc:javadoc```

The generated docs will be located in the `target/site/apidocs` folder.

### Package

Generating a jar file of the project can be done with the following command:

```mvn package```

The jar file will be located in the `target` folder. Note! There will be two jars generated, the correct one is the one without _original_ in the name.