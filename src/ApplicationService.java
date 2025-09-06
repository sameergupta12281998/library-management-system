import java.util.logging.Level;
import java.util.logging.Logger;

import modal.Book;
import modal.Patron;
import repository.BookRepository;
import repository.BranchRepository;
import repository.PatronRepository;
import repository.inmemory.InMemoryBookRepository;
import repository.inmemory.InMemoryBranchRepository;
import repository.inmemory.InMemoryPartonRepository;
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
        LibraryService libraryService = new LibraryService(bookRepository,patronRepository,branchRepository);

        Book book1 = new modal.Book("1234567890", "The Great Gatsby", "F. Scott Fitzgerald", 1925);
        Book book2 = new modal.Book("0987654321", "To Kill a Mockingbird", "Harper Lee", 1960);
        Book book3 = new modal.Book("1122334455", "1984", "George Orwell", 1949);

        // Add a new book
        libraryService.addOrUpdateBook(book1);
        libraryService.addOrUpdateBook(book2);
        libraryService.addOrUpdateBook(book3);



        Patron alice = libraryService.addPatron("Alice", "alice@example.com", "111-111");
        Patron bob = libraryService.addPatron("Bob", "bob@example.com", "222-222");


    }
}