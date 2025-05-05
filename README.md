# SEG1-WEBAPP-V2

Chat application for CS4800 Software Engineering, CSU Stanislaus, Spring 2025 semester.

## Description

A chat application with basic chat functionality including user created rooms and searching for chat rooms using tags.

## Getting Started

### Dependencies

* Java 24 runtime environment.

### Installing

**Step 1**

Ensure that a Java 24 runtime is installed:

https://www.oracle.com/java/technologies/downloads/

**Step 2**

*Ensure that src\main\resources\application.properties has the desired port number before attempting to build the jar file.*

```
mvnw.cmd package -f pom.xml

```

### Executing program

Using external terminal, start the program using the jar file created in the previous step

```
java -jar ./target/webapp-0.0.1-SNAPSHOT.jar

```

### Live version

http://ec2-54-197-20-205.compute-1.amazonaws.com/

Note: This may change periodically (and will need to be updated). If it does not work then please let me know.

## License

This project is not licensed.