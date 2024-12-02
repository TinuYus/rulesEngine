WINTER SUPPLEMENT CALCULATOR
This repository contains the application that calculates the amount a person or family is entitled to based on some defined business rules and eligibility.

**FEATURES**
The features of the appplication include:
 - Determines eligibility based on user input
 - Calculates amount due based on family composition and children
 - publishes the results with MQTT broker

**SETTING UP THE IDE ENVIRONMENT**
1. Install the Java Development Kit
2. Download a development tool (Eclipse, Intellij, e.t.c)
3. Create a maven project in the IDE
4. add the MQTT client library and gson dependencies to the pom.xml file
5. Configure the MQTT broker using test.mosquitto.org and port 1883
6. Create a new repository on github
7. Push the project to github

**HOW TO UPDATE THE MQTT TOPIC ID**
1. Go to the Winter Supplement Calculator web app
2. Copy the MQTT topic ID
3. Put the ID in the INPUT_TOPIC and OUTPUT_TOPIC

**UNIT TESTING**
1. Add the JUnit dependency to the pom.xml file
2. Write a test logic for each method in the application
3. Run the application as a JUnit test
4. Ensure there are 0 errors when the test is done
 
