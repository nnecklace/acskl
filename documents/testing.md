# Testing

Acskl has been tested thouroughly with both unit, and integration tests. Testing frameworks that have been used are JUnit and Mockito. Mockito dependecy has later on been removed. No End2End tests have been made. Manual, regression, and exploratory testing have been made by hand.

## Unit and Integration tests.

### Overview
All acskl's server side classes have been unit tested with a almost 100% test coverage. Client side has full 100% test coverage, apart from the UI code which has been skipped.

### Integration
`Database.java` tests fully the database integration with actual database writes and reads. 
All classes that depend on `Database.java` use a stub class for simulating database calls.
Client side doesn't perform any integration tests with a fake server or actual server. Only unit tests on client side.

### Test coverage

Server:
<img src="https://raw.githubusercontent.com/nnecklace/acskl/master/documents/diagrams/test-coverage-server.png" width="400px"/>

Server side test coverage is at about 90%. The missing 10% is in `Database.java` where the missing branches are edge cases where the database connection cannot be established and queries fail. This proved to be extremely and unexpectedly difficult to test and was left out.

Client:
<img src="https://raw.githubusercontent.com/nnecklace/acskl/master/documents/diagrams/test-coverage-client.png" width="400px"/>

Client side test coverage is almost 100%. One missing branch is when the connection cannot communicate with server and an exception is thrown. This was also extremely difficult to test in a reliable way and was therefore left out.

## Installation

`documents` folder includes two installation and setup guides `(manual-client.md, manual-server.md)` that would allow user to setup a server and client on any unix environment. Windows has not been tested. Both guides have been tested manually and are up to date.

## Requirements

Everything mentioned in [requirements specs](https://raw.githubusercontent.com/nnecklace/acskl/master/documents/requirement-specification.md) has been tested on both linux mint and mac osx.