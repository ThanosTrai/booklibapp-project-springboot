# Book Library Backend - Educational Project

This repository contains the back-end code for the Book Library Application, a full-stack project built with Java Spring Boot. This project is built using Java Spring Boot and is intended primarily for educational purposes. It is designed to work in conjunction with the Angular front-end, which can be found in a separate repository. 

This application serves as a client for fetching book data from Google Books API and includes features for user management, book searching, and favoriting books. It's built using Java Spring Boot with JWT authentication for secure user registration and login. 

## Related Repository

**Front-end (Angular):** [Book Library Front-End Repository](https://github.com/ThanosTrai/booklibapp-project-angular)<br>
Be sure to also set up the front-end part of this application for full functionality.

## Features

- User registration and login using JWT (JSON Web Tokens).
- Search for books by title, author, category, or ISBN.
- Ability to view detailed book information.
- Option to add or remove books from a personal favorites list.
- Profile management where users can update personal details and add a profile picture url.
- Users can delete their accounts.

## Prerequisites

Before running the application, ensure you have the following:

- Java JDK 11 or later.
- Gradle for building and running the project.
- An API key from [Google Books API](https://developers.google.com/books/docs/v1/using#APIKey)
- A secret key for JWT token generation.

## Setup and Local Development

### **Clone the Repository:**
   ```sh
   git clone https://github.com/ThanosTrai/booklibapp-project-springboot.git
   cd booklibapp-project-springboot
   ```

### **Configure Application Properties**
Create an application.properties file in the src/main/resources/ directory with the following content. Be sure to replace YOUR_GOOGLE_BOOKS_API_KEY and YOUR_JWT_SECRET_KEY with your actual keys:
   ```
   google.books.apiKey=YOUR_GOOGLE_BOOKS_API_KEY
   jwt.secret-key=YOUR_JWT_SECRET_KEY
   ```

### **Build the Application**
Use Gradle to build the application. Run the following command in the root directory of your project:
   ```
   ./gradlew build
   ```
If you are using a Windows system, use:
   ```
   gradlew.bat build
   ```

### **Run the Application**
After successfully building the project, you can start it by running:
   ```
   ./gradlew bootRun
   ```
For Windows:
   ```
   gradlew.bat bootRun
   ```
The application will now be running and accessible at http://localhost:8080.

### **Explore the Application**
With the server running, you can explore various functionalities such as user registration and login, book searches, managing favorites, and more, through the provided endpoints.

## Database Configuration

This application was developed and tested with MySQL as its database. It includes the MySQL JDBC driver (`mysql-connector-java`) in its dependencies.

To set up the application with a MySQL database:

1. **Install MySQL**: Download and install MySQL from the [official MySQL website](https://www.mysql.com/).

2. **Create a MySQL Database**: Create a new database in MySQL to be used by the application.

3. **Configure `application.properties`**: In the `src/main/resources/` directory, create or update the `application.properties` file with your MySQL connection details:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name?serverTimezone=UTC
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   ```

## Testing
- The API endpoints have been thorougly tested using Postman.
- For endpoints that require authentication, set the Authorization Type to Bearer Token in Postman.
- After registering and logging in through the API, use the JWT token provided in the response as the Bearer Token for authenticated requests.
