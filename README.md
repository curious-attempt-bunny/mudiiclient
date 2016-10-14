# Mud2 Client

## Getting setup

[Java](https://www.java.com/) is required for this program to run.

1. Download the [jar](http://mud2.net/mudiiclient/mudiiclient.jar).
2. Double click the jar file to launch.

If launching by double clicking does not work then you can run it from a command-line like so:

    java -jar COMPLETE_PATH_TO_JAR_FILE

e.g. in Windows:

    java -jar C:\Downloads\mudiiclient.jar


## Building it for yourself

Assuming you have the JDK installed and ant then you can run:

    ant jar

## Using IntelliJ IDEA

Ensure you add the data directory as a resource folder.

* "Project Structure" menu
* "Modules" navigation menu
* "mudiiclient" module
* "sources" tab
* "data" folder
* Mark as "Resources"
* "OK"