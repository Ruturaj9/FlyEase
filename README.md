FlyEase - Flight Booking System

Overview

FlyEase is a Java-based Flight Booking System with a graphical user interface (GUI) built using Swing and a MySQL database for data storage. It allows users to search for available flights, select seats, and book tickets. Admins can manage flight details through CRUD operations.

Files in Repository

FlightBookingSystem.java - Core application logic for managing flights and bookings.

mysql-connector-j-9.2.0.jar - MySQL JDBC driver for database connectivity.

.gitignore - Configuration to exclude unnecessary files from version control.

flight_booking.iml - IntelliJ IDEA project configuration file.

README.md - Project documentation.

Features

User Features:

View available flights (current time to 2 days onward)

Select and book available seats

Cancel booked seats

Admin Features:

Add, update, and delete flight information

Manage seat availability

View flight details in a dashboard

Technologies Used

Java (Swing) - For GUI development

MySQL (XAMPP) - For database management

JDBC - For connecting Java with MySQL

Installation & Setup

Clone the repository:

git clone https://github.com/yourusername/FlyEase.git

Import the project into IntelliJ IDEA (or any Java IDE).

Install MySQL and start XAMPP.

Create a database and import the necessary SQL schema.

Update database credentials in FlightBookingSystem.java.

Run FlightBookingSystem.java to launch the application.

Database Setup

Create a MySQL database named flyease.

Configure the database connection using mysql-connector-j-9.2.0.jar.

Usage

Run the application.

Login as an admin to manage flights.

Login as a user to book flights.
