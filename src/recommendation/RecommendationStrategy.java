package recommendation;

import java.util.List;

import modal.Book;

public interface RecommendationStrategy {
    List<Book> recommend(String patronId, int limit);
    String name();
}
