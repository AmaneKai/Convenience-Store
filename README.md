# Convenience Store Simulation - CCPROG3 Machine Project

A comprehensive retail store management system built with Java, featuring object-oriented programming principles, customer interaction simulation, inventory management, and graphical user interface.

---

## Table of Contents
- [Project Overview](#project-overview)
- [About the Simulation](#about-the-simulation)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Implementation Requirements](#implementation-requirements)
- [Features](#features)
- [Minimum Requirements](#minimum-requirements)
- [Development Guidelines](#development-guidelines)
- [Milestones](#milestones)
- [Evaluation Criteria](#evaluation-criteria)
- [Academic Integrity](#academic-integrity)
- [Contributing](#contributing)

---

## Project Overview

The Convenience Store Simulation models the operations of a small retail store using object-oriented programming principles in Java. This project serves as a major course output for CCPROG3, focusing on practical application of OOP concepts, GUI development, and system design.

**Learning Objectives:**
* Apply object-oriented design principles (encapsulation, inheritance, polymorphism, abstraction)
* Develop user-friendly graphical interfaces with mouse-controlled inputs
* Implement file handling for data persistence
* Design modular, clear, and reusable code architecture
* Demonstrate technical communication and documentation skills

## About the Simulation

The system allows users to manage a catalog of products, simulate customer interactions, handle purchases, and track sales. It maintains an inventory of various products with comprehensive attribute management and supports realistic retail operations.

**Core Functionality:**
1. **Inventory Management** - Add, restock, and update products dynamically
2. **Customer Simulation** - Generate customer interactions and shopping experiences
3. **Transaction Processing** - Handle purchases, payments, and receipt generation
4. **Membership System** - Points accumulation and discount application
5. **Real-time Updates** - Automatic inventory reduction and low-stock flagging

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Java compiler (`javac`) and runtime (`java`) available in command line
- JavaFX library (for GUI implementation)

### Installation
1. Clone or extract the project files
2. Navigate to the project directory
3. Ensure all source files are present in the correct package structure
4. Include JavaFX libraries if not bundled with JDK

### Project Structure
```
konbini/
├── src/
│   ├── models/              # Product, Customer, Transaction classes
│   ├── controllers/         # Business logic and MVC controllers
│   ├── views/               # GUI components and interfaces
│   └── utils/               # Helper classes and utilities
├── data/
│   ├── inventory.txt        # Product inventory data
│   ├── transactions.txt     # Transaction records
│   └── customers.txt        # Customer data
├── docs/
│   ├── UML_Diagrams.pdf     # Class diagrams
│   ├── javadoc/             # Generated documentation
│   └── test_scripts.txt     # Testing documentation
├── resources/
│   └── images/              # Product images and UI assets
├── README.md
├── .gitignore
└── Declaration_of_Original_Work.pdf
```

## Implementation Requirements

### Object-Oriented Design Principles
- **Encapsulation:** Private attributes with public accessor methods
- **Inheritance:** Product categories extending base Product class
- **Polymorphism:** Different payment methods and discount types
- **Abstraction:** Interface-based design for extensibility

### MVC Architecture
- **Model:** Product, Customer, Transaction data classes
- **View:** GUI components and user interface elements
- **Controller:** Business logic, event handling, and data management

## Features

### Product Management
- **Categories:** Food, Beverages, Toiletries, Cleaning Products, Medications
- **Attributes:** Name, price, quantity, category, expiration date, brand, variant
- **Operations:** Add new products, restock items, update information
- **Inventory Tracking:** Automatic quantity reduction, low-stock alerts

### Customer Experience
- **Shopping Cart:** Multiple item selection with running total
- **Checkout Process:** Tax calculation (VAT), payment processing, change computation
- **Membership Cards:** Points accumulation and redemption system
- **Discounts:** Senior discounts, membership points, promotional offers

### Transaction System
- **Receipt Generation:** Itemized purchases, totals, timestamps
- **Payment Methods:** Cash, card, or mixed payments
- **Record Keeping:** Console display and file storage
- **Points System:** 1 point per ₱50 spent, 1 point = ₱1 discount

### Additional Features
- **Expiration Tracking:** Perishable goods management
- **Real-life Simulation:** Authentic payment transaction flow
- **File Persistence:** Save/load inventory and customer data
- **User-friendly Interface:** Minimal interactions for operations

## Minimum Requirements

### Product Categories (5 required)
| Category | Examples |
|----------|----------|
| **Food** | Sandwich, Pastries, Fried Chicken, Instant Noodles, Chips |
| **Beverages** | Coffee, Soda, Juice, Energy Drinks, Water |
| **Toiletries** | Soap, Shampoo, Toothpaste, Beauty Products, Deodorant |
| **Cleaning Products** | Detergent, Tissue, Hand Sanitizer, Dishwashing Liquid, Wipes |
| **Medications** | Pain Relievers, Vitamins, Cough Medicine, Antacids, Band-aids |

### Inventory Requirements
- **Minimum:** 5 product types per category
- **Initial Stock:** 10 items per product type
- **Organization:** Products arranged in shelves for easy navigation
- **Functionality:** Product selection, payment transactions, inventory maintenance

## Development Guidelines

### Code Standards
- **Documentation:** Javadoc comments for all classes and methods
- **Naming Conventions:** CamelCase for classes, camelCase for methods/variables
- **Method Design:** Create methods and classes whenever possible
- **No Brute Force:** Implement efficient, well-structured solutions

### GUI Requirements
- **User-Friendly:** Minimal interactions to complete operations
- **Mouse-Controlled:** Point-and-click interface for all functions
- **Clear Navigation:** Intuitive product layout and menu system
- **Real-life Simulation:** Payment flow matching actual store operations

### File Handling
- **Data Persistence:** Save inventory and customer information
- **Format Flexibility:** Support for various data formats
- **Error Handling:** Robust file operations with exception management

## Milestones

### MCO1 - Object-Based Implementation
**Due:** October 24, 2025 (Friday), 9:00 PM

**Deliverables:**
1. UML Class Diagrams for complete application
2. Basic class implementation (object-based approach)
3. Console-based driver for testing core functionality
4. Command-line interface for product selection and payments
5. Video demonstration of program functionality

### MCO2 - Object-Oriented with GUI
**Due:** November 24, 2025 (Monday), 12:00 NN

**Deliverables:**
1. Enhanced UML Class Diagrams (object-oriented design)
2. Complete GUI implementation with mouse controls
3. Full MVC architecture implementation
4. File handling for data persistence
5. Live demonstration with individual assessment

## Evaluation Criteria

### Assessment Focus Areas
* **Design Quality** - Proper OOP implementation and MVC architecture
* **Functionality** - Complete feature set meeting specifications
* **User Experience** - Intuitive interface and smooth operations
* **Code Quality** - Documentation, standards compliance, modularity
* **Academic Integrity** - Original work without AI assistance

### Grading Components
* **Implementation** - Core functionality and feature completeness
* **Design** - UML diagrams and architectural decisions
* **Documentation** - Javadoc, comments, and technical writing
* **Testing** - Comprehensive test scripts and validation
* **Demonstration** - Live presentation and technical explanation

## Academic Integrity

> **WARNING:** Strict enforcement of academic honesty policy

* **NO** use of Generative AI tools or applications
* **ALL** code must be original team work
* **EVERY** member must understand and explain submitted work
* **VIOLATIONS** result in 0.0 course grade and disciplinary action

### Collaboration Rules
- **Team Size:** Maximum 2 members (pairs encouraged)
- **Communication:** Only with instructor and team member
- **Resources:** External libraries allowed, must be documented
- **Questions:** Use AnimoSpace discussion forum for clarifications

## Contributing

### Commit Message Format
Follow **Conventional Commits** standard:

| Type | Purpose | Example |
|------|---------|---------|
| `feat` | Add new feature | `feat: implement membership point system` |
| `fix` | Fix a bug | `fix: correct tax calculation in checkout` |
| `refactor` | Improve code structure | `refactor: optimize inventory management` |
| `perf` | Performance improvements | `perf: enhance GUI responsiveness` |
| `style` | Code formatting | `style: fix indentation in Product class` |
| `test` | Add/update tests | `test: add payment processing test cases` |
| `docs` | Update documentation | `docs: update README with new features` |
| `build` | Build system changes | `build: add JavaFX dependencies` |
| `chore` | Maintenance tasks | `chore: update .gitignore for IDE files` |

**Format:**
```
<type>: <description>

[optional body explaining the change]

[optional footer with issue references]
```

### Development Best Practices
* Use descriptive variable and method names
* Implement error handling for user inputs
* Follow MVC separation of concerns
* Write comprehensive Javadoc documentation
* Test all functionality before commits
* Use version control from MCO1 onwards

---

## Project Timeline

| Milestone | Date | Deliverable |
|-----------|------|-------------|
| **Project Release** | September 4, 2025 | Specifications available |
| **MCO1 Due** | October 24, 2025 (Friday), 9:00 PM | Object-based implementation + video |
| **MCO2 Due** | November 24, 2025 (Monday), 12:00 NN | Complete OOP + GUI implementation |

## Deliverables Checklist

### Both MCO1 & MCO2
- [ ] UML Class Diagrams (PDF/PNG)
- [ ] Source code with internal documentation
- [ ] Javadoc-generated external documentation
- [ ] Test scripts following specified format
- [ ] Signed declaration of original work
- [ ] Project backup to personal email

### MCO1 Additional
- [ ] Video demonstration of console application
- [ ] Basic object-based implementation

### MCO2 Additional
- [ ] Complete GUI with mouse controls
- [ ] MVC design pattern implementation
- [ ] File handling for data persistence
- [ ] Live demonstration capability

---

## Example Product Structure
```java
// Example inventory organization
Categories:
├── Food/
│   ├── Sandwich (₱45.00) - Stock: 10
│   ├── Pastries (₱25.00) - Stock: 10
│   └── Fried Chicken (₱65.00) - Stock: 10
├── Beverages/
│   ├── Coffee (₱35.00) - Stock: 10
│   ├── Soda Regular (₱20.00) - Stock: 10
│   └── Energy Drink (₱45.00) - Stock: 10
└── [Other categories...]
```

---

**Important Notes:**
- Programs must compile and run successfully during demonstration
- Non-functional programs receive 0 grade regardless of code quality  
- Individual explanations required during live demos
- Version control usage earns bonus points for MCO2
- All external resources must be properly cited using APA format
