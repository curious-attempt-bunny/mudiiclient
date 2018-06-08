# Mud2 Client

## Getting setup

Download and install mudiiclient via [mud2.net](http://mud2.net).

## Building it for yourself

Assuming you have the JDK installed and ant then you can run:

    ./gradlew jar

Or in Windows:

    gradlew jar

You should then be able to run it like so:

    java -jar build/libs/mudiiclient.jar

Or in Windows:

    java -jar build\libs\mudiiclient.jar

## Setting up a developer environment

Setup an IntelliJ Idea project like so:

    ./gradlew idea

## Running the tests

    ./gradlew test
