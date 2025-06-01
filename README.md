# EasyTourney Backend

This repository contains the backend service for **EasyTourney**, a platform designed to facilitate the organization and management of esports tournaments. The backend is developed using **Spring Boot** and **Java**, providing RESTful APIs to support frontend operations.

## 🚀 Features

- User authentication and authorization
- Tournament creation and management
- Match scheduling and result tracking
- Integration with frontend applications
- Environment-specific configurations

## 🛠️ Technologies Used

- Java 17+
- Spring Boot
- Gradle
- Jenkins (for CI/CD)
- PostgreSQL (or compatible relational database)

## 📦 Getting Started

### Prerequisites

- Java Development Kit (JDK) 17 or higher
- Gradle
- PostgreSQL database (Create database: EasyTourney and Flyway will automatically create table)

### Installation

1. **Clone the repository:**

```bash
git clone https://github.com/EasyTourney/easy-tourney-be.git
cd easy-tourney-be
```

2. **Configure the application:**

Create an `application-dev.properties` file in the `src/main/resources` directory with your local database configuration:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/easytourney
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. **Run the application:**

#### Option 1: Using IntelliJ IDEA (EASY)

1. Open the project in IntelliJ IDEA.
2. Make sure the project SDK is set to Java 17.
3. Ensure Gradle is set up as the build system.
4. Open `EasyTourneyBeApplication.java` located in `src/main/java/...`
5. Right-click the file and choose **Run 'EasyTourneyBeApplication'**.

#### Option 2: Using Gradle CLI

```bash
./gradlew bootRun --args='--spring.config.location=classpath:application-dev.properties'
```

This will start the backend server locally.

## 🧪 Running Tests

To execute the test suite, run:

```bash
./gradlew test
```

This will run all unit and integration tests.

## 📁 Project Structure

```bash
├── src/
│   ├── main/
│   │   ├── java/           # Application source code
│   │   └── resources/      # Configuration files
│   └── test/               # Test cases
├── build.gradle            # Gradle build configuration
├── Jenkinsfile             # CI/CD pipeline configuration
└── README.md               # Project documentation
```

## 🚀 Deployment

For production deployment, ensure that the `application.properties` file contains the appropriate database and environment configurations.
You can start the application with:

```bash
./gradlew bootRun --args='--spring.config.location=classpath:application.properties'
```

## 📄 License

This project is licensed under the **MIT License**.
See the [LICENSE](./LICENSE) file for details.

---

> For the frontend project, see [easy-tourney-fe](https://github.com/EasyTourney/easy-tourney-fe)
