package recommendation;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import modal.Book;
import modal.Loan;
import repository.BookRepository;
import repository.LoanRepository;

public class PopularityBasedRecommendation implements RecommendationStrategy {
    private final LoanRepository loanRepo;
    private final BookRepository bookRepo;

    public PopularityBasedRecommendation(LoanRepository loanRepo, BookRepository bookRepo){
        this.loanRepo = loanRepo;
        this.bookRepo = bookRepo;
    }

    @Override
    public List<Book> recommend(String patronId, int limit) {
        Map<String, Long> counts = loanRepo.findAll().stream()
                .collect(Collectors.groupingBy(Loan::getIsbn, Collectors.counting()));
        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit * 2L) // get a few extra in case of missing books
                .map(e -> bookRepo.findByIsbn(e.getKey()).orElse(null))
                .filter(Objects::nonNull)
                .distinct()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public String name(){ return "PopularityBased"; }
}
