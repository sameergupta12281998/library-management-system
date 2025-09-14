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
import modal.Reservation;
import notification.NotifierFactory;
import recommendation.AuthorAffinityRecommendation;
import recommendation.PopularityBasedRecommendation;
import repository.BookRepository;
import repository.BranchRepository;
import repository.LoanRepository;
import repository.PatronRepository;
import repository.ReservationRepository;
import repository.inmemory.InMemoryBookRepository;
import repository.inmemory.InMemoryBranchRepository;
import repository.inmemory.InMemoryLoanRepository;
import repository.inmemory.InMemoryPatronRepository;
import repository.inmemory.InMemoryReservationRepository;
import serivces.BookSearchService;
import serivces.Inventory;
import serivces.LendingService;
import serivces.LibraryService;
import serivces.RecommendationService;
import serivces.ReservationService;
import serivces.TransferService;

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

     // Setup repositories
        BookRepository bookRepo = new InMemoryBookRepository();
        PatronRepository patronRepo = new InMemoryPatronRepository();
        LoanRepository loanRepo = new InMemoryLoanRepository();
        ReservationRepository reservationRepo = new InMemoryReservationRepository();
        BranchRepository branchRepo = new InMemoryBranchRepository();

        // Core services
        Inventory inventory = new Inventory();
        LibraryService library = new LibraryService(bookRepo, patronRepo, branchRepo, inventory);
        LendingService lending = new LendingService(loanRepo, inventory);
        ReservationService reservations = new ReservationService(reservationRepo, patronRepo, inventory, NotifierFactory.Channel.CONSOLE);
        TransferService transfer = new TransferService(inventory);
        BookSearchService search = new BookSearchService(bookRepo);

        // Recommendation service (Strategy)
        RecommendationService recService = new RecommendationService(new PopularityBasedRecommendation(loanRepo, bookRepo));

        // Bootstrap (small sample data to get started)
        Branch central = new Branch("Central Library", "Main St");
        Branch east = new Branch("East Branch", "East Rd");
        library.addBranch(central);
        library.addBranch(east);

        Book b1 = new Book("Clean Code", "Robert C. Martin", "9780132350884", 2008);
        Book b2 = new Book("Effective Java", "Joshua Bloch", "9780134685991", 2018);
        Book b3 = new Book("Design Patterns", "Erich Gamma", "9780201633610", 1994);
        library.addOrUpdateBook(b1);
        library.addOrUpdateBook(b2);
        library.addOrUpdateBook(b3);

        library.addCopies(central.getId(), b1.getIsbn(), 2);
        library.addCopies(central.getId(), b2.getIsbn(), 1);
        library.addCopies(east.getId(), b2.getIsbn(), 2);
        library.addCopies(east.getId(), b3.getIsbn(), 1);

        Patron alice = library.addPatron("Alice", "alice@example.com", "111-111");
        Patron bob = library.addPatron("Bob", "bob@example.com", "222-222");
        //======================== take imput from console ========================


        try (Scanner sc = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMenu();
                int choice = readInt(sc, "Choose an option: ");
                switch (choice) {
                    case 0:
                        running = false;
                        break;
                    case 1:
                        handleAddBook(sc, library);
                        break;
                    case 2:
                        handleUpdateBook(sc, library, bookRepo);
                        break;
                    case 3:
                        handleRemoveBook(sc, library);
                        break;
                    case 4:
                        handleSearchBooks(sc, search);
                        break;
                    case 5:
                        listAllBooks(bookRepo);
                        break;
                    case 6:
                        handleAddBranch(sc, library);
                        break;
                    case 7:
                        listBranches(branchRepo);
                        break;
                    case 8:
                        handleAddCopies(sc, library, bookRepo, branchRepo);
                        break;
                    case 9:
                        handleTransfer(sc, transfer, branchRepo, bookRepo);
                        break;
                    case 10:
                        handleAddPatron(sc, library);
                        break;
                    case 11:
                        handleUpdatePatron(sc, patronRepo);
                        break;
                    case 12:
                        listPatrons(patronRepo);
                        break;
                    case 13:
                        handleCheckout(sc, lending, bookRepo, patronRepo, branchRepo);
                        break;
                    case 14:
                        handleReturn(sc, lending, loanRepo, reservations);
                        break;
                    case 15:
                        handleReserve(sc, reservations, bookRepo, patronRepo, branchRepo);
                        break;
                    case 16:
                        listAllLoans(loanRepo);
                        break;
                    case 17:
                        handlePatronHistory(sc, lending, patronRepo);
                        break;
                    case 18:
                        handleRecommendations(sc, recService, bookRepo, loanRepo);
                        break;
                    case 19:
                        handleSwitchStrategy(sc, recService, loanRepo, bookRepo);
                        break;
                    default:
                        System.out.println("Unknown option. Try again.");
                }
                System.out.println();
            }
        }

        log.info("Exiting interactive app.");
    }

    private static void printMenu() {
        System.out.println("=== Library Menu ===");
        System.out.println("0) Exit");
        System.out.println("1) Add Book");
        System.out.println("2) Update Book");
        System.out.println("3) Remove Book");
        System.out.println("4) Search Books");
        System.out.println("5) List All Books");
        System.out.println("6) Add Branch");
        System.out.println("7) List Branches");
        System.out.println("8) Add Copies to Branch");
        System.out.println("9) Transfer Copies Between Branches");
        System.out.println("10) Add Patron");
        System.out.println("11) Update Patron");
        System.out.println("12) List Patrons");
        System.out.println("13) Checkout Book");
        System.out.println("14) Return Book");
        System.out.println("15) Reserve Book");
        System.out.println("16) List All Loans");
        System.out.println("17) Show Patron Borrowing History");
        System.out.println("18) Show Recommendations for Patron");
        System.out.println("19) Switch Recommendation Strategy");
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



    private static void handleTransfer(Scanner sc, TransferService transfer, BranchRepository branchRepo, BookRepository bookRepo) {
        Branch from = chooseBranch(sc, branchRepo);
        if (from == null) return;
        Branch to = chooseBranch(sc, branchRepo);
        if (to == null) return;
        String isbn = chooseBook(sc, bookRepo);
        if (isbn == null) return;
        int copies = readInt(sc, "Copies to transfer: ");
        try {
            transfer.transfer(from.getId(), to.getId(), isbn, copies);
            System.out.println("Transfer successful.");
        } catch (Exception e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
    }

    private static void handleCheckout(Scanner sc, LendingService lending, BookRepository bookRepo, PatronRepository patronRepo, BranchRepository branchRepo) {
        String isbn = chooseBook(sc, bookRepo);
        if (isbn == null) return;
        Patron patron = choosePatron(sc, patronRepo);
        if (patron == null) return;
        Branch branch = chooseBranch(sc, branchRepo);
        if (branch == null) return;
        int days = readInt(sc, "Loan days (e.g., 14): ");
        Optional<Loan> loan = lending.chechout(isbn, patron.getId(), branch.getId(), days);
        if (loan.isPresent()) {
            System.out.println("Checked out. Loan id: " + loan.get().getId());
        } else {
            System.out.println("Could not checkout (no available copies). You may reserve the book.");
        }
    }

    private static void handleReturn(Scanner sc, LendingService lending, LoanRepository loanRepo, ReservationService reservations) {
        listActiveLoans(loanRepo);
        String loanId = readLine(sc, "Enter loan id to return: ");
        boolean ok = lending.returnBook(loanId);
        if (ok) {
            // find loan to notify reservations
            loanRepo.findById(loanId).ifPresent(l -> {
                reservations.onCopyAvailable(l.getIsbn(), l.getBranchID());
            });
            System.out.println("Returned successfully.");
        } else {
            System.out.println("Return failed (loan not found or already returned).");
        }
    }

    private static void handleReserve(Scanner sc, ReservationService reservations, BookRepository bookRepo, PatronRepository patronRepo, BranchRepository branchRepo) {
        String isbn = chooseBook(sc, bookRepo);
        if (isbn == null) return;
        Patron patron = choosePatron(sc, patronRepo);
        if (patron == null) return;
        Branch branch = chooseBranch(sc, branchRepo);
        if (branch == null) return;
        Reservation res = reservations.reserve(isbn, patron.getId(), branch.getId());
        System.out.println("Reserved: " + res.getId());
    }

    private static void handlePatronHistory(Scanner sc, LendingService lending, PatronRepository patronRepo) {
        Patron patron = choosePatron(sc, patronRepo);
        if (patron == null) return;
        List<Loan>  history = lending.borrowingHistory(patron.getId());
        System.out.println("History for " + patron.getName() + " (" + history.size() + "):");
        history.forEach(h -> System.out.println(" - " + h));
    }

    private static void handleRecommendations(Scanner sc, RecommendationService recService, BookRepository bookRepo, LoanRepository loanRepo) {
        String pid = readLine(sc, "Enter patron id for recommendations: ");
        List<Book> recs = recService.recommend(pid, 5);
        System.out.println("Recommendations (" + recs.size() + "):");
        recs.forEach(b -> System.out.println(" - " + b));
    }

    private static void handleSwitchStrategy(Scanner sc, RecommendationService recService, LoanRepository loanRepo, BookRepository bookRepo) {
        System.out.println("Choose strategy: 1) Popularity  2) Author-affinity");
        int s = readInt(sc, "Strategy: ");
        if (s == 1) {
            recService.setStrategy(new PopularityBasedRecommendation(loanRepo, bookRepo));
            System.out.println("Switched to Popularity-based.");
        } else if (s == 2) {
            recService.setStrategy(new AuthorAffinityRecommendation(loanRepo, bookRepo));
            System.out.println("Switched to Author-affinity.");
        } else {
            System.out.println("Unknown strategy.");
        }
    }

}