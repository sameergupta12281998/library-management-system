package serivces;

import java.lang.foreign.Linker.Option;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import modal.Book;
import modal.Branch;
import modal.Patron;
import repository.BookRepository;
import repository.BranchRepository;
import repository.PatronRepository;

public class LibraryService {

    private static final Logger log = Logger.getLogger(LibraryService.class.getName());

    private final BookRepository bookRepository;
    private final PatronRepository patronRepo;
    private final BranchRepository branchRepo;
    private final Inventory inventory;


    public LibraryService(BookRepository bookRepository, PatronRepository patronRepo, BranchRepository branchRepo, Inventory inventory) {
        this.inventory = inventory;
        this.branchRepo = branchRepo;
        this.patronRepo = patronRepo;
        this.bookRepository = bookRepository;
    }

    //Book Management
    public void addOrUpdateBook(Book book) {
        Optional<Book> existingBook = bookRepository.findByIsbn(book.getIsbn());
        if (existingBook.isPresent()) {
            bookRepository.update(book);
            log.info("Book updated: " + book);
        } else {
            bookRepository.save(book);
            log.info("Book added: " + book);
        }
    }

    public void removeBook(String isbn) {
        Optional<Book> existingBook = bookRepository.findByIsbn(isbn);
        if (existingBook.isPresent()) {
            bookRepository.deleteByIsbn(isbn);
            log.info("Book removed: " + existingBook.get());
        } else {
            log.warning("Attempted to remove non-existent book with ISBN: " + isbn);
        }
    }




    public List<Book> searchByTitle(String q){ return bookRepository.findByTitle(q); }
    public List<Book> searchByAuthor(String q){ return bookRepository.findByAuthor(q); }
    public Optional<Book> findByIsbn(String isbn){ return bookRepository.findByIsbn(isbn); }
    public List<Book> findAllBooks(){ return bookRepository.findAll(); }


    // Patron Management
    public Patron addPatron(String name, String email, String phone){
        Patron p = new Patron(name, email, phone);
        patronRepo.save(p);
        log.info(() -> "Added patron " + p);
        return p;
    }
    public void updatePatron(Patron p){
        patronRepo.update(p);
        log.info(() -> "Updated patron " + p);
    }
    public Optional<Patron> findPatronById(String id){ return patronRepo.findById(id); }
    public List<Patron> findAllPatrons(){ return patronRepo.findAll(); }


    // Branch Management
    public void addBranch(Branch b){
        branchRepo.save(b);
        log.info(() -> "Added branch " + b);
    }

    public Optional<Branch> findBranchById(String id){ return branchRepo.findById(id); }

    public List<Branch> findAllBranches(){ return branchRepo.findAll();};


    //Inventory Management


    public void addCopies(String branchId, String isbn, int copies){
        if (copies <= 0) throw new IllegalArgumentException("copies must be > 0");
        inventory.addCopies(branchId, isbn, copies);
        log.info(() -> "Added " + copies + " copies of isbn=" + isbn + " to branch=" + branchId);
    }

    public void removeCopies(String branchId, String isbn, int copies){
        inventory.removeCopies(branchId, isbn, copies);
        log.info(() -> "Removed " + copies + " copies of isbn=" + isbn + " from branch=" + branchId);
    }

    public int availableCopies(String branchId, String isbn){ return inventory.getAvailabeCount(branchId, isbn); }
   
    public int totalCopies(String branchId, String isbn){ return inventory.getTotalCount(branchId, isbn); }
    Inventory getInventory(){ return inventory; }
}

