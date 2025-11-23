# Convenience Store Simulation - CCPROG3 Machine Project

A comprehensive retail store management system built with Java, featuring object-oriented programming principles, customer interaction simulation, inventory management, and graphical user interface.

---

## Table of Contents
- [Project Overview](#project-overview)
- [About the Simulation](#about-the-simulation)
- [Getting Started](#getting-started)
- [Building and Running](#building-and-running)
- [Project Structure](#project-structure)
- [Implementation Requirements](#implementation-requirements)
- [Features](#features)
- [Design Patterns](#design-patterns)
- [Testing](#testing)
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
- Java Development Kit (JDK) 17 or higher
- Java compiler (`javac`) and runtime (`java`) available in command line
- Swing library (included with JDK)
- Gradle 9.2+ (included via wrapper)

### Installation
1. Clone or extract the project files
2. Navigate to the project directory
3. Ensure all source files are present in the correct package structure
4. Swing libraries are included with JDK (no additional setup needed)

## Building and Running

### Using Gradle

#### On Mac/Linux:
```bash
# Navigate to the project root directory
cd Convenience-Store

# Run the application with Gradle
./gradlew run 

# If permission issues occur, make the Gradle wrapper executable
chmod +x gradlew
```

#### On Windows:
```cmd
:: Navigate to the project root directory
cd Convenience-Store

:: Run the application with Gradle
gradlew.bat run 
```

### Manual Build

#### On Mac/Linux:
```bash
# Create build directory if it doesn't exist
mkdir -p build/classes/java/main

# Compile from project root (compile Main.java which will compile dependencies)
javac -d build/classes/java/main -sourcepath src/main/java src/main/java/com/konbini/Main.java

# Run the application from project root (so data/ directory is accessible)
java -cp build/classes/java/main com.konbini.Main
```

#### On Windows:
```bash
# Create build directory if it doesn't exist
if not exist build\classes\java\main mkdir build\classes\java\main

# Compile from project root (compile Main.java which will compile dependencies)
javac -d build\classes\java\main -sourcepath src\main\java src\main\java\com\konbini\Main.java

# Run the application from project root (so data\ directory is accessible)
java -cp build\classes\java\main com.konbini.Main
```

**Note:** Manual compilation must be run from the project root directory to ensure the application can access the `data/` and `receipts/` directories. The Gradle build handles this automatically.

### Creating a JAR File

#### On Mac/Linux:
```bash
# Build JAR file with Gradle
./gradlew jar

# Run the JAR file
java -jar build/libs/convenience-store.jar
```

#### On Windows:
```cmd
:: Build JAR file with Gradle
gradlew.bat jar

:: Run the JAR file
java -jar build\libs\convenience-store.jar
```

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "com.konbini.IntegrationTest"

# Generate test report (view at build/reports/tests/test/index.html)
./gradlew test --rerun-tasks
```

## Project Structure

```
Convenience-Store/
├── build.gradle                          # Gradle build configuration
├── data/                                 # Data storage directory
│   ├── customers.dat                     # Customer database
│   ├── employees.dat                     # Employee database
│   ├── products.dat                      # Product inventory
│   ├── transactions.dat                  # Transaction history
│   └── id_counters.dat                   # ID sequence counters
├── receipts/                             # Generated receipts
│   └── receipt_TRA####.txt               # Transaction receipts
├── deliverables/                         # Deliverables directory
├── gradle/                               # Gradle configuration
│   ├── libs.versions.toml                # Dependency version catalog
│   └── wrapper/                          # Gradle wrapper files
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradle.properties                     # Gradle properties
├── gradlew                               # Gradle wrapper script (Unix)
├── gradlew.bat                           # Gradle wrapper script (Windows)
├── README.md                             # Project documentation
├── settings.gradle                       # Gradle settings
├── specs/                                # Project specifications
│   └── CCPROG3 MCO Specifications - Convenience Store Simulation.pdf
└── src/                                  # Source code
    ├── main/
    │   └── java/
    │       └── com/
    │           └── konbini/              # Root package
    │               ├── controller/       # MVC Controllers
    │               │   ├── CartController.java
    │               │   ├── CartManagementController.java
    │               │   ├── CustomerController.java
    │               │   ├── CustomerManagementController.java
    │               │   ├── DataManagementController.java
    │               │   ├── EmployeeController.java
    │               │   ├── EmployeeManagementController.java
    │               │   ├── MainController.java
    │               │   ├── ProductController.java
    │               │   ├── ProductManagementController.java
    │               │   ├── TransactionController.java
    │               │   └── TransactionManagementController.java
    │               ├── dto/              # Data Transfer Objects
    │               │   ├── CartDTO.java
    │               │   ├── CustomerDTO.java
    │               │   ├── EmployeeDTO.java
    │               │   ├── ProductDTO.java
    │               │   ├── TransactionDTO.java
    │               │   └── TransactionItemDTO.java
    │               ├── Main.java         # Application entry point
    │               ├── model/            # Domain models
    │               │   ├── Cart.java
    │               │   ├── CartItem.java
    │               │   ├── Customer.java
    │               │   ├── Employee.java
    │               │   ├── MembershipCard.java
    │               │   ├── Product.java
    │               │   ├── ProductCategory.java
    │               │   ├── ProductSubcategory.java
    │               │   ├── Receipt.java
    │               │   ├── Transaction.java
    │               │   └── repository/   # Data access layer
    │               │       ├── CustomerRepository.java
    │               │       ├── EmployeeRepository.java
    │               │       ├── ProductRepository.java
    │               │       ├── TransactionRepository.java
    │               │       └── impl/     # Repository implementations
    │               │           ├── FileCustomerRepository.java
    │               │           ├── FileEmployeeRepository.java
    │               │           ├── FileProductRepository.java
    │               │           └── FileTransactionRepository.java
    │               ├── service/          # Business logic layer
    │               │   ├── CartService.java
    │               │   ├── CustomerService.java
    │               │   ├── EmployeeService.java
    │               │   ├── ProductService.java
    │               │   ├── TransactionService.java
    │               │   ├── discount/     # Discount strategies
    │               │   │   ├── DiscountStrategy.java
    │               │   │   ├── PointsRedemptionStrategy.java
    │               │   │   └── SeniorDiscountStrategy.java
    │               │   ├── impl/         # Service implementations
    │               │   │   ├── CartServiceImpl.java
    │               │   │   ├── CustomerServiceImpl.java
    │               │   │   ├── EmployeeServiceImpl.java
    │               │   │   ├── ProductServiceImpl.java
    │               │   │   └── TransactionServiceImpl.java
    │               │   └── tax/          # Tax calculation strategies
    │               │       ├── TaxStrategy.java
    │               │       └── VATTaxStrategy.java
    │               ├── util/             # Utility classes
    │               │   ├── FileUtil.java
    │               │   ├── IdGenerator.java
    │               │   └── UserSession.java
    │               └── view/             # UI components
    │                   ├── BaseView.java
    │                   ├── CartView.java
    │                   ├── CustomerView.java
    │                   ├── EmployeeView.java
    │                   ├── MainView.java
    │                   ├── ProductView.java
    │                   ├── StoreView.java
    │                   ├── TransactionView.java
    │                   └── swing/        # Swing implementations
    │                       ├── CartPanel.java
    │                       ├── CustomerMenuPanel.java
    │                       ├── CustomerPanel.java
    │                       ├── EmployeeLoginDialog.java
    │                       ├── EmployeeMenuPanel.java
    │                       ├── EmployeePanel.java
    │                       ├── ProductPanel.java
    │                       ├── SwingStoreView.java
    │                       ├── TransactionPanel.java
    │                       └── UserTypeSelectionDialog.java
    └── test/
        └── java/
            └── com/
                └── konbini/              # Test suite
                    ├── CartTest.java
                    ├── CustomerTest.java
                    ├── DiscountStrategyTest.java
                    ├── EdgeCaseTest.java
                    ├── EmployeeTest.java
                    ├── FileUtilTest.java
                    ├── IdGeneratorTest.java
                    ├── IntegrationTest.java
                    ├── ProductTest.java
                    ├── ReceiptTest.java
                    ├── SimpleTest.java
                    ├── TaxStrategyTest.java
                    ├── TransactionTest.java
                    └── UserSessionTest.java
```

## Implementation Requirements

### MVC Architecture
The application follows a strict Model-View-Controller design pattern:

**Model Layer:**
- Domain entities (Customer, Product, Transaction, Cart, Employee)
- Repository pattern for data access
- Service layer for business logic

**View Layer:**
- Swing-based graphical user interface
- Separate panels for different functionalities
- Responsive and user-friendly design

**Controller Layer:**
- Mediates between View and Model
- Handles user input and application flow
- Coordinates business operations

### Product Management
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
- **Employee Management:** Secure login and role-based access

## Features

### Customer Mode
- Browse products by category
- Add items to shopping cart
- View cart contents and total
- Apply discounts (senior, membership points)
- Complete checkout with multiple payment options
- Receive printed receipt

### Employee/Admin Mode
- Secure login authentication
- Product management (add, update, restock, delete)
- Customer management (register, view, update)
- Transaction history and reporting
- Employee management
- Data export and backup

## Design Patterns

### Strategy Pattern
**Discount Strategies:**
- `SeniorDiscountStrategy` - 20% discount for seniors
- `PointsRedemptionStrategy` - Redeem loyalty points

**Tax Strategies:**
- `VATTaxStrategy` - 12% Value Added Tax

### Repository Pattern
- `CustomerRepository` / `FileCustomerRepository`
- `ProductRepository` / `FileProductRepository`
- `TransactionRepository` / `FileTransactionRepository`
- `EmployeeRepository` / `FileEmployeeRepository`

### Builder Pattern
- `Transaction.Builder` - Fluent transaction construction

### Singleton Pattern
- `IdGenerator` - Centralized ID management
- `UserSession` - Session state management

## Testing

The project includes comprehensive test coverage with 14 test suites:

| Test Class | Purpose |
|------------|---------|
| `CartTest` | Shopping cart operations |
| `CustomerTest` | Customer management |
| `DiscountStrategyTest` | Discount calculations |
| `EdgeCaseTest` | Boundary and error conditions |
| `EmployeeTest` | Employee operations |
| `FileUtilTest` | File I/O operations |
| `IdGeneratorTest` | ID generation |
| `IntegrationTest` | End-to-end workflows |
| `ProductTest` | Product management |
| `ReceiptTest` | Receipt generation |
| `SimpleTest` | Basic functionality |
| `TaxStrategyTest` | Tax calculations |
| `TransactionTest` | Transaction processing |
| `UserSessionTest` | Session management |

Run tests with: `./gradlew test`

View test reports at: `build/reports/tests/test/index.html`

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
| `build` | Build system changes | `build: add Swing dependencies` |
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

| Milestone | Date                                    | Deliverable |
|-----------|-----------------------------------------|-------------|
| **Project Release** | September 4, 2025                       | Specifications available |
| **MCO1 Due** | October 24, 2025 (Friday), 9:00 PM      | Object-based implementation + video |
| **MCO2 Due** | November 26, 2025 (Wednesday), 11:50 PM | Complete OOP + GUI implementation |

## Deliverables Checklist

### Both MCO1 & MCO2
- [x] UML Class Diagrams (PDF/PNG)
- [x] Source code with internal documentation
- [x] Javadoc-generated external documentation
- [x] Test scripts following specified format
- [x] Signed declaration of original work
- [x] Project backup to personal email

### MCO1 Additional
- [x] Video demonstration of console application
- [x] Basic object-based implementation

### MCO2 Additional
- [x] Complete GUI with mouse controls
- [x] MVC design pattern implementation
- [x] File handling for data persistence
- [x] Live demonstration capability
- [x] Employee/Admin Login Menu

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

---

**Project Status:** Complete - All features implemented and tested