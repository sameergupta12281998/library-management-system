package recommendation;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import modal.Book;
import modal.Loan;
import repository.BookRepository;
import repository.LoanRepository;

public class AuthorAffinityRecommendation implements RecommendationStrategy {

     private final LoanRepository loanRepo;
    private final BookRepository bookRepo;

    public AuthorAffinityRecommendation(LoanRepository loanRepo, BookRepository bookRepo){
        this.loanRepo = loanRepo;
        this.bookRepo = bookRepo;
    }

   @Override
    public List<Book> recommend(String patronId, int limit) {
        List<Loan> history = loanRepo.findPatronById(patronId);
        Set<String> favAuthors = history.stream()
                .map(Loan::getIsbn)
                .map(isbn -> bookRepo.findByIsbn(isbn).orElse(null))
                .filter(Objects::nonNull)
                .map(Book::getAuthor)
                .collect(Collectors.toSet());
        if (favAuthors.isEmpty()) return Collections.emptyList();

        return bookRepo.findAll().stream()
                .filter(b -> favAuthors.contains(b.getAuthor()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public String name(){ return "AuthorAffinity"; }
   
    
}
