# acskl

Acskl -- _anagram for slack_ -- is a simple tcp server client chat application built with Java. The app allows you to send message from a tcp message client to the tcp server. This application is also my submission for Helsinki Univeristiy's _Ohjelmisto tekniikka (Software technique)_ course. 

## Documentation

[Requirement Specification](https://github.com/nnecklace/acskl/blob/master/documents/requirement-specification.md)

[Hour reporting](https://github.com/nnecklace/acskl/blob/master/documents/hours.md)

[Server Manual](https://github.com/nnecklace/acskl/blob/master/documents/manual-server.md)

[Architecture](https://github.com/nnecklace/acskl/blob/master/documents/architecture.md)

## Release

[Server Release](https://github.com/nnecklace/acskl/releases)

## Instructions

Detailed instructions on setup can be found in the both client and server manuals located in `documents`.

## Commands

### Server 

Make sure you are in the `server` directory for these commands to work.

#### Database setup

Create a resources folder inside 

```src/main```

```mkdir resources```

Create a sample database inside the resources folder.

```touch database.db``` 

Setup database with migrations

```sqlite3 database.db < ../../../migrations/migrations.sql``` 

#### Compile & Run

```mvn compile exec:java -Dexec.mainClass=server.App```

#### Tests

Run tests

```mvn test```

To generate `jacoco` report install the agent with:

```mvn jacoco:prepare-agent install```

Generate report

```mvn test jacoco:report``` 

#### Checkstyle

Run and generate checkstyle report

```mvn jxr:jxr checkstyle:checkstyle```

### Client

Make sure you are in the `client` directory when running the following commands.

#### Compile & Run

```mvn compile exec:java -Dexec.mainClass=client.App```

#### Tests

Run tests

```mvn test```

To generate `jacoco` report install the agent with:

```mvn jacoco:prepare-agent install```

Generate report

```mvn test jacoco:report``` 

The UI code is skipped for jacoco.

#### Checkstyle

Run and generate checkstyle report

```mvn jxr:jxr checkstyle:checkstyle```