# College ERP Management System

A desktop-based Enterprise Resource Planning (ERP) system developed using **Java Swing**, **JDBC**, and **MySQL** to streamline academic administration. The application provides role-based access for **Students**, **Instructors**, and **Administrators**, enabling efficient management of courses, enrollments, grading, and academic records.

---

## Features

### Authentication & Security

* Secure user authentication using **BCrypt password hashing**
* Role-based access control for Students, Instructors, and Administrators
* Separate authentication and academic databases for improved security

### Student Module

* View enrolled courses
* Register for available courses
* View grades and academic records
* Access personal profile information

### Instructor Module

* Manage assigned courses
* View enrolled students
* Assign and update student grades
* Maintain course-related information

### Administrator Module

* Manage students, instructors, and courses
* Create and update course offerings
* Assign instructors to courses
* Maintain institutional academic records

---

## Tech Stack

| Technology    | Purpose                   |
| ------------- | ------------------------- |
| Java          | Core programming language |
| Java Swing    | Desktop GUI               |
| JDBC          | Database connectivity     |
| MySQL         | Database management       |
| BCrypt        | Secure password hashing   |
| IntelliJ IDEA | Development environment   |
| Git & GitHub  | Version control           |

---

## Project Structure

```
ERP-Management-System/
│
├── auth/          # Authentication and login
├── access/        # Role-based access control
├── data/          # Database interaction
├── domain/        # Entity classes
├── service/       # Business logic
├── ui/            # Swing user interface
├── util/          # Utility classes
└── Main.java
```

---

## Database Design

The project uses **two separate MySQL databases**.

### Authentication Database

Stores:

* User credentials
* Password hashes (BCrypt)
* User roles

### ERP Database

Stores:

* Students
* Instructors
* Courses
* Enrollments
* Grades
* Academic records

This separation improves modularity and keeps authentication independent from academic data.

---

## Installation

### Prerequisites

* Java JDK 17 or later
* MySQL Server
* IntelliJ IDEA (recommended)

### Steps

1. Clone the repository

```bash
git clone https://github.com/yourusername/ERP-Management-System.git
```

2. Import the project into IntelliJ IDEA.

3. Create the required MySQL databases.

4. Update the database configuration with your MySQL credentials.

5. Run the project.

---

## Screenshots

> Add screenshots here before publishing.

Example:

* Login Screen
* Student Dashboard
* Instructor Dashboard
* Administrator Dashboard
* Course Registration
* Grade Management

---

## Key Design Decisions

* Implemented role-based authorization to ensure users access only permitted features.
* Used BCrypt hashing to securely store passwords instead of plain text.
* Separated authentication data from academic records using two independent databases.
* Organized the application into modular packages following object-oriented design principles.

---

## Future Improvements

* Attendance management
* Timetable generation
* PDF report generation
* Email notifications
* Course analytics dashboard
* Cloud database deployment

---

## Learning Outcomes

Through this project, I gained practical experience in:

* Object-Oriented Programming
* Java Swing GUI development
* JDBC connectivity
* Relational database design
* Role-based authentication
* Secure password management
* Software architecture and modular design
* Git and version control

