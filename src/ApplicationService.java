import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import modal.Book;
import modal.Branch;
import modal.Loan;
import modal.Patron;
import repository.BookRepository;
import repository.BranchRepository;
import repository.LoanRepository;
import repository.PatronRepository;
import repository.inmemory.InMemoryBookRepository;
import repository.inmemory.InMemoryBranchRepository;
import repository.inmemory.InMemoryPartonRepository;
import serivces.BookSearchService;
import serivces.Inventory;
import serivces.LibraryService;

public class ApplicationService {
    private static final Logger log = Logger.getLogger(ApplicationService.class.getName());

    public static void main(String[] args) {

        try {
            Logger root = Logger.getLogger("");
            if (root.getHandlers().length > 0) {
                root.getHandlers()[0].setLevel(Level.ALL);
            }
            root.setLevel(Level.INFO);
        } catch (Exception e) {
            log.severe("Failed to set logger level: " + e.getMessage());
        }
        log.info("Application started");

        BookRepository bookRepository = new InMemoryBookRepository();
        PatronRepository patronRepository = new InMemoryPartonRepository();
        BranchRepository branchRepository = new InMemoryBranchRepository();
        Inventory inventory = new Inventory();

        LibraryService libraryService = new LibraryService(bookRepository,patronRepository,branchRepository,inventory);

        Branch centralBranch = new Branch("1", "Central Library", "123 Main St");
        Branch eastBranch = new Branch("2", "East Branch", "456 East St");

        libraryService.addBranch(centralBranch);
        libraryService.addBranch(eastBranch);

        Book book1 = new modal.Book("1234567890", "The Great Gatsby", "F. Scott Fitzgerald", 1925);
        Book book2 = new modal.Book("0987654321", "To Kill a Mockingbird", "Harper Lee", 1960);
        Book book3 = new modal.Book("1122334455", "1984", "George Orwell", 1949);

        // Add a new book
        libraryService.addOrUpdateBook(book1);
        libraryService.addOrUpdateBook(book2);
        libraryService.addOrUpdateBook(book3);

        libraryService.addCopies(centralBranch.getId(), book1.getIsbn() , 10);
        libraryService.addCopies(eastBranch.getId(), book2.getIsbn() , 5);


        Patron alice = libraryService.addPatron("Alice", "alice@example.com", "111-111");
        Patron bob = libraryService.addPatron("Bob", "bob@example.com", "222-222");
 

    }


     /* ---------- Handlers ---------- */

    private static void handleAddBook(Scanner sc, LibraryService library) {
        String title = readLine(sc, "Title: ");
        String author = readLine(sc, "Author: ");
        String isbn = readLine(sc, "ISBN: ");
        int year = readInt(sc, "Publication year: ");
        Book b = new Book(title, author, isbn, year);
        library.addOrUpdateBook(b);
        System.out.println("Added/Updated book: " + b);
    }

    private static void handleUpdateBook(Scanner sc, LibraryService library, BookRepository bookRepo) {
        listAllBooks(bookRepo);
        String isbn = readLine(sc, "Enter ISBN of book to update: ");
        Optional<Book> existing = bookRepo.findByIsbn(isbn);
        if (existing.isEmpty()) {
            System.out.println("Book not found.");
            return;
        }
        Book cur = existing.get();
        System.out.println("Current: " + cur);
        String title = readLine(sc, "New title (leave blank to keep): ");
        String author = readLine(sc, "New author (leave blank to keep): ");
        String yearStr = readLine(sc, "New publication year (leave blank to keep): ");

        String newTitle = title.isBlank() ? cur.getTitle() : title;
        String newAuthor = author.isBlank() ? cur.getAuthor() : author;
        int newYear = cur.getPublicationYear();
        if (!yearStr.isBlank()) {
            try { newYear = Integer.parseInt(yearStr.trim()); } catch (NumberFormatException ignored) {}
        }

        Book updated = new Book(newTitle, newAuthor, cur.getIsbn(), newYear);
        library.addOrUpdateBook(updated);
        System.out.println("Updated to: " + updated);
    }

    private static void handleRemoveBook(Scanner sc, LibraryService library) {
        String isbn = readLine(sc, "ISBN to remove: ");
        library.removeBook(isbn);
        System.out.println("Removed (if existed) ISBN: " + isbn);
    }

    private static void handleSearchBooks(Scanner sc, BookSearchService search) {
        String q = readLine(sc, "Search query (title / author / ISBN): ");
        var results = search.search(q);
        System.out.println("Matches (" + results.size() + "):");
        results.forEach(b -> System.out.println(" - " + b));
    }

    private static void listAllBooks(BookRepository bookRepo) {
        List<Book> all = bookRepo.findAll();
        System.out.println("Books (" + all.size() + "):");
        all.forEach(b -> System.out.println(" - " + b));
    }

    private static void handleAddBranch(Scanner sc, LibraryService library) {
        String name = readLine(sc, "Branch name: ");
        String addr = readLine(sc, "Address: ");
        Branch b = new Branch(name, addr);
        library.addBranch(b);
        System.out.println("Added branch: " + b);
    }

    private static void listBranches(BranchRepository repo) {
        List<Branch> branches = repo.findAll();
        System.out.println("Branches (" + branches.size() + "):");
        branches.forEach(br -> System.out.println(" - " + br.getId() + " : " + br.getName() + " / " + br.getAddress()));
    }

    private static void handleAddCopies(Scanner sc, LibraryService library, BookRepository bookRepo, BranchRepository branchRepo) {
        String isbn = chooseBook(sc, bookRepo);
        if (isbn == null) return;
        Branch branch = chooseBranch(sc, branchRepo);
        if (branch == null) return;
        int copies = readInt(sc, "Number of copies to add: ");
        library.addCopies(branch.getId(), isbn, copies);
        System.out.println("Added copies.");
    }


    private static void handleAddPatron(Scanner sc, LibraryService library) {
        String name = readLine(sc, "Patron name: ");
        String email = readLine(sc, "Email: ");
        String phone = readLine(sc, "Phone: ");
        Patron p = library.addPatron(name, email, phone);
        System.out.println("Added patron: " + p);
    }

    private static void handleUpdatePatron(Scanner sc, PatronRepository patronRepo) {
        Patron p = choosePatron(sc, patronRepo);
        if (p == null) return;
        System.out.println("Current: " + p);
        String name = readLine(sc, "New name (leave blank to keep): ");
        String email = readLine(sc, "New email (leave blank to keep): ");
        String phone = readLine(sc, "New phone (leave blank to keep): ");
        if (!name.isBlank()) p.setName(name);
        if (!email.isBlank()) p.setEmail(email);
        if (!phone.isBlank()) p.setPhoneNumber(phone);
        patronRepo.update(p);
        System.out.println("Updated: " + p);
    }

    private static void listPatrons(PatronRepository repo) {
        List<Patron> all = repo.findAll();
        System.out.println("Patrons (" + all.size() + "):");
        all.forEach(p -> System.out.println(" - " + p.getId() + " : " + p.getName() + " / " + p.getEmail()));
    }


    private static String chooseBook(Scanner sc, BookRepository repo) {
        List<Book> all = repo.findAll();
        if (all.isEmpty()) {
            System.out.println("No books available.");
            return null;
        }
        System.out.println("Books:");
        for (int i = 0; i < all.size(); i++) {
            Book b = all.get(i);
            System.out.printf("%d) %s (ISBN:%s) by %s%n", i + 1, b.getTitle(), b.getIsbn(), b.getAuthor());
        }
        String idxOrIsbn = readLine(sc, "Choose book by number or enter ISBN directly: ");
        try {
            int idx = Integer.parseInt(idxOrIsbn.trim());
            if (idx >= 1 && idx <= all.size()) return all.get(idx - 1).getIsbn();
        } catch (NumberFormatException ignored) {}
        // treat as ISBN
        return idxOrIsbn.trim();
    }

    private static Branch chooseBranch(Scanner sc, BranchRepository repo) {
        List<Branch> all = repo.findAll();
        if (all.isEmpty()) {
            System.out.println("No branches available.");
            return null;
        }
        System.out.println("Branches:");
        for (int i = 0; i < all.size(); i++) {
            Branch b = all.get(i);
            System.out.printf("%d) %s (id:%s)%n", i + 1, b.getName(), b.getId());
        }
        int idx = readInt(sc, "Choose branch number: ");
        if (idx >= 1 && idx <= all.size()) return all.get(idx - 1);
        System.out.println("Invalid branch choice.");
        return null;
    }

    private static Patron choosePatron(Scanner sc, PatronRepository repo) {
        List<Patron> all = repo.findAll();
        if (all.isEmpty()) {
            System.out.println("No patrons available.");
            return null;
        }
        System.out.println("Patrons:");
        for (int i = 0; i < all.size(); i++) {
            Patron p = all.get(i);
            System.out.printf("%d) %s (id:%s) %n", i + 1, p.getName(), p.getId());
        }
        int idx = readInt(sc, "Choose patron number: ");
        if (idx >= 1 && idx <= all.size()) return all.get(idx - 1);
        System.out.println("Invalid patron choice.");
        return null;
    }

    private static void listActiveLoans(LoanRepository loanRepo) {
        List<Loan> active = loanRepo.findAll().stream().filter(l -> !l.isBookReturned()).collect(Collectors.toList());
        System.out.println("Active loans (" + active.size() + "):");
        active.forEach(l -> System.out.println(" - " + l.getId() + " | isbn:" + l.getIsbn() + " patron:" + l.getPatronID() + " branch:" + l.getBranchID()));
    }

    private static void listAllLoans(LoanRepository loanRepo) {
        List<Loan> all = loanRepo.findAll();
        System.out.println("All loans (" + all.size() + "):");
        all.forEach(l -> System.out.println(" - " + l.getId() + " | isbn:" + l.getIsbn() + " patron:" + l.getPatronID() + " returned:" + l.isBookReturned()));
    }


    private static String readLine(Scanner sc, String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

}