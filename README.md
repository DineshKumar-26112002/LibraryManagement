# Library Management System

This Library Management System is a Java-based web application designed to manage library operations effectively.

The application supports **Admin** and **User** roles, providing distinct features for each. 

The system implements **2-Step Authentication** for secure login and utilizes email notifications for book-related operations.

 The application is built using **Java**, **Jersey (JAX-RS)**, and **MySQL** and follows the RESTful API principles.

---

## Features

### **Admin Features**
1. **View Books**: See all available books in the library.
2. **Add Books**: Add new books to the library collection.
3. **View Users**: Manage and view the list of registered users.
4. **View Issued Books**: Track which books have been issued and their due dates.
5. **Logout**: Securely log out of the system.

### **User Features**
1. **View Books**: Browse available books in the library.
2. **Get Book**: Request to borrow a book from the library.
   - Email with book details in PDF format is sent to the registered email address.
3. **Return Book**: Return borrowed books to the library.
   - Email confirmation with book details in PDF format is sent to the registered email address.
4. **Signup**: Register as a new user with an email and mobile number.
5. **Logout**: Securely log out of the system.

### **2-Step Authentication**
1. On entering the correct **email** and **password**, an OTP is sent to the registered **mobile number**.
2. After verifying the mobile OTP, another OTP is sent to the registered **email address**.
3. Only after verifying both OTPs, the user is redirected to the Admin or User dashboard based on their role.

---

## Technologies Used

- **Backend**: Java (Jersey Framework)
- **Frontend**: JSP (Java Server Pages)
- **Database**: MySQL
- **Email Service**: JavaMail API
- **PDF Generation**: iText Library
- **SMS Service**: Third-party SMS Gateway (e.g., Twilio, Nexmo)
- **Server**: Apache Tomcat 11
- **Build Tool**: Maven

---

## Prerequisites

Before running the application, ensure the following software and services are available:

1. **Java Development Kit (JDK)** (version 8 or higher)
2. **Apache Tomcat Server** (version 10 or higher)
3. **Maven** (version 3 or higher)
4. **MySQL Database**
5. **SMTP Server**: For sending emails (e.g. Gmail).
6. **SMS Gateway**: For sending OTPs to mobile numbers.

---
