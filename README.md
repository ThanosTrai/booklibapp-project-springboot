# Book Library Application

This application serves as a client for fetching book data from Google Books API and includes features for user management, book searching, and favoriting books. It's built using Java Spring Boot with JWT authentication for secure user registration and login. This is an educational project.

## Features

- User registration and login using JWT (JSON Web Tokens).
- Search for books by author, title, category, or ISBN.
- Ability to view detailed book information.
- Option to add or remove books from a personal favorites list.
- Profile management where users can update personal details and add a profile picture url.
- Users can delete their accounts.

## Prerequisites

Before running the application, ensure you have the following:

- Java JDK 11 or later.
- Gradle for building and running the project.
- An API key from Google Books API. (https://developers.google.com/books/docs/v1/using#APIKey)
- A secret key for JWT token generation.

## Setup and Local Development

### **Clone the Repository:**
   ```sh
   git clone https://github.com/ThanosTrai/booklibapp-project-springboot.git
   cd booklibapp-project-springboot
   ```

Create an application.properties file in the src/main/resources/ directory with the following content. Be sure to replace YOUR_GOOGLE_BOOKS_API_KEY and YOUR_JWT_SECRET_KEY with your actual keys:
```
google.books.apiKey=YOUR_GOOGLE_BOOKS_API_KEY
jwt.secret-key=YOUR_JWT_SECRET_KEY
```
