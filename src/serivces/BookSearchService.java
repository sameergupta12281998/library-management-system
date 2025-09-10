package serivces;

import java.util.ArrayList;
import java.util.List;

import modal.Book;
import repository.BookRepository;

public class BookSearchService {

    private final BookRepository repo;

    public BookSearchService(BookRepository repo) {
        this.repo = repo;
    }
    
    
    public List<Book> search(String query){
        List<Book> results = new ArrayList<>();
        results.addAll(repo.findByTitle(query));
        results.addAll(repo.findByAuthor(query));
        repo.findByIsbn(query).ifPresent(results::add);
        return results;
    }
}
