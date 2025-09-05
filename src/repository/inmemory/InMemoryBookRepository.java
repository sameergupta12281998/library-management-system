package repository.inmemory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import modal.Book;
import repository.BookRepository;

public class InMemoryBookRepository implements BookRepository {


    private static final Logger log = Logger.getLogger(InMemoryBookRepository.class.getName());
    private final Map<String, Book> booksByIsbn = new HashMap<>();

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return Optional.ofNullable(booksByIsbn.get(isbn));
    }

    @Override
    public void save(Book book) {
        booksByIsbn.put(book.getIsbn(), book);
        log.fine(() -> "Saved book " + book);
    }

    @Override
    public void deleteByIsbn(String isbn) {
        booksByIsbn.remove(isbn);
        log.fine(() -> "Deleted book with ISBN " + isbn);
    }

    @Override
    public void update(Book book) {
        booksByIsbn.put(book.getIsbn(), book);
        log.fine(() -> "Updated book " + book);
    }

    @Override
    public List<Book> findAll() {
        return List.copyOf(booksByIsbn.values());
    }

    @Override
    public List<Book> findByAuthor(String author) {
        String authorLower = author.toLowerCase();
        return booksByIsbn.values().stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(authorLower))
                .toList();
    }

    @Override
    public List<Book> findByTitle(String title) {
        String titleLower = title.toLowerCase();
        return booksByIsbn.values().stream()
                .filter(book -> book.getTitle().toLowerCase().contains(titleLower))
                .toList();
    }
    
}
