# Book Library Application

This application serves as a client for fetching book data from Google Books API and includes features for user management, book searching, and favoriting books. It's built using Java Spring Boot with JWT authentication for secure user registration and login.

## Features

- User registration and login with JWT authentication (access and refresh tokens)
- Search for books by author, title, category, or ISBN
- View detailed book information
- Add or remove books from a personal favorites list
- Edit user profile settings (including personal details and profile url image)
- Delete user's account

## Prerequisites

Before running the application, ensure you have the following:

- Java JDK 11 or later
- Gradle (for building the project)
- An API key from Google Books API. (https://developers.google.com/books/docs/v1/using#APIKey)
- A secret key for JWT token generation

## Setup Instructions

1. **Clone the Repository:**
   ```sh
   git clone https://github.com/ThanosTrai/booklibapp-project-springboot.git
   cd booklibapp-project-springboot

2. Create the Application Properties File:
Create an application.properties file in the src/main/resources/ directory with the following content, replacing YOUR_GOOGLE_BOOKS_API_KEY and YOUR_JWT_SECRET_KEY with your actual keys:

google.books.apiKey=YOUR_GOOGLE_BOOKS_API_KEY <br>
jwt.secret-key=YOUR_JWT_SECRET_KEY
