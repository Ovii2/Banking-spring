# Banking Application

### Overview

This is a banking application built using React, Spring Boot, and MySQL. It allows users to manage their bank accounts, including performing deposit, withdrawal, and transfer operations, as well as viewing their transaction history.

### Usage

Register a new user account or log in with an existing one.
View your account balance, owner name, and account number.
Perform deposit, withdrawal, and transfer operations.
View your transaction history, including details such as amount, account number, and transaction date.

User authentication and authorization

- Account management (balance, account number, owner name)
- Deposit, withdraw, and transfer funds
- Transaction history with details (amount, account number, date)
- Responsive and modern UI design

Technologies Used

- Frontend: React, Tailwind CSS
- Backend: Spring Boot, Java
- Database: MySQL
- Other: JWT for authentication, Lombok for boilerplate code


UI preview

<img src="./frontend/src/assets/demo/Account1.png" alt="Desktop preview" width="300" />
<img src="./frontend/src/assets/demo/video.gif" alt="Desktop preview" width="300" />


## Setup and Installation

- Clone the repository
- Set up the MySQL database

  - Create a new database
  - Update the database connection details in the application.properties file

### Build and run the backend server:

Navigate to the banking-application directory

Run `mvn clean install` to build the project.

Run `mvn spring-boot:run` to start the server.

### Start the frontend development server:

Navigate to the frontend directory
Run `npm install` or `npm i` to install dependencies

Run `npm run dev` to start the development server
