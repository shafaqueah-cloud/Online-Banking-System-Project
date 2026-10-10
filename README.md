# Banking Management System
# Introduction

This is a console based application which gives access to do all kind of banking operations. JDBC has been used to develop this system which includes JDBC Driver, Connections, DAO implementations and exception handling. MySQL has been used for database.

## Requirements

- Java 21
- Maven
- MySQL with a database named `banking_system` and the `User` and `Accounts` tables expected by the application

## Configure and run

The default database connection is `jdbc:mysql://localhost:3306/banking_system`, user `root`. Set `BANK_DB_URL`, `BANK_DB_USER`, and `BANK_DB_PASSWORD` to override those connection settings. For example, in a shell:

```sh
export BANK_DB_USER=root
export BANK_DB_PASSWORD='your-mysql-password'
mvn compile exec:java -Dexec.mainClass=BankingManagementSystem.BankingApp
```

The MySQL Connector/J dependency is managed by Maven. You can also open the project in IntelliJ and run `BankingManagementSystem.BankingApp`.

## Features

- Sign up and sign in
- Open an account with an initial deposit
- Deposit, withdraw, and transfer funds
- View the balance after PIN verification
