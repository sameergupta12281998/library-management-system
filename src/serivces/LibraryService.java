package serivces;

import java.lang.foreign.Linker.Option;
import java.util.Optional;
import java.util.logging.Logger;

import modal.Book;
import repository.BookRepository;

public class LibraryService {

    private static final Logger log = Logger.getLogger(LibraryService.class.getName());

    private final BookRepository bookRepository;

    public LibraryService(BookRepository bookRepository) {
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
    
}
