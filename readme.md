# Test Store - Spring Boot E-Commerce Application
## Technologies Used

* **Backend:** Spring Boot, Spring Security, Spring Data JPA
* **Database:** SQLite with Hibernate
* **Frontend:** Thymeleaf (Server-Side Templating), JavaScript for live search and interactive elements
* **Build Tool:** Maven
* **Java Version:** 17
* **Libraries:** EasyExcel for bulk product management, Logback for logging

## Features

### Public-Facing Storefront

* **Homepage:** Displays a paginated grid of all available products.
* **Live Search:** A search bar allows users to filter products in real-time by either **Product Name** or **SKU**.
* **Product Detail Pages:** Each product has a dedicated page.
* **Shopping Cart:**
    * Full cart functionality for both guests and logged-in customers.
    * Users can update item quantities or remove items directly from the cart page.
    * Carts are persisted in the database and can be recovered by their ID.
* **User System:**
    * Secure user registration and a unified login page.
    * Passwords are encrypted using BCrypt.

### Admin Panel (Role-Based)

The application features a tiered admin system with different access levels.

#### **All Admins (`T1`, `T2`, `T3`)**

* **Statistics Dashboard:** A high-level overview of key site metrics.
* **View All Orders:** A paginated view of "Unfinished" and "Finished" orders.

#### **T2 & T3 Admins**

* **Product Management:** View, create, and edit all products with live search and pagination.
* **Bulk Product Management:** Download and upload Excel spreadsheets to manage products in bulk.

#### **T3 Admins Only**

* **User Management:** View and disable admin accounts; view a paginated list of all customer accounts.
* **Reporting:** Access detailed, interactive, and paginated reports for revenue, top customers, and top products.

## Setup and Running the Application

#### Prerequisites

* Java 17 (or newer)
* Apache Maven

#### Instructions

1.  Place all project files in a single root directory.
2.  Open a terminal or command prompt in that directory.
3.  Build the project using Maven:
    ```bash
    mvn clean package
    ```
4.  Run the application:
    ```bash
    java -jar target/store-0.0.1-SNAPSHOT.jar
    ```
5.  The application will be accessible at **`http://localhost:8080`**.

#### Default Accounts

The application will automatically create the following accounts when it starts with an empty database:

* **Top Admin:** `username: admin`, `password: admin` (Role: T3)
* **T2 Admin:** `username: t2admin`, `password: t2admin` (Role: T2)
* **T1 Admin:** `username: t1admin`, `password: t1admin` (Role: T1)
* **Default Customers:** `customer1` through `customer10`, with passwords matching the username.

## Database

This application uses a file-based **SQLite** database. On the first run, it will automatically create a `products.db` file in the root directory. To reset all data, stop the application, delete this file, and restart.