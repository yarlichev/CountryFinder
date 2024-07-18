# Welcome to the CountryFinder application repository!

This repository contains the CountryFinder application, which helps users to find out which 
country a phone number belongs to. This application can:

1. Parse wiki page with phone codes and assigned countries (url is specified in application.properties).
2. Save extracted data into a database.
3. In case of failed wiki page parsing, application can use old data, if it is not expired (expiration period specified in application.properties)
4. If there is no valid data users can see a warning message on a main page. 

To for successful building, compilation and testing, please you have
1. Java 17+
2. Configured JAVA_HOME (Java 17+)
3. Docker (To start container for test AvailableAppScenario IT). Otherwise, disable the test

To get test reports type:
mnv clean test site

Reports will be available in target/site/surefire-report.html