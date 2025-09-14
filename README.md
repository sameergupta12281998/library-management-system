# Library Management System

This is a Library Management System that helps manage books, patrons, branches, loans, and reservations. It also provides book recommendations to patrons based on their preferences.

---
* Features
-----------------------------------------
# 1. Book Management**
- Add, update, or remove books.
- Search for books by title, author, or ISBN.
- List all books in the library.

# 2. Branch Management**
- Add new library branches.
- List all branches.

# 3. Patron Management**
- Add or update patrons (library members).
- List all patrons.

# 4. Inventory Management**
- Add or remove copies of books at branches.
- Transfer books between branches.
- Check available and total copies of books at branches.

# 5. Loan Management**
- Checkout books to patrons for a specific number of days.
- Return books and update inventory.
- View borrowing history for patrons.
- List all loans or active loans.

# 6. Reservation Management
- Reserve books for patrons when copies are unavailable.
- Notify patrons when reserved books become available.
- Cancel reservations.

# 7. Book Recommendations
- Recommend books to patrons using two strategies:
  - **Popularity-Based Recommendation**: Suggests books based on their overall popularity.
  - **Author Affinity Recommendation**: Suggests books by authors that the patron has previously borrowed.

# 8. Interactive Console Application**
- The application provides a menu-driven interface for users to interact with the system.

--------------------------------------------------------------------------------------------------



# How to Run the Application
--------------------------------------------------
# 1. Setup:
   - Make sure you have Java installed (JDK 17 or higher).
   - Clone the project to your local machine.

# 2. Compile the Code:
   - Open a terminal and navigate to the `src` folder.
   - Run the following command to compile the code:
     ```sh
     javac ApplicationService.java
     ```

# 3. Run the Application:
   - Run the following command to start the application:
     ```sh
     java ApplicationService
     ```

# 4. Use the Menu:
   - Follow the on-screen menu to perform various operations like adding books, checking out books, and viewing recommendations.

--------------------------------------------------------------------------------------------------
