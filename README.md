Dev note:

Run application local with command:
./gradlew bootRun --args='--spring.config.location=classpath:application-dev.properties'

Run application server with command:
./gradlew bootRun --args='--spring.config.location=classpath:application.properties'

In the application.properties file, you keep the server database configuration unchanged. 
When coding on your local machine, you adjust the database configuration in the application-dev.properties file accordingly and run tests locally.

Note:

1. If adding new configurations to the application-dev.properties file, make sure to also add them to the application.properties file.
2. Ensure that "gradle build" successfully locally before creating a pull request.
3. In the future, when using .env, everyone applies it to the application-dev file. DO NOT APPLY .ENV TO THE APPLICATION.PROPERTIES FILE.


