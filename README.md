# Library Management System

A beginner-friendly Java + Spring Boot + Thymeleaf + MySQL project, structured similarly to the AmazonApplication project.

## Features

### User
- Login
- See currently issued books
- See available books
- Issue a book
- Return a book

### Admin
- Login
- See all books and copy availability
- Add a new book with number of copies
- Remove a book when all copies have been returned

## Project structure

```text
src/main/java/com/library/
├── controller/
│   ├── AuthController.java
│   ├── UserController.java
│   └── AdminController.java
├── dao/
│   ├── UserDAO.java
│   └── BookDAO.java
├── model/
│   ├── User.java
│   ├── Book.java
│   └── Issue.java
├── service/
│   ├── UserService.java
│   └── LibraryService.java
├── util/
│   └── DBConnection.java
└── LibraryManagementApplication.java

src/main/resources/
├── templates/
│   ├── login.html
│   ├── user-dashboard.html
│   └── admin-dashboard.html
└── static/css/style.css

database/library_db.sql
```

## Run in IntelliJ

1. Open the project as a Maven project.
2. Run `database/library_db.sql` in MySQL.
3. Open `DBConnection.java` and replace `YOUR_MYSQL_PASSWORD` with your MySQL root password.
4. Reload Maven dependencies.
5. Run `LibraryManagementApplication.java`.
6. Open `http://localhost:8080` in the browser.

## Demo login

User:
- Email: `user@library.com`
- Password: `user123`

Admin:
- Email: `admin@library.com`
- Password: `admin123`

Passwords are stored as plain text only for this college/demo project. A production application should use password hashing and proper authentication/authorization.
