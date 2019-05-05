# Group 10 - Setup #

## Continuous Integration ##

We used Gitlab CI, thus when a commit is pushed to this repository, our CI repositiory, which is accessable under [Gr10_CI_Project](https://gitlab.com/insch/sqm_gr10), pulls the new commit and performs our defined integration jobs.
The state can either be viewed directly in the CI section under Pipelines or better directly for the commit.

## Test Coverage ##

To check the projects test coverage we extended on the existing CI pipeline. Adding in this procedure Jacoco and Codecov.

**Access the Codecov report** via the badge:
[![codecov](https://codecov.io/gl/insch/sqm_gr10/branch/master/graph/badge.svg)](https://codecov.io/gl/insch/sqm_gr10)

<br/><br/>

# University Information System #

The University Information System (UIS) is a web-based management software for universities. <br/>
Students and faculty staff can organize and administrate their courses, the system furthermore assists students by suggesting future courses in a smart way.

## Setup and running ##

Execute the steps described below on your command-line.

Step 1 has only be done once (as long as you do not delete the files).

### 1. Setup ###

Download Javascript Libraries:

```
cd application/src/main/resources/static
npm install
./node_modules/.bin/webpack
```

_Prerequisite:_ npm (Node.js) has to be available on your computer.

### 2. Build ###

Build your project using Maven:

```
mvn clean install
```
or (in case you want to skip test execution)
```
mvn clean install -DskipTests
```

_Prerequisite:_  Maven 3.3.x or higher and Java 1.8 has to be available on your computer.

### 3. Execution ###

Deploy the application on Spring Boot's Embedded Apache Tomcat 7 Application Server:

```
cd application
mvn spring-boot:run
```

After start-up, the application is available under:
http://localhost:8080/

### 4. Sample Data and User Accounts ###

```at.ac.tuwien.inso.sqm.initializer.DataInitializer``` automatically generates sample data for the application.<br/>
Log in with the following credentials:

```
Student: emma - pass
Lecturer: eric - pass
Administrator: admin - pass
```



