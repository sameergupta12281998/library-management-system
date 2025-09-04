package repository;

import java.util.List;
import java.util.Optional;

import modal.Book;

public interface BookRepository {

    Optional<Book> findByIsbn(String isbn);
    void save(Book book);
    void deleteByIsbn(String isbn);
    void update(Book book);
    List<Book> findAll();
    List<Book> findByAuthor(String author);
    List<Book> findByTitle(String title);    
}
