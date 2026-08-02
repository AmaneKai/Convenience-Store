# Konbini Store

A convenience store management application built in Java with a JavaFX desktop interface.

## Overview

Konbini Store supports two kinds of users. Staff manage products, customers, checkout, and 
transaction history. Customers can create an account, browse the catalog, and check themselves out. 
Data is stored in CSV files under `data/`.

## Features

Staff:
- Dashboard with store statistics and sales charts
- Product management, including restocking and low stock and expiry tracking
- Customer registration and membership card management
- Checkout with VAT, senior citizen discount, and loyalty points
- Transaction history
- Employee management

Customer:
- Self service sign up and login, separate from staff accounts
- Product browsing and self checkout, with an order total preview before payment
- Account page showing profile, membership card, and purchase history

## Architecture

The application follows a layered, CQRS style design.

- `domain`: entities, repository interfaces, and business rules such as discounts, tax, and 
    checkout calculation
- `application`: commands, queries, and their handlers, dispatched through a mediator
- `infrastructure`: CSV persistence, dependency injection with Guice, password hashing, and 
    session management
- `view`: JavaFX screens, which only communicate with the application layer through the mediator

## Requirements

- JDK 17 or later
- No separate Gradle install needed, the wrapper is included

## Running the application

```bash
./gradlew run
```

On Windows:

```bash
gradlew.bat run
```

## Running tests

```bash
./gradlew test
```

Test reports are written to `build/reports/tests/test/index.html`.

## Test credentials

A seeded employee account is available for testing.

- Employee ID: EMP0001
- Password: password

Customer accounts are created from the sign up screen on first launch.

## Project structure

```
src/main/java/com/konbini/
  domain/           business entities and rules
  application/      commands, queries, and handlers
  infrastructure/   persistence, dependency injection, security
  view/             JavaFX screens
data/               CSV data files
```
